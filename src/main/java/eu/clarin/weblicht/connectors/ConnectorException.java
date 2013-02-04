package eu.clarin.weblicht.connectors;

/**
 *
 * @author akislev
 */
public class ConnectorException extends Exception {

    public ConnectorException(Throwable cause) {
        super(cause);
    }

    public ConnectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectorException(String message) {
        super(message);
    }
}
