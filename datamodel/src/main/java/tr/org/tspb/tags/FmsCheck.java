/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.tags;

import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;

/**
 *
 * @author telman
 */
public class FmsCheck {

    private final String db;
    private final String table;
    private final Document filter;
    private Check check;
    private FmsScriptRunner fmsScriptRunner;

    private FmsCheck(Document checkDoc, Map filter, ObjectId loginMemberId, FmsScriptRunner fmsScriptRunner) {
        this.fmsScriptRunner = fmsScriptRunner;
        this.db = checkDoc.getString("db");
        this.table = checkDoc.getString("table");;
        this.filter = FmsQuery
                .buildListQuery(checkDoc.getList("query", Document.class), filter, fmsScriptRunner, loginMemberId);

        this.check = Check.detect(checkDoc.getString("check"));

    }

    public boolean execute() {

        switch (this.check) {
            case EXIST:
                return fmsScriptRunner.findOne(db, table, filter) != null;
            case COUNT_ONE:
                return fmsScriptRunner.count(db, table, filter) == 1;
            case NOT_EXIST:
                return fmsScriptRunner.findOne(db, table, filter) == null;
        }

        return false;
    }

    public static FmsCheck build(Document checkDoc, Map filter, ObjectId loginMemberId,
            FmsScriptRunner fmsScriptRunner) {
        return new FmsCheck(checkDoc, filter, loginMemberId, fmsScriptRunner);
    }

    private enum Check {
        EXIST,
        NOT_EXIST,
        COUNT_ONE;

        static Check detect(String x) {
            switch (x) {
                case "result>0":
                    return EXIST;
                case "exists":
                    return EXIST;
                case "result=null":
                    return NOT_EXIST;
                case "count=1":
                    return COUNT_ONE;
                default:
                    return EXIST;
            }

        }
    }
}
