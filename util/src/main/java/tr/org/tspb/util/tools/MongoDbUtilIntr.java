package tr.org.tspb.util.tools;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import tr.org.tspb.exceptions.NullNotExpectedException;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.conversions.Bson;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.dao.MyItems;
import tr.org.tspb.dao.MyLookup;
import tr.org.tspb.pojo.RoleMap;
import tr.org.tspb.dao.TagEvent;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface MongoDbUtilIntr extends FmsMongoGridFsIntr {

    public void dropTable(String UYSDB, String tbbBankCollection);

    public void deleteOne(String database, String collectionName, Document document);

    public List<Document> aggregate(String db, String table, List<Document> aggregateList);

    public MongoCollection getCollection(String db, String collectionName, Document indexObject, boolean unique);

    public void createIndex(MyItems myItems);

    public void createIndex(FmsForm myForm, Document indexObject);

    public void createIndex(String db, String collectionName, Document indexObject);

    public void createIndexUnique(FmsForm myForm, Document indexObject);

    public void createIndexUnique(String db, String collectionName, Document indexObject);

    public void remove(String db, String collectionName, Map<String, Object> searchMap) throws RuntimeException;

    public List<Map<String, Object>> find(FmsForm myForm, String collectionName,
            Map<String, Object> searchMap,
            Map<String, Object> returnMap,
            int skip,
            int limit,
            Map<String, Object> sortMap,
            String searchPrefix) throws NullNotExpectedException;

    public DocumentRecursive wrapIt(FmsForm myForm, Document dBObject) throws NullNotExpectedException;

    public boolean insertIntoMongo(String db, String collectionName, List<Map> mongoListOfMap);

    public List<Document> findAll(String db, String collectionName);

    public void drop(String db, String collectionName);

    public Object getValue(Map dbObject, String dottedString);

    public Document trigger(Document projectSpaceMap, TagEvent trigger, List roles);

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

    public void updateMany(FmsForm myForm, Bson filter, Document record, UpdateOptions uo);

    public void updateMany(String database, String collection, Bson filter, Document record, UpdateOptions uo);

    public void updateMany(FmsForm myForm, Bson filter, Document record);

    public void updateMany(String database, String collection, Bson filter, Document record);

    public List<DocumentRecursive> findListAsName(String myFormDb, String myFormTable,
            FmsForm myForm, Document searcheDBObject, Integer limit);

    public void insertOne(String database, String collection, Document record);

    public void deleteMany(String database, String collection, Document document);

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
