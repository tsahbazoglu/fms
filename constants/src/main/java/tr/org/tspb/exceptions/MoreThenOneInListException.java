package tr.org.tspb.exceptions;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MoreThenOneInListException extends Exception {

    public MoreThenOneInListException() {
        super();
    }

    public MoreThenOneInListException(String message) {
        super(message.concat(" is more then one record in list"));
    }
}
