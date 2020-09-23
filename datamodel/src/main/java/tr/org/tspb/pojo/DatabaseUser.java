package tr.org.tspb.pojo;

import org.bson.Document;
import org.bson.types.ObjectId;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import static tr.org.tspb.constants.ProjectConstants.NAME;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class DatabaseUser {

    private final ObjectId objectId;
    private final String ldapUID;
    private final String name;

    public DatabaseUser(Document bsonObject, String usernameField) {
        this.objectId = (ObjectId) bsonObject.get(MONGO_ID);
        this.ldapUID = bsonObject.get(usernameField).toString();
        this.name = (String) bsonObject.get(NAME);
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public String getLdapUID() {
        return ldapUID;
    }

    public String getName() {
        return name;
    }

    public boolean notFound() {
        return objectId == null;
    }
}
