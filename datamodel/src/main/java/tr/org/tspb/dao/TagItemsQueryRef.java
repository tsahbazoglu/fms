package tr.org.tspb.dao;

import java.util.Map;
import org.bson.Document;
import tr.org.tspb.constants.ProjectConstants;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;

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

    public TagItemsQueryRef(Document ref, Map filter, FmsScriptRunner fmsScriptRunner) {

        this.fmsScriptRunner = fmsScriptRunner;

        this.db = ref.get("db", String.class);
        this.table = ref.get("itemTable", String.class);
        this.projection = ref.get("projection", String.class);

        this.query = new Document();
        Document query_ = ref.get("query", Document.class);

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

    public Object value() {
        Document ref = fmsScriptRunner.findOne(db, table, query);
        return ref == null ? "no result" : ref.get(projection);
    }

}
