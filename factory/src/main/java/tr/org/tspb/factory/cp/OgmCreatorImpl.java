package tr.org.tspb.factory.cp;

import htmlflow.StaticHtml;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyProject;
import java.util.Map;
import java.util.Set;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.http.HttpSession;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.pojo.RoleMap;
import org.bson.Document;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import tr.org.tspb.constants.ProjectConstants;
import static tr.org.tspb.constants.ProjectConstants.CFG_TABLE_PROJECT;
import static tr.org.tspb.constants.ProjectConstants.CODE;
import static tr.org.tspb.constants.ProjectConstants.COLLECTION;
import static tr.org.tspb.constants.ProjectConstants.COMMON;
import static tr.org.tspb.constants.ProjectConstants.COMPONENTTYPE;
import static tr.org.tspb.constants.ProjectConstants.CONFIG_DB;
import static tr.org.tspb.constants.ProjectConstants.CONVERTER;
import static tr.org.tspb.constants.ProjectConstants.CONVERTER_BSON_CONVERTER;
import static tr.org.tspb.constants.ProjectConstants.CONVERTER_INTEGER_CONVERTER;
import static tr.org.tspb.constants.ProjectConstants.CONVERTER_JS_FUNCTION_CONVERTER;
import static tr.org.tspb.constants.ProjectConstants.CONVERTER_MONEY_CONVERTER;
import static tr.org.tspb.constants.ProjectConstants.CONVERTER_NUMBER_CONVERTER;
import static tr.org.tspb.constants.ProjectConstants.CONVERTER_SELECT_ONE_OBJECTID_CONVERTER;
import static tr.org.tspb.constants.ProjectConstants.CONVERTER_SELECT_ONE_STRING_CONVERTER;
import static tr.org.tspb.constants.ProjectConstants.CONVERTER_TELMAN_STRING_CONVERTER;
import static tr.org.tspb.constants.ProjectConstants.DEFAULT_VALUE;
import static tr.org.tspb.constants.ProjectConstants.DIEZ;
import static tr.org.tspb.constants.ProjectConstants.DOLAR;
import static tr.org.tspb.constants.ProjectConstants.FIELDS_ROW;
import static tr.org.tspb.constants.ProjectConstants.FORMFIELDS;
import static tr.org.tspb.constants.ProjectConstants.FORMS;
import static tr.org.tspb.constants.ProjectConstants.FORM_DB;
import static tr.org.tspb.constants.ProjectConstants.FORM_FILTER;
import static tr.org.tspb.constants.ProjectConstants.FORM_KEY;
import static tr.org.tspb.constants.ProjectConstants.HAYIR;
import static tr.org.tspb.constants.ProjectConstants.LOGIN_FK;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import static tr.org.tspb.constants.ProjectConstants.MONGO_LDAP_UID;
import static tr.org.tspb.constants.ProjectConstants.MY_CONVERTER;
import static tr.org.tspb.constants.ProjectConstants.NAME;
import static tr.org.tspb.constants.ProjectConstants.ON_USER_ROLE;
import static tr.org.tspb.constants.ProjectConstants.PROJECT_KEY;
import static tr.org.tspb.constants.ProjectConstants.QUERY;
import static tr.org.tspb.constants.ProjectConstants.READONLY;
import static tr.org.tspb.constants.ProjectConstants.RENDERED;
import static tr.org.tspb.constants.ProjectConstants.RETURN_KEY;
import static tr.org.tspb.constants.ProjectConstants.RETVAL;
import static tr.org.tspb.constants.ProjectConstants.UYSDB;
import tr.org.tspb.converter.base.JsFunctionConverter;
import tr.org.tspb.converter.base.BsonConverter;
import tr.org.tspb.converter.base.MoneyConverter;
import tr.org.tspb.converter.base.NumberConverter;
import tr.org.tspb.converter.base.SelectOneObjectIdConverter;
import tr.org.tspb.converter.base.SelectOneStringConverter;
import tr.org.tspb.converter.base.TelmanStringConverter;
import tr.org.tspb.dao.ChildFilter;
import tr.org.tspb.dao.FmsAutoComplete;
import tr.org.tspb.dao.MyActions;
import static tr.org.tspb.dao.MyForm.ION_SETTING_ACTIVITY_STATUS;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.datamodel.expected.FmsRunMongoCmd;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;
import tr.org.tspb.pojo.ComponentType;
import tr.org.tspb.pojo.UserDetail;
import tr.org.tspb.factory.util.OnFlyItems;
import tr.org.tspb.dao.MyFieldComparator;
import tr.org.tspb.dao.TagEventCheckListDoc;
import tr.org.tspb.dao.TagLogin;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.util.tools.MongoDbUtilIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class OgmCreatorImpl implements OgmCreatorIntr {

    private MongoDbUtilIntr mongoDbUtil;
    FmsScriptRunner fmsScriptRunner = new FmsScriptRunner1();
    FmsRunMongoCmd fmsRunMongoCmd = new FmsRunMongoCmd1();
    Map<String, Document> cacheDocumentForm = new HashMap<>();

    public OgmCreatorImpl(MongoDbUtilIntr mongoDbUtil) {
        this.mongoDbUtil = mongoDbUtil;
    }

    private OgmCreatorImpl() {
    }

    private boolean calcRendered(RoleMap roleMap, Document docField, Map searchObject, UserDetail userDetail) {

        Document dboRendered = docField.get(RENDERED, Document.class);

        if (dboRendered == null) {
            return false;
        }

        Boolean booleanValue = dboRendered.getBoolean("boolean-value");
        Document refValue = dboRendered.get("ref-value", Document.class);
        String funcValue = dboRendered.getString("func-value");
        List<Document> checkList = dboRendered.getList("check-list", Document.class);

        if (booleanValue != null) {
            return booleanValue;
        } else if (funcValue != null) {
            funcValue = funcValue.replace(DIEZ, DOLAR);
            if (docField.get(FORM_DB) == null) {
                throw new RuntimeException("field." + docField.get("key") + ".rendered is defined as func. 'db' tag is required.");
            }
            Document commandResult = mongoDbUtil.runCommand(docField.get(FORM_DB).toString(),
                    funcValue, searchObject, roleMap.keySet());
            return Boolean.TRUE.equals(commandResult.get(RETVAL));
        } else if (refValue != null) {

            String onUserRole = (String) refValue.get(ON_USER_ROLE);
            if (onUserRole != null) {
                return roleMap.isUserInRole(onUserRole);
            } else {
                String db = (String) refValue.get(FORM_DB);
                String collection = (String) refValue.get(COLLECTION);
                String returnKey = (String) refValue.get(RETURN_KEY);
                Document query = (Document) refValue.get(QUERY);

                query = mongoDbUtil.replaceToDollar(query);

                Document result = mongoDbUtil.findOne(db, collection, (Document) query);

                if (result == null || (HAYIR.equals(result.get(returnKey)))) {
                    return false;
                }
            }
        } else if (checkList != null) {
            return TagEventCheckListDoc.value(fmsScriptRunner, searchObject, userDetail, roleMap, checkList);
        }

        return false;
    }

    private Object calcDefaultValue(MyProject myProject, Document docForm, Document docField, RoleMap roleMap, Map searchObject,
            ObjectId loginDataBaseUserId) {

        if (docField.get(DEFAULT_VALUE) == null) {
            return null;
        }

        Object defaultValue = docField.get(DEFAULT_VALUE);

        if ("SET_SESSIONID".equals(defaultValue)) {
            defaultValue = ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId();
        } else if (defaultValue instanceof Code) {

            if (roleMap.isUserInRole(myProject.getAdminAndViewerRole())) {
                defaultValue = "no default value is set for admin or viewer role when its a function.";
                return null;
            }

            Code defaultValueFunction = (Code) defaultValue;
            defaultValueFunction = new Code(defaultValueFunction.getCode().replace(DIEZ, DOLAR));

            Map search = new HashMap(searchObject);
            search.put(FORMS, docField.get(FORMS));
            search.put((String) docForm.get(LOGIN_FK), loginDataBaseUserId);

            try {

                if (docField.get(FORM_DB) == null) {
                    throw new Exception("db tag had not been set for field with defaultValue.");
                }

                Document commandResult = mongoDbUtil.runCommand(docField.get(FORM_DB).toString(),
                        defaultValueFunction.getCode(), search, roleMap.keySet());

                Object localDefaultValue = commandResult.get(RETVAL);

                if (localDefaultValue instanceof Document) {
                    defaultValue = ((Document) localDefaultValue).getObjectId(MONGO_ID);
                }

            } catch (Exception ex) {
                String msg = new StringBuilder()
                        .append(docForm.get(FORM_KEY))
                        .append(":")
                        .append(docField.get("key"))
                        .append(ex.getMessage())
                        .toString();
                throw new RuntimeException(msg);
            }
        }

        return defaultValue;
    }

    private boolean calcReadOnly(Document docField, Map searchObject, RoleMap roleMap) {
        Object dboReadonly = docField.get(READONLY);
        if (dboReadonly instanceof Code) {
            Code appearFunction = (Code) dboReadonly;
            appearFunction = new Code(appearFunction.getCode().replace(DIEZ, DOLAR));
            Document commandResult = mongoDbUtil.runCommand(
                    docField.get(FORM_DB).toString(),
                    appearFunction.getCode(),
                    searchObject, roleMap.keySet());
            return Boolean.TRUE.equals(commandResult.get(RETVAL));
        } else {
            return Boolean.TRUE.equals(dboReadonly);
        }
    }

    @Override
    public MyForm getMyFormExternal(MyProject myProject, String collection, Map formSearch, Map searchObject,
            RoleMap roleMap, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException {

        Document dboForm = mongoDbUtil.findOne("configdb", collection, new Document(formSearch));

        if (dboForm == null) {
            throw new NullNotExpectedException("no form regarding to formSearch query");
        }
        try {

            if (MyForm.cacheIonSettingIdCode == null) {
                MyForm.cacheIonSettingIdCode = new HashMap();

                List<Document> cursor = mongoDbUtil.find("iondb", ION_SETTING_ACTIVITY_STATUS);

                for (Document nextElement : cursor) {
                    MyForm.cacheIonSettingIdCode.put(nextElement.get(MONGO_ID).toString(), nextElement.get(CODE).toString());
                }

                cursor = mongoDbUtil.find("iondb", "ion_setting_notify_type");

                for (Document nextElement : cursor) {
                    MyForm.cacheIonSettingIdCode.put(nextElement.get(MONGO_ID).toString(), nextElement.get(CODE).toString());
                }

            }

            Map<String, MyField> fields = createFields(myProject, dboForm, searchObject, roleMap, userDetail);

            List< MyField> fieldsAsList = new ArrayList<>();
            for (MyField field : fields.values()) {
                if (roleMap.isUserInRole(field.getAccesscontrol())) {
                    fieldsAsList.add(field);
                }
            }
            Collections.sort(fieldsAsList, new MyFieldComparator());

            Map<String, MyField> rowFields = createRowFields(myProject, dboForm, searchObject, roleMap, userDetail);

            return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                    .withOthers()
                    .maskFilter()
                    .maskAccesscontrol()
                    .maskReadOnlyNote()
                    .maskUserNote()
                    .maskFields(fields)
                    .withFieldsAsList(fieldsAsList)
                    .maskRowFields(rowFields)
                    .maskFieldsKeySet()
                    .maskCurrentRendered()
                    .maskNotes()
                    .maskUpperNode()
                    .maskDimension()
                    .maskVersionFields()
                    .maskRequiredFields()
                    .maskAutosetFields()
                    .maskUploadMerge()
                    .maskMyRules()
                    .maskMyNotifies()
                    .validateForm()
                    .maskAjax()
                    .validateFields()
                    .maskWorkflowRelation()
                    .build();

        } catch (Exception ex) {
            throw new MongoOrmFailedException(ex);

        }
    }

    @Override
    public MyForm getMyFormXsmall(MyProject myProject, Map searchObject,
            RoleMap roleMap, UserDetail userDetail) throws NullNotExpectedException, MongoOrmFailedException {
        try {

            Document dboForm = cacheAndGetForm(myProject, myProject.getConfigTable(), searchObject);

            return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                    .maskKey()
                    .maskLoginFkField()
                    .maskFilter()
                    .maskDefaultQueries()
                    .maskDimension()
                    .maskFormType()
                    .build();
        } catch (Exception ex) {
            throw new MongoOrmFailedException(ex);
        }
    }

    @Override
    public MyForm getMyFormSmall(MyProject myProject, Document dboForm, Map searchObject,
            RoleMap roleMap, UserDetail userDetail) throws NullNotExpectedException, MongoOrmFailedException {

        try {

            Map<String, MyField> fieldsSmall = createFields(myProject, dboForm, searchObject, roleMap, userDetail);

            return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                    .withOthers()
                    .maskFilter()
                    .maskFields(fieldsSmall)
                    .build();

        } catch (Exception ex) {
            throw new MongoOrmFailedException(ex);
        }
    }

    @Override
    public MyForm getMyFormMedium(MyProject myProject, Document dboForm, Map searchObject,
            RoleMap roleMap, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException {
        try {

            Map<String, MyField> fieldsSmall = createFields(myProject, dboForm, searchObject, roleMap, userDetail);
            List< MyField> fieldsAsList = new ArrayList<>();
            for (MyField field : fieldsSmall.values()) {
                if (roleMap.isUserInRole(field.getAccesscontrol())) {
                    fieldsAsList.add(field);
                }
            }
            Collections.sort(fieldsAsList, new MyFieldComparator());

            return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                    .withOthers()
                    .maskFilter()
                    .maskFields(fieldsSmall)
                    .withFieldsAsList(fieldsAsList)
                    .maskDimension()
                    .maskAutosetFields()
                    .validateFields()
                    .maskFieldsDefaultValues()
                    .build();

        } catch (Exception ex) {
            throw new MongoOrmFailedException(ex);
        }
    }

    @Override
    public MyForm getMyFormLarge(MyProject myProject, String collection, Map formSearch, Map filter,
            RoleMap roleMap, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException {

        Document dboForm = cacheAndGetForm(myProject, collection, formSearch);

        if (dboForm == null) {
            throw new NullNotExpectedException("no form regarding to formSearch query");
        }

        try {
            Map<String, MyField> fields = createFields(myProject, dboForm, filter, roleMap, userDetail);

            Map<String, MyField> rowFields = createRowFields(myProject, dboForm, filter, roleMap, userDetail);

            List< MyField> fieldsAsList = new ArrayList<>();
            for (MyField field : fields.values()) {
                if (roleMap.isUserInRole(field.getAccesscontrol())) {
                    fieldsAsList.add(field);
                }
            }

            Collections.sort(fieldsAsList, new MyFieldComparator());

            List<ChildFilter> childs = new ArrayList<>();

            List<Document> listOfChilds = (List<Document>) dboForm.get("childs");

            if (listOfChilds != null) {
                for (Document child : listOfChilds) {
                    childs.add(new ChildFilter(child));
                }
            }

            return new MyForm.Builder(myProject, dboForm, filter, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                    .withOthers()
                    .maskFilter()
                    .maskZetDimension()
                    .maskAccesscontrol()
                    .maskReadOnlyNote()
                    .maskUserNote()
                    .maskFields(fields)
                    .withFieldsAsList(fieldsAsList)
                    .maskRowFields(rowFields)
                    .maskFieldsKeySet()
                    .maskRowFieldsKeySet()
                    .maskCurrentRendered()
                    .maskNotes()
                    .maskUpperNode()
                    .maskDimension()
                    .maskVersionFields()
                    .maskRequiredFields()
                    .maskAutosetFields()
                    .maskUploadMerge()
                    .maskMyRules()
                    .maskMyNotifies()
                    .validateForm()
                    .maskAjax()
                    .validateFields()
                    .maskWorkflowRelation()
                    .maskChilds(childs)
                    .validate()
                    .build();

        } catch (Exception ex) {
            throw new MongoOrmFailedException(ex);
        }
    }

    private Document cacheAndGetForm(MyProject myProject, String collection, Map formSearch) {
        StringBuilder sb = new StringBuilder();
        sb.append(collection);
        sb.append(" : ");
        sb.append(formSearch);
        String cacheKey = org.apache.commons.codec.digest.DigestUtils.sha256Hex(sb.toString());
        Document dboForm = cacheDocumentForm.get(cacheKey);
        if (dboForm == null) {
            dboForm = mongoDbUtil.findOne(CONFIG_DB, collection, new Document(formSearch));
            if (myProject != null) {
                dboForm.put(PROJECT_KEY, myProject.getKey());
            }
            cacheDocumentForm.put(cacheKey, dboForm);
        }
        return dboForm;
    }

    @Override
    public MyProject getMyProject(Document docProject, TagLogin tagLogin) throws NullNotExpectedException, FormConfigException {

        MyProject myProject = new MyProject(docProject,
                tagLogin.getDb(),
                tagLogin.getTable(),
                tagLogin.getUsernanmeField(),
                tagLogin.getFilter());

        return myProject;
    }

    /**
     *
     * @param projectKey
     * @return
     * @throws tr.org.tspb.exceptions.NullNotExpectedException
     */
    @Override
    public MyProject getMyProject(String projectKey, TagLogin tagLogin) throws NullNotExpectedException, FormConfigException {
        Document project = mongoDbUtil.findOne("configdb", CFG_TABLE_PROJECT, new Document(FORM_KEY, projectKey));

        if (project == null) {
            throw new NullNotExpectedException("The related project has not been found");
        }

        return getMyProject(project, tagLogin);
    }

    @Override
    public MyMap getCrudObject() {
        return new MyMap();
    }

    private Map<String, MyField> createFields(MyProject myProject, Document docForm, Map filter,
            RoleMap roleMap, UserDetail userDetail) throws NullNotExpectedException {

        if (docForm.get(FORMFIELDS) == null) {
            throw new NullNotExpectedException("fields property is resolved to null");
        }

        Map<String, MyField> fields = new HashMap<>();

        if (MyForm.SCHEMA_VERSION_110.equals(docForm.get(MyForm.SCHEMA_VERSION))) {
            for (Document docField : (List<Document>) docForm.get(FORMFIELDS)) {

                Converter converter = createConverter(docForm, docField);

                MyField myField = new MyField.Builder(userDetail.getDbo().getObjectId(), myProject, docField, fmsScriptRunner)
                        .maskAutoset((String) docForm.get(MyForm.SCHEMA_VERSION), roleMap)
                        .maskShortName()
                        .maskCode()
                        .withRendered(calcRendered(roleMap, docField, filter, userDetail))
                        .withReadonly(calcReadOnly(docField, filter, roleMap))
                        .maskAccesscontrol()
                        .maskNdTypeAndNdAxis()
                        .withDefaultValue(calcDefaultValue(myProject, docForm, docField, roleMap, filter, userDetail == null ? null : userDetail.getDbo().getObjectId()))
                        .maskComponentType()
                        .maskItemsAsMyItems((String) docForm.get(MyForm.SCHEMA_VERSION), filter, roleMap.isUserInRole(myProject.getAdminRole()), roleMap.keySet())
                        .withConverter(converter, null)
                        .maskRestOfThem()
                        .maskDescription()
                        .maskOrders()
                        .maskAjax()
                        .maskSearchAccess()
                        .maskEmbeddedAsList()
                        .cacheBsonConverter(converter instanceof BsonConverter)
                        .build();

                FmsAutoComplete autoComplete = new OnFlyItems(myProject, myField, docForm, filter, roleMap, userDetail, mongoDbUtil);

                myField.setAutoComplete(autoComplete);

                fields.put(myField.getKey(), myField);
            }

        } else {
            if (!(docForm.get(FORMFIELDS) instanceof Document)) {
                throw new NullNotExpectedException("fields property is resolved to null");
            }

            Document mongoFields = (Document) docForm.get(FORMFIELDS);

            Set<String> keySet = mongoFields.keySet();

            if (keySet.isEmpty()) {
                throw new NullNotExpectedException("fields property is resolved to null");
            }

            for (String fieldKey : keySet) {

                Document docField = (Document) mongoFields.get(fieldKey);

                Converter converter = createConverter(docForm, docField);

                MyField myField = new MyField.Builder(userDetail.getDbo().getObjectId(), myProject, docField, fmsScriptRunner)
                        .maskAutoset((String) docForm.get(MyForm.SCHEMA_VERSION), roleMap)
                        .maskShortName()
                        .maskCode()
                        .withRendered(calcRendered(roleMap, docField, filter, userDetail))
                        .withReadonly(calcReadOnly(docField, filter, roleMap))
                        .maskAccesscontrol()
                        .maskNdTypeAndNdAxis()
                        .withDefaultValue(calcDefaultValue(myProject, docForm, docField, roleMap, filter, userDetail == null ? null : userDetail.getDbo().getObjectId()))
                        .maskComponentType()
                        .maskItemsAsMyItems((String) docForm.get(MyForm.SCHEMA_VERSION), filter, roleMap.isUserInRole(myProject.getAdminRole()), roleMap.keySet())
                        .withConverter(converter, null)
                        .maskRestOfThem()
                        .maskDescription()
                        .maskOrders()
                        .maskAjax()
                        .maskSearchAccess()
                        .maskEmbeddedAsList()
                        .cacheBsonConverter(converter instanceof BsonConverter)
                        .build();

                FmsAutoComplete autoComplete = new OnFlyItems(myProject, myField, docForm, filter, roleMap, userDetail, mongoDbUtil);

                myField.setAutoComplete(autoComplete);

                fields.put(fieldKey, myField);
            }
        }

        return fields;
    }

    private Map<String, MyField> createRowFields(MyProject myProject, Document docForm,
            Map filter, RoleMap roleMap, UserDetail userDetail) {

        Map<String, MyField> fieldsRow = new HashMap<>();

        if (docForm.get(FIELDS_ROW) instanceof Document) {

            Document mongoFieldsRow = (Document) docForm.get(FIELDS_ROW);

            for (Map.Entry entry : mongoFieldsRow.entrySet()) {

                Document docField = (Document) entry.getValue();

                Converter converter = createConverter(docForm, docField);

                MyField myField = new MyField.Builder(userDetail.getDbo().getObjectId(), myProject, docField, fmsScriptRunner)
                        .maskAutoset((String) docForm.get(MyForm.SCHEMA_VERSION), roleMap)
                        .maskShortName()
                        .maskCode()
                        .withRendered(calcRendered(roleMap, docField, filter, userDetail))
                        .withReadonly(calcReadOnly(docField, filter, roleMap))
                        .maskAccesscontrol()
                        .maskNdTypeAndNdAxis()
                        .withDefaultValue(calcDefaultValue(myProject, docForm, docField, roleMap, filter, userDetail.getDbo().getObjectId()))
                        .maskComponentType()
                        .maskItemsAsMyItems((String) docForm.get(MyForm.SCHEMA_VERSION), filter, roleMap.isUserInRole(myProject.getAdminRole()), roleMap.keySet())
                        .withConverter(converter, null)
                        .maskRestOfThem()
                        .maskDescription()
                        .maskOrders()
                        .maskAjax()
                        .maskSearchAccess()
                        .maskEmbeddedAsList()
                        .build();

                FmsAutoComplete autoComplete = new OnFlyItems(myProject, myField, docForm, filter, roleMap, userDetail, mongoDbUtil);

                myField.setAutoComplete(autoComplete);

                fieldsRow.put(entry.getKey().toString(), myField);
            }
        }

        return fieldsRow;
    }

    private Converter createConverter(Document docForm, Document docField) {
        String converter = (String) docField.get(CONVERTER);

        Converter converterValue = null;

        if ((ComponentType.selectOneMenu.name().equals(docField.get(COMPONENTTYPE))
                || ComponentType.selectManyListbox.name().equals(docField.get(COMPONENTTYPE)))
                && (converter == null)) {

            String html = StaticHtml
                    .view()
                    .html()
                    .head()
                    .__()
                    .body()
                    .div().attrClass("container")
                    //.span().text(this.myField.getMyForm().printToConfigAnalyze(this.myField.getKey())).__()
                    .span().text(docForm.get(NAME)).__()
                    .br().__()
                    .u().text("error:").__()
                    .br().__()
                    .br().__()
                    .span().text("items based componetns require a converter").__()
                    .br().__()
                    .br().__()
                    .span().text("acceptable converters are : [none, SelectOneStringConverter, SelectOneObjectIdConverter]").__()
                    .br().__()
                    .br().__()
                    //.span().text(String.format("cfgdb.%s.update({key:'%s'},{$set:{'fields.%s.converter':'SelectOneStringConverter'}});", this.myField.getMyForm().getMyProject().getConfigTable(), this.myField.getMyForm().getKey(), this.myField.getKey())).__()
                    .span().text(docForm.get(NAME)).__()
                    .br().__()
                    .__()
                    .__()
                    .__()
                    .render();

            throw new RuntimeException(html);
        }

        if (docField.get(MY_CONVERTER) instanceof Converter) {
            converterValue = (Converter) docField.get(MY_CONVERTER);
        }

        if (converter == null && docField.get(MY_CONVERTER) instanceof String) {
            converter = docField.get(MY_CONVERTER).toString();
        }

        if (converter != null && converterValue == null) {
            switch (converter) {
                case CONVERTER_MONEY_CONVERTER:
                    converterValue = new MoneyConverter();
                    break;
                case CONVERTER_NUMBER_CONVERTER:
                case CONVERTER_INTEGER_CONVERTER:
                    converterValue = new NumberConverter();
                    break;
                case CONVERTER_JS_FUNCTION_CONVERTER:
                    converterValue = new JsFunctionConverter();
                    break;
                case CONVERTER_BSON_CONVERTER:
                    converterValue = new BsonConverter();
                    break;
                case CONVERTER_SELECT_ONE_OBJECTID_CONVERTER:
                    converterValue = new SelectOneObjectIdConverter();
                    break;
                case CONVERTER_SELECT_ONE_STRING_CONVERTER:
                    converterValue = new SelectOneStringConverter();
                    break;
                case CONVERTER_TELMAN_STRING_CONVERTER:
                    converterValue = new TelmanStringConverter();
                    break;
            }
        }

        if (docField.get(ProjectConstants.UYSFORMAT) != null) {
            converterValue = new NumberConverter();
        }

        return converterValue;
    }

    @Override
    public MyField getMyField(MyForm myForm, Document docField, Map filter,
            RoleMap roleMap, UserDetail userDetail) {

        Converter converter = createConverter(myForm.getDbo(), docField);

        calcReadOnly(docField, filter, roleMap);

        return new MyField.Builder(userDetail.getDbo().getObjectId(), myForm.getMyProject(), docField, fmsScriptRunner)
                .maskAutoset(myForm.getSchemaVersion(), roleMap)
                .maskShortName()
                .maskCode()
                .withRendered(calcRendered(roleMap, docField, filter, userDetail))
                .withReadonly(calcReadOnly(docField, filter, roleMap))
                .maskAccesscontrol()
                .maskNdTypeAndNdAxis()
                .withDefaultValue(calcDefaultValue(myForm.getMyProject(), myForm.getDbo(), docField, roleMap, filter, null))
                .maskComponentType()
                .maskItemsAsMyItems(myForm.getSchemaVersion(), filter, roleMap.isUserInRole(myForm.getMyProject().getAdminRole()), roleMap.keySet())
                .withConverter(converter, null)
                .maskRestOfThem()
                .maskDescription()
                .maskOrders()
                .maskAjax()
                .maskSearchAccess()
                .maskEmbeddedAsList()
                .build();
    }

    @Override
    public MyField getMyFieldPivot(MyForm myForm, Document docField, Map filter, RoleMap roleMap, UserDetail userDetail) {
        Converter converter = createConverter(myForm.getDbo(), docField);

        calcReadOnly(docField, filter, roleMap);

        return new MyField.Builder(userDetail.getDbo().getObjectId(), myForm.getMyProject(), docField, fmsScriptRunner)
                .maskAutoset(myForm.getSchemaVersion(), roleMap)
                .maskShortName()
                .maskCode()
                .withRendered(calcRendered(roleMap, docField, filter, userDetail))
                .withReadonly(calcReadOnly(docField, filter, roleMap))
                .maskNdTypeAndNdAxis()
                .withDefaultValue(calcDefaultValue(myForm.getMyProject(), myForm.getDbo(), docField, roleMap, filter, null))
                .maskComponentType()
                .maskItemsAsMyItems(myForm.getSchemaVersion(), filter, roleMap.isUserInRole(myForm.getMyProject().getAdminRole()), roleMap.keySet())
                .withConverter(converter, null)
                .maskRestOfThem()
                .maskDescription()
                .maskOrders()
                .maskAjax()
                .maskSearchAccess()
                .maskEmbeddedAsList()
                .build();

    }

    /**
     *
     * @param myFormLarge
     * @param roleMap
     * @param filter
     * @param userDetail
     * @return
     */
    @Override
    public MyActions getMyActions(MyForm myFormLarge, RoleMap roleMap, Document filter, UserDetail userDetail) {

        if (MyForm.SCHEMA_VERSION_100.equals(myFormLarge.getSchemaVersion())
                || MyForm.SCHEMA_VERSION_110.equals(myFormLarge.getSchemaVersion())) {
            return new MyActions.Build(myFormLarge.getMyProject().getViewerRole(), myFormLarge.getDb(),
                    roleMap, filter, myFormLarge.getActions(), fmsScriptRunner, userDetail)
                    .maskMyForm(myFormLarge)
                    .initAsSchemaVersion100()
                    .base()
                    .build();
        }

        return new MyActions.Build(myFormLarge.getMyProject().getViewerRole(), myFormLarge.getDb(),
                roleMap, filter, myFormLarge.getActions(), fmsScriptRunner, userDetail)
                .init()
                .base()
                .build();
    }

    private class FmsScriptRunner1 implements FmsScriptRunner {

        @Override
        public Document uysdbCommonFindOneLdapUID(String ldapUID) {
            return mongoDbUtil.findOne(UYSDB, COMMON, new Document(MONGO_LDAP_UID, ldapUID));
        }

        @Override
        public Document runCommand(String db, String code, Object... args) {
            return mongoDbUtil.runCommand(db, code, args);
        }

        @Override
        public List<Document> uysdbCommonFindSort(Document filter, Document sorter) {
            return mongoDbUtil.find(UYSDB, COMMON, filter, sorter, null);
        }

        @Override
        public Document findOne(String db, String collection, Document query) {
            Document filter = mongoDbUtil.replaceToDollar(query);
            return mongoDbUtil.findOne(db, collection, (Document) filter);
        }

        @Override
        public boolean runActionAsDbTableFilterResult(Document actionDoc, RoleMap roleMap, Map filter) {
            return mongoDbUtil.runActionAsDbTableFilterResult(actionDoc, roleMap, filter);
        }

        @Override
        public Document replaceToDolar(Document document) {
            return mongoDbUtil.replaceToDollar(document);
        }

    }

    private class FmsRunMongoCmd1 implements FmsRunMongoCmd {

        @Override
        public void dynamicButtonExecute(String db, String codeStr, MyMap crudObject) {
            try {
                Document cmdResult = mongoDbUtil.runCommand(db, codeStr, crudObject);
                System.out.println(cmdResult);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                //nothing
            }
        }

        @Override
        public Boolean calcActionValue(String db, String code, Map<String, Serializable> searchObject, Set roleKeySet) {

            Document commandResult = fmsScriptRunner.runCommand(db, code, searchObject, roleKeySet);

            return commandResult.getBoolean(RETVAL);
        }

    }

}
