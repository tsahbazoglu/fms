package tr.org.tspb.dao;

import javax.faces.model.SelectItem;
import org.bson.Document;
import org.bson.types.ObjectId;
import tr.org.tspb.constants.ProjectConstants;
import tr.org.tspb.dao.refs.PlainRecord;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyBaseRecord extends PlainRecord {

    private ObjectId objectId;
    private String objectIdStr;
    private String name;

    public MyBaseRecord() {
        /*
        
        the default constructor is called by jsf framework when we try to submit non succeeded form more than once
        So the default constructor have to be defined to avoid no method match exception
       
        NEVER PUT ANY CODE HERE
    
java.lang.IllegalStateException: java.lang.InstantiationException: tr.org.tspb.dao.MyBaseRecord
	at javax.faces.component.StateHolderSaver.restore(StateHolderSaver.java:133)
	at javax.faces.component.ComponentStateHelper.restoreState(ComponentStateHelper.java:276)
	at javax.faces.component.UIComponentBase.restoreState(UIComponentBase.java:1249)
	at javax.faces.component.UIOutput.restoreState(UIOutput.java:243)
	at javax.faces.component.UIInput.restoreState(UIInput.java:1512)
	at com.sun.faces.application.view.FaceletPartialStateManagementStrategy$2.visit(FaceletPartialStateManagementStrategy.java:372)
	at com.sun.faces.component.visit.FullVisitContext.invokeVisitCallback(FullVisitContext.java:127)
	at javax.faces.component.UIComponent.visitTree(UIComponent.java:1456)
Caused by: java.lang.InstantiationException: tr.org.tspb.dao.MyBaseRecord
	at java.lang.Class.newInstance(Class.java:427)
	at javax.faces.component.StateHolderSaver.restore(StateHolderSaver.java:130)
	... 65 more
Caused by: java.lang.NoSuchMethodException: tr.org.tspb.dao.MyBaseRecord.<init>()
	at java.lang.Class.getConstructor0(Class.java:3082)
	at java.lang.Class.newInstance(Class.java:412)
	... 66 more

         */
    }

    public MyBaseRecord(SelectItem item) {
        this.objectId = (ObjectId) item.getValue();
        this.objectIdStr = objectId.toString();
        this.name = item.getLabel();
    }

    public MyBaseRecord(Document doc, MyItems myItem) {

        StringBuilder view = new StringBuilder();
        for (String viewKey : myItem.getView()) {
            view.append(doc.get(viewKey));
            view.append(" - ");
        }
        this.objectId = (ObjectId) doc.get(ProjectConstants.MONGO_ID);
        this.objectIdStr = objectId.toString();
        this.name = view.toString();
    }

    public MyBaseRecord(String objectIdAsString) {
        this.objectId = new ObjectId(objectIdAsString);
    }

    @Override
    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    @Override
    public String getObjectIdStr() {
        return objectIdStr;
    }

    public void setObjectIdStr(String objectIdStr) {
        this.objectIdStr = objectIdStr;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
