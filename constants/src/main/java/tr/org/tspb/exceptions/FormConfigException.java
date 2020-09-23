package tr.org.tspb.exceptions;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class FormConfigException extends Exception {

    public FormConfigException() {
        super();
    }

    public FormConfigException(String message) {
        super(message.concat(" is not expeted to be null"));
    }

    public FormConfigException(String message, Exception ex) {
        super(message.concat(" is not expeted to be null"), ex);
    }

}
