package tr.org.tspb.exceptions;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class RecursiveLimitExceedException extends Exception {

    public RecursiveLimitExceedException() {
        super();
    }

    public RecursiveLimitExceedException(Exception ex) {
        super(ex);
    }

    public RecursiveLimitExceedException(String message) {
        super(message.concat(" could not init orm object"));
    }

    public RecursiveLimitExceedException(String message, Exception ex) {
        super(message.concat(" could not init orm object"), ex);
    }

}
