package eu.clarin.weblicht.connectors;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.Closeable;

/**
 *
 * @author akislev
 */
public class AbstractConnector implements Closeable {

    protected final Client client;

    protected AbstractConnector(Integer timeoutMilliseconds) {
        client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, timeoutMilliseconds);
    }

    protected AbstractConnector(Client client) {
        this.client = client;
    }

    public void close() {
        client.close();
    }
}
