package tr.org.tspb.dao;

import org.bson.Document;
import static tr.org.tspb.constants.ProjectConstants.COLLECTION;
import static tr.org.tspb.constants.ProjectConstants.FORM_DB;
import static tr.org.tspb.constants.ProjectConstants.MONGO_LDAP_UID;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class TagLogin {

    private final String db;
    private final String table;
    private final String usernanmeField;
    private final String emailField;
    private final Document filter;

    public TagLogin(Document docLogin) {
        this.db = docLogin.get(FORM_DB).toString();
        this.table = docLogin.get(COLLECTION).toString();
        this.usernanmeField = docLogin.get(MONGO_LDAP_UID).toString();
        this.emailField = "email";
        this.filter = docLogin.get("query", Document.class);
    }

    public String getDb() {
        return db;
    }

    public String getTable() {
        return table;
    }

    public String getUsernanmeField() {
        return usernanmeField;
    }

    public Document getFilter() {
        return filter;
    }

    public String getEmailField() {
        return emailField;
    }

}
