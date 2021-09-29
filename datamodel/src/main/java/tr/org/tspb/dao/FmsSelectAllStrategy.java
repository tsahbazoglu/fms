
/*
        "please-select": {
            "strategies-doc": ["ALL", "NONE", "QUERY"],
            "strategy": "QUERY",              
            "db": "gsodb",
            "table": "gso_org_status",
            "query": {
                "list": [
                    {
                        "key": "status",
                        "array-value": ["000", "001", "002"]
                    },
                    {
                        "key": "workflowStatus",
                        "array-value": ["000", "010"]
                    },
                    {
                        "key": "member",
                        "fms-value": "fms_code{{login_member_id}}"
                    }
                ]
            },
           "projection": "period"
        } 
 */
package tr.org.tspb.dao;

import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;
import tr.org.tspb.tags.FmsQuery;

/**
 *
 * @author telman
 */
public class FmsSelectAllStrategy {

    private List listOfObjectIds;

    public FmsSelectAllStrategy(FmsScriptRunner fmsScriptRunner,
            Document selectAllQuery, ObjectId loginMemberId, Map filter) {

        String strategy = selectAllQuery.getString("strategy");
        String db = selectAllQuery.getString("db");
        String table = selectAllQuery.getString("table");
        String projection = selectAllQuery.getString("projection");

        List<Document> queryList = selectAllQuery.get("query", Document.class).getList("list", Document.class);

        Document query = FmsQuery.buildListQuery(queryList, filter, fmsScriptRunner, loginMemberId);

        this.listOfObjectIds = fmsScriptRunner.findObjectIds(db, table, query, projection);

    }

    /**
     * @return the listOfObjectIds
     */
    public List getListOfObjectIds() {
        return listOfObjectIds;
    }

}
