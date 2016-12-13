package eu.clarin.weblicht.connectors.cr;

import eu.clarin.weblicht.bindings.cmd.cp.CenterProfile;
import eu.clarin.weblicht.bindings.cmd.cp.CenterProfileCMD;
import eu.clarin.weblicht.bindings.cr.Center;
import eu.clarin.weblicht.bindings.cr.Centers;
import eu.clarin.weblicht.connectors.AbstractConnector;
import eu.clarin.weblicht.connectors.cmditransform.CMDIReaderInterceptor;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.List;

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

    public List<Center> retrieveCenters() {
        WebTarget webTarget= client.target(centerRegistryUri);
        webTarget.register(CMDIReaderInterceptor.class);
        Centers centers;
        centers = webTarget.request(MediaType.APPLICATION_XML).get(Centers.class);
        return centers.getCenterProfiles();
    }

    public CenterProfile retrieveCenterProfile(Center center) {
        WebTarget webTarget = client.target(center.getLink());
        webTarget.register(CMDIReaderInterceptor.class);
        CenterProfileCMD cmd;
//        String cmdString = webTarget.request(MediaType.APPLICATION_XML).get(String.class);
//        System.out.println(cmdString);
        cmd = webTarget.request(MediaType.APPLICATION_XML).get(CenterProfileCMD.class);
        return cmd.getComponents().getCenterProfile();
    }
}
