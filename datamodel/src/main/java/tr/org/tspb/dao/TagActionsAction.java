/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import static tr.org.tspb.constants.ProjectConstants.DIEZ;
import static tr.org.tspb.constants.ProjectConstants.DOLAR;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_IN;
import static tr.org.tspb.constants.ProjectConstants.PERIOD;
import static tr.org.tspb.constants.ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_PERIOD;
import static tr.org.tspb.constants.ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID;
import tr.org.tspb.pojo.UserDetail;

/**
 *
 * @author telman
 */
public class TagActionsAction {

    private String db;
    private final boolean enable;
    private final ActionEnableResult enableResult;
    private String actionFunc;
    private List<Operation> operations;

    public TagActionsAction(boolean enable, ActionEnableResult enableResult, Document eventAction, Document registredFunctions, Map myfilter, UserDetail userDetail) {
        this.enable = enable;
        this.enableResult = enableResult;

        Document whattodo = eventAction.get("action", Document.class);

        if (whattodo != null) {

            this.db = whattodo.getString("db");

            if (whattodo.getString("func") != null) {
                this.actionFunc = whattodo.getString("func");
            } else if (whattodo.getString("registred-func-name") != null) {
                String func = registredFunctions.getString(whattodo.getString("registred-func-name"));
                if (func != null) {
                    this.actionFunc = func.replace(DIEZ, DOLAR);
                }
            } else if (whattodo.getList("list", Document.class) != null) {
                List<Document> docs = whattodo.getList("list", Document.class);
                this.operations = new ArrayList<>();
                for (Document doc : docs) {
                    this.operations.add(new Operation(doc, myfilter, userDetail));
                }
            }
        }
    }

    public boolean isEnable() {
        return enable;
    }

    public String getActionFunc() {
        return actionFunc;
    }

    public String getDb() {
        return db;
    }

    public ActionEnableResult getEnableResult() {
        return enableResult;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public class Operation {

        String db, table, op;
        Document filter, set;

        public Operation(Document doc, Map myfilter, UserDetail userDetail) {
            this.db = doc.getString("db");
            this.table = doc.getString("table");
            this.op = doc.getString("op");
            List<Document> _filter = doc.getList("filter", Document.class);
            List<Document> _set = doc.getList("set", Document.class);

            this.filter = createQuery(_filter, userDetail, myfilter);
            this.set = createQuery(_set, userDetail, myfilter);

        }

        public String getDb() {
            return db;
        }

        public String getTable() {
            return table;
        }

        public String getOp() {
            return op;
        }

        public Document getFilter() {
            return filter;
        }

        public Document getSet() {
            return set;
        }

    }

    private static Document createQuery(List<Document> filters, UserDetail userDetail, Map myFilter) throws RuntimeException {
        Document query = new Document();
        for (Document filter : filters) {
            String key = filter.getString("key");
            String stringValue = filter.getString("string-value");
            List<String> arrayValue = filter.getList("array-value", String.class);
            String fmsValue = filter.getString("fms-value");

            if (stringValue != null) {
                query.append(key, stringValue);
            } else if (arrayValue != null) {
                query.append(key, new Document(DOLAR_IN, arrayValue));
            } else if (fmsValue != null) {
                switch (fmsValue) {
                    case REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID:
                        query.append(key, userDetail.getDbo().getObjectId());
                        break;
                    case REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_PERIOD:
                        query.append(key, myFilter.get(PERIOD));
                        break;
                    default:
                        throw new RuntimeException(fmsValue.concat(" is not supported"));
                }
            } else {
                throw new RuntimeException(key.concat(" has a not supported type of value"));
            }
        }
        return query;
    }

}
