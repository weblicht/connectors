package eu.clarin.weblicht.connectors.oai;

import eu.clarin.weblicht.bindings.cmd.cp.*;
import eu.clarin.weblicht.bindings.oai.OAIPMH;
import eu.clarin.weblicht.bindings.oai.Record;
import eu.clarin.weblicht.bindings.oai.ResumptionToken;
import eu.clarin.weblicht.connectors.AbstractConnector;
import eu.clarin.weblicht.connectors.ConnectorException;
import eu.clarin.weblicht.connectors.cmditransform.CMDIReaderInterceptor;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author akislev
 */
public class OAIConnector extends AbstractConnector {

    private static final String VERB = "verb";
    private static final String METADATA_PREFIX = "metadataPrefix";
    private static final String SET = "set";
    private static final String LIST_RECORDS = "ListRecords";
    private static final String RESUMPTION_TOKEN = "resumptionToken";
    private static final String CMDI = "cmdi";

    public OAIConnector(Integer timeoutMilliseconds) {
        super(timeoutMilliseconds);
    }

    public OAIConnector(Client client) {
        super(client);
    }

    public List<Record> retrieveRecords(URI uri) throws ConnectorException {
        WebTarget oaiResource = buildWebResource(uri, null);
        return retrieveRecords(oaiResource);
    }

    public List<Record> retrieveRecords(URI uri, String webServiceSet) throws ConnectorException {
        WebTarget oaiResource = buildWebResource(uri, webServiceSet);
        return retrieveRecords(oaiResource);
    }

    public List<Record> retrieveRecords(Metadata metadata) throws ConnectorException {
        WebTarget oaiResource = buildWebResource(metadata);
        return retrieveRecords(oaiResource);
    }

    public List<Record> retrieveRecords(WebTarget oaiResource) throws ConnectorException {
        List<Record> records = null;
        WebTarget nextOAIResource = oaiResource;
        while (true) {
            OAIPMH oaipmh = retrieveOAIPMH(nextOAIResource);
            if (oaipmh.getListRecords() == null || oaipmh.getListRecords().getRecords() == null) {
                throw new ConnectorException(oaipmh.getError() != null ? oaipmh.getError() : "oai-pmh response contains no records");
            }
            ResumptionToken resumptionToken = oaipmh.getListRecords().getResumptionToken();
            if (resumptionToken != null) {
                if (records == null) {
                    records = new ArrayList<Record>();
                    oaiResource = client.target(oaiResource.getUriBuilder().replaceQuery(null).build()).queryParam(VERB, LIST_RECORDS);
                    oaiResource.register(CMDIReaderInterceptor.class);
                }
                records.addAll(oaipmh.getListRecords().getRecords());
                if (resumptionToken.getValue() != null && !resumptionToken.getValue().isEmpty()) {
                    nextOAIResource = oaiResource.queryParam(RESUMPTION_TOKEN, resumptionToken.getValue());
                } else {
                    return records;
                }
            } else {
                return oaipmh.getListRecords().getRecords();
            }
        }
    }

    public static List<Record> readRecords(InputStream inputStream) throws ConnectorException {
        try {
            JAXBContext context = JAXBContext.newInstance(OAIPMH.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            OAIPMH oaipmh = ((OAIPMH) unmarshaller.unmarshal(inputStream));
            inputStream.close();
            return oaipmh.getListRecords().getRecords();
        } catch (IOException ex) {
            throw new ConnectorException(ex);
        } catch (JAXBException ex) {
            throw new ConnectorException(ex);
        }
    }

    public OAIPMH retrieveOAIPMH(WebTarget oaiResource) throws ConnectorException {
        try {
            String result = oaiResource.request(MediaType.APPLICATION_XML).get(String.class);
            return oaiResource.request(MediaType.APPLICATION_XML).get(OAIPMH.class);
        } catch (ProcessingException ex) {
            throw new ConnectorException("unable to retrieve services", ex);
        } catch (WebApplicationException ex) {
            throw new ConnectorException("unable to retrieve services", ex);
        }
    }

    public InputStream retrieveInputStream(WebTarget oaiResource) throws ConnectorException {
        try {
            return oaiResource.request(MediaType.APPLICATION_XML).get(InputStream.class);
        } catch (ProcessingException ex) {
            throw new ConnectorException("unable to retrieve services", ex);
        } catch (WebApplicationException ex) {
            throw new ConnectorException("unable to retrieve services", ex);
        }
    }

    public WebTarget buildWebResource(Metadata metadata) throws ConnectorException {
        URI uri = metadata.getOaiAccessPoint();
        if (uri != null) {
            return buildWebResource(uri, metadata.getWebServicesSet());
        }
        throw new ConnectorException("bad oai access point uri: " + uri);
    }

    public WebTarget buildWebResource(URI uri, String webServiceSet) throws ConnectorException {
            UriBuilder uriBuilder = UriBuilder.fromUri(uri);
            uriBuilder = uriBuilder.replaceQuery(null);
            WebTarget oaiResource = client.target(uriBuilder.build()).queryParam(VERB, LIST_RECORDS).queryParam(METADATA_PREFIX, CMDI);
            if (webServiceSet != null && !webServiceSet.isEmpty()) {
                oaiResource = oaiResource.queryParam(SET, webServiceSet);
            }
            oaiResource.register(CMDIReaderInterceptor.class);
            return oaiResource;

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
