package tr.org.tspb.util.tools;

import java.util.List;
import org.bson.Document;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import static tr.org.tspb.constants.ProjectConstants.DIEZ;
import static tr.org.tspb.constants.ProjectConstants.DOLAR;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class RecurcivlyReplaceableDocument {

    private final Document document;

    public RecurcivlyReplaceableDocument(Document document) {
        this.document = document;
    }

    public Object getValue(String... keys) {
        Object value = null;
        for (String key : keys) {
            if (value instanceof Document) {
                value = new RecurcivlyReplaceableDocument((Document) value).get(key);
            } else {
                value = document.get(key);
            }
        }

        return value;
    }

    public Document replaceToDollar() {

        Document result = new Document();

        for (String key : document.keySet()) {
            Object value = document.get(key);
            if (value instanceof ObjectId) {
                result.put(key.replace(DIEZ, DOLAR).replace("!DOT!", "."), value);
            } else if (value instanceof List) {
                result.put(key.replace(DIEZ, DOLAR).replace("!DOT!", "."), value);
            } else if (value instanceof Document) {
                result.put(key.replace(DIEZ, DOLAR).replace("!DOT!", "."), new RecurcivlyReplaceableDocument((Document) value).replaceToDollar());
            } else if (value instanceof Code) {
                value = new Code(value.toString().replace(DIEZ, DOLAR).replace("!DOT!", "."));
                key = key.replace(DIEZ, DOLAR).replace("!DOT!", ".");
                result.put(key, value);
            } else if (value instanceof Number) {
                result.put(key.replace(DIEZ, DOLAR).replace("!DOT!", "."), value);
            } else if (value instanceof List) {
                result.put(key.replace(DIEZ, DOLAR).replace("!DOT!", "."), value);
            } else {
                value = value.toString().replace(DIEZ, DOLAR).replace("!DOT!", ".");
                result.put(key.replace(DIEZ, DOLAR).replace("!DOT!", "."), value);
            }
        }

        return result;
    }

    private Object get(String key) {
        return document.get(key);
    }

}
