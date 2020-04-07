package eu.clarin.weblicht.connectors.oai;

import eu.clarin.weblicht.bindings.cmd.ws.Components;
import eu.clarin.weblicht.bindings.oai.Record;
import org.junit.Test;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertFalse;

/**
 * @author akislev
 */
public class OAIConnectorTest {
    private static final int TIMEOUT = 3000;
    public static final URI EKUT_REPOSITORY_URI = URI.create("https://talar.sfb833.uni-tuebingen.de:8443/erdora/rest/oai");

    @Test
    public void testRetrieveRecords_URI() throws Exception {
        OAIConnector connector = new OAIConnector(TIMEOUT);
        List<Record> result = connector.retrieveRecords(EKUT_REPOSITORY_URI);
        assertFalse(result.isEmpty());
        for (Record r : result) {
            Components comp = r.getCMD().getComponents();
            try {
                String name = comp.getWebLichtWebService().getService().getName().getValue();
                System.out.println("" + name);
            } catch (NullPointerException xc) {
                System.out.println("not a webservice: "  + comp);
            }
        }
    }
}
