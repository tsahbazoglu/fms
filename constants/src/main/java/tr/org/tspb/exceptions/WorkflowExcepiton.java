package tr.org.tspb.exceptions;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class WorkflowExcepiton extends Exception {

    public WorkflowExcepiton() {
        super();
    }

    public WorkflowExcepiton(String message) {
        super(message.concat(" is not expeted to be null"));
    }

    public WorkflowExcepiton(String message, Exception ex) {
        super(message.concat(" is not expeted to be null"), ex);
    }

}
