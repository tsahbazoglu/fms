package tr.org.tspb.dao;

import tr.org.tspb.pojo.RoleMap;
import static tr.org.tspb.constants.ProjectConstants.*;
import tr.org.tspb.wf.MyRule;
import htmlflow.StaticHtml;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import tr.org.tspb.datamodel.expected.FmsRunMongoCmd;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;
import tr.org.tspb.pojo.UserDetail;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyForm implements MyFormXs {

    public static Map<String, String> cacheIonSettingIdCode;
    public static final String SCHEMA_VERSION_100 = "1.0.0";
    public static final String SCHEMA_VERSION_110 = "1.1.0";
    public static final String SCHEMA_VERSION = "schemaVersion";

    public static final String ION_SETTING_ACTIVITY_STATUS = "ion_setting_activity_status";

    private static final String ION_FORM_1160_NOTIFY_TYPE_001 = "ion_form_1160_notify_type_001";
    private static final String ION_FORM_1160_NOTIFY_TYPE_000 = "ion_form_1160_notify_type_000";
    private static final String ION_SETTING_NOTIFY_TYPE_001 = "ion_setting_notify_type_001";
    private static final String ION_SETTING_NOTIFY_TYPE_000 = "ion_setting_notify_type_000";

    private String loginFkField;
    private String posAmountField;
    private String posOrderIdField;
    private String importTextFormat;
    private String name;
    private final List<MyField> autosetFields = new ArrayList<>();
    private final List<String> versionFields = new ArrayList<>();
    private final Document requiredFields = new Document();
    private String accesscontrol;
    private String esignEmailBccRecipients;
    private String upperNode;
    private String projectKey;
    private String key;
    private Document searchForm;
    private String table;
    private String snapshotCollection;
    private String userNote;//popup note
    private String userConstantNote;//constantly stay on the page
    private String readOnlyNote;//constantly stay on the page
    private String db;
    private String calculateCollection;
    private String form;
    private String group;
    private String shortName;
    private String pageName;
    private String versionCollection;
    private String formType;
    private String anotherEimzaColletionKey;
    private String controlCollection;
    private Document defaultSortField;
    private Document subGroups;
    private Document dbo;
    private Document constraintItems;
    private Document eventPreSave;
    private Document eventPostSave;
    private Document eventPostDelete;
    private Document eventPreDelete;
    private Document eventFormSelection;
    private Document accessControlLevelTwo;
    private Document myNamedQueries;
    private Document events;
    private Document excelFormat;
    private List<String> zetDimension;
    private HashMap<String, Object> defaultCurrentQuery;
    private HashMap<String, Object> defaultHistoryQuery;
    private List uniqueIndexList;
    private Code funcNote;//constantly stay on the page
    private Code esignEmailToRecipients;
    private Number menuOrder;
    private Number columnCount;
    private Number dimension;
    private Number historyPosition;
    private int handsonRowHeaderWidth;
    private int handsonColWidths;
    private Boolean historyRendered;
    private Boolean currentRendered;
    private Boolean tranzient;
    private boolean showHistory;
    private boolean hasAttachedFiles;
    private boolean versionEnable;
    private boolean esignable;
    private Set<String> fieldsKeySet;
    private List<String> userConstantNoteList = new ArrayList<>();
    private List<String> ajaxFields = new ArrayList<>();
    private List<MyRule> workflowSteps = new ArrayList<>();
    private String workflowStartStep = "s0";
    private String schemaVersion;
    private Object actions;
    private Object calculateQuery;
    private CrudRelatedKeyRender crkr;
    private MyProject myProject;
    private MyActions myActions;
    private MyNotifies myNotifies;
    private MyWorkflowRelation myWorkflowRelation;
    private boolean hasWorkflowRelation;
    private FmsScriptRunner fmsScriptRunner;
    private FmsRunMongoCmd fmsRunMongoCmd;
    private List<ChildFilter> childs;
    private String restApi;
    private boolean jsonViewer;

    /*
     some fields include on fly calculation. 
     As an exmaple we can present "rendered" property of balanceAbstract.consolidated field relied on on fly search filter.
     By introducing formCache we prevent superfluous calculations.
     */
    private Map<String, MyField> fields;
    private List< MyField> fieldsAsList;
    private Map<String, MyField> fieldsRow;
    private MyMerge uploadMerge;

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public boolean isJsonViewer() {
        return jsonViewer;
    }

    public boolean isWorkFlowActive() {
        return workflowSteps != null && !workflowSteps.isEmpty();
    }

    private List<String> fieldsRowKeys;

    public MyForm() {
    }

    public MyNotifies getMyNotifies() {
        return myNotifies;
    }

    public String getPosOrderIdField() {
        return posOrderIdField;
    }

    public String getAccesscontrol() {
        return accesscontrol;
    }

    public String getPosAmountField() {
        return posAmountField;
    }

    public List getUniqueIndexList() {
        return uniqueIndexList;
    }

    public MyMerge getUploadMerge() {
        return uploadMerge;
    }

    public String getUpperNode() {
        return upperNode;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public String getEsignEmailBccRecipients() {
        return esignEmailBccRecipients;
    }

    public Code getEsignEmailToRecipients() {
        return esignEmailToRecipients;
    }

    public Boolean getCurrentRendered() {
        return currentRendered;
    }

    public Document getEventFormSelection() {
        return eventFormSelection;
    }

    public Document getEventPreSave() {
        return eventPreSave;
    }

    public Map<String, MyField> getFieldsRow() {
        return Collections.unmodifiableMap(fieldsRow);
    }

    public List<String> getFieldsRowKeys() {
        return Collections.unmodifiableList(fieldsRowKeys);
    }

    public List<MyRule> getWorkflowSteps() {
        return Collections.unmodifiableList(workflowSteps);
    }

    public boolean isShowHistory() {
        return showHistory;
    }

    public void setShowHistory(boolean showHistory) {
        this.showHistory = showHistory;
    }

    public Document getExcelFormat() {
        return excelFormat;
    }

    public Document getFindAndSaveFilter() {
        return searchForm;
    }

    public Object getCalculateQuery() {
        return this.calculateQuery;
    }

    public Number getColumnCount() {
        return columnCount;
    }

    public Document getEventPreDelete() {
        return eventPreDelete;
    }

    public int getHandsonColWidths() {
        return handsonColWidths;
    }

    public int getHandsonRowHeaderWidth() {
        return handsonRowHeaderWidth;
    }

    public Document getSubGroups() {
        return subGroups;
    }

    public String getGroup() {
        return group;
    }

    public String getReadOnlyNote() {
        return readOnlyNote;
    }

    public Code getFuncNote() {
        return funcNote;
    }

    public Document getDbo() {
        return dbo;
    }

    public String getSnapshotCollection() {
        return snapshotCollection;
    }

    public String getVersionCollection() {
        return versionCollection;
    }

    public List<String> getVersionFields() {
        return Collections.unmodifiableList(versionFields);
    }

    public String getShortName() {
        return shortName;
    }

    public Document getAccessControlLevelTwo() {
        return accessControlLevelTwo;
    }

    public List<MyField> getAutosetFields() {
        return Collections.unmodifiableList(autosetFields);
    }

    public Document getRequiredFields() {
        return requiredFields;
    }

    public Map getDefaultSortField() {
        return defaultSortField;
    }

    public String getForm() {
        return form;
    }

    public Boolean getTranzient() {
        return tranzient;
    }

    public Document getEventPostDelete() {
        return eventPostDelete;
    }

    public Document getEventPostSave() {
        return eventPostSave;
    }

    public Set<String> getFieldsKeySet() {
        return Collections.unmodifiableSet(fieldsKeySet);
    }

    public Document getConstraintItems() {
        return constraintItems;
    }

    public Object getActions() {
        return actions;
    }

    public MyActions getMyActions() {
        return myActions;
    }

    public String getDb() {
        return db;
    }

    public Boolean getHistoryRendered() {
        return historyRendered;
    }

    public String getUserNote() {
        return userNote;
    }

    public String getName() {
        return name;
    }

    public Document getEvents() {
        return events;
    }

    public String getTable() {
        return table;
    }

    public String getKey() {
        return key;
    }

    public Map<String, MyField> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    public MyField getField(String key) {
        return fields.get(key);
    }

    public MyField getFieldRow(String key) {
        return fieldsRow.get(key);
    }

    public Document getMyNamedQueries() {
        return myNamedQueries;
    }

    public String getConstantNote() {
        return userConstantNote;
    }

    @Deprecated
    /**
     * getCalculateQuery should be used instead of this
     */
    public String getCalculateCollection() {
        return calculateCollection;
    }

    public List<String> getUserConstantNoteList() {
        return Collections.unmodifiableList(userConstantNoteList);
    }

    public String getAnotherEimzaColletionKey() {
        return anotherEimzaColletionKey;
    }

    public boolean isHasAttachedFiles() {
        return hasAttachedFiles;
    }

    public String getImportTextFormat() {
        return importTextFormat;
    }

    public Number getMenuOrder() {
        return menuOrder;
    }

    public void runAjaxBulk(Map<String, MyField> componentMap, MyMap crudObject, RoleMap roleMap, UserDetail userDetail) {

        crudObject.initUnSet();

        for (String ajaxFieldKey : ajaxFields) {
            MyField myField = getField(ajaxFieldKey);
            switch (myField.getAjaxAction()) {
                case "ion_form_1350_override_required_check":
                    runAjaxIonForm1350OverrideRequiredCheck(componentMap, this, crudObject);
                    break;
                case "render":
                    runAjaxRender(myField, componentMap, this, crudObject, roleMap, userDetail, null);
                    break;
                default:
                    break;
            }
        }
    }

    public void runAjax__uys_member_generate_ldapUID(Map crudObject) {
        if (crudObject.get(MONGO_ID) == null) {

            String memberType = (String) crudObject.get(MEMBER_TYPE);
            if (memberType instanceof String) {
                Map<String, Document> preAlphaMap = new HashMap<>();
                String LDAP_UID_FORMAT = "format";
                String SEARCH = "jnakvjnsakdjvnkasjdvaswe";

                preAlphaMap.put("AK", new Document("ldapuidPre", "A")//
                        .append(LDAP_UID_FORMAT, "A%05d")
                        .append(SEARCH, new Document(MEMBER_TYPE, "AK").append(MONGO_LDAP_UID, new Document(DOLAR_OPTIONS, "i").append(DOLAR_REGEX, "A0"))));
                preAlphaMap.put("B", new Document("ldapuidPre", "B")//
                        .append(LDAP_UID_FORMAT, "B%05d")
                        .append(SEARCH, new Document(MEMBER_TYPE, new Document(DOLAR_IN, Arrays.asList("B", "MB", "KTB", "YB", "KB")))
                                .append(MONGO_LDAP_UID, new Document(DOLAR_OPTIONS, "i").append(DOLAR_REGEX, "B0"))));
                preAlphaMap.put("MB", new Document("ldapuidPre", "B")//
                        .append(LDAP_UID_FORMAT, "B%05d")
                        .append(SEARCH, new Document(MEMBER_TYPE, new Document(DOLAR_IN, Arrays.asList("B", "MB", "KTB", "YB", "KB")))
                                .append(MONGO_LDAP_UID, new Document(DOLAR_OPTIONS, "i").append(DOLAR_REGEX, "B0"))));
                preAlphaMap.put("KTB", new Document("ldapuidPre", "B")//
                        .append(LDAP_UID_FORMAT, "B%05d")
                        .append(SEARCH, new Document(MEMBER_TYPE, new Document(DOLAR_IN, Arrays.asList("B", "MB", "KTB", "YB", "KB")))
                                .append(MONGO_LDAP_UID, new Document(DOLAR_OPTIONS, "i").append(DOLAR_REGEX, "B0"))));
                preAlphaMap.put("YB", new Document("ldapuidPre", "B")//
                        .append(LDAP_UID_FORMAT, "B%05d")
                        .append(SEARCH, new Document(MEMBER_TYPE, new Document(DOLAR_IN, Arrays.asList("B", "MB", "KTB", "YB", "KB")))
                                .append(MONGO_LDAP_UID, new Document(DOLAR_OPTIONS, "i").append(DOLAR_REGEX, "B0"))));
                preAlphaMap.put("KB", new Document("ldapuidPre", "B")//
                        .append(LDAP_UID_FORMAT, "B%05d")
                        .append(SEARCH, new Document(MEMBER_TYPE, new Document(DOLAR_IN, Arrays.asList("B", "MB", "KTB", "YB", "KB")))
                                .append(MONGO_LDAP_UID, new Document(DOLAR_OPTIONS, "i").append(DOLAR_REGEX, "B0"))));
                preAlphaMap.put("PYŞ", new Document("ldapuidPre", "PY")//
                        .append(LDAP_UID_FORMAT, "PY%04d")
                        .append(SEARCH, new Document(MEMBER_TYPE, "PYŞ").append(MONGO_LDAP_UID, new Document(DOLAR_OPTIONS, "i").append(DOLAR_REGEX, "PY0"))));
                preAlphaMap.put("MKYO", new Document("ldapuidPre", "MK")//
                        .append(LDAP_UID_FORMAT, "MK%04d")
                        .append(SEARCH, new Document(MEMBER_TYPE, "MKYO").append(MONGO_LDAP_UID, new Document(DOLAR_OPTIONS, "i").append(DOLAR_REGEX, "MK0"))));
                preAlphaMap.put("GYO", new Document("ldapuidPre", "GY")//
                        .append(LDAP_UID_FORMAT, "GY%04d")
                        .append(SEARCH, new Document(MEMBER_TYPE, "GYO").append(MONGO_LDAP_UID, new Document(DOLAR_OPTIONS, "i").append(DOLAR_REGEX, "GY0"))));
                preAlphaMap.put("GSYO", new Document("ldapuidPre", "GS")//
                        .append(LDAP_UID_FORMAT, "GS%04d")
                        .append(SEARCH, new Document(MEMBER_TYPE, "GSYO").append(MONGO_LDAP_UID, new Document(DOLAR_OPTIONS, "i").append(DOLAR_REGEX, "GS0"))));

                Document matcher = preAlphaMap.get(memberType);

                List<Document> x = fmsScriptRunner
                        .uysdbCommonFindSort(((Document) preAlphaMap.get(memberType).get(SEARCH)), new Document(MONGO_LDAP_UID, -1));

                for (Document doc : x) {
                    String ldapUID = doc.get(MONGO_LDAP_UID).toString();
                    ldapUID = String.format(matcher.get("format").toString(),
                            Integer.valueOf(ldapUID.substring(matcher.get("ldapuidPre").toString().length())) + 1);

                    if (fmsScriptRunner.uysdbCommonFindOneLdapUID(ldapUID) == null) {
                        crudObject.put(MONGO_LDAP_UID, ldapUID);
                    } else {
                        crudObject.put(MONGO_LDAP_UID, "ldapUID=".concat(ldapUID).concat(" olarak belirlendi ama böyle bir kurum zaten var."));
                    }
                }

            }

        }
    }

    public void runAjaxIonForm1170NotifyTypeToggle(Map crudObject, RoleMap loginController) {
        fieldsAsList.clear();
        throw new UnsupportedOperationException();
    }

    public void runAjaxIonForm1170ActivityStatusToggle(Map crudObject, RoleMap loginController) {
        fieldsAsList.clear();
        throw new UnsupportedOperationException();
    }

    public void runAjaxIonForm1160NotifyTypeToggle(Map crudObject, RoleMap loginController) {
        fieldsAsList.clear();
        throw new UnsupportedOperationException();
    }

    public void runAjaxIonForm1160ActivityStatusToggle(Map crudObject, RoleMap loginController) {
        fieldsAsList.clear();
        throw new UnsupportedOperationException();
    }

    public void runAjaxIonForm1350OverrideRequiredCheck(Map<String, MyField> componentMap,
            final MyForm selectedForm, Map crudObject) {
        if ("ion_form_1350".equals(selectedForm.getKey())) {

            String checkFieldKey = "pay_sahipligi_varmi";
            String effefctedFieldKey = "pay_sahipligi_orani";

            Object checkFieldValue = crudObject.get(checkFieldKey);

            if (HAYIR.equals(checkFieldValue)) {
                crudObject.remove(effefctedFieldKey);
            }

            MyField mf = componentMap.get(effefctedFieldKey);

            if (mf != null) {
                mf.setRendered("EVET".equals(checkFieldValue));
            }

            Collections.sort(fieldsAsList, new MyFieldComparator());
        }
    }

    public void runAjaxRender(MyField myField,
            Map<String, MyField> componentMap,
            final MyForm selectedForm,
            MyMap crudObject,
            RoleMap roleMap,
            UserDetail userDetail,
            Document filter) {

        myField.getAjaxEffectedKeys().forEach((key) -> {
            crudObject.removeUnSetKey(key);
        });

        Document crudObjAsDoc = new Document(crudObject);
        crudObjAsDoc.remove(INODE);

        Document commandResult = fmsScriptRunner.runCommand(selectedForm.getDb(),
                myField.getAjaxShowHide().getCode(), crudObject.get(myField.getKey()), crudObjAsDoc, roleMap);

        Object result = commandResult.get(RETVAL);

        if (result instanceof Boolean) {
            if (Boolean.TRUE.equals(result)) {
                for (String key : myField.getAjaxEffectedKeys()) {
                    componentMap.get(key).setRendered(true);
                    componentMap.get(key).createSelectItems(filter, crudObject, roleMap, userDetail, true);
                }
            } else {
                for (String ajaxEffectedKey : myField.getAjaxEffectedKeys()) {
                    crudObject.remove(ajaxEffectedKey);
                    MyField myField1 = componentMap.get(ajaxEffectedKey);
                    if (myField1 != null) {
                        myField1.setRendered(false);
                    }
                }
            }
        } else if (result instanceof Document) {
            Document resultDBO = ((Document) result);
            for (String key : resultDBO.keySet()) {
                MyField myField1 = componentMap.get(key);
                if (myField1 != null) {
                    if (Boolean.TRUE.equals(resultDBO.get(key))) {
                        myField1.setRendered(true);
                        myField1.createSelectItems(filter, crudObject, roleMap, userDetail, true);
                    } else {
                        crudObject.remove(key);
                        crudObject.addUnSetKey(key);
                        myField1.setRendered(false);
                    }
                }
            }
        }
    }

    public void runAjaxIonForm1340KasStatusSelection(Map<String, MyField> componentMap, final MyForm selectedForm, Map crudObject,
            List<String> selectedFormAjaxFieldKeySetAsList) {
        if ("ion_form_1340".equals(selectedForm.getKey())) {
            Object value = crudObject.get("kas_status");

            if ("EVET".equals(value)) {
                if (componentMap.containsKey("karli_kas_musteri_sayisi")) {
                    componentMap.get("karli_kas_musteri_sayisi").setRendered(true);
                }
                if (componentMap.containsKey("kas_musteri_sayisi")) {
                    componentMap.get("kas_musteri_sayisi").setRendered(true);
                }
                if (componentMap.containsKey("kar_oran")) {
                    componentMap.get("kar_oran").setRendered(true);
                }
                if (componentMap.containsKey("zarar_oran")) {
                    componentMap.get("zarar_oran").setRendered(true);
                }
            } else if (HAYIR.equals(value)) {
                crudObject.remove("karli_kas_musteri_sayisi");
                crudObject.remove("kas_musteri_sayisi");
                crudObject.remove("kar_oran");
                crudObject.remove("zarar_oran");
                if (componentMap.containsKey("karli_kas_musteri_sayisi")) {
                    componentMap.get("karli_kas_musteri_sayisi").setRendered(false);
                }
                if (componentMap.containsKey("kas_musteri_sayisi")) {
                    componentMap.get("kas_musteri_sayisi").setRendered(false);
                }
                if (componentMap.containsKey("kar_oran")) {
                    componentMap.get("kar_oran").setRendered(false);
                }
                if (componentMap.containsKey("zarar_oran")) {
                    componentMap.get("zarar_oran").setRendered(false);
                }
            }
            Collections.sort(selectedFormAjaxFieldKeySetAsList, new Comparator<String>() {
                @Override
                public int compare(String t, String t1) {
                    String nameCurrent = selectedForm.getField(t).getOrder().toString();
                    String nameNext = selectedForm.getField(t1).getOrder().toString();
                    return nameNext.compareTo(nameCurrent);
                }
            });
        }
    }

    public void initActions(MyActions myActions) {
        this.myActions = myActions;
        this.esignable = this.myActions.isEsign();
    }

    public void arrangeActions(RoleMap roleMap, Document searchObject, MyMap crudObject) {
        this.myActions = new MyActions.Build(this.getMyProject().getViewerRole(), this.getDb(), roleMap, searchObject, actions, fmsScriptRunner)
                .init()
                .base()
                .maskSaveWithCurrentCrudObject(crudObject)
                .maskDeleteWithSave()
                .build();
        this.esignable = this.myActions.isEsign();
    }

    @Override
    public HashMap<String, Object> getDefaultCurrentQuery() {
        return defaultCurrentQuery;
    }

    @Override
    public HashMap<String, Object> getDefaultHistoryQuery() {
        return defaultHistoryQuery;
    }

    @Override
    public MyProject getMyProject() {
        return myProject;
    }

    @Override
    public String getFormType() {
        return formType;
    }

    @Override
    public Number getDimension() {
        return dimension;
    }

    @Override
    public String getLoginFkField() {
        return loginFkField;
    }

    @Override
    public Number getHistoryPosition() {
        return historyPosition;
    }

    @Override
    public String printToConfigAnalyze(String fieldKey) {

        String html = StaticHtml
                .view()
                .html()
                .head()
                .__()
                .body()
                .div().attrClass("container")
                .br().__()
                .br().__()
                .u().text("config:").__()
                .br().__()
                .br().__()
                .span().text("cfgdb=db.getSisterDB('configdb');").__()
                .br().__()
                .span().text(String.format("cfgdb.%s.findOne({key:'%s'});", myProject.getConfigTable(), key)).__()
                .br().__()
                .span().text(String.format("cfgdb.%s.findOne({key:'%s'}).db;", myProject.getConfigTable(), key)).__()
                .br().__()
                .span().text(String.format("cfgdb.%s.findOne({key:'%s'}).collection;", myProject.getConfigTable(), key)).__()
                .br().__()
                .span().text(String.format("cfgdb.%s.findOne({key:'%s'},{fields:false});", myProject.getConfigTable(), key)).__()
                .br().__()
                .span().text(String.format("cfgdb.%s.findOne({key:'%s'},{'fields.%s':true});", myProject.getConfigTable(), key, fieldKey)).__()
                .br().__()
                .br().__()
                .u().text("update:").__()
                .br().__()
                .br().__()
                .span().text(String.format("cfgdb.%s.update({key:'%s'},{$set:{'fields.%s.xxxxx':'xxxxx'}});", myProject.getConfigTable(), key, fieldKey)).__()
                .br().__()
                .br().__()
                .u().text("sss:").__()
                .br().__()
                .br().__()
                .span().text(String.format("cfgdb.%s.update({key:'%s'},{$set:{'loginFkField':'kurulusId'}});", myProject.getConfigTable(), key)).__()
                .br().__()
                .span().text(String.format("cfgdb.%s.update({key:'%s'},{$set:{'loginFkField':'_id'}});", myProject.getConfigTable(), key)).__()
                .br().__()
                .__()
                .__() //body
                .__() //html
                .render();

        return html;

    }
    private static final String BR = "<br/>";

    public String getControlCollection() {
        return controlCollection;
    }

    public void addField(MyField field) {
        this.fields.put(field.getKey(), field);
    }

    public boolean isHasWorkflowRelation() {
        return hasWorkflowRelation;
    }

    public List<String> getZetDimension() {
        return zetDimension;
    }

    public List<ChildFilter> getChilds() {
        return childs;
    }

    public List< MyField> getFieldsAsList() {
        return fieldsAsList;
    }

    public boolean isVersionEnable() {
        return versionEnable;
    }

    public boolean isEsignable() {
        return esignable;
    }

    public String getWorkflowStartStep() {
        return workflowStartStep;
    }

    public String getRestApi() {
        return restApi;
    }

    public String getPageName() {
        return pageName;
    }

    private static class OrderedKey {

        private final int orderno;
        private final String key;

        OrderedKey(int orderno, String key) {
            this.orderno = orderno;
            this.key = key;
        }

    }

    abstract static class CrudRelatedKeyRender<T extends Map> {

        boolean getRender(T t, String key) {
            return false;
        }
    }

    public RoleMap roleMap;
    public UserDetail userDetail;
    public Map searchObject;

    public static class Builder {

        private final MyProject myProject;
        private final MyForm myForm;
        private final Document dbObjectForm;
        private final Map searchObject;
        private final boolean admin;

        public Builder(MyProject myProject, Document dbObjectForm, Map searchObject, RoleMap roleMap, UserDetail userDetail,//
                FmsScriptRunner fmsScriptRunner, FmsRunMongoCmd fmsRunMongoCmd) {
            this.myForm = new MyForm();
            this.myProject = myProject;
            this.myForm.fmsScriptRunner = fmsScriptRunner;
            this.myForm.fmsRunMongoCmd = fmsRunMongoCmd;
            this.myForm.myProject = myProject;
            this.myForm.roleMap = roleMap;
            this.myForm.userDetail = userDetail;
            this.myForm.searchObject = searchObject;
            this.dbObjectForm = dbObjectForm;
            this.searchObject = searchObject;
            this.admin = roleMap.isUserInRole(myProject.getAdminAndViewerRole());
        }

        public Builder maskWorkflowRelation() {
            Object workFlowRelation = dbObjectForm.get("workFlowRelation");
            this.myForm.myWorkflowRelation = new MyWorkflowRelation();
            this.myForm.hasWorkflowRelation = workFlowRelation != null;
            return this;
        }

        public Builder maskAccesscontrol() {
            StringBuilder accesscontrolSB = new StringBuilder();
            if (dbObjectForm.get(ACCESS_CONTROL) instanceof String) {
                accesscontrolSB.append((String) dbObjectForm.get(ACCESS_CONTROL));
            } else if (dbObjectForm.get(ACCESS_CONTROL) instanceof List) {
                for (Object obj : (Iterable<? extends Object>) dbObjectForm.get(ACCESS_CONTROL)) {
                    accesscontrolSB.append((String) obj);
                    accesscontrolSB.append(COMMA);
                }
            }

            if (this.myForm.getMyProject() != null) {

                if (this.myForm.getMyProject().getAdminRole() != null && accesscontrolSB.indexOf(this.myForm.getMyProject().getAdminRole()) < 0) {
                    accesscontrolSB.append(COMMA);
                    accesscontrolSB.append(this.myForm.getMyProject().getAdminRole());
                }

                if (this.myForm.getMyProject().getViewerRole() != null && accesscontrolSB.indexOf(this.myForm.getMyProject().getViewerRole()) < 0) {
                    accesscontrolSB.append(COMMA);
                    accesscontrolSB.append(this.myForm.getMyProject().getViewerRole());
                }
            }

            this.myForm.accesscontrol = accesscontrolSB.toString();
            return this;
        }

        public Builder maskReadOnlyNote() {
            Object readOnlyNoteObject = this.dbObjectForm.get(READ_ONLY_NOTE);
            if (readOnlyNoteObject instanceof String) {
                this.myForm.readOnlyNote = (String) readOnlyNoteObject;
            } else if (readOnlyNoteObject instanceof Code) {
                Code code = (Code) readOnlyNoteObject;
                code = new Code(code.getCode().replace(DIEZ, DOLAR));
                Document commandResult = this.myForm.fmsScriptRunner
                        .runCommand("configdb", code.getCode(), searchObject, this.myForm.roleMap.keySet());
                this.myForm.readOnlyNote = (String) commandResult.get(RETVAL);
            }
            return this;
        }

        public Builder maskUserNote() {
            Object userNoteObject = dbObjectForm.get(USER_NOTE);
            if (userNoteObject instanceof String) {
                this.myForm.userNote = (String) userNoteObject;
            } else if (userNoteObject instanceof Code) {
                Code code = (Code) userNoteObject;
                code = new Code(code.getCode().replace(DIEZ, DOLAR));
                Document commandResult = this.myForm.fmsScriptRunner
                        .runCommand("configdb", code.getCode(), searchObject, this.myForm.roleMap.keySet());
                this.myForm.userNote = (String) commandResult.get(RETVAL);
            }
            return this;
        }

        public Builder maskZetDimension() throws Exception {
            this.myForm.zetDimension = (List) dbObjectForm.get("zetDimension");
            return this;
        }

        public Builder maskUploadMerge() throws Exception {

            Document dbo = (Document) dbObjectForm.get(UPLOAD_CONFIG);

            if (dbo != null) {
                this.myForm.uploadMerge = new MyMerge(dbo, this.myForm);
            }

            return this;
        }

        public Builder maskFieldsKeySet() {
            List<OrderedKey> orderedKeys = new ArrayList<>();

            for (String mykey : this.myForm.fields.keySet()) {
                Integer order = this.myForm.getField(mykey).getOrder();
                if (order == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(BR);
                    sb.append(BR);
                    sb.append(mykey.concat(" 'order' is missed."));
                    sb.append(BR);
                    sb.append(BR);
                    sb.append("please contact with related module administrator.");
                    sb.append(BR);
                    sb.append(BR);
                    sb.append("use configdb".concat(BR));
                    sb.append(BR);
                    sb.append(String.format("db.%s.findOne({key:'%s'})", this.myForm.getMyProject().getConfigTable(), this.myForm.getKey()));
                    sb.append(BR);
                    sb.append(String.format("db.%s.findOne({key:'%s'},{'fields.%s':1})", this.myForm.getMyProject().getConfigTable(), this.myForm.getKey(), mykey));
                    throw new RuntimeException(sb.toString());
                }
                orderedKeys.add(new OrderedKey(order, mykey));
            }

            Collections.sort(orderedKeys, new Comparator<OrderedKey>() {
                @Override
                public int compare(OrderedKey t1, OrderedKey t2) {
                    int m1 = t1.orderno;
                    int m2 = t2.orderno;
                    return Integer.compare(m2, m1);
                }
            });

            this.myForm.fieldsKeySet = new LinkedHashSet<>();
            for (OrderedKey orderedKey : orderedKeys) {
                this.myForm.fieldsKeySet.add(orderedKey.key);
            }

            return this;
        }

        public Builder maskCurrentRendered() {
            this.myForm.currentRendered = Boolean.TRUE;

            Object tempCurrentRendered = dbObjectForm.get(CURRENT_RENDERED);
            if (tempCurrentRendered instanceof Boolean) {
                this.myForm.currentRendered = (Boolean) tempCurrentRendered;
            } else if (tempCurrentRendered instanceof Code) {
                Code code = (Code) tempCurrentRendered;
                code = new Code(code.getCode().replace(DIEZ, DOLAR));
                Document commandResult = this.myForm.fmsScriptRunner
                        .runCommand("configdb", code.getCode(), searchObject, this.myForm.roleMap.keySet());
                this.myForm.currentRendered = Boolean.TRUE.equals(commandResult.get(RETVAL));
            }
            return this;
        }

        public Builder maskNotes() {
            Object objUserConstantNote = dbObjectForm.get(USER_CONSTANT_NOTE);
            if (objUserConstantNote instanceof String) {
                this.myForm.userConstantNote = (String) objUserConstantNote;
            } else if (objUserConstantNote instanceof Code) {
                if (admin) {
                    this.myForm.userConstantNote = "";
                } else {
                    Code code = (Code) objUserConstantNote;
                    code = new Code(code.getCode().replace(DIEZ, DOLAR));
                    Document commandResult = this.myForm.fmsScriptRunner
                            .runCommand(CONFIG_DB, code.getCode(), searchObject, this.myForm.roleMap.keySet());

                    Object returnValue = commandResult.get(RETVAL);

                    if (returnValue instanceof String) {
                        this.myForm.userConstantNote = (String) returnValue;
                    } else if (returnValue instanceof List) {
                        this.myForm.userConstantNote = "";
                        this.myForm.userConstantNoteList = (List) returnValue;
                    }
                }
            }

            if (dbObjectForm.get(USER_CONSTANT_NOTE_LIST) instanceof List) {
                this.myForm.userConstantNoteList = (List) dbObjectForm.get(USER_CONSTANT_NOTE_LIST);
            }
            return this;
        }

        public Builder maskEvents() throws Exception {

            if (dbObjectForm.get(FORM_EVENTS) instanceof Document) {

                this.myForm.events = (Document) dbObjectForm.get(FORM_EVENTS);

                for (String eventKey : this.myForm.events.keySet()) {
                    Document event = (Document) this.myForm.events.get(eventKey);
                    if (event.get(ACTION) == null) {
                        String errorMsg = MessageFormat//
                                .format("<ul><li>The \"{0}.action\" property is missed.</br>"
                                        + "It seems you have defined \"{0}\" on form,</br>"
                                        + "but forget to set \"action\" on this.", eventKey);
                        throw new Exception(errorMsg);
                    }
                }
            }
            return this;
        }

        public Builder maskUpperNode() throws Exception {
            if (MyForm.SCHEMA_VERSION_110.equals(dbObjectForm.get(MyForm.SCHEMA_VERSION))) {
                this.myForm.upperNode = ((List<String>) dbObjectForm.get(UPPER_NODES)).get(0);
            } else {
                this.myForm.upperNode = ((Document) dbObjectForm.get(UPPER_NODES)).keySet().iterator().next().toString();
            }
            return this;
        }

        public Builder maskKey() throws Exception {
            this.myForm.key = dbObjectForm.get(FORM_KEY).toString();
            return this;
        }

        public Builder maskFormType() {
            this.myForm.formType = (String) dbObjectForm.get(FORM_TYPE);
            return this;
        }

        public Builder maskLoginFkField() throws Exception {
            this.myForm.loginFkField = (String) dbObjectForm.get(LOGIN_FK);
            return this;
        }

        public Builder maskDimension() throws Exception {

            Object dim = dbObjectForm.get(DIMENSION_LOWER_CASE);

            if (dim instanceof Document) {
                Document onUserRole = (Document) ((Document) dim).get(ON_USER_ROLE);
                for (String key : onUserRole.keySet()) {
                    if (this.myForm.roleMap.isUserInRole(key)) {
                        this.myForm.dimension = (Number) ((Document) onUserRole.get(key)).get(VALUE);
                    }
                }
            } else if (dim instanceof Number) {
                this.myForm.dimension = (Number) dbObjectForm.get(DIMENSION_LOWER_CASE);
            }

            if (this.myForm.dimension == null) {
                this.myForm.dimension = 0;
            }

            return this;
        }

        public Builder maskVersionFields() {
            for (String fieldKey : this.myForm.getFields().keySet()) {
                MyField field = this.myForm.getField(fieldKey);
                if (field.isVersion()) {
                    this.myForm.versionFields.add(field.getField());
                    this.myForm.versionEnable = true;
                }
            }
            return this;
        }

        public Builder maskRequiredFields() {
            for (String fieldKey : this.myForm.getFields().keySet()) {
                MyField field = this.myForm.getField(fieldKey);
                if (field.isVersion()) {
                    this.myForm.requiredFields.put(field.getField(), true);
                }
            }
            return this;
        }

        public Builder maskMyRules() {
            Document dboWorkflowSteps = (Document) dbObjectForm.get("workflow-steps");
            if (dboWorkflowSteps != null) {

                Object workflowStartStep = dbObjectForm.get("workflow-start-step");
                if (workflowStartStep instanceof String) {
                    this.myForm.workflowStartStep = workflowStartStep.toString();
                }

                this.myForm.workflowSteps = new ArrayList<>();
                for (Object obj : dboWorkflowSteps.values()) {
                    this.myForm.workflowSteps.add(new MyRule((Document) obj, this.myForm.fmsScriptRunner));
                }
            }
            return this;
        }

        public Builder maskMyNotifies() {
            Object obj = dbObjectForm.get(MyNotifies.MONGO_KEY);
            if (obj instanceof List) {
                this.myForm.myNotifies = new MyNotifies.Builder((List) obj, this.myForm.fmsScriptRunner, this.myForm.roleMap.keySet())
                        .build();
            }
            return this;
        }

        public Builder maskDefaultQueries() {

            if (SCHEMA_VERSION_110.equals(dbObjectForm.get(SCHEMA_VERSION))) {
                String dfcq = (String) dbObjectForm.get(DEFAULT_CURRENT_QUERY);
                String dfhq = (String) dbObjectForm.get(DEFAULT_HISTORY_QUERY);

                List<ObjectId> list = (List<ObjectId>) myForm.userDetail.getLoginFkSearchMapInListOfValues().get(DOLAR_IN);
                Document docArg = new Document(this.myForm.searchObject);
                if (this.myForm.loginFkField == null) {
                    throw new RuntimeException("loginFkField = null");
                }
                docArg.append(this.myForm.loginFkField, list.get(0));

                this.myForm.defaultCurrentQuery = new HashMap();
                this.myForm.defaultHistoryQuery = new HashMap();

                if (dfcq != null) {
                    Document docCurrentQuery = (Document) this.myForm.fmsScriptRunner
                            .runCommand("test", dfcq, docArg, this.myForm.roleMap).get(RETVAL);
                    this.myForm.defaultCurrentQuery.putAll(docCurrentQuery);

                }

                if (dfhq != null) {
                    Document docHistoryQuery = (Document) this.myForm.fmsScriptRunner
                            .runCommand("test", dfhq, docArg, this.myForm.roleMap).get(RETVAL);
                    this.myForm.defaultHistoryQuery.putAll(docHistoryQuery);
                }
            } else {
                Code dfcq = (Code) dbObjectForm.get(DEFAULT_CURRENT_QUERY);
                Code dfhq = (Code) dbObjectForm.get(DEFAULT_HISTORY_QUERY);

                List<ObjectId> list = (List<ObjectId>) myForm.userDetail.getLoginFkSearchMapInListOfValues().get(DOLAR_IN);
                Document docArg = new Document(this.myForm.searchObject);
                if (this.myForm.loginFkField == null) {
                    throw new RuntimeException("loginFkField = null");
                }
                docArg.append(this.myForm.loginFkField, list.get(0));

                this.myForm.defaultCurrentQuery = new HashMap();
                this.myForm.defaultHistoryQuery = new HashMap();

                if (dfcq != null) {
                    Document docCurrentQuery = (Document) this.myForm.fmsScriptRunner
                            .runCommand("test", dfcq.getCode(), docArg, this.myForm.roleMap).get(RETVAL);
                    this.myForm.defaultCurrentQuery.putAll(docCurrentQuery);

                }

                if (dfhq != null) {
                    Document docHistoryQuery = (Document) this.myForm.fmsScriptRunner
                            .runCommand("test", dfhq.getCode(), docArg, this.myForm.roleMap).get(RETVAL);
                    this.myForm.defaultHistoryQuery.putAll(docHistoryQuery);
                }
            }

            if (admin) {
                myForm.getDefaultCurrentQuery().remove(myForm.getLoginFkField());
                myForm.getDefaultHistoryQuery().remove(myForm.getLoginFkField());
            }

            return this;
        }

        public Builder maskFieldsDefaultValues() {

            for (MyField myField : this.myForm.getAutosetFields()) {
                myField.createDefaultCurrentValue(this.myForm);
                myField.createDefaultHistoryValue(this.myForm);
            }

            return this;
        }

        public Builder maskAutosetFields() throws Exception {

            for (String fieldKey : this.myForm.getFields().keySet()) {
                MyField field = this.myForm.getField(fieldKey);

                Object autosetAttr = field.getAutoset();

                if (autosetAttr != null) {
                    if (field.getItemsAsMyItems() == null) {
                        throw new Exception(String.format(""
                                + "<b>Module Config File Validataion Failed.</b></br></br>"
                                + "There is an autoset field \"%s\" without \"items\" property.</br>"
                                + "Please make an ensure that this field shoud be defined as \"autoset\".</br>"
                                + "You must define an \"items\" property for this field in case of ensuring for above mentioned.", //
                                field.getName()));
                    }

                    if (!this.myForm.roleMap.isUserInRole(this.myForm.myProject.getAdminAndViewerRole())
                            || this.myForm.dimension.intValue() >= 2) {

                        if (autosetAttr instanceof Boolean && Boolean.TRUE.equals(autosetAttr)) {
                            this.myForm.autosetFields.add(field);
                        } else if (autosetAttr instanceof Document) {

                            Document onUserRoleMap = ((Document) autosetAttr);

                            Set<String> roles = onUserRoleMap.keySet();

                            int maxPriority = 0;
                            String value = ACCEPT;

                            for (String role : roles) {
                                if (this.myForm.roleMap.isUserInRole(role)) {
                                    Document roleDef = (Document) ((Document) field.getAutoset()).get(role);
                                    int rolePriority = ((Number) roleDef.get(PRIORITY)).intValue();
                                    if (rolePriority > maxPriority) {
                                        maxPriority = rolePriority;
                                        value = ((String) roleDef.get(VALUE));
                                    }
                                }
                            }
                            if (ACCEPT.equals(value)) {
                                this.myForm.autosetFields.add(field);
                            }
                        } else {
                            this.myForm.autosetFields.add(field);
                        }
                    }
                }
            }

            Collections.sort(this.myForm.autosetFields, new MyFieldComparator());

            return this;
        }

        public Builder validateForm() throws Exception {
            String errorMessage = "";
            if (!this.myForm.versionFields.isEmpty() && this.myForm.versionCollection == null) {
                errorMessage += MessageFormat.format("<ul><li>There is a field(s) marked to version,</br>"
                        + "but you miss \"versionCollection\" property on the related form \"{0}\".</br>", this.myForm.name);
            }
            if (!errorMessage.isEmpty()) {
                throw new Exception(errorMessage);
            }

            if (this.myForm.dimension.intValue() == 2 && this.myForm.autosetFields.isEmpty()) {
                throw new Exception(""//
                        .concat("<table border=1>")
                        .concat("<tr>")
                        .concat("<td>cause</td><td>:</td>")
                        .concat("<td>")
                        .concat("due to {dimension:2} form property, the form</br>")
                        .concat("is set to view as pivot, but there is no field with \"autoset\" property.")
                        .concat("</td>")
                        .concat("</tr>")
                        .concat("<table>"));
            }

            boolean hasMeasure = false;

            for (String fieldKey : this.myForm.getFields().keySet()) {
                MyField field = this.myForm.getField(fieldKey);
                if (MEASURE.equals(field.getNdType())) {
                    hasMeasure = true;
                }
            }

            if (this.myForm.dimension.intValue() == 2 && !hasMeasure) {
                throw new Exception(""//
                        .concat("<table border=1>")
                        .concat("<tr>")
                        .concat("<td>cause</td><td>:</td>")
                        .concat("<td>")
                        .concat("due to {dimension:2} form property, the form</br>")
                        .concat("is set to view as pivot, but the MEASURE field is missed.")
                        .concat("</td>")
                        .concat("</tr>")
                        .concat("<tr>")
                        .concat("<td>solution</td><td>:</td><td>define a field with {ndType:\"MEASURE\"} property</td>")
                        .concat("</tr>")
                        .concat("<tr>")
                        .concat("<td>example</td><td>:</td>")
                        .concat("<td>")
                        .concat("db.graphComission.update(</br>")
                        .concat("\t\t{key: \"comissionIncome\"},</br>")
                        .concat("\t\t{$set: {\"fields.value\":{</br>")
                        .concat("...</br>")
                        .concat("\"ndType\": \"MEASURE\",</br>")
                        .concat("...</br>")
                        .concat("</td>")
                        .concat("</tr>")
                        .concat("<table>"));
            }
            return this;
        }

        public Builder validate() throws Exception {
            if (this.myForm.getDb() == null) {
                throw new NullPointerException("db attribute is resolved to null");
            }
            if (this.myForm.getKey() == null) {
                throw new NullPointerException("key attribute is resolved to null");
            }
            if (this.myForm.getFieldsKeySet() == null) {
                throw new RuntimeException("fields attribute is resolved to null");
            }
            return this;
        }

        public Builder validateFields() throws Exception {

            String errorFormat = "\"{0}\" property is not defined on field \"{1}\"";

            for (String fieldKey : this.myForm.getFields().keySet()) {

                MyField field = this.myForm.getFields().get(fieldKey);

                String errorMessage = "";

                // sadece 3 boyutlularda bu durumu denetle (dimension == 2)
                if (this.myForm.dimension.intValue() == 2 && "java.lang.Integer".equals(field.getValueType())//
                        && field.getMyconverter() == null && field.getUysformat() == null) {
                    errorMessage += MessageFormat.format(errorFormat, CONVERTER, field.getName());
                    errorMessage += "</br>";
                }

                if (field == null) {
                    throw new Exception("field is null");
                }

                if (field.getOrder() == null) {
                    errorMessage += MessageFormat.format(errorFormat, ORDER, field.getName());
                    errorMessage += "</br>";
                }
                if (field.getReportOrder() == null) {
                    errorMessage += MessageFormat.format(errorFormat, REPORT_ORDER, field.getName());
                    errorMessage += "</br>";
                }
                if (field.getField() == null) {
                    errorMessage += MessageFormat.format(errorFormat, FIELD, field.getName());
                    errorMessage += "</br>";
                }
                if (FORM_DIMENSION.equals(field.getNdType())
                        && field.getItemsAsMyItems() == null
                        && field.getDefaultValue() == null) {
                    throw new Exception(String.format(""
                            + "<b>Module Config File Validataion Failed.</b></br><hr></br>"
                            + "There is a field <b>{%s:{name:\"%s\", ndType:\"DIMENSION\"}}</b> missed <b>\"items\"</b> AND <b>\"defaultValue\"</b> properties.</br></br>"
                            + "You must define \"at least\" one of the above mentioned properties for DIMENSION marked field.</br></br>"//
                            + "Otherwise, the internal mapReduce process will not produce pivot view on the related collection.</br>" //
                            + "This property is meaningfull for setting the defaultValue to the realted dimension field that prevent</br>"//
                            + "null state which is important for interanal java mapReduce process</br>", //
                            field.getKey(), field.getName()));
                }
                if (!errorMessage.isEmpty()) {
                    throw new Exception(errorMessage + this.myForm.printToConfigAnalyze(fieldKey));
                }
            }

            return this;
        }

        public Builder fixMe() throws Exception {

            if ("ion_form_1170".equals(this.myForm.key)) {
                this.myForm.crkr = new CrudRelatedKeyRender<Map>() {
                    @Override
                    boolean getRender(Map t, String key) {

                        if (t == null || key == null) {
                            return true;
                        }

                        Object notify_type = t.get("notify_type");
                        if (notify_type != null) {
                            notify_type = cacheIonSettingIdCode.get(notify_type.toString());
                        }

                        Object activity_status = t.get(ACTIVITY_STATUS);
                        if (activity_status != null) {
                            activity_status = cacheIonSettingIdCode.get(activity_status.toString());
                        }

                        boolean xxx = true;
                        switch (key) {
                            case ACTIVITY_STATUS:
                                xxx = ION_SETTING_NOTIFY_TYPE_001.equals(notify_type);
                                break;
                            case ATTACHMENTS:
                                xxx = ION_SETTING_NOTIFY_TYPE_001.equals(notify_type) && "ion_setting_activity_status_001".equals(activity_status);
                                break;
                            case "manager_ad_soyad":
                                xxx = ION_SETTING_NOTIFY_TYPE_000.equals(notify_type) || ION_SETTING_NOTIFY_TYPE_001.equals(notify_type) || "ion_setting_notify_type_003".equals(notify_type);
                                break;
                            case "posta_kodu":
                                xxx = ION_SETTING_NOTIFY_TYPE_000.equals(notify_type) || ION_SETTING_NOTIFY_TYPE_001.equals(notify_type) || "ion_setting_notify_type_004".equals(notify_type);
                                break;
                            case TELEPHONE:
                                xxx = ION_SETTING_NOTIFY_TYPE_000.equals(notify_type) || ION_SETTING_NOTIFY_TYPE_001.equals(notify_type) || "ion_setting_notify_type_005".equals(notify_type);
                                break;
                            case "faks":
                                xxx = ION_SETTING_NOTIFY_TYPE_000.equals(notify_type) || ION_SETTING_NOTIFY_TYPE_001.equals(notify_type) || "ion_setting_notify_type_006".equals(notify_type);
                                break;
                            case "org_name":
                                xxx = ION_SETTING_NOTIFY_TYPE_000.equals(notify_type) || ION_SETTING_NOTIFY_TYPE_001.equals(notify_type) || "ion_setting_notify_type_002".equals(notify_type);
                                break;
                            case "org_type":
                                xxx = ION_SETTING_NOTIFY_TYPE_000.equals(notify_type) || ION_SETTING_NOTIFY_TYPE_001.equals(notify_type) || "ion_setting_notify_type_002".equals(notify_type);
                                break;
                            case "address":
                                xxx = ION_SETTING_NOTIFY_TYPE_000.equals(notify_type) || ION_SETTING_NOTIFY_TYPE_001.equals(notify_type);
                                break;
                            case "bulundugu_il":
                                xxx = ION_SETTING_NOTIFY_TYPE_000.equals(notify_type) || ION_SETTING_NOTIFY_TYPE_001.equals(notify_type);
                                break;
                            case "bulundugu_ilce":
                                xxx = ION_SETTING_NOTIFY_TYPE_000.equals(notify_type) || ION_SETTING_NOTIFY_TYPE_001.equals(notify_type);
                                break;
                        }
                        return xxx;
                    }
                };
            } else if ("ion_form_1160".equals(this.myForm.key) && this.myForm.roleMap.isUserInRole("pyuser,akuser")) {
                this.myForm.crkr = new CrudRelatedKeyRender<Map>() {
                    @Override
                    boolean getRender(Map t, String key) {

                        if (t == null || key == null) {
                            return true;
                        }

                        Object notifyType = t.get("notify_type");
                        if (notifyType != null) {
                            notifyType = cacheIonSettingIdCode.get(notifyType.toString());
                        }

                        Object activityStatus = t.get(ACTIVITY_STATUS);
                        if (activityStatus != null) {
                            activityStatus = cacheIonSettingIdCode.get(activityStatus.toString());
                        }
                        switch (key) {
                            case ACTIVITY_STATUS:
                                return ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType);
                            case ATTACHMENTS:
                                return ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) && "ion_setting_activity_status_001".equals(activityStatus);
                            case "posta_kodu":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) || "ion_form_1160_notify_type_002".equals(notifyType);
                            case TELEPHONE:
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) || "ion_form_1160_notify_type_003".equals(notifyType);
                            case "call_center_no":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) || "ion_form_1160_notify_type_004".equals(notifyType);
                            case "faks":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) || "ion_form_1160_notify_type_005".equals(notifyType);
                            case EMAIL:
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) || "ion_form_1160_notify_type_006".equals(notifyType);
                            case "web_site":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) || "ion_form_1160_notify_type_007".equals(notifyType);
                            case "tax_office_name":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) || "ion_form_1160_notify_type_008".equals(notifyType);
                            case "tax_id_no":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) || "ion_form_1160_notify_type_009".equals(notifyType);
                            case "main_aggrement_date":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) || "ion_form_1160_notify_type_010".equals(notifyType);
                            case "tic_sicil_kayit_il":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) || "ion_form_1160_notify_type_011".equals(notifyType);
                            case "tic_sicil_kayit_tarihi":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) || "ion_form_1160_notify_type_012".equals(notifyType);
                            case "tic_sicil_no":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) || "ion_form_1160_notify_type_013".equals(notifyType);
                            case "tic_sicil_meslek_grubu":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) || "ion_form_1160_notify_type_014".equals(notifyType);
                            case "nace_kodu":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType) || "ion_form_1160_notify_type_015".equals(notifyType);
                            case "city":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType);
                            case "country":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType);
                            case "address":
                                return ION_FORM_1160_NOTIFY_TYPE_000.equals(notifyType) || ION_FORM_1160_NOTIFY_TYPE_001.equals(notifyType);
                        }
                        return true;
                    }

                };
            }

            return this;
        }

        public Builder withFieldsAsList(List<MyField> fieldsAsList) {
            this.myForm.fieldsAsList = fieldsAsList;
            return this;
        }

        public Builder withOthers() {

            this.myForm.jsonViewer = "treetable".equals(dbObjectForm.get("guiviewer"));

            //string properties
            this.myForm.anotherEimzaColletionKey = (String) dbObjectForm.get("anotherEimzaColletionKey");
            this.myForm.calculateCollection = (String) dbObjectForm.get(CALCULATE_COLLECTION);
            this.myForm.table = (String) dbObjectForm.get(COLLECTION);
            this.myForm.posAmountField = (String) dbObjectForm.get("posAmountField");
            this.myForm.posOrderIdField = (String) dbObjectForm.get("posOrderIdField");
            this.myForm.versionCollection = (String) dbObjectForm.get("versionCollection");
            this.myForm.snapshotCollection = (String) dbObjectForm.get(SNAPSHOT_COLLECTION);
            this.myForm.db = (String) dbObjectForm.get(FORM_DB);
            Object defaultSortField = dbObjectForm.get(DEFAULT_SORT_FIELD);
            if (defaultSortField instanceof Document) {
                this.myForm.defaultSortField = (Document) defaultSortField;
            }

            this.myForm.esignEmailBccRecipients = (String) dbObjectForm.get(ESIGN_EMAIL_BCC_RECIPIENTS);
            this.myForm.importTextFormat = (String) dbObjectForm.get(IMPORT_TEXT_FROMAT);
            this.myForm.form = (String) dbObjectForm.get(FORM);
            this.myForm.formType = (String) dbObjectForm.get(FORM_TYPE);
            this.myForm.group = (String) dbObjectForm.get(GROUP);
            this.myForm.name = (String) dbObjectForm.get(NAME);
            this.myForm.key = (String) dbObjectForm.get(FORM_KEY);
            this.myForm.schemaVersion = (String) dbObjectForm.get(SCHEMA_VERSION);
            this.myForm.shortName = (String) dbObjectForm.get(SHORT_NAME);
            this.myForm.pageName = (String) dbObjectForm.get(PAGE_NAME);
            this.myForm.projectKey = (String) dbObjectForm.get(PROJECT_KEY);
            this.myForm.loginFkField = (String) dbObjectForm.get(LOGIN_FK);
            this.myForm.controlCollection = (String) dbObjectForm.get(CONTROL_COLLECTION);

            //boolean properties
            this.myForm.historyRendered = Boolean.TRUE.equals(dbObjectForm.get(HISTORY_RENDERED));
            this.myForm.showHistory = Boolean.TRUE.equals(dbObjectForm.get(SHOW_HISTORY));
            this.myForm.tranzient = Boolean.TRUE.equals(dbObjectForm.get("tranzient"));

            // unexpected properties //FIXME typesafe
            this.myForm.dbo = dbObjectForm;
            this.myForm.actions = dbObjectForm.get(CONFIG_ATTR_ACTIONS);
            this.myForm.calculateQuery = dbObjectForm.get(CALCULATE_QUERY);

            // json properties
            if (dbObjectForm.get(CONSTRAINT_ITEMS) instanceof Document) {
                this.myForm.constraintItems = (Document) dbObjectForm.get(CONSTRAINT_ITEMS);
            }
            if (dbObjectForm.get(ACCESS_CONTROL_LEVEL_TWO) instanceof Document) {
                this.myForm.accessControlLevelTwo = (Document) dbObjectForm.get(ACCESS_CONTROL_LEVEL_TWO);
            }
            if (dbObjectForm.get(EXCEL_FORMAT) instanceof Document) {
                this.myForm.excelFormat = (Document) dbObjectForm.get(EXCEL_FORMAT);
            }
            if (dbObjectForm.get(EVENT_PRE_SAVE) instanceof Document) {
                this.myForm.eventPreSave = (Document) dbObjectForm.get(EVENT_PRE_SAVE);
            }
            if (dbObjectForm.get(EVENT_POST_SAVE) instanceof Document) {
                this.myForm.eventPostSave = (Document) dbObjectForm.get(EVENT_POST_SAVE);
            }
            if (dbObjectForm.get(EVENT_POST_DELETE) instanceof Document) {
                this.myForm.eventPostDelete = (Document) dbObjectForm.get(EVENT_POST_DELETE);
            }
            if (dbObjectForm.get(EVENT_FORM_SELECTION) instanceof Document) {
                this.myForm.eventFormSelection = (Document) dbObjectForm.get(EVENT_FORM_SELECTION);
            }
            if (dbObjectForm.get(EVENT_PRE_DELETE) instanceof Document) {
                this.myForm.eventPreDelete = (Document) dbObjectForm.get(EVENT_PRE_DELETE);
            }
            if (dbObjectForm.get(SUB_GROUPS) instanceof Document) {
                this.myForm.subGroups = ((Document) dbObjectForm.get(SUB_GROUPS));
            }
            if (dbObjectForm.get(MY_NAMED_QUERIES) instanceof Document) {
                this.myForm.myNamedQueries = (Document) dbObjectForm.get(MY_NAMED_QUERIES);
            }

            // code properties
            if (dbObjectForm.get(ESIGN_EMAIL_TO_RECIPIENTS) instanceof Code) {
                this.myForm.esignEmailToRecipients = (Code) dbObjectForm.get(ESIGN_EMAIL_TO_RECIPIENTS);
            }
            if (dbObjectForm.get(FUNC_NOTE) instanceof Code) {
                this.myForm.funcNote = (Code) dbObjectForm.get(FUNC_NOTE);
            }

            // number properties
            if (dbObjectForm.get(COLUMN_COUNT) instanceof Number) {
                this.myForm.columnCount = (Number) dbObjectForm.get(COLUMN_COUNT);
            }
            if (dbObjectForm.get(HANDSON_ROW_HEADER_WIDTH) instanceof Number) {
                this.myForm.handsonRowHeaderWidth = ((Number) dbObjectForm.get(HANDSON_ROW_HEADER_WIDTH)).intValue();
            } else {
                this.myForm.handsonRowHeaderWidth = 100;
            }
            if (dbObjectForm.get(HANDSON_COL_WIDTH) instanceof Number) {
                this.myForm.handsonColWidths = ((Number) dbObjectForm.get(HANDSON_COL_WIDTH)).intValue();
            } else {
                this.myForm.handsonColWidths = 100;
            }
            if (dbObjectForm.get(HISTORY_POSITION) instanceof Number) {
                this.myForm.historyPosition = (Number) dbObjectForm.get(HISTORY_POSITION);
            }
            if (dbObjectForm.get(UNIQUE_INDEX_LIST) instanceof List) {
                this.myForm.uniqueIndexList = (List) dbObjectForm.get(UNIQUE_INDEX_LIST);
            }

            return this;
        }

        public Builder maskFilter(Document filter) {
            this.myForm.searchForm = filter;
            return this;
        }

        public Builder maskChilds(List<ChildFilter> childs) {
            this.myForm.childs = childs;
            return this;
        }

        public Builder maskAjax() {
            for (MyField myField : this.myForm.fields.values()) {
                if (myField.getAjaxEffectedKeys() != null && !myField.getAjaxEffectedKeys().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (String ajaxKey : myField.getAjaxEffectedKeys()) {
                        MyField relatedField = this.myForm.getField(ajaxKey);
                        if (relatedField != null) {
                            String styleClass = "id-class-".concat(ajaxKey);
                            relatedField.setStyleClass(styleClass);
                            sb.append(String.format("@(.%s),", styleClass));
                        }
                    }
                    myField.setAjaxUpdate(sb.toString());
                }
            }
            return this;
        }

        public Builder maskRowFieldsKeySet() {
            this.myForm.fieldsRowKeys = new ArrayList<>();
            for (String key : this.myForm.fieldsRow.keySet()) {
                this.myForm.fieldsRowKeys.add(key);
            }
            return this;
        }

        public Builder maskFields(Map<String, MyField> fields) {

            this.myForm.fields = fields;

            for (MyField myField : fields.values()) {
                if (INPUT_FILE.equals(myField.getComponentType())) {
                    myForm.hasAttachedFiles = true;
                }

                if (myField.isAjax()) {
                    myForm.ajaxFields.add(myField.getKey());
                }
            }
            return this;
        }

        public Builder maskRowFields(Map<String, MyField> rowFields) {
            this.myForm.fieldsRow = rowFields;
            return this;
        }

        public MyForm build() throws Exception {
            return this.myForm;
        }

    }

}
