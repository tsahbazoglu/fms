package tr.org.tspb.exceptions;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class LdapException extends Exception {

    public LdapException() {
        super();
    }

    public LdapException(String message) {
        super(message.concat(" is not expeted to be null"));
    }

    public LdapException(String message, Exception ex) {
        super(message.concat(" is not expeted to be null"), ex);
    }

}
