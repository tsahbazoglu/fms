package tr.org.tspb.dao;

import org.bson.Document;
import org.bson.types.Code;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class DynamicButton {

    private String label;
    private String db;
    private Code code;
    private MyMap crudObject;
    private FmsScriptRunner fmsScriptRunner;

    private DynamicButton() {

    }

    public DynamicButton(Document document, FmsScriptRunner fmsScriptRunner) {
        this.fmsScriptRunner = fmsScriptRunner;
        this.label = document.getString("label");
        this.db = document.getString("db");
        this.code = (Code) document.get("op");
    }

    public String getLabel() {
        return label;
    }

    public String execute() {
        fmsScriptRunner.runCommand(db, code.getCode(), crudObject);
        return null;
    }

    public void setCrudObject(MyMap crudObject) {
        this.crudObject = crudObject;
    }

}
