package eu.clarin.weblicht.connectors.cr;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import eu.clarin.weblicht.bindings.cmd.cp.CenterProfile;
import eu.clarin.weblicht.bindings.cmd.cp.CenterProfileCMD;
import eu.clarin.weblicht.bindings.cr.Center;
import eu.clarin.weblicht.bindings.cr.Centers;
import eu.clarin.weblicht.connectors.AbstractConnector;
import eu.clarin.weblicht.connectors.ConnectorException;
import java.net.URI;
import java.util.List;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author akislev
 */
public class CenterRegistryConnector extends AbstractConnector {

    private final URI centerRegistryUri;

    public CenterRegistryConnector(URI centerRegistryUri, Integer timeoutMilliseconds) {
        super(timeoutMilliseconds);
        this.centerRegistryUri = centerRegistryUri;
    }

    public CenterRegistryConnector(Client client, URI centerRegistryUri) {
        super(client);
        this.centerRegistryUri = centerRegistryUri;
    }

    public List<Center> retrieveCenters() throws ConnectorException {
        WebResource webResource = client.resource(centerRegistryUri);
        Centers centers;
        try {
            centers = webResource.accept(MediaType.APPLICATION_XML).get(Centers.class);
        } catch (UniformInterfaceException ex) {
            throw new ConnectorException(ex);
        } catch (ClientHandlerException ex) {
            throw new ConnectorException(ex);
        }
        return centers.getCenterProfiles();
    }

    public CenterProfile retrieveCenterProfile(Center center) throws ConnectorException {
        WebResource webResource = client.resource(center.getLink());
        CenterProfileCMD cmd;
        try {
            cmd = webResource.accept(MediaType.APPLICATION_XML).get(CenterProfileCMD.class);
        } catch (UniformInterfaceException ex) {
            throw new ConnectorException(ex);
        } catch (ClientHandlerException ex) {
            throw new ConnectorException(ex);
        }
        return cmd.getComponents().getCenterProfile();
    }
}
