package tr.org.tspb.util.tools;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import tr.org.tspb.exceptions.NullNotExpectedException;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import tr.org.tspb.dao.MyFile;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyItems;
import tr.org.tspb.dao.MyLookup;
import tr.org.tspb.pojo.RoleMap;
import tr.org.tspb.dao.FmsFile;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface MongoDbUtilIntr extends Serializable {

    public void dropTable(String UYSDB, String tbbBankCollection);

    public void deleteOne(String database, String collectionName, Document document);

    public List<Document> aggregate(String db, String table, List<Document> aggregateList);

    public MongoCollection getCollection(String db, String collectionName, Document indexObject, boolean unique);

    public void createIndex(MyItems myItems);

    public void createIndex(MyForm myForm, Document indexObject);

    public void createIndex(String db, String collectionName, Document indexObject);

    public void createIndexUnique(MyForm myForm, Document indexObject);

    public void createIndexUnique(String db, String collectionName, Document indexObject);

    public void remove(String db, String collectionName, Map<String, Object> searchMap) throws RuntimeException;

    public void removeFile(String db, DBObject filter) throws RuntimeException;

    public void removeFile(String db, ObjectId objectId) throws RuntimeException;

    public GridFSDBFile findFile(String db, ObjectId objectId) throws RuntimeException;

    public MyFile findFileAsMyFile(String db, ObjectId objectId) throws IOException;

    public MyFile findFileAsMyFileInputStream(String db, ObjectId objectId) throws IOException;

    public void copyFiles(String fromDb, String toDb, DBObject fromSearch) throws IOException;

    public GridFSDBFile findFile(String db, DBObject filter) throws RuntimeException;

    public List<Map<String, Object>> find(String db, Document selectedForm, String collectionName,
            Map<String, Object> searchMap,
            Map<String, Object> returnMap,
            int skip,
            int limit,
            Map<String, Object> sortMap,
            String searchPrefix) throws NullNotExpectedException;

    public DocumentRecursive wrapIt(Document selectedForm, Document dBObject) throws NullNotExpectedException;

    public boolean insertIntoMongo(String db, String collectionName, List<Map> mongoListOfMap);

    public List<Document> findAll(String db, String collectionName);

    public void drop(String db, String collectionName);

    public Object getValue(Map dbObject, String dottedString);

    public Document trigger(Document projectSpaceMap, Document trigger, List roles);

    public List<String> replaceParams(Object... objects);

    public String replaceFuncCode(String code);

    public Document findOne(String database, String collection, Bson filter);

    public Document findOneWithProjection(String database, String collection,
            Bson filter, Bson projection);

    public boolean runActionAsDbTableFilterResult(Document actionDoc, RoleMap roleMap, Map filter);

    public Document expandQuery(Document query, Map filter);

    // private static final Map<String, Document> funcCahce = new HashMap<>();
    public Document runCommand(String database, String code, Object... args);

    public void updatePush(String database, String collection, Bson filter, Document record);

    public void updateOne(String database, String collection, Bson filter, Document record);

    public void upsertOne(String database, String collection, Bson filter, Document record);

    public void updateMany(MyForm myForm, Bson filter, Document record, UpdateOptions uo);

    public void updateMany(String database, String collection, Bson filter, Document record, UpdateOptions uo);

    public void updateMany(MyForm myForm, Bson filter, Document record);

    public void updateMany(String database, String collection, Bson filter, Document record);

    public List<DocumentRecursive> findListAsName(String myFormDb, String myFormTable,
            MyForm myForm, Document searcheDBObject, Integer limit);

    public void insertOne(String database, String collection, Document record);

    public void deleteMany(String database, String collection, Document document);

    public List<GridFSDBFile> findFiles(String db, DBObject filter);

    public List<MyFile> findFilesAsMyFile(String db, DBObject filter) throws IOException;

    public int countFile(String db, BasicDBObject basicDBObject);

    public List<FmsFile> findFilesAsFmsFileNoContent(String db, BasicDBObject basicDBObject, int skip, int limit);

    public List<MyFile> findFileList(String db, BasicDBObject basicDBObject, int skip, int limit);

    public List<GridFSDBFile> findFiles(String db, String filename);

    public GridFSInputFile createFile(String gridfsdb, File file) throws IOException;

    public GridFSInputFile createFile(String gridfsdb, InputStream inputStream);

    public GridFSInputFile createFile(String gridfsdb, byte[] bytes);

    public long count(String database, String table, Bson relativeQuery);

    public List<Document> find(String database, String collection);

    public List<Document> find(String database, String collection, Bson filter);

    public List<Document> findWithProjection(String database, String collection, Bson filter, Bson projection);

    public List<Document> findWithProjection(String database, String collection,
            Bson filter, Bson sort, Number limit, Bson projection);

    public List<Document> findProjectLookup(String database, String collection,
            Bson filter, Bson sort, Number limit, Bson projection, MyLookup myLookup, Document resultProjection);

    public List<Document> find(String database, String collection, Bson filter, Bson sort, Number limit);

    public List<Document> findSkipLimit(String database, String collection, Bson filter, Number skip, Number limit);

    public List<Document> createCursor(String myFormDb, String myFormTable, Document searcheDBObject, Integer limit);

    public List<Document> createCursor(String myFormDb, String myFormTable, Bson searcheDBObject, Integer limit);

    public Document replaceToDollar(Document document);

    public Document cacheAndGet(Map<String, Object> map);

}
