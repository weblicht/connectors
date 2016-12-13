package eu.clarin.weblicht.connectors.cr;

import eu.clarin.weblicht.bindings.cr.Center;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wqiu on 07/12/16.
 */
public class CenterRegistryConnectorTest {

    private static final int TIMEOUT = 3000;
    private static final URI REPOSITORY_URI = URI.create("https://centres.clarin.eu/restxml/");
    private CenterRegistryConnector centerRegistryConnector = null;
    private List<Center> centers;

    @Before
    public void setUp() throws Exception {
        centerRegistryConnector = new CenterRegistryConnector(REPOSITORY_URI, TIMEOUT);
        centers = centerRegistryConnector.retrieveCenters();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void retrieveCenters() throws Exception {
        centers = centerRegistryConnector.retrieveCenters();

    }

    @Test
    public void retrieveCenterProfile() throws Exception {
        System.out.println(centerRegistryConnector.retrieveCenterProfile(centers.get(0)));

    }

}