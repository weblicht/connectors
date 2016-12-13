package eu.clarin.weblicht.connectors.oai;

import eu.clarin.weblicht.bindings.oai.Record;
import java.net.URI;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author akislev
 */
public class OAIConnectorTest {

    private static final int TIMEOUT = 3000;
    private static final URI REPOSITORY_URI = URI.create("http://weblicht.sfs.uni-tuebingen.de/oaiprovider/");
    private static final URI REPOSITORY_URI_BERLIN = URI.create("http://clarin.bbaw.de:8088/oaiprovider");
    private OAIConnector connector;

    public OAIConnectorTest() {
    }

    @Before
    public void setUp() {
        connector = new OAIConnector(TIMEOUT);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of retrieveRecords method, of class OAIConnector.
     */
    @Test
    public void testRetrieveRecords_URI() throws Exception {
        System.out.println("retrieveRecords");
        List<Record> result = connector.retrieveRecords(REPOSITORY_URI);
    }

    @Test
    public void testRetrieveRecords_URI_Berlin() throws Exception {
        System.out.println("retrieveRecords");
        List<Record> result = connector.retrieveRecords(REPOSITORY_URI_BERLIN);
    }

}