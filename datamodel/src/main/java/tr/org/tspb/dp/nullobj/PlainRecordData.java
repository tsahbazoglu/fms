package tr.org.tspb.dp.nullobj;

import javax.faces.model.SelectItem;
import org.bson.Document;
import tr.org.tspb.dao.MyBaseRecord;
import tr.org.tspb.dao.MyItems;
import tr.org.tspb.dao.NullRecord;
import tr.org.tspb.dao.refs.PlainRecord;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class PlainRecordData {

    private static final PlainRecord NULL_RECORD = new NullRecord();

    public static PlainRecord getPlainRecord(Document doc, MyItems myItem) {
        if (doc == null) {
            return NULL_RECORD;
        }
        return new MyBaseRecord(doc, myItem);
    }

    public static PlainRecord getNullPlainRecord() {
        return NULL_RECORD;
    }

    public static PlainRecord getPlainRecord(SelectItem item) {
        if (item == null) {
            return NULL_RECORD;
        }
        return new MyBaseRecord(item);
    }

    public static PlainRecord getPlainRecord(String objectIdAsString) {
        if (objectIdAsString == null) {
            return NULL_RECORD;
        }
        return new MyBaseRecord(objectIdAsString);
    }

}
