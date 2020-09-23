package tr.org.tspb.dao;

import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class ChildFilter {

    private final String formKey;
    private final String fieldKey;
    private final String db;
    private final String table;

    public ChildFilter(Document doc) {
        this.formKey = doc.get("formKey").toString();
        this.fieldKey = doc.get("fieldKey").toString();
        this.db = doc.get("db").toString();
        this.table = doc.get("table").toString();
    }

    public String getFormKey() {
        return formKey;
    }

    public String getDb() {
        return db;
    }

    public String getTable() {
        return table;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public String print(ObjectId recordId) {
        return String.format("db.getSisterDB('%s').%s.findOne({%s:ObjectId('%s')})", db, table, fieldKey, recordId);
    }

}
