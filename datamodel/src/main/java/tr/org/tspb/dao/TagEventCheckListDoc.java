/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.dao;

import java.util.List;
import java.util.Map;
import org.bson.Document;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_IN;
import static tr.org.tspb.constants.ProjectConstants.PERIOD;
import static tr.org.tspb.constants.ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_PERIOD;
import static tr.org.tspb.constants.ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;
import tr.org.tspb.pojo.RoleMap;
import tr.org.tspb.pojo.UserDetail;

/**
 *
 * @author telman
 */
public class TagEventCheckListDoc {

    public static boolean value(FmsScriptRunner fmsScriptRunner, Map myFilter, UserDetail userDetail, RoleMap roleMap, List<Document> checks) {

        Boolean result = null;

        Document noRoleDoc = null;

        for (Document check : checks) {
            List<String> roles = check.getList("roles", String.class);
            if (roles == null) {
                noRoleDoc = check;
            } else if (roleMap.isUserInRole(roles)) {
                result = applyCheck(check, result, userDetail, myFilter, fmsScriptRunner);
            }
        }

        if (result == null && noRoleDoc != null) {
            result = applyCheck(noRoleDoc, result, userDetail, myFilter, fmsScriptRunner);
        }

        return Boolean.TRUE.equals(result);
    }

    private static boolean applyCheck(Document check, boolean result, UserDetail userDetail, Map myFilter, FmsScriptRunner fmsScriptRunner) throws RuntimeException {
        Boolean value = check.getBoolean("value");
        String func = check.getString("func");
        Document doc = check.get("ref", Document.class);
        if (value != null) {
            result = result && Boolean.TRUE.equals(value);
        } else if (func != null) {
            throw new UnsupportedOperationException("func is not supported yet");
        } else if (doc != null) {

            String db = doc.getString("db");
            String table = doc.getString("table");
            List<Document> filters = doc.getList("query", Document.class);
            String decision = doc.getString("check");
            Document query = createQuery(filters, userDetail, myFilter);

            switch (decision) {
                case "existence":
                    result = result && fmsScriptRunner.findOne(db, table, query) != null;
                    break;
                case "non-existence":
                    result = result && fmsScriptRunner.findOne(db, table, query) == null;
                    break;
                default:
                    result = result && fmsScriptRunner.findOne(db, table, query) != null;
            }

        }
        return result;
    }

    private static Document createQuery(List<Document> filters, UserDetail userDetail, Map myFilter) throws RuntimeException {
        Document query = new Document();
        for (Document filter : filters) {
            String key = filter.getString("key");
            String stringValue = filter.getString("string-value");
            List<String> arrayValue = filter.getList("array-value", String.class);
            String fmsValue = filter.getString("fms-value");

            if (stringValue != null) {
                query.append(key, stringValue);
            } else if (arrayValue != null) {
                query.append(key, new Document(DOLAR_IN, arrayValue));
            } else if (fmsValue != null) {
                switch (fmsValue) {
                    case REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID:
                        query.append(key, userDetail.getDbo().getObjectId());
                        break;
                    case REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_PERIOD:
                        query.append(key, myFilter.get(PERIOD));
                        break;
                    default:
                        throw new RuntimeException(fmsValue.concat(" is not supported"));
                }
            } else {
                throw new RuntimeException(key.concat(" has a not supported type of value"));
            }
        }
        return query;
    }
}
