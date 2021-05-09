package tr.org.tspb.util.tools;

import static tr.org.tspb.constants.ProjectConstants.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import tr.org.tspb.dao.FmsForm;
 
/**
 *
 * @author Telman Şahbazoğlu
 */
public class MongoDbVersion {

    private MongoDbUtilIntr mongoDbUtil;

    private static final MongoDbVersion instance = new MongoDbVersion();

    public static MongoDbVersion instance(MongoDbUtilIntr mongoDbUtil) {
        instance.mongoDbUtil = mongoDbUtil;
        return instance;
    }

    private final String _ID = "_id";
    private final String VERSION_DB = "telman_version_db";
    private final String VERSION_COLLECTION = "telman_version_collection";
    private final String MASTER_RECORD = "master_record";
    private final String DATE = "date";
    private final String IP = "ip";
    private final String USERNAME = OPERATOR_LDAP_UID;
    private final String GLOBAL_LAST_REVISION_NUMBER_COLLECTION = "telman_global_revision_number_coll";
    private final String REVISION = "revison";
    private final String LAST_REVISION = "last_revison";
    private final String RECORD_TYPE = "record_type";

    private MongoDbVersion() {

    }

    private Long getOrGenerateGlobalRevisionNumber(String versionDB, String versionCollection, String recordType) {

        Document dbo = mongoDbUtil.findOne(versionDB, GLOBAL_LAST_REVISION_NUMBER_COLLECTION, new Document());

        long currentRevision = 1L;

        if (dbo == null) {
            mongoDbUtil.insertOne(versionDB, GLOBAL_LAST_REVISION_NUMBER_COLLECTION, new Document(LAST_REVISION, currentRevision));
        } else {

            currentRevision = ((Number) dbo.get(LAST_REVISION)).longValue();

            if (mongoDbUtil.findOne(versionDB, versionCollection, new Document(REVISION, currentRevision).append(RECORD_TYPE, recordType)) != null) {

                currentRevision++;

                mongoDbUtil.updateMany(versionDB, GLOBAL_LAST_REVISION_NUMBER_COLLECTION,
                        new BsonDocument(), new Document(LAST_REVISION, currentRevision));
            }
        }

        return currentRevision;

    }

    /**
     *
     * @param versionDB
     * @param versionColl
     * @param recordType
     * @param record
     * @param ip
     * @param username
     * @param importantFields
     * @throws Exception
     */
    public void archive( //
            String versionDB, //
            String versionColl,//
            String recordType,//
            Map record, //
            String ip,
            String username,
            List<String> importantFields) throws Exception {

        if (record.get(_ID) == null) {
            throw new Exception("record does not have an _id property");
        }

        String versionDatabase = versionDB == null ? VERSION_DB : versionDB;
        String versionCollection = versionColl == null ? VERSION_COLLECTION : versionColl;

        Document history = new Document();

        history.put(DATE, new Date());
        history.put(IP, ip);//
        history.put(USERNAME, username);
        history.put(RECORD_TYPE, recordType);
        history.put(REVISION, getOrGenerateGlobalRevisionNumber(versionDatabase, versionCollection, recordType));

        for (String key : importantFields) {
            history.put(key, record.get(key));
        }

        history.put(MASTER_RECORD, record);

        mongoDbUtil.insertOne(versionDatabase, versionCollection, history);
    }

    public Map<String, List> fetch(FmsForm myForm, String versionDB, String versionCollection, ObjectId recordID, List<String> importantFields) {

        List<DocumentRecursive> rowList = mongoDbUtil.findListAsName(versionDB, versionCollection,
                myForm, new Document(MASTER_RECORD.concat(DOT).concat(_ID), recordID), null);

        List<Document> columnList = new ArrayList<>();

        for (String key : importantFields) {
            columnList.add(new Document()
                    .append("key", key)
                    .append(TYPE, "string")
                    .append(NAME, myForm.getField(key).getName())
                    .append("datePattern", myForm.getField(key).getDatePattern())
                    .append("myField", myForm.getField(key)));
        }

        columnList.add(new Document()
                .append("key", REVISION)
                .append(NAME, "* Revizyon")
                .append(TYPE, "string"));

        columnList.add(new Document()
                .append("key", "date")//
                .append(NAME, "* İşlem Tarihi")
                .append("datePattern", "dd.MM.yyyy")
                .append(TYPE, "date"));

        columnList.add(new Document()
                .append("key", OPERATOR_LDAP_UID)
                .append(NAME, "* İşlem Yapan")
                .append(TYPE, "string"));

        columnList.add(new Document()
                .append("key", "ip")
                .append(NAME, "* İşlem Yapan Makine IP")
                .append(TYPE, "string"));

        Map map = new HashMap();
        map.put(COLUMN_LIST, columnList);
        map.put(ROW_LIST, rowList);

        return map;
    }

}
