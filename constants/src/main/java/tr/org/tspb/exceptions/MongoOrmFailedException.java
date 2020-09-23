package tr.org.tspb.exceptions;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MongoOrmFailedException extends Exception {

    public MongoOrmFailedException() {
        super();
    }

    public MongoOrmFailedException(Exception ex) {
        super(ex);
    }

    public MongoOrmFailedException(String message) {
        super(message.concat(" could not init orm object"));
    }

    public MongoOrmFailedException(String message, Exception ex) {
        super(message.concat(" could not init orm object"), ex);
    }

}
