package tr.org.tspb.pivot.simple.ctrl;

import com.mongodb.client.model.Filters;
import tr.org.tspb.util.service.DlgCtrl;
import tr.org.tspb.common.services.LoginController;
import static tr.org.tspb.constants.ProjectConstants.*;
import tr.org.tspb.common.qualifier.ViewerController;
import tr.org.tspb.common.qualifier.MyQualifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.bson.Document;
import org.bson.types.ObjectId;
import tr.org.tspb.util.stereotype.MyController;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.common.services.AppScopeSrvCtrl;
import tr.org.tspb.pivot.simple.datamodel.ItemProvider;
import tr.org.tspb.pivot.simple.datamodel.PivotRecord;
import tr.org.tspb.service.FilterService;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.tools.MongoDbUtilIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyController
@MyQualifier(myEnum = ViewerController.simplePivotCtrl)
public class SimplePivotCtrl extends SimplePivotCtrlAdmin {

    @Inject
    @MyLoginQualifier
    private LoginController loginController;

    @Inject
    private DlgCtrl dialogController;

    @Inject
    @KeepOpenQualifier
    private MongoDbUtilIntr mongoDbUtil;

    @Inject
    private AppScopeSrvCtrl appScopeSrvCtrl;

    @Inject
    private FilterService filterService;

    private List<String> selectedFormMessages;
    private Map<String, Boolean> actionsMap;
    protected Set<String> roles;
    private PivotRecord pivotRecord;

    public void init(MyForm myForm) {

        String role = "lmalskdvm";

        if (loginController.isUserInRole("akuser")) {
            role = "AK";
        } else if (loginController.isUserInRole("pyuser")) {
            role = "PYŞ";
        } else {
            role = "YO";
        }

        this.itemProvider = new ItemProvider(mongoDbUtil, role, myForm);

        this.myForm = myForm;
        this.roles = loginController.getRolesAsSet();

        if (loginController.isUserInRole(myForm.getMyProject().getAdminAndViewerRole())) {
            refreshLazy();
        } else {
            this.pivotRecord = findOne();
        }

        this.actionsMap = new HashMap();

        if (loginController.isUserInRole(myForm.getMyProject().getAdminAndViewerRole())) {
            this.actionsMap.put(ACTION_SAVE, Boolean.FALSE);
        } else if (loginController.isUserInRole(myForm.getMyProject().getUserRole())) {
            this.actionsMap.put(ACTION_SAVE, Boolean.TRUE);
        }

        selectedFormMessages = createFormMsg(myForm);

    }

    public String save() {
        ObjectId loginID = loginController.getLoggedUserDetail().getDbo().getObjectId();

        if (pivotRecord.getObjectId() == null) {
            mongoDbUtil.upsertOne(myForm.getDb(), myForm.getTable(),
                    Filters.eq("member", loginID),
                    pivotRecord.toDocument());

            if (pivotRecord.ok()) {
                //FIXME messagebundle
                dialogController.showPopupInfo2("Bilgilendirme", "Yanıtlarınız Kaydedildi. Anket Tamamlandı");
            } else {
                dialogController.showPopupError("Yanıtlarınız Kaydedildi. Eksikler mevcut. Anket Tamamlanmadı");
            }

        } else if (loginID.equals(pivotRecord.getMember())) {
            mongoDbUtil.updateOne(myForm.getDb(), myForm.getTable(),
                    Filters.eq("_id", pivotRecord.getObjectId()),
                    pivotRecord.toDocument());
            if (pivotRecord.ok()) {
                dialogController.showPopupInfo2("Bilgilendirme", "Yanıtlarınız Kaydedildi. Anket Tamamlandı");
            } else {
                dialogController.showPopupError("Yanıtlarınız Kaydedildi. Eksikler mevcut. Anket Tamamlanmadı");
            }
        } else {
            dialogController.showPopupError("Hata Oluştu");
        }

        return null;
    }

    public LoginController getLoginMB() {
        return loginController;
    }

    public void setLoginMB(LoginController loginMB) {
        this.loginController = loginMB;
    }

    public MyForm getSelectedForm() {
        return myForm;
    }

    public void setSelectedForm(MyForm selectedForm) {
        this.myForm = selectedForm;
    }

    public PivotRecord getPivotRecord() {
        return pivotRecord;
    }

    public void setPivotRecord(PivotRecord pivotRecord) {
        this.pivotRecord = pivotRecord;
    }

    public PivotRecord findOne() {

        PivotRecord record = PivotRecord.toObject(itemProvider,
                mongoDbUtil.findOne(myForm.getDb(),
                        myForm.getTable(),
                        Filters.eq("member", loginController.getLoggedUserDetail().getDbo().getObjectId())));

        return record;
    }

    public Map getActionsMap() {
        return Collections.unmodifiableMap(this.actionsMap);
    }

    public List<String> getSelectedFormFieldKeySetAsList() {
        List<String> list = new ArrayList<>();
        for (String key : myForm.getFieldsKeySet()) {
            if (loginController.isUserInRole(myForm.getField(key).getAccesscontrol())) {
                list.add(key);
            }
        }
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String t, String t1) {
                String nameCurrent = myForm.getField(t).getOrder().toString();
                String nameNext = myForm.getField(t1).getOrder().toString();
                return nameNext.compareTo(nameCurrent);
            }
        });
        return list;
    }

    public List<String> getSelectedFormMessages() {
        return selectedFormMessages;
    }

    /**
     * @return the itemProvider
     */
    public ItemProvider getItemProvider() {
        return itemProvider;
    }

    public List<String> createFormMsg(MyForm myForm) {
        List<String> msgs = new ArrayList<>();

        if (myForm.getConstantNote() != null && !myForm.getConstantNote().isEmpty()) {
            msgs.add(myForm.getConstantNote());
        }
        if (myForm.getUserConstantNoteList() != null) {
            for (String message : myForm.getUserConstantNoteList()) {
                msgs.add(message);
            }
        }
        //TODO why myForm.getMyActions()=null
//        if (myForm.getMyActions().isSave() && myForm.getReadOnlyNote() != null) {
//            msgs.add(myForm.getReadOnlyNote());
//        }
        if (myForm.getFuncNote() != null) {
            Document commandResult = mongoDbUtil.runCommand(myForm.getDb(), myForm.getFuncNote(), filterService.getTableFilterCurrent());
            String commandResultValue = commandResult.getString(RETVAL);
            if (commandResultValue != null) {
                msgs.add(commandResultValue);
            }
        }
        return msgs;
    }

}
