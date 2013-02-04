package eu.clarin.weblicht.connectors;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.Closeable;

/**
 *
 * @author akislev
 */
public class AbstractConnector implements Closeable {

    protected final Client client;

    protected AbstractConnector(Integer timeoutMilliseconds) {
        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
        client.setConnectTimeout(timeoutMilliseconds);
    }

    protected AbstractConnector(Client client) {
        this.client = client;
    }

    public void close() {
        client.destroy();
    }
}
