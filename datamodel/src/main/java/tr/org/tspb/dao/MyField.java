package tr.org.tspb.dao;

import tr.org.tspb.pojo.ForumColumnCellKey;
import tr.org.tspb.pojo.UserDetail;
import tr.org.tspb.pojo.RoleMap;
import static tr.org.tspb.constants.ProjectConstants.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import org.bson.Document;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import tr.org.tspb.dao.refs.PlainRecord;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;
import tr.org.tspb.exceptions.FormConfigException;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyField {

    private static final String DEFAULT_CURRENT_VALUE = "defaultCurrentValue";
    private static final String DEFAULT_HISTORY_VALUE = "defaultHistoryValue";

    private ObjectId loginMemberId;
    private MyForm myForm;

// <editor-fold defaultstate="collapsed" desc="encapsulated fields">

    /*
     * Aa	Bb	Cc	Dd	Ee	Ff	Gg
     * Hh	Ii	Jj	Kk      Ll	Mm	Nn
     * Oo	Pp	Qq	Rr	Ss	Tt	Uu
     * Vv	Ww	Xx	Yy	Zz
     */
    // A
    private String accesscontrol;
    private Boolean searchAccess;
    private Boolean quickFilter;
    private Boolean autoset;
    private String ajaxAction;
    private String ajaxShowHide;
    private String ajaxUpdate;
    private List<String> ajaxEffectedKeys = new ArrayList<>();
    private boolean ajax;
    private TagAjaxRef tagAjaxRef;
    // C
    private String code;//this is the case for nd
    private String calculateEngine;
    private String componentType;
    private String calculateOnClient;
    private String calculate;
    private Boolean calculateOnListView;
    private Boolean calculateOnCrudView;
    private Boolean calculateAfterSave;
    private Boolean calculateAfterDelete;
    private Boolean calculateOnSave;
    private Converter converterValue;
    private String converterInstance;
    private String converterFormat;
    private String converterParam;
    private Map<String, String> cacheBsonConverter = new HashMap<>();
    private Map<ForumColumnCellKey, Converter> cacheMapConverter = new HashMap<>();
    // D
    private String description;
    private boolean renderDesc;
    private boolean renderPopupDesc;
    private String dateRangeBeginKey;
    private String dateRangeEndKey;
    private ObjectId defaultCurrentValue;
    private ObjectId defaultHistoryValue;
    private boolean disabled;
    private Document dbo;
    private Boolean dateRangeControl;
    private Boolean dateRangeValidate;
    private Object divider;
    private Object defaultValue;
    // E
    private boolean embeddedAsList;
    // F
    private String fieldNote;
    private String field;
    private String fileType = "/(\\.|\\/)(pdf)$/";
    private int fileLimit;
    // I
    private ObjectId _id;//this is the case for nd
    private boolean immediate;
    // H
    private String href;
    // K
    private String key;
    // L
    private List<SelectItem> listOfValues;
    private Boolean loginFK;
    // M
    private Object minFractationDigits;
    private Object maxFractationDigits;
    private List<SelectItem> selectItemsCurrent = new ArrayList<>();
    private List<SelectItem> selectItemsHistory = new ArrayList<>();
    private MyItems itemsAsMyItems;
    private String myDatePattern;
    private String mask;
    private String myFormKey;
    private Double maxValue;
    private String maxMoney;
    private boolean money;
    // N
    private String name;
    private String ndType;
    private String ndAxis;
    // O
    private Integer order;
    private Object observer;
    private Object observableAttr;
    private Object observerAttr;
    private Object observable;
    // P
    private String popupDesc;
    // R
    private String refCollection;
    private Integer reportOrder;
    private boolean rendered;
    private boolean required;
    private boolean reportRendered;
    private boolean roleCheck;
    private boolean readonly;
    // S
    private String sessionKey;
    private String subGroup;
    private String shortName;
    private String style;
    private String labelStyle;
    private String styleClass;
    private Boolean shouldCheckNegative;
    // U
    private String uysformat;
    // V
    private Object valueChangeListenerAction;
    private String valueType;
    private String visible;
    private List<String> viewKey;
    private boolean version;
    // W
    private int width;
    private boolean workflow;
    private MyMap crudRecord = new MyMap();
    private FmsAutoComplete fmsAutoComplete;
    private FmsScriptRunner fmsScriptRunner;
    // 
    private boolean isAutoComplete;

// </editor-fold>
    public MyField() {

    }

// <editor-fold defaultstate="collapsed" desc="getters">
    public String getAjaxShowHide() {
        return ajaxShowHide;
    }

    public MyItems getItemsAsMyItems() {
        return itemsAsMyItems;
    }

    public Boolean getDateRangeControl() {
        return dateRangeControl;
    }

    public Boolean getCalculateAfterDelete() {
        return calculateAfterDelete;
    }

    public Boolean getCalculateAfterSave() {
        return calculateAfterSave;
    }

    public Boolean getSearchAccess() {
        return searchAccess;
    }

    public Boolean getCalculateOnListView() {
        return calculateOnListView;
    }

    public Boolean getCalculateOnCrudView() {
        return calculateOnCrudView;
    }

    public String getMask() {
        if (mask == null || mask.isEmpty()) {
            mask = "(999) 999-9999";
        }
        return mask;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public String getDatePattern() {
        return myDatePattern;
    }

    public String getFileType() {
        return fileType;
    }

    public String getHref() {
        return href;
    }

    public String getField() {
        return field;
    }

    public String getFieldNote() {
        return fieldNote;
    }

    //FIXME need to be removed
    public String getKey() {
        return key;
    }

    public String getSubGroup() {
        return subGroup;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getComponentType() {
        if (componentType == null) {
            componentType = "inputText";
        }
        return componentType;
    }

    public boolean isAutoComplete() {
        return isAutoComplete;
    }

    public String getValueType() {
        return valueType;
    }

    public String getAccesscontrol() {
        return accesscontrol;
    }

    public String getStyle() {
        return style;
    }

    public String getCalculateEngine() {
        return calculateEngine;
    }

    public String getNdType() {
        return ndType;
    }

    public String getRefCollection() {
        return refCollection;
    }

    public String getNdAxis() {
        return ndAxis;
    }

    public String getCode() {
        return code;
    }

    public String getPopupDescription() {
        return popupDesc;
    }

    public String getDescription() {
        return description;
    }

    public String getDateRangeBeginKey() {
        return dateRangeBeginKey;
    }

    public String getDateRangeEndKey() {
        return dateRangeEndKey;
    }

    public String getAjaxAction() {
        return ajaxAction;
    }

    public String getAjaxUpdate() {
        return ajaxUpdate;
    }

    public String getMyFormKey() {
        return myFormKey;
    }

    public String getVisible() {
        return visible;
    }

    public String getMaxMoney() {
        return maxMoney;
    }

    public String getConverterInstance() {
        return converterInstance;
    }

    public String getConverterFormat() {
        return converterFormat;
    }

    public String getUysformat() {
        return uysformat;
    }

    public List<String> getViewKey() {
        return viewKey;
    }

    public Object getObservable() {
        return observable;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public String getCalculateOnClient() {
        return calculateOnClient;
    }

    public String getCalculate() {
        return calculate;
    }

    public Integer getOrder() {
        return order;
    }

    public Integer getReportOrder() {
        return reportOrder;
    }

    public int getWidth() {
        return width;
    }

    public boolean isHasHref() {
        return href != null;
    }

    public boolean isVersion() {
        return version;
    }

    public boolean isRendered() {
        return Boolean.TRUE.equals(rendered);
    }

    public boolean isRequired() {
        return Boolean.TRUE.equals(required);
    }

    public boolean isRoleCheck() {
        return roleCheck;
    }

    public boolean isMoney() {
        return money;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public Boolean getAutoset() {
        return autoset;
    }

    public Object getObserver() {
        return observer;
    }

    public Object getObservableAttr() {
        return observableAttr;
    }

    public Object getObserverAttr() {
        return observerAttr;
    }

    public int getFileLimit() {
        return fileLimit;
    }

    public ObjectId getDefaultHistoryValue() {
        return defaultHistoryValue;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public Object getValueChangeListenerAction() {
        return valueChangeListenerAction;
    }

    public ObjectId getDefaultCurrentValue() {
        return defaultCurrentValue;
    }

    public Object getMinFractationDigits() {
        return minFractationDigits;
    }

    public Object getMaxFractationDigits() {
        return maxFractationDigits;
    }

    public Object getDivider() {
        return divider;
    }

    public Converter getMyconverter() {
        return converterValue;
    }

    public boolean isReportRendered() {
        return reportRendered;
    }

    public List<SelectItem> getSelectItemsCurrent() {
        return Collections.unmodifiableList(selectItemsCurrent);
    }

    public List<SelectItem> getSelectItemsHistory() {
        return Collections.unmodifiableList(selectItemsHistory);
    }

    public ObjectId getId() {
        return _id;
    }

    public List<SelectItem> getListOfValues() {
        return Collections.unmodifiableList(listOfValues);
    }

    public Document getDbo() {
        return dbo;
    }

    public Boolean getCalculateOnSave() {
        return calculateOnSave;
    }

    public Boolean getDateRangeValidate() {
        return dateRangeValidate;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public Boolean getShouldCheckNegative() {
        return shouldCheckNegative;
    }

    public boolean isAjax() {
        return ajax;
    }

    public List<String> getAjaxEffectedKeys() {
        return Collections.unmodifiableList(ajaxEffectedKeys);
    }

    public Object getReferance() {
        if (dbo.get(MONGO_ID) != null) {
            return dbo.get(MONGO_ID);
        } else {
            return dbo.get(FIELD);
        }
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="setter">
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public void setSearchAccess(Boolean searchAccess) {
        this.searchAccess = searchAccess;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public void setAjaxUpdate(String ajaxUpdate) {
        this.ajaxUpdate = ajaxUpdate;
    }

// </editor-fold>
    @Override
    public int hashCode() {
        return (12345 + this.key.hashCode()) * (67890 + getReferance().hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MyField other = (MyField) obj;

        return Objects.equals(this.key, other.key);
    }

    @Override
    public String toString() {
        if (this.key == null) {
            throw new RuntimeException("Coordinate.java, dimensionOrMeasure:".concat("key=null").concat(dbo.toString()));
        }
        if (getReferance() == null) {
            throw new RuntimeException("Coordinate.java, dimensionOrMeasure:".concat("getReferance()=null").concat(dbo.toString()));
        }
        return this.key + "-" + getReferance().toString();
    }

    public void calcWfRendered(MyMap crud, RoleMap roleMap, Map searchObject) {

        if (!workflow) {
            return;
        }

        Object dboRendered = this.dbo.get(RENDERED);

        boolean localRendered = Boolean.TRUE.equals(dboRendered);

        if (dboRendered instanceof Document) {

            Document renderedObject = (Document) dboRendered;

            String onUserRole = (String) renderedObject.get(ON_USER_ROLE);
            if (onUserRole != null) {
                localRendered = roleMap.isUserInRole(onUserRole);
            } else {
                String db = (String) renderedObject.get(FORM_DB);
                String collection = (String) renderedObject.get(COLLECTION);
                String returnKey = (String) renderedObject.get(RETURN_KEY);
                Document query = (Document) renderedObject.get(QUERY);

                Document result = fmsScriptRunner.findOne(db, collection, (Document) query);

                if (result == null || (HAYIR.equals(result.get(returnKey)))) {
                    localRendered = false;
                }
            }

        } else if (dboRendered instanceof Code) {
            Code appearFunction = (Code) dboRendered;
            appearFunction = new Code(appearFunction.getCode().replace(DIEZ, DOLAR));

            if (this.dbo.get(FORM_DB) == null) {
                throw new RuntimeException("field." + this.getKey() + ".rendered is defined as func. 'db' tag is required.");
            }

            Document crudDoc = new Document(crud);
            crudDoc.remove(INODE);

            Document commandResult = fmsScriptRunner.runCommand(this.dbo.get(FORM_DB).toString(),
                    appearFunction.getCode(), crudDoc, searchObject, roleMap.keySet());

            localRendered = Boolean.TRUE.equals(commandResult.get(RETVAL));
        }

        this.rendered = localRendered;
    }

    public void createDefaultCurrentValue(MyForm myForm) {
        Code defaultCurrentValueCode = (Code) this.dbo.get(DEFAULT_CURRENT_VALUE);

        if (myForm.roleMap.isUserInRole(myForm.getMyProject().getAdminAndViewerRole())) {
            defaultCurrentValueCode = (Code) this.dbo.get("adminFunc");
        }

        if (defaultCurrentValueCode != null) {
            defaultCurrentValueCode = new Code(defaultCurrentValueCode.getCode().replace(DIEZ, DOLAR));

            String itemsDB = itemsAsMyItems.getDb();

            Document commandResult = fmsScriptRunner.runCommand(itemsDB, defaultCurrentValueCode.getCode(),
                    new Document(myForm.getDefaultCurrentQuery()), null);

            Object returnObject = commandResult.get(RETVAL);

            if (returnObject instanceof Document) {
                this.defaultCurrentValue = (ObjectId) ((Document) returnObject).get(MONGO_ID);
            }
        }

        if (this.defaultCurrentValue == null && this.key.equals(myForm.getLoginFkField())) {
            List<ObjectId> list = (List<ObjectId>) myForm.userDetail.getLoginFkSearchMapInListOfValues().get(DOLAR_IN);
            this.defaultCurrentValue = list.get(0);
        }

    }

    public void createDefaultHistoryValue(MyForm myForm) {
        Code defaultHistoryValueCode = (Code) this.dbo.get(DEFAULT_HISTORY_VALUE);

        if (myForm.roleMap.isUserInRole(myForm.getMyProject().getAdminAndViewerRole())) {
            defaultHistoryValueCode = (Code) this.dbo.get("adminFunc");
        }

        if (defaultHistoryValueCode != null) {
            defaultHistoryValueCode = new Code(defaultHistoryValueCode.getCode().replace(DIEZ, DOLAR));

            String itemsDB = itemsAsMyItems.getDb();

            Document commandResult = fmsScriptRunner.runCommand(itemsDB, defaultHistoryValueCode.getCode(),
                    new Document(myForm.getDefaultCurrentQuery()), null);

            Object returnObject = commandResult.get(RETVAL);

            if (returnObject instanceof Document) {
                this.defaultHistoryValue = (ObjectId) ((Document) returnObject).get(MONGO_ID);
            }
        }

        if (this.defaultHistoryValue == null && this.key.equals(myForm.getLoginFkField())) {
            List<ObjectId> list = (List<ObjectId>) myForm.userDetail.getLoginFkSearchMapInListOfValues().get(DOLAR_IN);
            this.defaultHistoryValue = list.get(0);
        }
    }

    public List<PlainRecord> completeMethod(String query) {

        Map search = new HashMap(fmsAutoComplete.getFilter());
        if (query != null && query.length() >= 1) {
            Document queryRexegIgnoreCaseQuery = new Document().append(DOLAR_REGEX, query).append(DOLAR_OPTIONS, "i");
            search.put(itemsAsMyItems.getSearchField(), queryRexegIgnoreCaseQuery);
            itemsAsMyItems.reCreateQuery(loginMemberId, search, crudRecord, fmsAutoComplete.getRoleMap(), fmsScriptRunner);
        }
        Document resultFilter = new Document(search);
        resultFilter.putAll(itemsAsMyItems.getQuery());
        return fmsAutoComplete.completeMethod(resultFilter);
    }

    public void createSelectItems(Map filter, MyMap crudObject, RoleMap roleMap, UserDetail userDetail, boolean ajax) {
        if (this.itemsAsMyItems != null) {
            if (ajax) {
                this.itemsAsMyItems.reCreateQuery(loginMemberId, filter, crudObject, roleMap, fmsScriptRunner);
            }
            this.selectItemsCurrent = fmsAutoComplete.createSelectItems(filter, crudObject);
            this.selectItemsHistory = fmsAutoComplete.createSelectItemsHistory(filter, crudObject);
        }
    }

    public Boolean getLoginFK() {
        return loginFK;
    }

    public String getConverterParam() {
        return converterParam;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public boolean isRenderDesc() {
        return renderDesc;
    }

    public boolean isRenderPopupDesc() {
        return renderPopupDesc;
    }

    public boolean isEmbeddedAsList() {
        return embeddedAsList;
    }

    public boolean isWorkflow() {
        return workflow;
    }

    public void setAutoComplete(FmsAutoComplete autoComplete) {
        this.fmsAutoComplete = autoComplete;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public Map<String, String> getCacheBsonConverter() {
        return cacheBsonConverter;
    }

    public void setCrudRecord(MyMap crudRecord) {
        this.crudRecord = crudRecord;
    }

    public String getLabelStyle() {
        return labelStyle;
    }

    public Boolean getQuickFilter() {
        return quickFilter;
    }

    public MyForm getMyForm() {
        return myForm;
    }

    public void setMyForm(MyForm myForm) {
        this.myForm = myForm;
    }

    public TagAjaxRef getTagAjaxRef() {
        return tagAjaxRef;
    }

    public static class Builder {

        private final MyField myField;
        private MyProject myProject;

        public Builder(Document docField) {
            this.myField = new MyField();
            this.myField.dbo = docField;
        }

        public Builder(ObjectId loginMemberId, MyProject myProject, Document docField, FmsScriptRunner fmsScriptRunner) {

            this.myProject = myProject;
            this.myField = new MyField();
            //
            this.myField.loginMemberId = loginMemberId;
            //
            this.myField.fmsScriptRunner = fmsScriptRunner;
            this.myField._id = (ObjectId) docField.get(MONGO_ID);
            this.myField.code = (String) docField.get(CODE);

            maskCalculate(docField);

            this.myField.dbo = docField;
            this.myField.dateRangeControl = Boolean.TRUE.equals(docField.get(DATE_RANGE_CONTROL));
            this.myField.dateRangeValidate = Boolean.TRUE.equals(docField.get(DATE_RANGE_VALIDATE));
            this.myField.dateRangeBeginKey = (String) docField.get(DATE_RANGE_BEGIN_KEY);
            this.myField.dateRangeEndKey = (String) docField.get(DATE_RANGE_END_KEY);
            this.myField.divider = docField.get(DIVIDER);
            this.myField.disabled = Boolean.TRUE.equals(docField.get(DISABLED));
            this.myField.field = (String) docField.get(FIELD);
            this.myField.fieldNote = (String) docField.get(FIELD_NOTE);
            this.myField.href = (String) docField.get("href");
            this.myField.immediate = Boolean.TRUE.equals(docField.get(IMMEDIATE));
            this.myField.key = (String) docField.get(FORM_KEY);
            this.myField.loginFK = Boolean.TRUE.equals(docField.get("loginFK"));
            this.myField.myFormKey = (String) docField.get(FORM_KEY);
            this.myField.money = Boolean.TRUE.equals(docField.get(MONEY));
            this.myField.maxMoney = (String) docField.get(MAX_MONEY);

            Number number = docField.get(MAX_VALUE, Number.class);
            if (number != null) {
                this.myField.maxValue = number.doubleValue();
            }

            this.myField.mask = (String) docField.get(MASK);
            this.myField.minFractationDigits = docField.get(MIN_FRACTATION_DIGITIS);
            this.myField.maxFractationDigits = docField.get(MAX_FRACTATION_DIGITIS);
            this.myField.name = (String) docField.get(NAME);
            this.myField.observable = docField.get(OBSERVABLE);
            this.myField.observableAttr = docField.get(OBSERVABLE);
            this.myField.observerAttr = docField.get(OBSERVER);
            this.myField.observer = docField.get(OBSERVER);
            this.myField.required = Boolean.TRUE.equals(docField.get(REQUIRED));
            this.myField.roleCheck = Boolean.TRUE.equals(docField.get(ROLECHECK));
            this.myField.reportRendered = Boolean.TRUE.equals(docField.get(REPORT_RENDERED));
            this.myField.refCollection = (String) docField.get(REF_COLLECTION);
            this.myField.subGroup = (String) docField.get(SUB_GROUP);
            this.myField.shouldCheckNegative = Boolean.TRUE.equals(docField.get(SHOULD_CHECK_NEGOTIF));
            this.myField.uysformat = (String) docField.get(FORMAT);
            this.myField.version = Boolean.TRUE.equals(docField.get(VERSION));
            this.myField.valueType = (String) docField.get(VALUE_TYPE);
            this.myField.visible = (String) docField.get(VISIBLE);
            this.myField.valueChangeListenerAction = docField.get(VALUE_CHANGE_LISTENER_ACTION);
            this.myField.workflow = Boolean.TRUE.equals(docField.get(WORKFLOW));
        }

        public Builder maskCalculate(Document docField) {

            Document docCalc = docField.get(CALCULATE, Document.class);
            if (docCalc != null) {
                this.myField.calculate = docCalc.getString(CALCULATE_ACTION);
                this.myField.calculateOnSave = Boolean.TRUE.equals(docCalc.get(CALCULATE_ON_SAVE));
                this.myField.calculateAfterSave = Boolean.TRUE.equals(docCalc.get(CALCULATE_AFTER_SAVE));
                this.myField.calculateAfterDelete = Boolean.TRUE.equals(docCalc.get(CALCULATE_AFTER_DELETE));
                this.myField.calculateOnListView = Boolean.TRUE.equals(docCalc.get(CALCULATE_ON_LIST_VIEW));
                this.myField.calculateOnCrudView = Boolean.TRUE.equals(docCalc.get(CALCULATE_ON_CRUD_VIEW));
                this.myField.calculateOnClient = docCalc.getString(CALCULATE_ON_CLIENT);
                this.myField.converterInstance = docCalc.getString(CONVERTER_INSTANCE);
                this.myField.converterFormat = docCalc.getString(CONVERTER_FORMAT);
                this.myField.converterParam = docCalc.getString(CONVERTER_PARAM);
                this.myField.calculateEngine = docCalc.getString(CALCULATE_ENGINE);//FIXME
            } else {
                this.myField.calculateOnSave = false;
                this.myField.calculateAfterSave = false;
                this.myField.calculateAfterDelete = false;
                this.myField.calculateOnListView = false;
                this.myField.calculateOnCrudView = false;
            }

            return this;
        }

        public Builder maskId() {
            this.myField._id = this.myField.dbo.get(MONGO_ID, ObjectId.class);
            return this;
        }

        public Builder maskField() {
            this.myField.field = (String) this.myField.dbo.get(FIELD);
            return this;
        }

        public Builder maskKey() {
            this.myField.key = (String) this.myField.dbo.get(FORM_KEY);
            return this;
        }

        public Builder maskCode() {
            this.myField.code = (String) this.myField.dbo.get(CODE);
            if (this.myField.code == null || MEASURE.equals(this.myField.dbo.get(ND_TYPE))) {
                this.myField.code = (String) this.myField.dbo.get(FORM_KEY);
            }
            return this;
        }

        public Builder maskSearchAccess() {
            Object searchAccess = this.myField.dbo.get(SEARCH_ACCESS);
            this.myField.searchAccess = !Boolean.FALSE.equals(searchAccess);

            Object quickFilter = this.myField.dbo.get(QUICK_FILTER);
            this.myField.quickFilter = !Boolean.FALSE.equals(quickFilter);
            return this;
        }

        public Builder withRendered(boolean rendered) {
            this.myField.rendered = rendered;
            return this;
        }

        public Builder withReadonly(boolean readOnly) {
            this.myField.readonly = readOnly;
            return this;
        }

        public Builder maskAccesscontrol() {
            StringBuilder accesscontrolSB = new StringBuilder();

            Object objAccessControl = this.myField.dbo.get(ACCESS_CONTROL);

            if (objAccessControl == null) {
                throw new RuntimeException(ACCESS_CONTROL.concat(" = null"));
            }

            if (objAccessControl instanceof List) {
                for (String access : (List<String>) objAccessControl) {
                    accesscontrolSB.append((String) access);
                    accesscontrolSB.append(",");
                }
            } else {
                accesscontrolSB.append((String) this.myField.dbo.get(ACCESS_CONTROL));
            }

            if (this.myProject != null) {
                if (this.myProject.getAdminRole() != null) {
                    String[] roles = this.myProject.getAdminRole().split(COMMA);
                    for (String role : roles) {
                        if (accesscontrolSB.indexOf(role) < 0) {
                            accesscontrolSB.append(COMMA);
                            accesscontrolSB.append(role);
                        }
                    }
                }

                if (this.myProject.getViewerRole() != null) {
                    String[] roles = this.myProject.getViewerRole().split(COMMA);
                    for (String role : roles) {
                        if (accesscontrolSB.indexOf(role) < 0) {
                            accesscontrolSB.append(COMMA);
                            accesscontrolSB.append(role);
                        }
                    }
                }
            }

            this.myField.accesscontrol = accesscontrolSB.toString();
            return this;
        }

        public Builder maskNdTypeAndNdAxis() {
            this.myField.ndType = (String) this.myField.dbo.get(ND_TYPE);
            this.myField.ndAxis = (String) this.myField.dbo.get(ND_AXIS);

            if (this.myField.ndType != null && (!MEASURE.equals(this.myField.ndType)) && this.myField.ndAxis == null) {
                throw new RuntimeException("ndAxis == null");
            }
            return this;
        }

        public Builder maskComponentType() {
            this.myField.componentType = (String) this.myField.dbo.get(COMPONENTTYPE);
            if (this.myField.componentType == null || this.myField.componentType.isEmpty()) {
                this.myField.componentType = "inputText";
            }
            this.myField.isAutoComplete = "autoComplete".equals(this.myField.componentType);
            return this;
        }

        public Builder withDefaultValue(Object defaultValue) {
            this.myField.defaultValue = defaultValue;
            return this;
        }

        public Builder maskItemsAsMyItems(String schemaVersion, Map filter, boolean admin, Set<String> roles)
                throws FormConfigException {

            //if (MyForm.SCHEMA_VERSION_110.equals(schemaVersion)) {
            maskItemsAsMyItemsSchemaVersion110(schemaVersion, filter, admin, roles);
            //} else {
            //  maskItemsAsMyItemsNoScema(schemaVersion, filter, admin, roles);
            //}

            return this;
        }

        private Builder maskItemsAsMyItemsNoScema(String schemaVersion, Map filter, boolean admin, Set<String> roles) {

            Object items = this.myField.dbo.get(ITEMS);
            if (items instanceof Document) {
                this.myField.itemsAsMyItems = new MyItems.Builder(filter, items, myField.fmsScriptRunner)
                        .withQuery(admin)
                        .withSort(roles)
                        .withView(roles)
                        .withHistoryQuery(admin)
                        .withItemType(MyItems.ItemType.doc)
                        .withLookup()
                        .withQueryProjection()
                        .withResultProjection()
                        .withParent(this.myField)
                        .build();
            } else if (items instanceof List) {
                this.myField.itemsAsMyItems = new MyItems.Builder(items)
                        .withItemType(MyItems.ItemType.list)
                        .withList()
                        .withParent(this.myField)
                        .build();
            } else if (items instanceof Code) {
                throw new UnsupportedOperationException("maskItemsAsMyItems.code");
            }

            return this;
        }

        private Builder maskItemsAsMyItemsSchemaVersion110(String schemaVersion, Map filter, boolean admin, Set<String> roles)
                throws FormConfigException {

            try {

                Document itemsDoc = this.myField.dbo.get(ITEMS, Document.class);

                if (itemsDoc == null) {
                    return this;
                }

                if (itemsDoc.get("list") != null) {
                    Object items = itemsDoc.get("list");
                    this.myField.itemsAsMyItems = new MyItems.Builder(items)
                            .withItemType(MyItems.ItemType.list)
                            .withList()
                            .withParent(this.myField)
                            .build();
                } else if (itemsDoc.get("func") != null) {
                    throw new UnsupportedOperationException("maskItemsAsMyItems.code");
                } else if (itemsDoc.get("ref") != null) {
                    Object items = itemsDoc.get("ref");
                    this.myField.itemsAsMyItems = new MyItems.Builder(filter, items, myField.fmsScriptRunner)
                            .withQuerySchemaVersion110(this.myField.loginMemberId, admin)
                            .withSortSchemaVersion110(roles)
                            .withViewSchemaVersion110(roles)
                            .withHistoryQuerySchemaVersion110(this.myField.loginMemberId, admin)
                            .withItemType(MyItems.ItemType.doc)
                            .withLookup()
                            .withQueryProjection()
                            .withResultProjection()
                            .withParent(this.myField)
                            .build();
                }

            } catch (Exception e) {
                throw new FormConfigException("field : " + this.myField.key + " : error in getting field.items<br/><br/> " + e.getLocalizedMessage(), e);
            }

            return this;
        }

        public Builder withConverter(Converter converterValue, MyForm myForm) {

            if (converterValue == null) {
                return this;
            }

            this.myField.converterValue = converterValue;

            if (converterValue.getClass().getSimpleName().equalsIgnoreCase("BsonConverter")) {
                // this.myField.createMyItemsOnSession(null, null, roleMap, this.myField.myForm.userDetail);
                this.myField.viewKey = Arrays.asList("name");
            } else if (converterValue.getClass().getSimpleName().equalsIgnoreCase("SelectOneObjectIdConverter")) {

                if (this.myField.getItemsAsMyItems() != null) {

                    if (MyItems.ItemType.list.equals(this.myField.getItemsAsMyItems().getItemType())) {
                        Map<String, String> itemMap = new HashMap();
                        for (SelectItem selectItem : (Iterable<? extends SelectItem>) this.myField.getItemsAsMyItems().getList()) {
                            itemMap.put(selectItem.getValue().toString(), selectItem.getLabel());
                        }
                        // ((SelectOneObjectIdConverter) this.myField.converterValue).setItemMap(itemMap);
                    }

                    this.myField.viewKey = this.myField.getItemsAsMyItems().getView();

                }

            } else if (converterValue.getClass().getSimpleName().equalsIgnoreCase("TelmanStringConverter")) {
                if (this.myField.getItemsAsMyItems() != null) {
                    throw new RuntimeException(new StringBuilder()
                            .append(this.myField.getKey())
                            .append("<br/>")
                            .append("\"TelmanStringConverter\" converter conflicts with \"items\" attribute.")
                            .append("<br/>")
                            .append("<br/>")
                            .append("acceptable convrters are : ")
                            .append("[")
                            .append("none, ")
                            .append("SelectOneStringConverter, ")
                            .append("SelectOneObjectIdConverter")
                            .append("]").toString());
                }
            }
            return this;
        }

        public Builder maskRestOfThem() {
            if (this.myField.dbo.get(LIST_OF_VALUES) != null) {
                this.myField.listOfValues = (List<SelectItem>) this.myField.dbo.get(LIST_OF_VALUES);
            }
            this.myField.fileLimit = this.myField.dbo.get(FILE_LIMIT) == null ? 1 : ((Number) this.myField.dbo.get(FILE_LIMIT)).intValue();
            if (this.myField.dbo.get(FILE_TYPE) instanceof String) {
                this.myField.fileType = (String) this.myField.dbo.get(FILE_TYPE);
            }
            Object tmpWidth = this.myField.dbo.get("width");
            this.myField.width = tmpWidth == null ? 100 : ((Number) tmpWidth).intValue();
            this.myField.style = (this.myField.dbo.get(STYLE) == null)
                    ? "white-space:nowrap;font-family: monospace;text-align:left;"
                    : "white-space:nowrap;".concat(this.myField.dbo.get(STYLE).toString());
            this.myField.labelStyle = (this.myField.dbo.get(LABEL_STYLE) == null)
                    ? ""
                    : "white-space:nowrap;".concat(this.myField.dbo.get(LABEL_STYLE).toString());
            this.myField.myDatePattern = (this.myField.dbo.get(MY_DATE_PATTERN) instanceof String) ? this.myField.dbo.get(MY_DATE_PATTERN).toString() : "yyyy.MM.dd HH:mm";
            return this;
        }

        public Builder maskDescription() {

            this.myField.description = (String) this.myField.dbo.get(DESCRIPTION);
            this.myField.renderDesc = this.myField.rendered && this.myField.description != null;

            this.myField.popupDesc = (String) this.myField.dbo.get(POPUP_DESCRIPTION);
            this.myField.renderPopupDesc = this.myField.rendered && this.myField.popupDesc != null;

            return this;
        }

        public Builder maskOrders() {

            if (this.myField.dbo.get(ORDER) instanceof Number) {
                this.myField.order = ((Number) this.myField.dbo.get(ORDER)).intValue();
            } else {
                this.myField.order = 0;
            }

            if (this.myField.dbo.get(REPORT_ORDER) instanceof Number) {
                this.myField.reportOrder = ((Number) this.myField.dbo.get(REPORT_ORDER)).intValue();
            } else {
                this.myField.reportOrder = 0;
            }

            return this;
        }

        public Builder maskAjax() {

            Document ajax = this.myField.dbo.get(AJAX, Document.class);
            if (ajax == null) {
                return this;
            }

            this.myField.ajax = Boolean.TRUE.equals(ajax.getBoolean("enable"));
            this.myField.ajaxShowHide = ajax.getString("show-hide");
            this.myField.ajaxAction = ajax.getString(AJAX_ACTION);
            this.myField.ajaxEffectedKeys = ajax.getList(AJAX_EFFECTED_KEYS, String.class);

            Document ajaxRef = ajax.get("ref", Document.class);
            if (ajaxRef != null) {
                this.myField.tagAjaxRef = new TagAjaxRef(ajaxRef);
            }

            return this;
        }

        public Builder maskEmbeddedAsList() {
            this.myField.embeddedAsList = Boolean.TRUE.equals(this.myField.dbo.get("embeddedAsList"));
            return this;
        }

        public Builder cacheBsonConverter(boolean isBsonConverter) {
            if (isBsonConverter) {
                List list = this.myField.getItemsAsMyItems().getList();
                for (Object obj : list) {
                    FmsCodeName fmsCodeName = new FmsCodeName((Map) obj);
                    this.myField.getCacheBsonConverter().put(fmsCodeName.getCode(), fmsCodeName.getName());
                }
            }
            return this;
        }

        public Builder maskAutoset(String schemaVersion, RoleMap roleMap) {
            if (MyForm.SCHEMA_VERSION_110.equals(schemaVersion)) {
                maskAutosetSchemaVersion110(roleMap);
            } else {
                maskAutosetNoSchema();
            }
            return this;
        }

        private void maskAutosetSchemaVersion110(RoleMap roleMap) {

            Document autoset = this.myField.dbo.get(AUTOSET, Document.class);

            if (autoset == null) {
                this.myField.autoset = false;
                return;
            }

            Boolean value = autoset.get(VALUE, Boolean.class);

            if (value == null) {
                List<Document> list = autoset.get("list", List.class);

                Document noRoleDoc = null;
                boolean noRole = true;

                for (Document docRoleValue : list) {
                    List<String> roles = docRoleValue.get("roles", List.class);
                    if (roles == null) {
                        noRoleDoc = docRoleValue;
                    } else if (roleMap.isUserInRole(roles)) {
                        noRole = false;
                        value = docRoleValue.getBoolean(VALUE);
                    }
                }

                if (noRole && noRoleDoc != null) {
                    value = noRoleDoc.getBoolean(VALUE);
                }
            }

            this.myField.autoset = Boolean.TRUE.equals(value);

        }

        private void maskAutosetNoSchema() {
            this.myField.autoset = this.myField.dbo.get(AUTOSET, Boolean.class);
        }

        public Builder maskShortName() {
            this.myField.shortName = (String) this.myField.dbo.get(SHORT_NAME);
            if (this.myField.shortName == null) {
                this.myField.shortName = (String) this.myField.dbo.get(NAME);
            }
            return this;
        }

        public Builder maskName() {
            this.myField.name = (String) this.myField.dbo.get(NAME);
            return this;
        }

        public MyField build() {
            return this.myField;
        }

    }
}
