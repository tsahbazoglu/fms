package tr.org.tspb.dao;

import org.bson.Document;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class CalculateTag {

    private String func;
    private Document doc;

    private boolean function;
    private boolean document;
    private boolean available = true;

    public CalculateTag(Document calculateTag) {
        if (calculateTag == null) {
            available = false;
            return;
        }

        if (calculateTag.get("func") != null) {
            this.func = calculateTag.get("func", String.class);
            function = true;
        }

        if (calculateTag.get("doc") != null) {
            this.doc = calculateTag.get("doc", Document.class);
            document = true;
        }
    }

    public String getFunc() {
        return func;
    }

    public Document getDoc() {
        return doc;
    }

    public boolean isFunction() {
        return function;
    }

    public boolean isDocument() {
        return document;
    }

    public boolean isAvailable() {
        return available;
    }

}
