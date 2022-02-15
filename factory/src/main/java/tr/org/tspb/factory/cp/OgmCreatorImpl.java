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
import static tr.org.tspb.constants.ProjectConstants.FORM_CHILD_FIELDS;
import static tr.org.tspb.constants.ProjectConstants.FORM_DB;
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
import static tr.org.tspb.constants.ProjectConstants.REPLACEABLE_KEY_FMS_VALUE;
import static tr.org.tspb.constants.ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_TYPE;
import static tr.org.tspb.constants.ProjectConstants.REPLACEABLE_KEY_WORD_FOR_THIS_FORM;
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
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.dao.MyActions;
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
            UserDetail userDetail) {

        ObjectId loginDataBaseUserId = (userDetail == null) ? null : userDetail.getDbo().getObjectId();

        if (docField.get(DEFAULT_VALUE) == null) {
            return null;
        }

        Document defaultValue = docField.get(DEFAULT_VALUE, Document.class);

        String stringValue = defaultValue.get("string-value", String.class);
        String funcValue = defaultValue.get("func-value", String.class);
        List<String> listValue = defaultValue.getList("list-value", String.class);

        if (stringValue != null) {
            if ("SET_SESSIONID".equals(defaultValue)) {
                return ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId();
            }
            return stringValue;
        } else if (listValue != null) {
            return listValue;
        }

        if (!FmsForm.SCHEMA_VERSION_111.equals(docForm.getString(FmsForm.SCHEMA_VERSION))) {
            if (funcValue != null) {

                if (roleMap.isUserInRole(myProject.getAdminAndViewerRole())) {
                    return "no default value is set for admin or viewer role when its a function.";
                }

                funcValue = funcValue.replace(DIEZ, DOLAR);

                Map search = new HashMap(searchObject);
                search.put(FORMS, docField.get(FORMS));
                search.put((String) docForm.get(LOGIN_FK), loginDataBaseUserId);

                try {

                    if (docField.get(FORM_DB) == null) {
                        throw new Exception("db tag had not been set for field with defaultValue.");
                    }

                    Document commandResult = mongoDbUtil
                            .runCommand(docField.get(FORM_DB, String.class), funcValue, search, roleMap.keySet());

                    Object localDefaultValue = commandResult.get(RETVAL);

                    if (localDefaultValue instanceof Document) {
                        return ((Document) localDefaultValue).getObjectId(MONGO_ID);
                    }

                    return localDefaultValue;

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
        } else {

            List<Document> listOfRoleBasedValues = defaultValue.getList("list-of-role-based-values", Document.class);

            String value = null;

            Document noRoleDoc = null;
            boolean noRole = true;

            for (Document docRoleValue : listOfRoleBasedValues) {
                List<String> roles = docRoleValue.get("roles", List.class);
                if (roles == null) {
                    noRoleDoc = docRoleValue;
                } else if (roleMap.isUserInRole(roles)) {
                    noRole = false;
                    value = resolveValue(docRoleValue, userDetail, docForm);
                }
            }

            if (noRole && noRoleDoc != null) {
                value = resolveValue(noRoleDoc, userDetail, docForm);
            }

            return value;

        }

        return null;
    }

    private String resolveValue(Document docRoleValue, UserDetail userDetail, Document docForm) {

        Document refValue = docRoleValue.get("ref-value", Document.class);

        if (refValue != null) {
            String db = refValue.getString("db");
            String table = refValue.getString("table");
            String projection = refValue.getString("projection");
            List<Document> queryList = refValue.getList("query", Document.class);

            Document filter = new Document();
            for (Document doc : queryList) {
                String key = doc.getString("key");
                String fmsValue = doc.getString(REPLACEABLE_KEY_FMS_VALUE);
                String strValue = doc.getString("string-value");
                if (fmsValue != null) {
                    switch (fmsValue) {
                        case REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_TYPE:
                            filter.append(key, userDetail.getDbo().getMemberType());
                            break;
                        case REPLACEABLE_KEY_WORD_FOR_THIS_FORM:
                            filter.append(key, docForm.getString("form"));
                            break;
                        default:
                            throw new RuntimeException("not supported value type");
                    }
                } else if (strValue != null) {
                    filter.append(key, strValue);
                }
            }

            return mongoDbUtil.findOne(db, table, filter).getString(projection);

        }

        return null;

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
    public FmsForm getMyFormExternal(MyProject myProject, String collection, Map formSearch, Map searchObject,
            RoleMap roleMap, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException {

        Document dboForm = mongoDbUtil.findOne("configdb", collection, new Document(formSearch));

        if (dboForm == null) {
            throw new NullNotExpectedException("no form regarding to formSearch query");
        }
        try {

            Map<String, MyField> fields = createFields(myProject, dboForm, searchObject, roleMap, userDetail);

            List< MyField> fieldsAsList = new ArrayList<>();
            for (MyField field : fields.values()) {
                if (roleMap.isUserInRole(field.getAccesscontrol())) {
                    fieldsAsList.add(field);
                }
            }
            Collections.sort(fieldsAsList, new MyFieldComparator());

            Map<String, MyField> rowFields = createRowFields(myProject, dboForm, searchObject, roleMap, userDetail);

            String schemaVersion = dboForm.getString(FmsForm.SCHEMA_VERSION);

            if (schemaVersion == null) {
                return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                        .withOthers()
                        .maskMultiUpload()
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
            } else {
                switch (schemaVersion) {
                    case FmsForm.SCHEMA_VERSION_100:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskMultiUpload()
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
                    case FmsForm.SCHEMA_VERSION_110:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskMultiUpload()
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
                    case FmsForm.SCHEMA_VERSION_111:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskMultiUpload()
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
                    default:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskMultiUpload()
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
                }
            }

        } catch (Exception ex) {
            throw new MongoOrmFailedException(ex);

        }
    }

    @Override
    public FmsForm getMyFormXsmall(MyProject myProject, Map searchObject,
            RoleMap roleMap, UserDetail userDetail) throws NullNotExpectedException, MongoOrmFailedException {
        try {

            Document dboForm = cacheAndGetForm(myProject, myProject.getConfigTable(), searchObject);

            String schemaVersion = dboForm.getString(FmsForm.SCHEMA_VERSION);

            if (schemaVersion == null) {
                return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                        .maskKey()
                        .maskLoginFkField()
                        .maskFilter()
                        .maskDefaultQueries()
                        .maskDimension()
                        .maskFormType()
                        .build();
            } else {
                switch (schemaVersion) {
                    case FmsForm.SCHEMA_VERSION_100:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .maskKey()
                                .maskLoginFkField()
                                .maskFilter()
                                .maskDefaultQueries()
                                .maskDimension()
                                .maskFormType()
                                .build();
                    case FmsForm.SCHEMA_VERSION_110:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .maskKey()
                                .maskLoginFkField()
                                .maskFilter()
                                .maskDefaultQueries()
                                .maskDimension()
                                .maskFormType()
                                .build();
                    case FmsForm.SCHEMA_VERSION_111:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .maskKey()
                                .maskLoginFkField()
                                .maskFilter()
                                .maskDefaultQueries()
                                .maskDimension()
                                .maskFormType()
                                .build();
                    default:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .maskKey()
                                .maskLoginFkField()
                                .maskFilter()
                                .maskDefaultQueries()
                                .maskDimension()
                                .maskFormType()
                                .build();
                }
            }

        } catch (Exception ex) {
            throw new MongoOrmFailedException(ex);
        }
    }

    @Override
    public FmsForm getMyFormSmall(MyProject myProject, Document dboForm, Map searchObject,
            RoleMap roleMap, UserDetail userDetail) throws NullNotExpectedException, MongoOrmFailedException {

        try {

            Map<String, MyField> fieldsSmall = createFields(myProject, dboForm, searchObject, roleMap, userDetail);

            String schemaVersion = dboForm.getString(FmsForm.SCHEMA_VERSION);

            if (schemaVersion == null) {
                return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                        .withOthers()
                        .maskFilter()
                        .maskFields(fieldsSmall)
                        .build();
            } else {
                switch (schemaVersion) {
                    case FmsForm.SCHEMA_VERSION_100:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskFilter()
                                .maskFields(fieldsSmall)
                                .build();
                    case FmsForm.SCHEMA_VERSION_110:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskFilter()
                                .maskFields(fieldsSmall)
                                .build();
                    case FmsForm.SCHEMA_VERSION_111:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskFilter()
                                .maskFields(fieldsSmall)
                                .build();
                    default:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskFilter()
                                .maskFields(fieldsSmall)
                                .build();
                }
            }

        } catch (Exception ex) {
            throw new MongoOrmFailedException(ex);
        }
    }

    @Override
    public FmsForm getMyFormMedium(MyProject myProject, Document dboForm, Map searchObject,
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

            String schemaVersion = dboForm.getString(FmsForm.SCHEMA_VERSION);

            if (schemaVersion == null) {
                return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                        .withOthers()
                        .maskMultiUpload()
                        .maskFilter()
                        .maskFields(fieldsSmall)
                        .withFieldsAsList(fieldsAsList)
                        .maskDimension()
                        .maskAutosetFields()
                        .validateFields()
                        .maskFieldsDefaultValues()
                        .build();
            } else {
                switch (schemaVersion) {
                    case FmsForm.SCHEMA_VERSION_100:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskMultiUpload()
                                .maskFilter()
                                .maskFields(fieldsSmall)
                                .withFieldsAsList(fieldsAsList)
                                .maskDimension()
                                .maskAutosetFields()
                                .validateFields()
                                .maskFieldsDefaultValues()
                                .build();
                    case FmsForm.SCHEMA_VERSION_110:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskMultiUpload()
                                .maskFilter()
                                .maskFields(fieldsSmall)
                                .withFieldsAsList(fieldsAsList)
                                .maskDimension()
                                .maskAutosetFields()
                                .validateFields()
                                .maskFieldsDefaultValues()
                                .build();
                    case FmsForm.SCHEMA_VERSION_111:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskMultiUpload()
                                .maskFilter()
                                .maskFields(fieldsSmall)
                                .withFieldsAsList(fieldsAsList)
                                .maskDimension()
                                .maskAutosetFields()
                                .validateFields()
                                .maskFieldsDefaultValues()
                                .build();
                    default:
                        return new MyForm.Builder(myProject, dboForm, searchObject, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskMultiUpload()
                                .maskFilter()
                                .maskFields(fieldsSmall)
                                .withFieldsAsList(fieldsAsList)
                                .maskDimension()
                                .maskAutosetFields()
                                .validateFields()
                                .maskFieldsDefaultValues()
                                .build();
                }
            }

        } catch (Exception ex) {
            throw new MongoOrmFailedException(ex);
        }
    }

    @Override
    public FmsForm getMyFormLarge(MyProject myProject, String collection, Map formSearch, Map filter,
            RoleMap roleMap, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException {

        Document dboForm = cacheAndGetForm(myProject, collection, formSearch);

        if (dboForm == null) {
            throw new NullNotExpectedException("no form regarding to formSearch query");
        }

        try {
            Map<String, MyField> fields = createFields(myProject, dboForm, filter, roleMap, userDetail);

            List<MyField> childFields = createChildFields(myProject, dboForm, filter, roleMap, userDetail);

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

            String schemaVersion = dboForm.getString(FmsForm.SCHEMA_VERSION);

            if (schemaVersion == null) {
                return new MyForm.Builder(myProject, dboForm, filter, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                        .withOthers()
                        .maskMultiUpload()
                        .maskFilter()
                        .maskZetDimension()
                        .maskAccesscontrol()
                        .maskReadOnlyNote()
                        .maskUserNote()
                        .maskFields(fields)
                        .maskChildFields(childFields)
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
            } else {
                switch (schemaVersion) {
                    case FmsForm.SCHEMA_VERSION_100:
                        return new MyForm.Builder(myProject, dboForm, filter, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskMultiUpload()
                                .maskFilter()
                                .maskZetDimension()
                                .maskAccesscontrol()
                                .maskReadOnlyNote()
                                .maskUserNote()
                                .maskChildFields(childFields)
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
                    case FmsForm.SCHEMA_VERSION_110:
                        return new MyForm.Builder(myProject, dboForm, filter, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskMultiUpload()
                                .maskFilter()
                                .maskZetDimension()
                                .maskAccesscontrol()
                                .maskReadOnlyNote()
                                .maskUserNote()
                                .maskChildFields(childFields)
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
                    case FmsForm.SCHEMA_VERSION_111:
                        return new MyForm.Builder(myProject, dboForm, filter, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskMultiUpload()
                                .maskFilter()
                                .maskZetDimension()
                                .maskAccesscontrol()
                                .maskReadOnlyNote()
                                .maskUserNote()
                                .maskChildFields(childFields)
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
                    default:
                        return new MyForm.Builder(myProject, dboForm, filter, roleMap, userDetail, fmsScriptRunner, fmsRunMongoCmd)
                                .withOthers()
                                .maskMultiUpload()
                                .maskFilter()
                                .maskZetDimension()
                                .maskAccesscontrol()
                                .maskReadOnlyNote()
                                .maskUserNote()
                                .maskChildFields(childFields)
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

                }
            }

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
                List<Document> fieldList = dboForm.getList("fields", Document.class);
                if (fieldList != null) {
                    for (Document fieldDoc : fieldList) {
                        String ref = fieldDoc.getString("$fms-ref");
                        if (ref != null) {
                            fieldDoc.putAll(myProject.getJsonSchemaDef().get(ref, Document.class));
                        }
                    }
                }
                Document actionsDoc = dboForm.get("actions", Document.class);
                if (actionsDoc != null) {
                    for (String actionKey : actionsDoc.keySet()) {
                        Document action = actionsDoc.get(actionKey, Document.class);
                        String ref = action.getString("$fms-ref");
                        if (ref != null) {
                            action.putAll(myProject.getJsonSchemaDef().get(ref, Document.class));
                        }
                    }
                }
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

    private List<MyField> createChildFields(MyProject myProject, Document docForm, Map filter,
            RoleMap roleMap, UserDetail userDetail) throws NullNotExpectedException, FormConfigException {

        List<Document> childFields = docForm.getList(FORM_CHILD_FIELDS, Document.class);

        List< MyField> fields = new ArrayList<>();

        if (childFields == null) {
            return fields;
        }

        if (FmsForm.SCHEMA_VERSION_110.equals(docForm.getString(FmsForm.SCHEMA_VERSION))
                || FmsForm.SCHEMA_VERSION_111.equals(docForm.getString(FmsForm.SCHEMA_VERSION))) {

            for (Document docField : childFields) {

                Converter converter = createConverter(docForm, docField);

                MyField myField = new MyField.Builder(userDetail.getDbo().getObjectId(), myProject, docField, fmsScriptRunner)
                        .maskAutoset((String) docForm.get(FmsForm.SCHEMA_VERSION), roleMap)
                        .maskShortName()
                        .maskCode()
                        .withRendered(calcRendered(roleMap, docField, filter, userDetail))
                        .withReadonly(calcReadOnly(docField, filter, roleMap))
                        .maskAccesscontrol()
                        .maskNdTypeAndNdAxis()
                        .withDefaultValue(calcDefaultValue(myProject, docForm, docField, roleMap, filter, userDetail))
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

                fields.add(myField);
            }

        }

        return fields;
    }

    private Map<String, MyField> createFields(MyProject myProject, Document docForm, Map filter,
            RoleMap roleMap, UserDetail userDetail) throws NullNotExpectedException, FormConfigException {

        if (docForm.get(FORMFIELDS) == null) {
            throw new NullNotExpectedException("fields property is resolved to null");
        }

        Map<String, MyField> fields = new HashMap<>();

        if (FmsForm.SCHEMA_VERSION_110.equals(docForm.getString(FmsForm.SCHEMA_VERSION))
                || FmsForm.SCHEMA_VERSION_111.equals(docForm.getString(FmsForm.SCHEMA_VERSION))) {

            for (Document docField : (List<Document>) docForm.get(FORMFIELDS)) {

                Converter converter = createConverter(docForm, docField);

                MyField myField = new MyField.Builder(userDetail.getDbo().getObjectId(), myProject, docField, fmsScriptRunner)
                        .maskAutoset((String) docForm.get(FmsForm.SCHEMA_VERSION), roleMap)
                        .maskShortName()
                        .maskCode()
                        .withRendered(calcRendered(roleMap, docField, filter, userDetail))
                        .withReadonly(calcReadOnly(docField, filter, roleMap))
                        .maskAccesscontrol()
                        .maskNdTypeAndNdAxis()
                        .withDefaultValue(calcDefaultValue(myProject, docForm, docField, roleMap, filter, userDetail))
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
                        .maskAutoset(docForm.getString(FmsForm.SCHEMA_VERSION), roleMap)
                        .maskShortName()
                        .maskCode()
                        .withRendered(calcRendered(roleMap, docField, filter, userDetail))
                        .withReadonly(calcReadOnly(docField, filter, roleMap))
                        .maskAccesscontrol()
                        .maskNdTypeAndNdAxis()
                        .withDefaultValue(calcDefaultValue(myProject, docForm, docField, roleMap, filter, userDetail))
                        .maskComponentType()
                        .maskItemsAsMyItems(docForm.getString(FmsForm.SCHEMA_VERSION), filter, roleMap.isUserInRole(myProject.getAdminRole()), roleMap.keySet())
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
            Map filter, RoleMap roleMap, UserDetail userDetail) throws FormConfigException {

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
                        .withDefaultValue(calcDefaultValue(myProject, docForm, docField, roleMap, filter, userDetail))
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
        return createConverter2(docForm,
                docField.getString(CONVERTER),
                docField.getString(COMPONENTTYPE),
                docField.get(ProjectConstants.UYSFORMAT) != null,
                docField.get(MY_CONVERTER));
    }

    private Converter createConverter2(Document docForm, String converter,
            String componentType, boolean hasUysFormat, Object myConverter) {

        Converter converterValue = null;

        if ((ComponentType.selectOneMenu.name().equals(componentType)
                || ComponentType.selectManyListbox.name().equals(componentType))
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

        if (myConverter instanceof Converter) {
            converterValue = (Converter) myConverter;
        }

        if (converter == null && myConverter instanceof String) {
            converter = myConverter.toString();
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

        if (hasUysFormat) {
            converterValue = new NumberConverter();
        }

        return converterValue;
    }

    @Override
    public MyField getMyField(FmsForm myForm, Document docField, Map filter,
            RoleMap roleMap, UserDetail userDetail) throws FormConfigException {

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
    public MyField getMyFieldPivot(FmsForm myForm, Document docField, Map filter, RoleMap roleMap, UserDetail userDetail)
            throws FormConfigException {
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
    public MyActions getMyActions(FmsForm myFormLarge, RoleMap roleMap, Document filter, UserDetail userDetail) {

        if (FmsForm.SCHEMA_VERSION_100.equals(myFormLarge.getSchemaVersion())
                || FmsForm.SCHEMA_VERSION_110.equals(myFormLarge.getSchemaVersion())
                || FmsForm.SCHEMA_VERSION_111.equals(myFormLarge.getSchemaVersion())) {
            return new MyActions.Build(myFormLarge.getMyProject().getViewerRole(), myFormLarge.getDb(),
                    roleMap, filter, myFormLarge.getActions(), fmsScriptRunner, userDetail)
                    .maskMyForm(myFormLarge)
                    .initAsSchemaVersion100(null)
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
        public long count(String db, String collection, Document query) {
            Document filter = mongoDbUtil.replaceToDollar(query);
            return mongoDbUtil.count(db, collection, (Document) filter);
        }

        @Override
        public boolean runActionAsDbTableFilterResult(Document actionDoc, RoleMap roleMap, Map filter) {
            return mongoDbUtil.runActionAsDbTableFilterResult(actionDoc, roleMap, filter);
        }

        @Override
        public Document replaceToDolar(Document document) {
            return mongoDbUtil.replaceToDollar(document);
        }

        @Override
        public List<ObjectId> findObjectIds(String db, String collection, Document query, String projection) {
            List<Document> docs = mongoDbUtil
                    .findWithProjection(db, collection, query, new Document(projection, Boolean.TRUE));
            if (docs != null) {
                List<ObjectId> list = new ArrayList<>();
                for (Document doc : docs) {
                    list.add(doc.getObjectId(projection));
                }
                return list;
            }
            return null;
        }

        @Override
        public List<Document> aggreagate(String db, String table, List<Document> listOfAggrDoc) {
            return mongoDbUtil.aggregate(db, table, listOfAggrDoc);
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
