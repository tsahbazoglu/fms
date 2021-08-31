/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.dao;

import java.util.List;
import org.bson.Document;
import static tr.org.tspb.constants.ProjectConstants.COLLECTION;
import static tr.org.tspb.constants.ProjectConstants.MESSAGE;
import static tr.org.tspb.constants.ProjectConstants.TYPE;

/**
 *
 * @author telman
 */
public class TagEvent {

    private String db;
    private TagEventType type;
    private String table;
    private String op;
    private String msg;
    private String jsFunction;
    private Document cacheQuery;

    public enum TagEventType {
        showWarnErrPopup,
        application,
        nothing
    }

    public static TagEvent value(Document docEvent, Document registredFunctions) {

        if (docEvent == null) {
            return null;
        }

        TagEvent tagEvent = new TagEvent();

        tagEvent.db = docEvent.getString("db");
        String type = docEvent.getString(TYPE);

        if (type != null) {
            switch (type) {
                case "showWarnErrPopup":
                    tagEvent.type = TagEventType.showWarnErrPopup;
                    break;
                case "application":
                    tagEvent.type = TagEventType.showWarnErrPopup;
                    break;
                default:
                    throw new RuntimeException(type.concat(" is not supported event type"));
            }
        } else {
            tagEvent.type = TagEventType.nothing;
        }

        tagEvent.table = docEvent.getString(COLLECTION);
        tagEvent.msg = docEvent.getString(MESSAGE);

        List<String> ul = docEvent.getList("ul", String.class);
        if (ul != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<ul>");
            for (String string : ul) {
                sb.append("<li>").append(string).append("</li>");
            }
            sb.append("</ul>");
            tagEvent.msg = sb.toString();
        }

        tagEvent.jsFunction = docEvent.getString("jsFunction");

        tagEvent.cacheQuery = docEvent.get("cacheQuery", Document.class);

        String op = docEvent.get("op", String.class);
        if (op == null) {
            String registredOpName = docEvent.getString("registred-func-name");
            op = registredFunctions.getString(registredOpName);
        }
        tagEvent.op = op;

        return tagEvent;
    }

    public String getDb() {
        return db;
    }

    public String getOp() {
        return op;
    }

    public TagEventType getType() {
        return type;
    }

    public String getTable() {
        return table;
    }

    public String getMsg() {
        return msg;
    }

    public Document getCacheQuery() {
        return cacheQuery;
    }

    public String getJsFunction() {
        return jsFunction;
    }

}
