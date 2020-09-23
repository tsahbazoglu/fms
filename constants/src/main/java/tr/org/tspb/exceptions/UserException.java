package tr.org.tspb.exceptions;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class UserException extends Exception {

    private String title;
    private String type;

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable t) {
        super(message, t);
    }

    public UserException(String title, String message, Throwable t) {
        super(message, t);
        this.title = title;
    }

    public UserException(String title, String message) {
        super(message);
        this.title = title;
    }

    public UserException(String type, String title, String message) {
        super(message);
        this.type = type;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }
}
