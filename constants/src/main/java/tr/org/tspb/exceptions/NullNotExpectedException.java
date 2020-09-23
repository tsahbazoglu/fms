package tr.org.tspb.exceptions;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class NullNotExpectedException extends Exception {

    public NullNotExpectedException() {
        super();
    }

    public NullNotExpectedException(String message) {
        super(message.concat(" is not expeted to be null"));
    }

    public NullNotExpectedException(String message, Exception ex) {
        super(message.concat(" is not expeted to be null"), ex);
    }

}
