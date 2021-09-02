package tr.org.tspb.dao;

import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import tr.org.tspb.constants.ProjectConstants;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;
import tr.org.tspb.tags.FmsQuery;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class TagItemsQueryRef {

    /*
    
    "ref-value": {
        "db": "uysdb",
        "itemTable": "common",
        "query": {
            "_id": "fms_code{{filter_member}}"
        },
        "projection": "memberType"
    }
    
     */
    private String db;
    private String table;
    private String projection;
    private Document query;
    private FmsScriptRunner fmsScriptRunner;

    public TagItemsQueryRef(Document ref, Map filter, FmsScriptRunner fmsScriptRunner, ObjectId loginMemberId) {

        this.fmsScriptRunner = fmsScriptRunner;

        this.db = ref.get("db", String.class);
        this.table = ref.get("itemTable", String.class);
        this.projection = ref.get("projection", String.class);

        this.query = new Document();
        Document query_ = ref.get("query", Document.class);

        List<Document> listOfFilter = query_.get("list", List.class);

        if (listOfFilter != null) {
            this.query = FmsQuery.buildListQuery(listOfFilter, filter, fmsScriptRunner, loginMemberId);
        } else {
            for (String key : query_.keySet()) {
                Object value = query_.get(key);
                if (value instanceof String) {
                    switch (value.toString()) {
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_MEMBER:
                            value = filter.get("member");
                            break;
                        default:
                    }
                }
                this.query.put(key, value == null ? "no result" : value);
            }
        }

    }

    public Object value() {
        Document ref = fmsScriptRunner.findOne(db, table, query);
        return ref == null ? "no result" : ref.get(projection);
    }

    public List<ObjectId> values() {
        return fmsScriptRunner.findObjectIds(db, table, query, projection);
    }

}
