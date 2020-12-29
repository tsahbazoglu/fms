package tr.org.tspb.util.tools;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import static tr.org.tspb.constants.ProjectConstants.COLLECTION_NAME;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_SET;
import static tr.org.tspb.constants.ProjectConstants.FORM_DB;
import static tr.org.tspb.constants.ProjectConstants.ITEMS;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import static tr.org.tspb.constants.ProjectConstants.NAME;
import tr.org.tspb.exceptions.NullNotExpectedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import tr.org.tspb.codec.MyBaseRecordCodec;
import tr.org.tspb.codec.NullRecordCodec;
import tr.org.tspb.constants.ProjectConstants;
import static tr.org.tspb.constants.ProjectConstants.DIEZ;
import static tr.org.tspb.constants.ProjectConstants.DOLAR;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_UNSET;
import static tr.org.tspb.constants.ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_TYPE;
import static tr.org.tspb.constants.ProjectConstants.RETVAL;
import tr.org.tspb.dao.MyBaseRecord;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyFile;
import tr.org.tspb.dao.MyFileNoContent;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyItems;
import tr.org.tspb.dao.MyLookup;
import tr.org.tspb.dao.refs.PlainRecord;
import tr.org.tspb.pojo.RoleMap;
import tr.org.tspb.dao.FmsFile;
import tr.org.tspb.dao.TagEvent;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MongoDbUtilImplKeepOpen implements MongoDbUtilIntr {

    @Inject
    private Logger logger;

    public static final String RW_REFERANS = "referans";
    public static final String RW_REPORT = "report";
    public static final String RW_SORT = "sort";
    public static final String RW_USERS = "users";
    private static MongoCredential mongoCredential;
    private static MongoClientOptions mongoClientOptions;

    public String mongoAdminUser = "to be set on server jndi prop";
    public String mongoAdminPswd = "to be set on server jndi prop";
    public ServerAddress serverAddress = new ServerAddress();

    private MongoClient mongoClient;

    public MongoDbUtilImplKeepOpen(String mongoAdminUser, String mongoAdminPswd, ServerAddress serverAddress) {
        this.mongoAdminUser = mongoAdminUser;
        this.mongoAdminPswd = mongoAdminPswd;
        this.serverAddress = serverAddress;
        mongoClient = connect();
    }

    private MongoClientOptions getMongoClientOptions() {
        if (mongoClientOptions == null) {

            CodecRegistry defaultRegistry = MongoClient.getDefaultCodecRegistry();
            CodecRegistry myBaseRecordRregistry = CodecRegistries.fromCodecs(new MyBaseRecordCodec());
            CodecRegistry nullRecordRregistry = CodecRegistries.fromCodecs(new NullRecordCodec());
            CodecRegistry allInOne = CodecRegistries.fromRegistries(defaultRegistry, myBaseRecordRregistry, nullRecordRregistry);

            MongoClientOptions.Builder optionBuilder = MongoClientOptions.builder()
                    // .maxConnectionIdleTime(10000)
                    .writeConcern(WriteConcern.JOURNALED)
                    .codecRegistry(allInOne);
            mongoClientOptions = optionBuilder.build();
        }
        return mongoClientOptions;
    }

    private MongoCredential getMongoCredential() {
        if (mongoCredential == null) {
            mongoCredential = MongoCredential.createCredential(mongoAdminUser, "admin", mongoAdminPswd.toCharArray());
        }
        return mongoCredential;
    }

    private MongoClient connect() {
        //MongoClient mongo = new MongoClient(serverAddress, getMongoCredential(), getMongoClientOptions());
        MongoClient mongo = new MongoClient(serverAddress, getMongoClientOptions());
        return mongo;
    }

    private static void close(MongoClient mongoClient) {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    private static boolean isThereTable(MongoClient mongoClient, String db, String collectionName) {
        boolean result = false;
        MongoIterable<String> collection = mongoClient.getDatabase(db).listCollectionNames();
        for (String s : collection) {
            if (s.equals(collectionName)) {
                result = true;
            }
        }
        return result;
    }

    public void dropTable(String UYSDB, String tbbBankCollection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void deleteOne(String database, String collectionName, Document document) {
        mongoClient.getDatabase(database).getCollection(collectionName).deleteOne(document);
    }

    public List<Document> aggregate(String db, String table, List<Document> aggregateList) {

        AggregateIterable<Document> output = mongoClient.getDatabase(db).getCollection(table)
                .aggregate(aggregateList);

        List<Document> list = new ArrayList<>();//188788 record heap space
        for (Document doc : output) {
            list.add(doc);
        }

        return list;

    }

    public MongoCollection getCollection(String db, String collectionName, Document indexObject, boolean unique) {

        MongoCollection dbCollection;

        if (!isThereTable(mongoClient, db, collectionName)) {
            mongoClient.getDatabase(db).createCollection(collectionName);
        }

        dbCollection = mongoClient.getDatabase(db).getCollection(collectionName);

        if (indexObject != null) {
            Document index = new Document();
            for (String key : indexObject.keySet()) {
                index.put(key, 1);
            }
            IndexOptions indexOptions = new IndexOptions().unique(true);
            dbCollection.createIndex(index, indexOptions);
        }
        return dbCollection;
    }

    public void createIndex(MyItems myItems) {
        createIndex(myItems.getDb(), myItems.getTable(), myItems.getSort());
    }

    public void createIndex(MyForm myForm, Document indexObject) {
        createIndex(myForm.getDb(), myForm.getTable(), indexObject);
    }

    public void createIndex(String db, String collectionName, Document indexObject) {
        ListIndexesIterable<Document> indexes = mongoClient.getDatabase(db).getCollection(collectionName).listIndexes();

        // there is no predefined function on mongodb driver to prevent 
        // re-create of an existing index with defferent index options like unique
        // so we proivde it with custom coding
        boolean shouldCreate = true;
        for (Document index : indexes) {
            shouldCreate = shouldCreate && compareIndexes(index, indexObject);
        }

        if (shouldCreate) {
            mongoClient.getDatabase(db).getCollection(collectionName).createIndex(indexObject);
        }
    }

    private boolean compareIndexes(Document index1, Document index2) {

        if (index1.isEmpty()) {
            return false;
        }

        Set<String> index1_key_keys = ((Map) index1.get("key")).keySet();
        Set<String> index2_keys = index2.keySet();

        return index1_key_keys.retainAll(index2_keys);

    }

    public void createIndexUnique(MyForm myForm, Document indexObject) {
        createIndexUnique(myForm.getDb(), myForm.getTable(), indexObject);
    }

    public void createIndexUnique(String db, String collectionName, Document indexObject) {
        mongoClient.getDatabase(db).getCollection(collectionName).createIndex(indexObject, new IndexOptions().unique(true));
    }

    public void remove(String db, String collectionName, Map<String, Object> searchMap) throws RuntimeException {
        mongoClient.getDatabase(db).getCollection(collectionName, null).deleteMany(new Document(searchMap));
    }

    public void removeFile(String db, DBObject filter) throws RuntimeException {
        new GridFS(mongoClient.getDB(db)).remove(filter);
    }

    public void removeFile(String db, ObjectId objectId) throws RuntimeException {
        new GridFS(mongoClient.getDB(db)).remove(objectId);
    }

    public GridFSDBFile findFile(String db, ObjectId objectId) throws RuntimeException {
        GridFSDBFile gridFSDBFile = new GridFS(mongoClient.getDB(db)).find(objectId);
        return gridFSDBFile;
    }

    public MyFile findFileAsMyFile(String db, ObjectId objectId) throws IOException {
        GridFSDBFile gridFSDBFile = new GridFS(mongoClient.getDB(db)).find(objectId);
        MyFile myFile = new MyFile(gridFSDBFile).withBytes();
        return myFile;
    }

    public MyFile findFileAsMyFileInputStream(String db, ObjectId objectId) throws IOException {
        GridFSDBFile gridFSDBFile = new GridFS(mongoClient.getDB(db)).find(objectId);
        MyFile myFile = new MyFile(gridFSDBFile);
        return myFile;
    }

    public void copyFiles(String fromDb, String toDb, DBObject fromSearch) throws IOException {
        List<GridFSDBFile> list = new GridFS(mongoClient.getDB(fromDb)).find(fromSearch);
        for (GridFSDBFile fileIonUploadedFile : list) {
            String sha256 = DigestUtils.sha256Hex(fileIonUploadedFile.getInputStream());
            GridFSInputFile gridFSInputFileAttachment = createFile(toDb, fileIonUploadedFile.getInputStream());
            gridFSInputFileAttachment.setFilename(fileIonUploadedFile.getFilename());
            gridFSInputFileAttachment.setMetaData(new BasicDBObject(fileIonUploadedFile.getMetaData().toMap()).append("sha256", sha256));
            gridFSInputFileAttachment.save();
        }
    }

    public GridFSDBFile findFile(String db, DBObject filter) throws RuntimeException {
        GridFSDBFile gridFSDBFile = new GridFS(mongoClient.getDB(db)).findOne(filter);
        return gridFSDBFile;
    }

    @Override
    public List<Map<String, Object>> find(MyForm myForm, String collectionName,
            Map<String, Object> searchMap,
            Map<String, Object> returnMap,
            int skip,
            int limit,
            Map<String, Object> sortMap,
            String searchPrefix) throws NullNotExpectedException {

        List<Map<String, Object>> listMaps = new ArrayList<>();

        if (!isThereTable(mongoClient, myForm.getDb(), collectionName)) {
            return listMaps;//Collections.emptyList();
        }

        Document returnObject = returnMap == null ? new Document() : new Document(returnMap);
        Document searchObject = searchMap == null ? new Document() : new Document(searchMap);

        FindIterable<Document> dbCursor = mongoClient.getDatabase(myForm.getDb()).getCollection(collectionName).find(searchObject);

        if (sortMap != null && !sortMap.isEmpty()) {
            dbCursor.sort(new Document(sortMap));
        }

        dbCursor.skip(skip).limit(limit);

        for (Document doc : dbCursor) {
            listMaps.add(wrapIt(myForm, doc));
        }
        return listMaps;
    }

    public DocumentRecursive wrapIt(MyForm myForm, Document dBObject) throws NullNotExpectedException {
        Map manualDbRefs = new HashMap();

        for (String key : dBObject.keySet()) {
            if (dBObject.get(key) instanceof ObjectId && !MONGO_ID.equals(key)) {
                Map def = new HashMap();
                MyField field = myForm.getField(key);

                if (field != null) {
                    MyItems itemsDbo = field.getItemsAsMyItems();
                    if (itemsDbo != null) {
                        String myDb = itemsDbo.getDb();
                        def.put(FORM_DB, myDb == null ? myForm.getDb() : myDb);
                        def.put(COLLECTION_NAME, itemsDbo.getTable());
                        def.put(MONGO_ID, dBObject.get(key));
                        manualDbRefs.put(key, def);
                    } else {
                        logger.error("items is null : {}", key);
                    }
                }
            }
        }
        return new DocumentRecursive(dBObject, manualDbRefs, this);
    }

    public boolean insertIntoMongo(String db, String collectionName, List<Map> mongoListOfMap) {

        List<Document> list = new ArrayList<>();
        for (Map map : mongoListOfMap) {
            list.add(new Document(map));
            if (list.size() == 50000) {//prevent heap space error on 136063 record count
                mongoClient.getDatabase(db).getCollection(collectionName).insertMany(list);
                list = new ArrayList<>();
            }
        }
        mongoClient.getDatabase(db).getCollection(collectionName).insertMany(list);
        return true;//writeResult == null || writeResult.getError() == null;
    }

    public List<Document> findAll(String db, String collectionName) {
        List<Document> list = new ArrayList<>();//188788 record heap space
        FindIterable<Document> cursor = mongoClient.getDatabase(db).getCollection(collectionName).find();
        for (Document doc : cursor) {
            list.add(doc);
        }
        return list;
    }

    public void drop(String db, String collectionName) {
        mongoClient.getDatabase(db).getCollection(collectionName).drop();
    }

    public Object getValue(Map dbObject, String dottedString) {
        String path[] = dottedString.split("[.]");
        Object value = null;
        for (String key : path) {
            if (value instanceof Map) {
                value = ((Document) value).get(key);
            } else {
                value = dbObject.get(key);
            }
        }
        return value;
    }

    @Override
    public Document trigger(Document projectSpaceMap, TagEvent trigger, List roles) {
        if (trigger != null) {
            String function = trigger.getOp();

            if (function != null) {
                final Document command = new Document();
                //command.put("$eval", String.format(replaceFuncCode(function.getCode()), replaceParams(projectSpaceMap)));
                Document result = runCommand(trigger.getDb(), function, projectSpaceMap, roles);

                Object returnValue = result.get(RETVAL);

                if (returnValue instanceof Document) {
                    return (Document) returnValue;
                }
            }
        }
        return new Document();

    }

    public List<String> replaceParams(Object... objects) {

        List<String> params = new ArrayList<>();

        for (Object obj : objects) {
            params.add(obj.toString());
        }

        return params;

    }

    public String replaceFuncCode(String code) {

        int start = code.indexOf("(");
        int end = code.indexOf(")");
        int paramCount = code.substring(start, end).split(",").length;

        StringBuilder sb = new StringBuilder();

        sb.append(code.substring(0, start));

        for (int i = 0; i < paramCount; i++) {
            sb.append("%s");
            if (i < paramCount) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public Document findOne(String database, String collection, Bson filter) {
        Document document = mongoClient.getDatabase(database).getCollection(collection).find(filter).first();
        return document;
    }

    public Document findOneWithProjection(String database, String collection,
            Bson filter, Bson projection) {
        try {
            Document document = mongoClient.getDatabase(database)
                    .getCollection(collection).find(filter).projection(projection).first();
            return document;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public boolean runActionAsDbTableFilterResult(Document actionDoc, RoleMap roleMap, Map filter) {
        String detectedRole = "none";

        for (String role : actionDoc.keySet()) {
            if (roleMap.isUserInRole(role) || roleMap.isUserInRole(role.replaceAll("role-", ""))) {
                detectedRole = role;
            }
        }

        if (actionDoc.get(detectedRole) instanceof Boolean) {
            return actionDoc.get(detectedRole, Boolean.class);
        }

        if (actionDoc.get("role-".concat(detectedRole)) instanceof Boolean) {
            return actionDoc.get("role-".concat(detectedRole), Boolean.class);
        }

        Document def = actionDoc.get("role-".concat(detectedRole), Document.class);

        Document query = def.get("query", Document.class);

        query = expandQuery(query, filter);

        Document doc = findOneWithProjection(def.getString("db"), def.getString("table"), query, new Document("_id", true));

        String check = def.getString("check");

        switch (check) {
            case "result>0":
                return doc != null;

        }
        return true;

    }

    public Document expandQuery(Document query, Map filter) {
        query = replaceToDollar(query);
        for (String key : query.keySet()) {
            if (query.get(key) instanceof String) {
                String value = query.get(key).toString();
                if (value.startsWith("${filter.") && value.endsWith("}")) {
                    String filterKey = value.substring(9, value.length() - 1);
                    query.put(key, filter.get(filterKey));
                }
            } else if (query.get(key) instanceof Document) {
                Document subQuery = query.get(key, Document.class);
                if (subQuery.containsKey(MyItems.ITEM_DB)) {
                    Document q = expandQuery(subQuery.get("query", Document.class), filter);
                    Document subResult = findOneWithProjection(
                            subQuery.getString(MyItems.ITEM_DB),
                            subQuery.getString(MyItems.ITEM_TABLE),
                            q,
                            new Document(subQuery.getString("projection"), true));
                    query.put(key, subResult == null ? null : subResult.get(subQuery.getString("projection")));
                }
            }
        }
        return query;
    }

    // private static final Map<String, Document> funcCahce = new HashMap<>();
    @Override
    public Document runCommand(String database, String code, Object... args) {
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(database);
//        sb.append(" : ");
//        sb.append(code);
//        for (Object obj : args) {
//            sb.append(" : ");
//            sb.append(obj == null ? "null" : obj.toString());
//        }
//        String cacheKey = org.apache.commons.codec.digest.DigestUtils.sha256Hex(sb.toString());
//        Document document = funcCahce.get(cacheKey);
//        if (document != null) {
//            return document;
//        }

        List<Object> list = new ArrayList<>();

        for (Object obj : args) {
            if (obj instanceof Map) {
                Document doc = new Document((Map) obj);
                for (String key : doc.keySet()) {
                    if (doc.get(key) instanceof PlainRecord) {
                        doc.put(key, doc.get(key, PlainRecord.class).getObjectId());
                    } else if (doc.get(key) instanceof Object[]) {
                        Object[] multiValues = (Object[]) doc.get(key);
                        List<String> list1 = new ArrayList<>();
                        for (Object obj1 : multiValues) {
                            list1.add(obj1.toString());
                        }
                        doc.put(key, list1);
                    }
                }
                list.add(doc);
            } else if (obj instanceof PlainRecord) {
                list.add(((PlainRecord) obj).getObjectId());
            } else {
                list.add(obj);
            }
        }

        code = code.replace(DIEZ, DOLAR);
        //  code = code.replace(REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_TYPE, );

        Document document = mongoClient.getDatabase(database).runCommand(new MyExecDoc(code, list));
//        funcCahce.put(cacheKey, document);

        return document;
    }

    @Override
    public void updatePush(String database, String collection, Bson filter, Document record) {

        for (String key : record.keySet()) {
            if (record.get(key) instanceof Object[]) {
                Object[] multiValues = (Object[]) record.get(key);
                List<String> list = new ArrayList<>();
                for (Object obj : multiValues) {
                    list.add(obj.toString());
                }
                record.put(key, list);
            } else if (record.get(key) instanceof MyBaseRecord) {
                record.put(key, ((MyBaseRecord) record.get(key)).getObjectId());
            }
        }

        try {
            mongoClient.getDatabase(database).getCollection(collection)
                    .updateOne(filter, new Document(ProjectConstants.DOLAR_PUSH, record));
        } catch (Exception ex) {
            throw ex;
        }

    }

    @Override
    public void upsertOne(String database, String collection, Bson filter, Document record) {

        for (String key : record.keySet()) {
            if (record.get(key) instanceof Object[]) {
                Object[] multiValues = (Object[]) record.get(key);
                List<String> list = new ArrayList<>();
                for (Object obj : multiValues) {
                    list.add(obj.toString());
                }
                record.put(key, list);
            } else if (record.get(key) instanceof String) {
                record.put(key, ((String) record.get(key)).replaceAll("<", "").replaceAll(">", ""));
            } else if (record.get(key) instanceof MyBaseRecord) {
                record.put(key, ((MyBaseRecord) record.get(key)).getObjectId());
            }
        }

        try {
            mongoClient.getDatabase(database).getCollection(collection)
                    .updateOne(filter, new Document(DOLAR_SET, record), new UpdateOptions().upsert(true));
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public void updateOne(String database, String collection, Bson filter, Document record) {

        for (String key : record.keySet()) {
            if (record.get(key) instanceof Object[]) {
                Object[] multiValues = (Object[]) record.get(key);
                List<String> list = new ArrayList<>();
                for (Object obj : multiValues) {
                    list.add(obj.toString());
                }
                record.put(key, list);
            } else if (record.get(key) instanceof String) {
                record.put(key, ((String) record.get(key)).replaceAll("<", "").replaceAll(">", ""));
            } else if (record.get(key) instanceof MyBaseRecord) {
                record.put(key, ((MyBaseRecord) record.get(key)).getObjectId());
            }
        }

        try {

            Document unset = (Document) record.remove(DOLAR_UNSET);

            Document setUnset = new Document();

            if (unset != null && !unset.isEmpty()) {
                for (String key : unset.keySet()) {
                    record.remove(key);
                }
                setUnset.append(DOLAR_UNSET, unset);

            }
            setUnset.append(DOLAR_SET, record);

            mongoClient.getDatabase(database).getCollection(collection).updateOne(filter, setUnset);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void updateMany(MyForm myForm, Bson filter, Document record, UpdateOptions uo) {
        updateMany(myForm.getDb(), myForm.getTable(), filter, record, uo);
    }

    public void updateMany(MyForm myForm, Bson filter, Document record) {
        updateMany(myForm.getDb(), myForm.getTable(), filter, record);
    }

    public void updateMany(String database, String collection, Bson filter, Document record, UpdateOptions uo) {
        mongoClient.getDatabase(database).getCollection(collection)
                .updateMany(filter, new Document(DOLAR_SET, record), uo);
    }

    public void updateMany(String database, String collection, Bson filter, Document record) {
        mongoClient.getDatabase(database).getCollection(collection)
                .updateMany(filter, new Document(DOLAR_SET, record));
    }

    public List<DocumentRecursive> findListAsName(String myFormDb, String myFormTable, MyForm myForm, Document searcheDBObject, Integer limit) {
        List<Document> cursor = createCursor(myFormDb, myFormTable, searcheDBObject, limit);
        return cursorToListAsName(cursor, myForm);
    }

    private List<DocumentRecursive> cursorToListAsName(List<Document> cursor, MyForm myForm) {
        List<DocumentRecursive> list = new ArrayList<>();

        for (Document document : cursor) {
            Map manualDbRefs = new HashMap();
            armBsonRefs(document, myForm, manualDbRefs);
            list.add(new DocumentRecursive(document, manualDbRefs, this));
        }
        return list;
    }

    private static void armBsonRefs(Document document, MyForm myForm, Map manualDbRefs) {
        Set<String> keySet = new HashSet<>(document.keySet());
        for (String key : keySet) {
            Object keyValue = document.get(key);

            if (keyValue instanceof ObjectId && !MONGO_ID.equals(key)) {
                Map def = new HashMap();
                MyField myField = myForm.getField(key);
                if (myField != null && myField.getItemsAsMyItems() != null) {
                    String myDb = myField.getItemsAsMyItems().getDb();
                    if (myDb == null) {
                        myDb = myForm.getDb();
                    }
                    String myColl = myField.getRefCollection() == null ? myField.getItemsAsMyItems().getTable() : myField.getRefCollection();
                    def.put(FORM_DB, myDb == null ? myForm.getDb() : myDb);
                    def.put(COLLECTION_NAME, myColl);
                    def.put(MONGO_ID, keyValue);
                    def.put("viewKeys", myField.getViewKey() == null ? Arrays.asList(NAME) : myField.getViewKey());
                    manualDbRefs.put(key, def);
                } else {
                    document.remove(key);
                }
            }
        }
    }

    public void insertOne(String database, String collection, Document record) {

        for (String key : record.keySet()) {
            if (record.get(key) instanceof String) {
                record.put(key, ((String) record.get(key)).replaceAll("<", "").replaceAll(">", ""));
            }
        }

        Document setUnset = new Document(DOLAR_SET, record);
        if (record.containsKey(DOLAR_UNSET)) {
            setUnset.append(DOLAR_UNSET, record.remove(DOLAR_UNSET));
            if (((Document) setUnset.get(DOLAR_UNSET)).isEmpty()) {
                setUnset.remove(DOLAR_UNSET);
            }
        }

        mongoClient.getDatabase(database).getCollection(collection).insertOne(record);
    }

    public void deleteMany(String database, String collection, Document document) {
        mongoClient.getDatabase(database).getCollection(collection).deleteMany(document);
    }

    public List<GridFSDBFile> findFiles(String db, DBObject filter) {
        List<GridFSDBFile> list = new GridFS(mongoClient.getDB(db)).find(filter);
        return list;
    }

    public List<MyFile> findFilesAsMyFile(String db, DBObject filter) throws IOException {
        List<MyFile> listOfMyFile = new ArrayList<>();
        List<GridFSDBFile> list = new GridFS(mongoClient.getDB(db)).find(filter);

        for (GridFSDBFile gridFSDBFile : list) {
            listOfMyFile.add(new MyFile(gridFSDBFile));
        }

        return listOfMyFile;
    }

    public int countFile(String db, BasicDBObject basicDBObject) {
        int count = new GridFS(mongoClient.getDB(db)).getFileList(basicDBObject).count();
        return count;
    }

    public List<FmsFile> findFilesAsFmsFileNoContent(String db, BasicDBObject basicDBObject, int skip, int limit) {

        DBCursor cursor = new GridFS(mongoClient.getDB(db)).getFileList(basicDBObject);
        cursor.skip(skip);
        cursor.limit(limit);

        List<FmsFile> listOut = new ArrayList<>();
        for (DBObject document : cursor) {
            try {
                listOut.add(new MyFileNoContent((GridFSDBFile) document));
            } catch (IOException ex) {
                logger.error("error occured", ex);
            }
        }
        return listOut;
    }

    public List<MyFile> findFileList(String db, BasicDBObject basicDBObject, int skip, int limit) {

        DBCursor cursor = new GridFS(mongoClient.getDB(db)).getFileList(basicDBObject);
        cursor.skip(skip);
        cursor.limit(limit);

        List<MyFile> listOut = new ArrayList<>();
        for (DBObject document : cursor) {
            try {
                listOut.add(new MyFile((GridFSDBFile) document));
            } catch (IOException ex) {
                logger.error("error occured", ex);
            }
        }
        return listOut;
    }

    public List<GridFSDBFile> findFiles(String db, String filename) {
        List<GridFSDBFile> list = new GridFS(mongoClient.getDB(db)).find(filename);
        return list;
    }

    public GridFSInputFile createFile(String gridfsdb, File file) throws IOException {
        GridFS gridFS = new GridFS(mongoClient.getDB(gridfsdb));
        GridFSInputFile gridFSInputFile = gridFS.createFile(file);
        return gridFSInputFile;
    }

    public GridFSInputFile createFile(String gridfsdb, InputStream inputStream) {
        GridFS gridFS = new GridFS(mongoClient.getDB(gridfsdb));
        GridFSInputFile gridFSInputFile = gridFS.createFile(inputStream);
        return gridFSInputFile;
    }

    public GridFSInputFile createFile(String gridfsdb, byte[] bytes) {
        GridFS gridFS = new GridFS(mongoClient.getDB(gridfsdb));
        GridFSInputFile gridFSInputFile = gridFS.createFile(bytes);
        return gridFSInputFile;
    }

    public long count(String database, String table, Bson relativeQuery) {
        long count = mongoClient.getDatabase(database).getCollection(table).countDocuments(relativeQuery);
        return count;
    }

    public List<Document> find(String database, String collection) {
        FindIterable<Document> list = mongoClient.getDatabase(database).getCollection(collection).find();
        List<Document> listOut = new ArrayList<>();
        for (Document document : list) {
            listOut.add(document);
        }
        return listOut;
    }

    public List<Document> find(String database, String collection, Bson filter) {
        FindIterable<Document> list = mongoClient.getDatabase(database).getCollection(collection).find(filter);
        List<Document> listOut = new ArrayList<>();
        for (Document document : list) {
            listOut.add(document);
        }
        return listOut;
    }

    public List<Document> findWithProjection(String database, String collection, Bson filter, Bson projection) {
        FindIterable<Document> list = mongoClient.getDatabase(database).getCollection(collection)
                .find(filter)
                .projection(projection);
        List<Document> listOut = new ArrayList<>();
        for (Document document : list) {
            listOut.add(document);
        }
        return listOut;
    }

    public List<Document> findWithProjection(String database, String collection,
            Bson filter, Bson sort, Number limit, Bson projection) {
        FindIterable<Document> list = mongoClient.getDatabase(database).getCollection(collection)
                .find(filter)
                .projection(projection);

        if (sort != null) {
            list.sort(sort);
        }

        if (limit != null) {
            list.limit(limit.intValue());
        }

        List<Document> listOut = new ArrayList<>();
        for (Document document : list) {
            listOut.add(document);
        }
        return listOut;
    }

    @Override
    public List<Document> findProjectLookup(String database, String collection,
            Bson filter, Bson sort, Number limit, Bson projection, MyLookup myLookup, Document resultProjection) {

        List<Document> documents = findWithProjection(database, collection, filter, sort, limit, projection);

        if (myLookup == null) {
            return documents;
        }

        for (Document doc : documents) {

            String foreignKey = myLookup.getFk();

            Document foreignDoc = findOneWithProjection(myLookup.getDb(),
                    myLookup.getTable(),
                    Filters.eq("_id", doc.getObjectId(foreignKey)),
                    myLookup.getFp());

            doc.append(foreignKey, foreignDoc);

            if (resultProjection != null) {

                for (Map.Entry resultProjectionEntry : resultProjection.entrySet()) {

                    String type = ((Document) resultProjectionEntry.getValue()).getString("type");
                    List subkey = Arrays.asList(((Document) resultProjectionEntry.getValue()).getString("subkey").split("[.]"));

                    Class clazz = String.class;

                    switch (type) {
                        case "objectid":
                            clazz = ObjectId.class;
                            break;
                        case "string":
                            clazz = String.class;
                            break;
                        case "number":
                            clazz = Number.class;
                            break;
                        default:
                            break;

                    }
                    doc.put(resultProjectionEntry.getKey().toString(), doc.getEmbedded(subkey, clazz));
                }
            }

        }

        return documents;
    }

    public List<Document> find(String database, String collection, Bson filter, Bson sort, Number limit) {
        FindIterable<Document> list = mongoClient
                .getDatabase(database)
                .getCollection(collection)
                .find(filter);

        if (sort != null) {
            list.sort(sort);
        }

        if (limit != null) {
            list.limit(limit.intValue());
        }

        List<Document> listOut = new ArrayList<>();
        for (Document document : list) {
            listOut.add(document);
        }
        return listOut;
    }

    public List<Document> findSkipLimit(String database, String collection, Bson filter, Number skip, Number limit) {
        FindIterable<Document> list = mongoClient.getDatabase(database).getCollection(collection)
                .find(filter);

        if (skip != null) {
            list.skip(skip.intValue());
        }

        if (limit != null) {
            list.limit(limit.intValue());
        }

        List<Document> listOut = new ArrayList<>();
        for (Document document : list) {
            listOut.add(document);
        }
        return listOut;
    }

    @Override
    public List<Document> createCursor(String myFormDb, String myFormTable, Document searcheDBObject, Integer limit) {
        FindIterable<Document> cursor = mongoClient.getDatabase(myFormDb).getCollection(myFormTable).find(searcheDBObject);
        if (limit != null) {
            cursor = cursor.limit(limit);
        }
        List<Document> listOut = new ArrayList<>();
        for (Document document : cursor) {
            listOut.add(document);
        }
        return listOut;
    }

    @Override
    public List<Document> createCursor(String myFormDb, String myFormTable, Bson searcheDBObject, Integer limit) {
        FindIterable<Document> cursor = mongoClient.getDatabase(myFormDb).getCollection(myFormTable).find(searcheDBObject);
        if (limit != null) {
            cursor = cursor.limit(limit);
        }
        List<Document> listOut = new ArrayList<>();
        for (Document document : cursor) {
            listOut.add(document);
        }
        return listOut;
    }

    @Override
    public Document replaceToDollar(Document document) {
        if (document == null) {
            return null;
        }
        return new RecurcivlyReplaceableDocument(document).replaceToDollar();
    }

    private final Map<Map, Document> MAP_OF_RECORD = new HashMap<>();

    public Document cacheAndGet(Map<String, Object> map) {
        if (MAP_OF_RECORD.get(map) == null) {
            String db = (String) map.get("db");
            String collectionName = (String) map.get("collectionName");
            ObjectId id = (ObjectId) map.get("_id");
            MAP_OF_RECORD.put(map, findOne(db, collectionName, new Document("_id", id)));
        }
        return MAP_OF_RECORD.get(map);
    }

}
