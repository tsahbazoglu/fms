package tr.org.tspb.wf;

import tr.org.tspb.constants.ProjectConstants;
import tr.org.tspb.dao.MyActions;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.dao.MyMap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.Code;
import static tr.org.tspb.constants.ProjectConstants.FORMACTIONS;
import static tr.org.tspb.constants.ProjectConstants.FORMFIELDS;
import tr.org.tspb.pojo.RoleMap;
import org.bson.Document;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;
import tr.org.tspb.pojo.UserDetail;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyRule implements Serializable {

    private String code;
    private String name;
    private String image;
    private int guiX;
    private int guiY;
    private Code controlFunc;
    private Code onEntry;
    private Code onEntryFrom;
    private Map<String, Boolean> fields = new HashMap<>();
    private MyActions myActions;
    private boolean last;
    private Document dbo;
    private FmsScriptRunner fmsScriptRunner;
    private UserDetail userDetail; //FIXME should be iitialized

    private List<MyTransition> transitions;

    private MyRule() {

    }

    public MyRule(Document dbo, FmsScriptRunner fmsScriptRunner) {

        this.dbo = dbo;

        Document state = (Document) this.dbo.get("state");

        this.code = state.get(ProjectConstants.CODE).toString();
        this.name = state.get(ProjectConstants.NAME).toString();
        this.last = Boolean.TRUE.equals(state.get("last"));

        this.guiX = ((Number) state.get(ProjectConstants.GUI_X)).intValue();
        this.guiY = ((Number) state.get(ProjectConstants.GUI_Y)).intValue();

        this.controlFunc = (Code) dbo.get("controlFunc");
        this.onEntry = (Code) dbo.get("onEnter");
        this.onEntryFrom = (Code) dbo.get("onEnterFrom");

        if (onEntryFrom == null) {
            throw new RuntimeException("ontry event had not been defined on step : " + this.getCode() + " : " + this.getName());
        }

        List<Document> t = (List<Document>) dbo.get("transitions");
        if (t != null) {
            transitions = new ArrayList<>();
            for (Document dbo1 : t) {
                transitions.add(new MyTransition(dbo1));
            }
        }
    }

    public String getCode() {
        return code;
    }

    public Code getControlFunc() {
        return controlFunc;
    }

    public Map<String, Boolean> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    public int getGuiX() {
        return guiX;
    }

    public int getGuiY() {
        return guiY;
    }

    public MyActions getMyActions() {
        return myActions;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public Code getOnEntryFrom() {
        return onEntryFrom;
    }

    public List<MyTransition> getTransitions() {
        return Collections.unmodifiableList(transitions);
    }

    public boolean isLast() {
        return last;
    }

    public void maskFields(FmsForm myForm, Map searchObject, RoleMap loginController) {

        fields = new HashMap<>();
        Object mongoFields = dbo.get(FORMFIELDS);

        if (mongoFields instanceof Document) {

            Document fields = (Document) mongoFields;

            for (String fieldKey : fields.keySet()) {
                fields.put(fieldKey, true);
            }
        }
    }

    public void arrangeActions(FmsForm myForm, RoleMap roleMap, Document searchObject) {
        this.myActions = new MyActions.Build(myForm.getMyProject().getViewerRole(), myForm.getDb(),
                roleMap, searchObject, dbo.get(FORMACTIONS), fmsScriptRunner, userDetail)
                .init()
                .base()
                .build();
    }

    public void arrangeActions(FmsForm myForm, RoleMap roleMap, Document searchObject, MyMap crudObject, Object actions) {
        this.myActions = new MyActions.Build(myForm.getMyProject().getViewerRole(), myForm.getDb(),
                roleMap, searchObject, actions, fmsScriptRunner, userDetail)
                .init()
                .base()
                .maskSaveWithCurrentCrudObject(crudObject)
                .maskDeleteWithSave()
                .build();
    }

    @Override
    public String toString() {
        return this.code;
    }

    public Code getOnEntry() {
        return onEntry;
    }

}
