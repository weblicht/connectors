package eu.clarin.weblicht.connectors.cr;

import eu.clarin.weblicht.bindings.cmd.cp.CenterProfile;
import eu.clarin.weblicht.bindings.cmd.cp.Metadata;
import eu.clarin.weblicht.bindings.cr.Center;
import eu.clarin.weblicht.connectors.oai.OAIConnectorTest;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by wqiu on 07/12/16.
 */
public class CenterRegistryConnectorTest {
    private static final int TIMEOUT = 3000;
    private static final URI REPOSITORY_URI = URI.create("https://centres.clarin.eu/restxml/");
    private static final URI STAGING_REPOSITORY_URI = URI.create("https://centres-staging.clarin.eu/restxml/");

    @Test
    public void retrieveCenters() throws Exception {
        retrieveCenters(REPOSITORY_URI);
    }

    @Test
    public void retrieveStagingCenters() throws Exception {
        retrieveCenters(STAGING_REPOSITORY_URI);
    }

    public void retrieveCenters(URI uri) throws Exception {
        CenterRegistryConnector centerRegistryConnector = new CenterRegistryConnector(uri, TIMEOUT);
        List<Center> centers = centerRegistryConnector.retrieveCenters();

        Center center1;
        {
            Optional<Center> c1 = centers.stream().filter(c -> c.getId().equals("1")).findFirst();
            assertTrue(c1.isPresent());
            center1 = c1.get();
        }
        System.out.println(center1.getId());
        System.out.println(center1.getCenterName());
        System.out.println(center1.getLink());

        CenterProfile centerProfile = centerRegistryConnector.retrieveCenterProfile(center1);
        List<Metadata> metadata = centerProfile.getCenterExtendedInformation().getMetadata();
        assertEquals(OAIConnectorTest.EKUT_REPOSITORY_URI, metadata.get(0).getOaiAccessPoint());

        for (Metadata md : metadata) {
            System.out.println("old: set=" + md.getWebServicesSet() + "; type=" + md.getWebServiceType());
            System.out.println("new: ");
            if (md.getOaiPmhSets() != null && md.getOaiPmhSets().getSet() != null)
                md.getOaiPmhSets().getSet().forEach(s ->
                        System.out.println("\ttype: " + s.getSetType() + "; spec:" + s.getSetSpec()));
        }
    }
}
