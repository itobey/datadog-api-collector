package exception;

/**
 * Custom exception that is thrown when something regarding the adapter goes sour.
 */
public class DatadogAdapterException extends Exception {

    /**
     * Default Ctor.
     */
    public DatadogAdapterException() {
    }

    /**
     * Default Ctor with message.
     *
     * @param message the message
     */
    public DatadogAdapterException(String message) {
        super(message);
    }
}