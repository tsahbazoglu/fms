package tr.org.tspb.uys.freedesign;

import static tr.org.tspb.constants.ProjectConstants.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.primefaces.model.file.UploadedFile;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.pojo.RoleMap;
import tr.org.tspb.pojo.UserDetail;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyRecord {

    private static Map<String, String> memberCacheNameById;

    private ObjectId mongoId;
    private String memberName;

    private UploadedFile uploadedFile;

    private List<MyLicense> listOfLicense;

    protected List<Map<String, String>> listFileData;

    private Map<String, Object> crudMap = new HashMap<>();
    private Map<String, MyField> crudFieldMap;

    private Boolean renderedInOut;

    private MyRecord(RoleMap roleMap, UserDetail userDetail, FmsForm myForm, Map<String, String> cacheDbMamberMap) {
        crudFieldMap = new HashMap<>(myForm.getFields());
        for (String key : crudFieldMap.keySet()) {
            if (myForm.getField(key).getItemsAsMyItems() != null) {
                myForm.getField(key).createSelectItems(null, null, roleMap, userDetail, false);
            }
        }
        crudMap.put(FORM_NAME, myForm.getField(FORM_NAME).getDefaultValue().toString());
        crudMap.put(DESCRIPTION, myForm.getField(DESCRIPTION).getDefaultValue().toString());
    }

    public MyRecord(RoleMap roleMap, UserDetail userDetail, Map o, FmsForm myForm, Map<String, String> cacheDbMamberMap) {
        this(roleMap, userDetail, myForm, cacheDbMamberMap);

        crudMap = o;

        for (String key : myForm.getFieldsKeySet()) {
            if (myForm.getField(key).getDefaultValue() != null) {
                crudMap.put(key, myForm.getField(key).getDefaultValue());
            }
        }
        this.mongoId = (ObjectId) o.get(MONGO_ID);

        this.memberName = cacheDbMamberMap.get(o.get(MEMBER).toString());
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Document getAsDBObject() {
        Document o = new Document(crudMap);
        return o;
    }

    public ObjectId getMongoId() {
        return mongoId;
    }

    public String getMongoIdAsString() {
        return mongoId.toString();
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public List<MyLicense> getListOfLicense() {
        return Collections.unmodifiableList(listOfLicense);
    }

    public void setListOfLicense(List<MyLicense> listOfLicense) {
        this.listOfLicense = listOfLicense;
    }

    public Map<String, Object> getCrudMap() {
        return crudMap;
    }

    public void setCrudMap(Map<String, Object> crudMap) {
        this.crudMap = crudMap;
    }

    public Map<String, MyField> getCrudFieldMap() {
        return Collections.unmodifiableMap(crudFieldMap);
    }

    public void setCrudFieldMap(Map<String, MyField> crudFieldMap) {
        this.crudFieldMap = crudFieldMap;
    }

    public Boolean getRenderedInOut() {
        return renderedInOut;
    }

    public void setRenderedInOut(Boolean renderedInOut) {
        this.renderedInOut = renderedInOut;
    }

}
