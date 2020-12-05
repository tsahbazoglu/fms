/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.dao;

import org.bson.Document;
import static tr.org.tspb.constants.ProjectConstants.DIEZ;
import static tr.org.tspb.constants.ProjectConstants.DOLAR;

/**
 *
 * @author telman
 */
public class FmsAction {

    private String db;
    private final boolean enable;
    private final ActionEnableResult enableResult;
    private String actionFunc;

    public FmsAction(boolean enable, ActionEnableResult enableResult, Document eventAction, Document registredFunctions) {
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

}
