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
import tr.org.tspb.datamodel.expected.FmsScriptRunner;
import tr.org.tspb.pojo.UserDetail;
import tr.org.tspb.tags.FmsCheck;
import tr.org.tspb.tags.FmsQuery;

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
    private List<FmsCheck> checkList;

    public TagActionsAction(boolean enable, ActionEnableResult enableResult,
            Document eventAction, Document registredFunctions, Map myfilter,
            UserDetail userDetail, FmsScriptRunner fmsScriptRunner) {
        this.enable = enable;
        this.enableResult = enableResult;

        Document whattodo = eventAction.get("action", Document.class);

        if (whattodo != null) {

            this.db = whattodo.getString("db");

            List<Document> checkList;

            if ((checkList = whattodo.getList("check-list", Document.class)) != null) {
                this.checkList = new ArrayList<>();
                for (Document document : checkList) {
                    this.checkList.add(FmsCheck.build(document, myfilter,
                            userDetail.getDbo().getObjectId(), fmsScriptRunner));
                }
            }

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

    public List<FmsCheck> getCheckList() {
        return checkList;
    }

    public class Operation {

        String db;
        String table;
        String op;
        Document set;
        Document filter;
        Boolean ifcase;

        public Operation(Document doc, Map myfilter, UserDetail userDetail) {
            this.ifcase = doc.getBoolean("case");
            this.db = doc.getString("db");
            this.table = doc.getString("table");
            this.op = doc.getString("op");
            List<Document> _filter = doc.getList("filter", Document.class);
            List<Document> _set = doc.getList("set", Document.class);
            this.filter = FmsQuery.buildListQuery(_filter, myfilter, null, userDetail.getDbo().getObjectId());
            this.set = FmsQuery.buildListQuery(_set, myfilter, null, userDetail.getDbo().getObjectId());
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

        public Boolean getIfcase() {
            return ifcase;
        }

    }

}
