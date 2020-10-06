package tr.org.tspb.dao;

import tr.org.tspb.pojo.RoleMap;
import static tr.org.tspb.constants.ProjectConstants.*;
import tr.org.tspb.constants.ProjectConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import org.bson.types.Code;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyActions {

    private boolean create;
    private boolean delete;
    private boolean save;
    private boolean saveAs;
    private boolean download;
    private boolean esign;
    private boolean checkAll;
    private boolean email;
    private boolean eimza1D;
    private boolean eimza2D;
    private boolean emailToAll;
    private boolean upload;
    private boolean customDownload;
    private boolean payment;
    private boolean pdf;
    private boolean ldap;
    private FmsScriptRunner fmsScriptRunner;

    private List<DynamicButton> list;

    private MyActions() {
    }

    public boolean isPayment() {
        return payment;
    }

    public boolean isPdf() {
        return pdf;
    }

    public void merge(MyActions myActions) {
        if (myActions == null) {
            return;
        }

        this.create = this.create || myActions.create;

        this.delete = myActions.delete;
        this.save = myActions.save;
        this.saveAs = myActions.saveAs;
        this.download = myActions.download;
        this.esign = myActions.esign;
        this.checkAll = myActions.checkAll;
        this.email = myActions.email;
        this.eimza1D = myActions.eimza1D;
        this.eimza2D = myActions.eimza2D;
        this.emailToAll = myActions.emailToAll;
        this.upload = myActions.upload;
        this.customDownload = myActions.customDownload;
    }

    public boolean isCreate() {
        return create;
    }

    public boolean isCustomDownload() {
        return customDownload;
    }

    public boolean isDelete() {
        return delete;
    }

    public boolean isEimza1D() {
        return eimza1D;
    }

    public boolean isSave() {
        return save;
    }

    public boolean isDownload() {
        return download;
    }

    public boolean isEsign() {
        return esign;
    }

    public boolean isCheckAll() {
        return checkAll;
    }

    public boolean isEmail() {
        return email;
    }

    public boolean isEimza2D() {
        return eimza2D;
    }

    public boolean isEmailToAll() {
        return emailToAll;
    }

    public boolean isSaveAs() {
        return saveAs;
    }

    public boolean isUpload() {
        return upload;
    }

    public void reset() {
        //this.create = false;
        this.delete = false;
        this.save = false;
        this.saveAs = false;
        this.download = false;
        this.esign = false;
        this.checkAll = false;
        this.email = false;
        this.eimza1D = false;
        this.eimza2D = false;
        this.emailToAll = false;
        this.upload = false;
        this.customDownload = false;
    }

    public boolean isLdap() {
        return ldap;
    }

    public List<DynamicButton> getList() {
        return list;
    }

    public static class Build {

        private MyActions myActions;
        private Map<String, Boolean> map = new HashMap<>();
        private static final Set<String> ACTIONS = new HashSet<>(Arrays.asList(
                ACTION_NEW,
                "delete",
                ACTION_SAVE,
                SAVE_AS,
                ACTION_DOWNLOAD,
                EIMZA,
                "checkAll",
                EMAIL,
                "eimza1D",
                "eimza2D",
                EMAIL_TO_ALL,
                "upload",
                "customDownload",
                "payment",
                "pdf",
                "ldap"
        ));

        public Build(String viewerRole, String db, RoleMap roleMap, Document searchObject,//
                Object attrActions, FmsScriptRunner fmsScriptRunner) {

            this.myActions = new MyActions();
            this.myActions.fmsScriptRunner = fmsScriptRunner;

            this.myActions.reset();

            Document actions = new Document();

            if (attrActions instanceof List) {
                for (String key : (Iterable<? extends String>) attrActions) {
                    actions.put(key, true);
                }
            } else if (attrActions instanceof Document) {
                actions.putAll((Document) attrActions);
            }

            for (String key : actions.keySet()) {

                if (roleMap.isUserInRole(ProjectConstants.ARCHITECT_ROLE)) {
                    map.put(key, Boolean.TRUE);
                    continue;
                }

                Object attrActionValue = ((Document) attrActions).get(key);

                if (roleMap.isUserInRole(viewerRole)) {
                    map.put(key, Boolean.FALSE);
                } else if (attrActionValue instanceof Boolean) {
                    map.put(key, Boolean.TRUE.equals(attrActionValue));
                } else if (attrActionValue instanceof Code) {
                    String code = ((Code) attrActionValue).getCode();
                    code = code.replace(DIEZ, DOLAR);

                    Document commandResult = fmsScriptRunner.runCommand(db,
                            code, searchObject, roleMap.keySet());

                    Boolean result = commandResult.getBoolean(RETVAL);
                    map.put(key, result);
                } else if (attrActionValue instanceof Document) {
                    map.put(key, Boolean.TRUE.equals(((Document) attrActionValue).get(ENABLE)));
                    String onUserRole = ((Document) attrActionValue).getString(ON_USER_ROLE);
                    if (onUserRole == null) {
                        onUserRole = ((Document) attrActionValue).getString("onuserrole");
                    }
                    if (onUserRole == null) {
                        map.put(key, fmsScriptRunner.runActionAsDbTableFilterResult((Document) attrActionValue, roleMap, searchObject));
                    } else {
                        map.put(key, roleMap.isUserInRole(onUserRole));
                    }
                } else if (attrActionValue instanceof List) {

                    myActions.list = new ArrayList<>();

                    List<Document> listOfDoc = (List) attrActionValue;

                    for (Document document : listOfDoc) {
                        myActions.list.add(new DynamicButton(document, this.myActions.fmsScriptRunner));
                    }

                }
            }
        }

        public Build base() {

            for (String key : map.keySet()) {
                if (!ACTIONS.contains(key)) {
                    throw new RuntimeException(String.format("'%s' action is not supported", key));
                }
                Boolean value = map.get(key);

                switch (key) {
                    case ACTION_NEW:
                        myActions.create = value;
                        break;
                    case "delete":
                        myActions.delete = value;
                        break;
                    case ACTION_SAVE:
                        myActions.save = value;
                        break;
                    case SAVE_AS:
                        myActions.saveAs = value;
                        break;
                    case ACTION_DOWNLOAD:
                        myActions.download = value;
                        break;
                    case EIMZA:
                        myActions.esign = value;
                        break;
                    case "checkAll":
                        myActions.checkAll = value;
                        break;
                    case EMAIL:
                        myActions.email = value;
                        break;
                    case "eimza1D":
                        myActions.eimza1D = value;
                        break;
                    case "eimza2D":
                        myActions.eimza2D = value;
                        break;
                    case EMAIL_TO_ALL:
                        myActions.emailToAll = value;
                        break;
                    case "upload":
                        myActions.upload = value;
                        break;
                    case "customDownload":
                        myActions.customDownload = value;
                        break;
                    case "payment":
                        myActions.payment = value;
                        break;
                    case "pdf":
                        myActions.pdf = value;
                        break;
                    case "ldap":
                        myActions.ldap = value;
                        break;
                }
            }
            return this;
        }

        public Build maskSaveWithCurrentCrudObject(MyMap crudObject) {

            if (!this.myActions.save) {
                this.myActions.save = this.myActions.create && crudObject.isNew();
            }

            if (this.myActions.list != null) {
                for (DynamicButton dynamicButton : this.myActions.list) {
                    dynamicButton.setCrudObject(crudObject);
                }
            }

            return this;
        }

        public Build maskDeleteWithSave() {
            if (this.myActions.delete) {
                this.myActions.delete = this.myActions.save;
            }
            return this;
        }

        public MyActions build() {
            return myActions;
        }
    }

}
