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
import tr.org.tspb.pojo.UserDetail;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyActions {

    private FmsForm myForm;

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
    private boolean sendForms;
    private boolean norecord;
    private FmsScriptRunner fmsScriptRunner;

    private TagActionsAction saveAction;
    private TagActionsAction deleteAction;
    private TagActionsAction checkAllAction;
    private TagActionsAction sendFormAction;
    private TagActionsAction norecordAction;

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

    public TagActionsAction getSaveAction() {
        return saveAction;
    }

    public TagActionsAction getDeleteAction() {
        return deleteAction;
    }

    public TagActionsAction getCheckAllAction() {
        return checkAllAction;
    }

    public boolean isSendForms() {
        return sendForms;
    }

    public TagActionsAction getSendFormAction() {
        return sendFormAction;
    }

    public boolean isNorecord() {
        return norecord;
    }

    public TagActionsAction getNorecordAction() {
        return norecordAction;
    }

    public static class Build {

        private MyActions myActions;
        private Map<String, Boolean> map = new HashMap<>();
        private static final Set<String> ACTIONS = new HashSet<>(Arrays.asList(ACTION_NEW,
                ACTION_DELETE,
                ACTION_SAVE,
                ACTION_SAVE_AS,
                ACTION_DOWNLOAD,
                EIMZA,
                ACTION_CHECK_ALL,
                EMAIL,
                EMAIL_TO_ALL,
                ACTION_SEND_FROMS,
                ACTION_NORECORD,
                "eimza1D",
                "eimza2D",
                "upload",
                "customDownload",
                "payment",
                "pdf",
                "ldap"
        ));

        private String viewerRole;
        private String db;
        private RoleMap roleMap;
        private Document searchObject;
        private Object attrActions;
        private FmsScriptRunner fmsScriptRunner;
        private UserDetail userDetail;

        public Build(String viewerRole, String db, RoleMap roleMap, Document searchObject,//
                Object attrActions, FmsScriptRunner fmsScriptRunner, UserDetail userDetail) {

            this.viewerRole = viewerRole;
            this.db = db;
            this.roleMap = roleMap;
            this.userDetail = userDetail;
            this.searchObject = searchObject;
            this.attrActions = attrActions;
            this.fmsScriptRunner = fmsScriptRunner;

            this.myActions = new MyActions();
            this.myActions.fmsScriptRunner = fmsScriptRunner;

            this.myActions.reset();
        }

        public Build init() {

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
            return this;
        }

        public Build initAsSchemaVersion100(MyMap crudObject) {

            for (String key : ((Document) attrActions).keySet()) {

                Document action = ((Document) attrActions).get(key, Document.class);

                boolean enable = false;

                ActionEnableResult enableResult = null;

                List<String> permit = action.getList("permit", String.class);
                List<String> block = action.getList("block", String.class);
                Boolean shoot = action.getBoolean("shoot");
                String func = action.getString("func");
                Document ref = action.get("ref", Document.class);

                if (permit == null || roleMap.isUserInRole(permit)) {
                    enable = true;
                }

                if (block != null && roleMap.isUserInRole(block)) {
                    enable = false;
                }

                if (func != null) {
                    func = func.replace(DIEZ, DOLAR);
                    Document commandResult = fmsScriptRunner.runCommand(db, func, searchObject, roleMap.keySet());
                    enable = commandResult.getBoolean(RETVAL);
                } else if (ref != null) {
                    enable = TagActionRef.calc(ref, searchObject, userDetail, fmsScriptRunner, crudObject);
                } else if (action.get(EVENT_ENABLE) != null) {
                    enableResult = checkControlResultSchemaVersion110(searchObject, action);
                    enable = enableResult.isEnable();
                } else if (shoot != null) {
                    enable = shoot;
                }

                map.put(key, enable);

                switch (key) {
                    case ACTION_SAVE:
                        myActions.saveAction = new TagActionsAction(enable, enableResult, action,
                                myActions.myForm.getMyProject().getRegistredFunctions(),
                                searchObject, userDetail, fmsScriptRunner);
                        break;
                    case ACTION_CHECK_ALL:
                        myActions.checkAllAction = new TagActionsAction(enable, enableResult, action,
                                myActions.myForm.getMyProject().getRegistredFunctions(),
                                searchObject, userDetail, fmsScriptRunner);
                        break;
                    case ACTION_SEND_FROMS:
                        myActions.sendFormAction = new TagActionsAction(enable, enableResult, action,
                                myActions.myForm.getMyProject().getRegistredFunctions(),
                                searchObject, userDetail, fmsScriptRunner);
                        break;
                    case ACTION_NORECORD:
                        myActions.norecordAction = new TagActionsAction(enable, enableResult, action,
                                myActions.myForm.getMyProject().getRegistredFunctions(),
                                searchObject, userDetail, fmsScriptRunner);
                        break;
                }
            }
            return this;
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
                    case ACTION_DELETE:
                        myActions.delete = value;
                        break;
                    case ACTION_SAVE:
                        myActions.save = value;
                        break;
                    case ACTION_SAVE_AS:
                        myActions.saveAs = value;
                        break;
                    case ACTION_DOWNLOAD:
                        myActions.download = value;
                        break;
                    case EIMZA:
                        myActions.esign = value;
                        break;
                    case ACTION_CHECK_ALL:
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
                    case ACTION_SEND_FROMS:
                        myActions.sendForms = value;
                        break;
                    case ACTION_NORECORD:
                        myActions.norecord = value;
                        break;
                }
            }
            return this;
        }

        public Build maskSaveWithCurrentCrudObject(MyMap crudObject) {

            if (!this.myActions.save) {
                this.myActions.save = this.myActions.create && crudObject.isNew();
            }

            if (this.myActions.save) {
                this.myActions.saveAction.enable();
            }

            if (this.myActions.list != null) {
                for (DynamicButton dynamicButton : this.myActions.list) {
                    dynamicButton.setCrudObject(crudObject);
                }
            }

            return this;
        }

        public Build maskMyForm(FmsForm myForm) {
            this.myActions.myForm = (FmsForm) myForm;
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

        //
        private ActionEnableResult checkControlResultSchemaVersion110(Document filter, Document action) {

            ActionEnableResult controlResult = new ActionEnableResult();

            Document enable = action.get(EVENT_ENABLE, Document.class);

            String func = enable.getString("func");
            List<Document> listOfChecks = enable.getList("list", Document.class);

            if (func != null && !roleMap.isUserInRole(this.myActions.myForm.getMyProject().getAdminAndViewerRole())) {
                func = func.replace(DIEZ, DOLAR);

                Document commandResult = fmsScriptRunner
                        .runCommand(enable.getString(FORM_DB), func, filter, roleMap.keySet());

                Object value = commandResult.get(RETVAL);
                controlResult.setEnable(Boolean.TRUE.equals(value));
            } else if (listOfChecks != null) {
                controlResult.setEnable(TagEventCheckListDoc.value(fmsScriptRunner, filter, userDetail, roleMap, listOfChecks));
            }

            if (controlResult.isEnable()) {
                Object gui = action.get(GUI);

                if (gui instanceof Code) {
                    Document commandResult = fmsScriptRunner.runCommand(this.myActions.myForm.getDb(), ((Code) gui).getCode(), filter, null);
                    gui = commandResult.get(RETVAL);
                }

                if (gui instanceof Document) {

                    Document guiDoc = (Document) gui;

                    controlResult.setStyle(guiDoc.getString(STYLE));
                    controlResult.setCaption(guiDoc.getString("caption"));
                    controlResult.setDynamicButtonName(guiDoc.getString("caption"));

                    Document objectAction = action.get(ACTION, Document.class);

                    String actionFunc = objectAction.getString("func");
                    Document actionRef = objectAction.get("ref", Document.class);

                    if (actionFunc != null) {
                        controlResult.setMyaction(actionFunc);
                    } else if (actionRef != null) {
                        controlResult.setMyActionType(actionRef.get(TYPE).toString());

                        if ("START_DIALOG".equals(controlResult.getMyActionType())) {
                            controlResult.setDialog(actionRef.get("dialog").toString());
                        }

                        if (actionRef.get("func") != null) {
                            controlResult.setMyaction(actionRef.get("func").toString());
                        }

                        if (actionRef.get(JAVA_FUNC) != null) {
                            controlResult.setJavaFunc(actionRef.getString(JAVA_FUNC));
                        }
                    }
                    controlResult.setSuccessMessage(((Document) gui).getString(SUCCESS_MESSAGE));
                    controlResult.setFailMessage(((Document) gui).getString(FAIL_MESSAGE));
                }
            }
            return controlResult;
        }

    }

}
