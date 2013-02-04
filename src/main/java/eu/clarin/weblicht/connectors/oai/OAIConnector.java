package eu.clarin.weblicht.connectors.oai;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import eu.clarin.weblicht.bindings.cmd.cp.MetadataScheme;
import eu.clarin.weblicht.bindings.cmd.cp.WebServiceType;
import eu.clarin.weblicht.bindings.cmd.cp.Metadata;
import eu.clarin.weblicht.bindings.cmd.cp.SimpleMetadataScheme;
import eu.clarin.weblicht.bindings.cmd.cp.SimpleWebServiceType;
import eu.clarin.weblicht.bindings.oai.OAIPMHBinding;
import eu.clarin.weblicht.connectors.AbstractConnector;
import eu.clarin.weblicht.connectors.ConnectorException;
import java.net.URI;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 *
 * @author akislev
 */
public class OAIConnector extends AbstractConnector {

    private static final String VERB = "verb";
    private static final String METADATA_PREFIX = "metadataPrefix";
    private static final String SET = "set";
    private static final String LIST_RECORDS = "ListRecords";
    private static final String CMDI = "cmdi";

    public OAIConnector(Integer timeoutMilliseconds) {
        super(timeoutMilliseconds);
    }

    public OAIConnector(Client client) {
        super(client);
    }

    public OAIPMHBinding retrieveServices(Metadata metadata) throws ConnectorException {
        WebResource oaiResource = buildWebResource(metadata);
        return retrieveServices(oaiResource);
    }

    public OAIPMHBinding retrieveServices(WebResource oaiResource) throws ConnectorException {
        try {
            return oaiResource.accept(MediaType.APPLICATION_XML).get(OAIPMHBinding.class);
        } catch (UniformInterfaceException ex) {
            throw new ConnectorException("unable to retrieve services", ex);
        } catch (ClientHandlerException ex) {
            throw new ConnectorException("unable to retrieve services", ex);
        }
    }

    public WebResource buildWebResource(Metadata metadata) {
        URI uri = metadata.getOaiAccessPoint();
        String webServiceSet = metadata.getWebServicesSet();
        if (uri != null && uri.getQuery() != null) {
            UriBuilder uriBuilder = UriBuilder.fromUri(uri);
            uriBuilder = uriBuilder.replaceQuery(null);
            WebResource oaiResource = client.resource(uriBuilder.build()).queryParam(VERB, LIST_RECORDS).queryParam(METADATA_PREFIX, CMDI);
            if (webServiceSet != null && !webServiceSet.isEmpty()) {
                oaiResource = oaiResource.queryParam(SET, webServiceSet);
            }
            return oaiResource;

        }
        return null;
    }

    public static boolean hasCMDI(Metadata metadata) {
        for (MetadataScheme scheme : metadata.getMetadataScheme()) {
            if (scheme.getValue() == SimpleMetadataScheme.CMDI) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasWebLichtWebServices(Metadata metadata) {
        for (WebServiceType type : metadata.getWebServiceType()) {
            if (type.getValue() == SimpleWebServiceType.WEB_LICHT) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasWebServicesSet(Metadata metadata) {
        return (metadata.getWebServicesSet() != null && !metadata.getWebServicesSet().isEmpty());
    }
}
