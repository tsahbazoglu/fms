package tr.org.tspb.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import static tr.org.tspb.constants.ProjectConstants.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import tr.org.tspb.util.tools.DocumentRecursive;
import tr.org.tspb.util.stereotype.MyServices;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import tr.org.tspb.common.services.AppScopeSrvCtrl;
import tr.org.tspb.common.services.BaseService;
import tr.org.tspb.common.services.LoginController;
import tr.org.tspb.dao.MyActions;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyFile;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyFormXs;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.dao.MyProject;
import tr.org.tspb.dao.TagEvent;
import tr.org.tspb.datamodel.custom.TuikData;
import tr.org.tspb.datamodel.gui.FormItem;
import tr.org.tspb.datamodel.gui.ModuleItem;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.factory.cp.OgmCreatorIntr;
import tr.org.tspb.factory.qualifier.OgmCreatorQualifier;
import tr.org.tspb.pojo.DatabaseUser;
import tr.org.tspb.pojo.PostSaveResult;
import tr.org.tspb.pojo.PreSaveResult;
import tr.org.tspb.pojo.UserDetail;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.tools.MongoDbUtilIntr;
import tr.org.tspb.uys.freedesign.MyLicense;
import tr.org.tspb.uys.freedesign.MyRecord;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyServices
public class RepositoryService implements Serializable {

    @Inject
    @MyLoginQualifier
    LoginController loginController;

    @Inject
    @KeepOpenQualifier
    private MongoDbUtilIntr mongoDbUtil;

    @Inject
    private AppScopeSrvCtrl appScopeSrvCtrl;

    @Inject
    @OgmCreatorQualifier
    private OgmCreatorIntr ogmCreator;

    @Inject
    private FilterService filterService;

    @Inject
    private BaseService baseService;

    private static final String CRUD_OBJECT_ID = "crud_object_id";
    private final Map<String, MyForm> cacheMyFormLarge = new HashMap<>();

    Gson gsonConverter = new Gson();
    Type gsonType = new TypeToken<List<String>>() {
    }.getType();

    public RepositoryService() {
    }

    public Document expandCrudObject(MyForm myForm, Document operatedObject) {

        operatedObject.remove(STATE);//is used later to dect saveable object on logout
        operatedObject.remove("rowSelected");//just to sutisfy the icefaces
        /*
         * Note that by default, the "dot" matches all characters except line
         * break characters. So the .* in the above example will only match to
         * the end of a line.
         *
         * Note that \s matches any whitespace, including line breaks.
         *
         * If line breaks are not significant, then in many types of expression
         * you will need to use the Pattern.DOTALL flag to denote that the dot
         * can match literally any character, including line break characters.
         * As mentioned, \s will match line breaks, so may be more useful in
         * some situations.
         *
         * Without the MULTILINE flag, the ^ and $ now match against the
         * beginning and end of the expression but nothing else.
         *
         *
         */

        Pattern pattern = Pattern.compile("(\\{|\\[).*(\\}|\\])", Pattern.DOTALL);

        for (String key : operatedObject.keySet()) {
            //FIXME Provide Converter

            Object object = operatedObject.get(key);

            if (object instanceof String //
                    && !((String) object).startsWith("function")//
                    && pattern.matcher((CharSequence) object).matches()) {

                if (object.toString().startsWith("[")) {
                    List<String> list = gsonConverter.fromJson(object.toString(), gsonType);
                    operatedObject.put(key, list);
                } else {
                    operatedObject.put(key, Document.parse(object.toString()));
                }

            }
        }

        return operatedObject;
    }

    public PostSaveResult runEventPostSave(Document operatedObject, MyForm myForm, MyMap crudObject) throws MongoOrmFailedException {

        TagEvent tagEvent = myForm.getEventPostSave();

        if (tagEvent != null) {

            switch (tagEvent.getType()) {
                case application:
                    Map calculateAreaSearchMap = new HashMap();
                    Document cacheQueryMap = tagEvent.getCacheQuery();
                    for (String key : cacheQueryMap.keySet()) {
                        calculateAreaSearchMap.put(key, crudObject.get(key));
                    }
                    calculateAreaSearchMap.put(FORM_DB, tagEvent.getDb());
                    calculateAreaSearchMap.put(COLLECTION, tagEvent.getTable());
                    appScopeSrvCtrl.removeApplicationSearchResults(calculateAreaSearchMap);
                    break;
                case showWarnErrPopup:
                    return new PostSaveResult(true, tagEvent.getMsg(), PostSaveResult.MessageGuiType.facesMessage, null);
                default:
                    Document result = mongoDbUtil.trigger(operatedObject, tagEvent, loginController.getRolesAsList());
                    return new PostSaveResult(true, result.get("facesMessage", String.class), PostSaveResult.MessageGuiType.facesMessage, null);
            }
        }
        return PostSaveResult.getNullSingleton();
    }

    public PreSaveResult runEventPreSave(Map query, MyForm myForm, MyMap crudObject) throws MongoOrmFailedException {

        if (myForm.getEventPreSave() == null) {
            return PreSaveResult.getNullSingleton();
        }

        String eventPreSaveDB = myForm.getEventPreSave().getDb();

        if (eventPreSaveDB == null) {
            return PreSaveResult.getErrSingleton();
        }

        Document myCrudObject = new Document(crudObject);
        myCrudObject.remove(INODE);// we remove it bacuase of MyForm class cannot be serialized for mongo.doEval

        String code = myForm.getEventPreSave().getJsFunction();
        Document commandResult = mongoDbUtil.runCommand(eventPreSaveDB, code, query, myCrudObject);
        Object result = commandResult.get(RETVAL);

        if (Boolean.TRUE.equals(result)) {
            return PreSaveResult.getErrSingleton();
        } else if (result instanceof Document) {
            Document resultJSON = (Document) result;
            if ("facesMessage".equals(resultJSON.get("gui"))) {
                String msg = resultJSON.get("facesMessage").toString();
                PreSaveResult.ErrType severity;
                switch (resultJSON.get("facesMessageSeverity").toString()) {
                    case "error":
                        severity = PreSaveResult.ErrType.error;
                        break;
                    case "info":
                        severity = PreSaveResult.ErrType.info;
                        break;
                    case "warn":
                        severity = PreSaveResult.ErrType.warn;
                        break;
                    default:
                        severity = PreSaveResult.ErrType.info;
                        break;
                }
                return new PreSaveResult(true, msg, PreSaveResult.MessageGuiType.facesMessage, severity);
            } else {
                String msg = resultJSON.get("popupMessage").toString();
                return new PreSaveResult(true, msg, PreSaveResult.MessageGuiType.facesMessage, null);
            }
        }
        return PreSaveResult.getNullSingleton();
    }

    public List<DocumentRecursive> findList(MyForm myForm, Document searcheDBObject, Integer limit) {
        return findList(myForm.getDb(), myForm.getTable(), myForm, searcheDBObject, limit);
    }

    public List<DocumentRecursive> findList(String myFormDb, String myFormTable, MyForm myForm, Document searcheDBObject, Integer limit) {
        List<Document> cursor = mongoDbUtil.createCursor(myFormDb, myFormTable, searcheDBObject, limit);
        return cursorToList(cursor, myForm);
    }

    public List<DocumentRecursive> findList(String myFormDb, String myFormTable, MyForm myForm, Bson searcheDBObject, Integer limit) {
        List<Document> cursor = mongoDbUtil.createCursor(myFormDb, myFormTable, searcheDBObject, limit);
        return cursorToList(cursor, myForm);
    }

    public List<DocumentRecursive> cursorToList(List<Document> cursor, MyForm myForm) {
        List<DocumentRecursive> list = new ArrayList<>();

        for (Document dbObject : cursor) {
            Map manualDbRefs = new HashMap();
            armBsonRefs(dbObject, myForm, manualDbRefs);
            list.add(new DocumentRecursive(dbObject, manualDbRefs, mongoDbUtil));
        }
        return list;
    }

    private void armBsonRefs(Document document, MyForm myForm, Map manualDbRefs) {
        Set<String> keySet = new HashSet<>(document.keySet());
        for (String key : keySet) {
            Object keyValue = document.get(key);

            if (keyValue instanceof ObjectId && !MONGO_ID.equals(key)) {
                Map def = new HashMap();
                MyField field = myForm.getField(key);
                if (field != null) {
                    String myDb = field.getItemsAsMyItems().getDb();
                    if (myDb == null) {
                        myDb = myForm.getDb();
                    }
                    String myColl = field.getRefCollection() == null ? field.getItemsAsMyItems().getTable() : field.getRefCollection();
                    def.put(FORM_DB, myDb == null ? myForm.getDb() : myDb);
                    def.put(COLLECTION_NAME, myColl);
                    def.put(MONGO_ID, keyValue);
                    manualDbRefs.put(key, def);
                } else {
                    document.remove(key);
                }
            }
        }
    }

    public List<Map<String, String>> findGridFsFileList(String myCommonRecordID) {
        return findGridFsFileList(new ObjectId(myCommonRecordID));
    }

    public List<Map<String, String>> findGridFsFileList(ObjectId myCommonRecordID) {
        List<Map<String, String>> returnList = new ArrayList<>();

        if (myCommonRecordID != null) {

            List<GridFSDBFile> files = mongoDbUtil.findFiles(baseService.getProperties().getUploadTable(), new BasicDBObject()
                    .append(METADATA.concat(DOT).concat(CRUD_OBJECT_ID), myCommonRecordID));
            for (GridFSDBFile gridFSDBFile : files) {
                Map<String, String> fileInfoPresent = new HashMap<>();
                fileInfoPresent.put(FILE_ID, gridFSDBFile.getId().toString());
                fileInfoPresent.put(FILE_NAME, gridFSDBFile.getFilename());
                fileInfoPresent.put(FILE_SIZE, String.valueOf(gridFSDBFile.getLength()));
                returnList.add(fileInfoPresent);
            }
        }

        List<GridFSDBFile> files = mongoDbUtil.findFiles(baseService.getProperties().getUploadTable(), new BasicDBObject()//
                .append(METADATA.concat(DOT).concat(CRUD_OBJECT_ID), null)//
                .append(METADATA.concat(DOT).concat("username"), loginController.getLoggedUserDetail().getUsername()));

        for (GridFSDBFile gridFSDBFile : files) {
            Map<String, String> fileInfoPresent = new HashMap<>();
            fileInfoPresent.put(FILE_ID, gridFSDBFile.getId().toString());
            fileInfoPresent.put(FILE_NAME, gridFSDBFile.getFilename());
            fileInfoPresent.put(FILE_SIZE, String.valueOf(gridFSDBFile.getLength()));
            //FIXME messagebundle
            fileInfoPresent.put(FILE_STATE, "Bu ek henüz ilişkilendirilmemiş. Günlük Sistem Bakımında otomotik silinecek. İlişkilendirmek için ilgili formda 'Kaydet' tuşuna basınız");
            fileInfoPresent.put(FILE_COLOR, "#ECA0A0");
            returnList.add(fileInfoPresent);
        }
        return returnList;
    }

    public List<Map<String, String>> findGridFsFileList(MyForm myForm) {

        List<Map<String, String>> returnList = new ArrayList<>();

        List<GridFSDBFile> files = mongoDbUtil.findFiles(baseService.getProperties().getUploadTable(), new BasicDBObject()//
                .append(METADATA.concat(DOT).concat("selectFormKey"), myForm.getKey())//
                .append(METADATA.concat(DOT).concat("username"), loginController.getLoggedUserDetail().getUsername()));

        for (GridFSDBFile gridFSDBFile : files) {
            Map<String, String> fileInfoPresent = new HashMap<>();

            fileInfoPresent.put(FILE_ID, gridFSDBFile.getId().toString());
            fileInfoPresent.put(FILE_NAME, gridFSDBFile.getFilename());
            fileInfoPresent.put(FILE_SIZE, String.valueOf(gridFSDBFile.getLength()));
            fileInfoPresent.put(FILE_STATE, gridFSDBFile.getMetaData().get(CRUD_OBJECT_ID) != null ? "" : "Bu ek henüz ilişkilendirilmemiş. Günlük Sistem Bakımında otomotik silinecek. İlişkilendirmek için ilgili formda 'Kaydet' tuşuna basınız");
            fileInfoPresent.put(FILE_COLOR, "#ECA0A0");
            returnList.add(fileInfoPresent);
        }
        return returnList;

    }

    public void writeImimTuik(MyForm myForm, List<TuikData> listOfToBeUpsert) {

        for (TuikData tuikData : listOfToBeUpsert) {

            tuikData.createMeta(
                    (ObjectId) appScopeSrvCtrl.cacheMemberByLdapUID(tuikData.getLdapUID()).get(MONGO_ID),
                    (ObjectId) appScopeSrvCtrl.cachePeriodByValue(((Number) tuikData.getPeriodValue()).intValue()).get(MONGO_ID),
                    myForm.getKey()
            );

            mongoDbUtil.updateMany(myForm.getDb(), myForm.getTable(),
                    tuikData.searchDoc(), tuikData.toDoc(),
                    new UpdateOptions().upsert(true));

        }

    }

    public List<ModuleItem> createModuleList() throws NullNotExpectedException, FormConfigException {
        List<ModuleItem> moduleList = new ArrayList();

        List listOfRoles = new ArrayList();
        for (Object role : loginController.getRolesAsSet()) {
            listOfRoles.add(role);
        }

        Bson filter = Filters.and(
                Filters.elemMatch(ACCESS_CONTROL, Filters.eq(DOLAR_IN, listOfRoles)),
                Filters.eq(TYPE, NODE),
                Filters.eq(ENABLED, true));

        List<Document> projectsCursor = mongoDbUtil.find(CONFIG_DB, CFG_TABLE_PROJECT);

        //add accessible modules in each project
        for (Document project : projectsCursor) {

            MyProject myProject = appScopeSrvCtrl.getProject(project.getString(FORM_KEY), project);

            List<Document> listOfModules = mongoDbUtil.find(CONFIG_DB, myProject.getConfigTable(), filter);

            for (Document moduleDoc : listOfModules) {
                moduleList.add(new ModuleItem(moduleDoc, myProject));
            }
        }

        Collections.sort(moduleList, new Comparator<ModuleItem>() {
            @Override
            public int compare(ModuleItem t1, ModuleItem t2) {
                return Integer.compare(t1.getMenuOrder(), t2.getMenuOrder());
            }
        });
        return moduleList;

    }

    public List<FormItem> findModuleForms(ModuleItem moduleItem) {

        Document query = new Document()
                .append(UPPER_NODES.concat(DOT).concat(moduleItem.getModuleKey()), new Document(DOLAR_EXISTS, true))
                .append(ACCESS_CONTROL, new Document(DOLAR_IN, loginController.getRolesAsList()));

        List<Document> documents = mongoDbUtil.find(CONFIG_DB, moduleItem.getProject().getConfigTable(), query);

        List<FormItem> formItems = new ArrayList<>();

        for (Document dBObject : documents) {

            Object formDisabled = dBObject.get(DISABLED);

            if (Boolean.TRUE.equals(formDisabled)) {
                continue;
            }

            if (formDisabled instanceof Document && !loginController.isUserInRole(((Document) formDisabled).getString(NOT_ON_USER_ROLE))) {
                continue;
            }

            formItems.add(new FormItem(dBObject));
        }

        return formItems;

    }

    public List<FormItem> findModuleFormsSchemaVersion110(ModuleItem moduleItem) {

        Bson filter = Filters.and(Filters.eq(MyForm.SCHEMA_VERSION, MyForm.SCHEMA_VERSION_110),
                Filters.eq(UPPER_NODES, moduleItem.getModuleKey()),
                Filters.eq(ACCESS_CONTROL, new Document(DOLAR_IN, loginController.getRolesAsList())));

        List<Document> documents = mongoDbUtil.find(CONFIG_DB, moduleItem.getProject().getConfigTable(), filter);

        List<FormItem> formItems = new ArrayList<>();

        for (Document dBObject : documents) {

            Object formDisabled = dBObject.get(DISABLED);

            if (Boolean.TRUE.equals(formDisabled)) {
                continue;
            }

            if (formDisabled instanceof Document && !loginController.isUserInRole(((Document) formDisabled).getString(NOT_ON_USER_ROLE))) {
                continue;
            }

            formItems.add(new FormItem(dBObject));
        }

        return formItems;

    }

    public ModuleItem getModuleItem(String configTable, String upperNodeKey) {

        Document mongoModuleForm = mongoDbUtil.findOne(CONFIG_DB, configTable, new Document(FORM_KEY, upperNodeKey));
        return new ModuleItem(mongoModuleForm);

    }

    public String createWelcomePage(String welcomepage) {

        String aboutContent = "UNDER CONSTRUCT";

        /*
        docker cp UNDER_CONSTRUCTION_WELCOME_PAGE fms-mongodb:/home/fms
        docker cp welcome-anket-masak-2 fms-mongodb:/home/fms
        
        mongofiles -d=welcomedb list
        
        mongofiles -d=welcomedb delete UNDER_CONSTRUCTION_WELCOME_PAGE
        mongofiles -d=welcomedb put UNDER_CONSTRUCTION_WELCOME_PAGE
  
        
        mongofiles -d=welcomedb delete welcome-anket-masak-2
        mongofiles -d=welcomedb put welcome-anket-masak-2
  
         */
        List<GridFSDBFile> gridFSDBFiles = mongoDbUtil.findFiles("welcomedb", welcomepage);

        if (gridFSDBFiles.size() > 0) {
            InputStream attachmentStream = gridFSDBFiles.get(0).getInputStream();

            StringBuilder textBuilder = new StringBuilder();

            try (Reader reader = new BufferedReader(new InputStreamReader(attachmentStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            } catch (Exception ex) {

            }
            aboutContent = textBuilder.toString();
        }
        return aboutContent;

    }

    public DatabaseUser getDatabaseUser(MyProject myProject, ModuleItem moduleItem, String username) {

        String loginDB = myProject.getLoginDetailDb();
        String loginTable = myProject.getLoginDetailTable();
        String usernameField = myProject.getLoginDetailLdapUid();

        Document memberDbo = mongoDbUtil.findOne(loginDB, loginTable,
                new Document(usernameField, loginController.getLoggedUserDetail().getUsername()));

        if (memberDbo == null) {
            throw new RuntimeException(String.format("no detail info regarding to logged user",
                    loginController.getLoggedUserDetail().getUsername(), moduleItem.getName()));
        }

        return new DatabaseUser(memberDbo, usernameField);

    }

    public MyFormXs getMyFormXs(MyProject myProject, String formKey)
            throws NullNotExpectedException, MongoOrmFailedException {

        StringBuilder sb = new StringBuilder();
        sb.append("xs:");
        sb.append(myProject.getKey());
        sb.append(":");
        sb.append(formKey);
        sb.append(":");
        sb.append(loginController.getRoleMap());
        sb.append(":");
        sb.append(loginController.getLoggedUserDetail());

        String cacheKey = org.apache.commons.codec.digest.DigestUtils.sha256Hex(sb.toString());

        MyForm myForm = cacheMyFormLarge.get(cacheKey);

        if (myForm == null) {
            myForm = ogmCreator.getMyFormXsmall(myProject, new Document(FORM_KEY, formKey),
                    loginController.getRoleMap(), loginController.getLoggedUserDetail());
            cacheMyFormLarge.put(cacheKey, myForm);
        }

        return myForm;

    }

    public MyProject getMyProject(String projectKey) throws NullNotExpectedException, FormConfigException {
        return ogmCreator
                .getMyProject(mongoDbUtil.findOne(CONFIG_DB, CFG_TABLE_PROJECT, new Document(FORM_KEY, projectKey)));
    }

    public MyForm getMyFormLarge(MyProject myProject, String formKey)
            throws NullNotExpectedException, MongoOrmFailedException {

        MyForm myForm = cacheMyFormLarge.get(formKey);
        if (myForm == null) {
            myForm = ogmCreator.getMyFormLarge(myProject,
                    myProject.getConfigTable(),
                    new Document(FORM_KEY, formKey),
                    new Document(),
                    loginController.getRoleMap(),
                    loginController.getLoggedUserDetail());
            cacheMyFormLarge.put(formKey, myForm);
        }
        return myForm;
    }

    public MyForm getMyFormLargeWithTableFilter(MyProject myProject, String formKey)
            throws NullNotExpectedException, MongoOrmFailedException {

        return ogmCreator.getMyFormLarge(myProject,
                myProject.getConfigTable(),
                new Document(FORM_KEY, formKey),
                filterService.getTableFilterCurrent(),
                loginController.getRoleMap(),
                loginController.getLoggedUserDetail());

    }

    public MyForm getMyFormLargeWithBaseFilter(MyProject myProject, String formKey)
            throws NullNotExpectedException, MongoOrmFailedException {

        StringBuilder sb = new StringBuilder();
        sb.append("large+basefilter:");
        sb.append(myProject.getKey());
        sb.append(":");
        sb.append(formKey);
        sb.append(":");
        sb.append(filterService.getBaseFilterCurrent());
        sb.append(":");
        sb.append(loginController.getRoleMap());
        sb.append(":");
        sb.append(loginController.getLoggedUserDetail());

        String cacheKey = org.apache.commons.codec.digest.DigestUtils.sha256Hex(sb.toString());

        //MyForm myForm = cacheMyFormLarge.get(cacheKey);
        //   if (myForm == null) {
        MyForm myForm = ogmCreator.getMyFormLarge(myProject,
                myProject.getConfigTable(),
                new Document(FORM_KEY, formKey),
                filterService.getBaseFilterCurrent(),
                loginController.getRoleMap(),
                loginController.getLoggedUserDetail());
        // cacheMyFormLarge.put(cacheKey, myForm);
        // }
        return myForm;
    }

    public Map getRecord(String db, String table, Object showDetailObject) {
        Document record = mongoDbUtil.findOne(db, table, new Document(MONGO_ID, showDetailObject));
        return record;
    }

    public MyActions getAndCacheMyAction(MyForm myFormLarge) {
        String cacheKey = createCachActionsKey(myFormLarge);
        MyActions myActions = appScopeSrvCtrl.getCacheActions(cacheKey);
        // if (myActions == null) {
        myActions = ogmCreator
                .getMyActions(myFormLarge, loginController.getRoleMap(), filterService.getBaseFilterCurrent(), loginController.getLoggedUserDetail());
        appScopeSrvCtrl.putCacheActions(cacheKey, myActions);
        // }
        return myActions;
    }

    private String createCachActionsKey(MyForm myFormLarge) {
        StringBuilder sb = new StringBuilder();
        sb.append(myFormLarge.getKey());
        sb.append(" : ");
        sb.append(loginController.getRoleMap());
        sb.append(" : ");
        sb.append(filterService.getTableFilterCurrent());
        String cacheKey = org.apache.commons.codec.digest.DigestUtils.sha256Hex(sb.toString());
        return cacheKey;
    }

    public void updateMany(String db, String table, Map searchMap, Map<String, Object> record, boolean b) {

        mongoDbUtil.updateMany(db, table,
                new Document(searchMap),
                new Document(record),
                new UpdateOptions().upsert(true));

    }

    public void updateMany(String db, String table, Map searchMap, Map<String, Object> record) {

        mongoDbUtil.updateMany(db, table,
                new Document(searchMap),
                new Document(record));

    }

    public Map oneAsUserOrDefault(String db, String table, Map<String, Object> searchMap) {
        Map map = oneAsUser(db, table, searchMap);
        if (map == null) {
            map = new HashMap(searchMap);
            map.put(DEFAULT_LOGIN_FK_FILED_NAME, loginController.getLoggedUserDetail().getDbo().getObjectId());
        }
        return map;
    }

    public Map oneAsUser(String db, String table, Map<String, Object> searchMap) {
        Map dBObject = new HashMap(searchMap);
        dBObject.put(FORM, table);
        dBObject.put(DEFAULT_LOGIN_FK_FILED_NAME, loginController.getLoggedUserDetail().getDbo().getObjectId());

        return mongoDbUtil.findOne(db, table, new Document(dBObject));
    }

    public Map one(String db, String table, Map<String, Object> searchMap) {
        return mongoDbUtil.findOne(db, table, new Document(searchMap));
    }

    public long count(String db, String table, Map<String, Object> searchMap) {
        return mongoDbUtil.count(db, table, new Document(searchMap));
    }

    public Map oneById(String anketdb, String anket_masak, String idStr) {
        Map searchMap = new HashMap();
        searchMap.put(MONGO_ID, new ObjectId(idStr));
        return mongoDbUtil.findOne(anketdb, anket_masak, new Document(searchMap));
    }

    public List<Map> list(String db, String coll, Map query) {
        List<Document> documents = mongoDbUtil.find(db, coll, new Document(query));
        List<Map> documentsAsMap = new ArrayList<>();
        for (Document document : documents) {
            documentsAsMap.add(document);
        }
        return documentsAsMap;
    }

    private final static String DEFAULT_LOGIN_FK_FILED_NAME = "member";

    public List<Map> listAsUser(MyForm selectedForm, Map searchMap) {

        Map dBObject = new HashMap(searchMap);
        dBObject.put(FORM, selectedForm.getTable());
        dBObject.put(DEFAULT_LOGIN_FK_FILED_NAME, loginController.getLoggedUserDetail().getDbo().getObjectId());

        List<Document> documents = mongoDbUtil.find(selectedForm.getDb(), selectedForm.getTable(), new Document(dBObject));

        List<Map> documentsAsMap = new ArrayList<>();
        for (Document document : documents) {
            documentsAsMap.add(document);
        }

        return documentsAsMap;

    }

    public List<Map> listAsLoggedUser(MyForm selectedForm) {

        Map dBObject = new HashMap();
        dBObject.put(FORM, selectedForm.getTable());
        dBObject.put(DEFAULT_LOGIN_FK_FILED_NAME, loginController.getLoggedUserDetail().getDbo().getObjectId());

        List<Document> documents = mongoDbUtil.find(selectedForm.getDb(), selectedForm.getTable(), new Document(dBObject));
        List<Map> documentsAsMap = new ArrayList<>();
        for (Document document : documents) {
            documentsAsMap.add(document);
        }

        return documentsAsMap;

    }

    public List<Map> findSkipLimitAsLoggedUser(String db, String table, Map searchedMap, int startRow, int maxResults) {

        UserDetail userDetail = loginController.getLoggedUserDetail();

        //we are not ensure who wich function will call this function and need to secure it from unauthorized calls
        if (!loginController.isUserInRole("ionadmin")) {
            searchedMap.put(DEFAULT_LOGIN_FK_FILED_NAME, userDetail.getDbo().getObjectId());
        }
        List<Document> documents = mongoDbUtil
                .findSkipLimit(db, table, new Document(searchedMap), startRow, maxResults);

        List<Map> documentsAsMap = new ArrayList<>();
        for (Document document : documents) {
            documentsAsMap.add(document);
        }

        return documentsAsMap;

    }

    public void removeFileById(String toBeDeletedFileID) {
        mongoDbUtil.removeFile(baseService.getProperties().getUploadTable(), new ObjectId(toBeDeletedFileID));
    }
    public static final String JOIN_ID = "joinId";

    public void updateManyMyLicenseAsUser(MyForm myForm, Map searchForUpdate, MyLicense myLicense, int joinId) {

        ObjectId memberId = loginController.getLoggedUserDetail().getDbo().getObjectId();

        searchForUpdate.put(DEFAULT_LOGIN_FK_FILED_NAME, memberId);

        if (myLicense.getAsDBObject().get(MONGO_ID) != null) {
            mongoDbUtil
                    .updateMany(myForm, new Document(searchForUpdate), myLicense.getAsDBObject().append(JOIN_ID, joinId));
        } else {
            mongoDbUtil
                    .updateMany(myForm, new Document(searchForUpdate), myLicense.getAsDBObject().append(JOIN_ID, joinId),
                            new UpdateOptions().upsert(true));
        }
    }

    public void updateManyMyRecordAsUser(MyForm myForm, Map searchForUpdate, MyRecord myRecord, int joinId) {

        ObjectId memberId = loginController.getLoggedUserDetail().getDbo().getObjectId();

        searchForUpdate.put(DEFAULT_LOGIN_FK_FILED_NAME, memberId);

        if (myRecord.getCrudMap().get(MONGO_ID) != null) {
            mongoDbUtil.updateMany(myForm, new Document(searchForUpdate), myRecord.getAsDBObject().append(JOIN_ID, joinId));
        } else {
            mongoDbUtil
                    .updateMany(myForm, new Document(searchForUpdate), myRecord.getAsDBObject().append(JOIN_ID, joinId), new UpdateOptions().upsert(true));
        }

    }

    public void createFile(MyForm selectedForm, MyRecord myRecord) throws IOException {

        DBObject metadata = new BasicDBObject();
        // null points to the fact that this record(file) is not related yet.
        // we will use this NULL state during the search over all other forms
        metadata.put("crud_object_id", null);
        metadata.put("selectFormKey", selectedForm.getKey());
        metadata.put("selectFormName", selectedForm.getName());
        metadata.put("username", loginController.getLoggedUserDetail().getUsername());

        GridFSInputFile gridFSInputFile = mongoDbUtil
                .createFile(baseService.getProperties().getUploadTable(), myRecord.getUploadedFile().getInputstream());

        gridFSInputFile.setFilename(myRecord.getUploadedFile().getFileName());
        gridFSInputFile.setMetaData(metadata);
        gridFSInputFile.save();

    }

    public MyFile findFileAsMyFileInputStream(String ion_uploaded_files, String objectID) throws IOException {
        return mongoDbUtil.findFileAsMyFileInputStream(baseService.getProperties().getUploadTable(), new ObjectId(objectID));
    }

    public MyFile findFileAsMyFile(String ion_uploaded_files, String objectID) throws IOException {
        return mongoDbUtil.findFileAsMyFile(baseService.getProperties().getUploadTable(), new ObjectId(objectID));
    }

}
