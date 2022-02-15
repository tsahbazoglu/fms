/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import static tr.org.tspb.constants.ProjectConstants.DIEZ;
import static tr.org.tspb.constants.ProjectConstants.DOLAR;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_IN;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_NE;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_NIN;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_REGEX;
import static tr.org.tspb.constants.ProjectConstants.PERIOD;
import static tr.org.tspb.constants.ProjectConstants.REPLACEABLE_KEY_FMS_VALUE;
import static tr.org.tspb.constants.ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_PERIOD;
import static tr.org.tspb.constants.ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID;
import static tr.org.tspb.constants.ProjectConstants.VALUE;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;
import tr.org.tspb.pojo.RoleMap;
import tr.org.tspb.pojo.UserDetail;

/**
 *
 * @author telman
 */
public class TagEventCheckListDoc {

    public static boolean value(FmsScriptRunner fmsScriptRunner, Map myFilter, UserDetail userDetail, RoleMap roleMap, List<Document> checks) {

        List<Document> noRoleChecks = new ArrayList<>();
        boolean noRole = true;

        boolean result = true;

        for (Document check : checks) {
            List<String> roles = check.getList("roles", String.class);
            if (roles == null) {
                noRoleChecks.add(check);
            } else if (roleMap.isUserInRole(roles)) {
                noRole = false;
                result = applyCheck(check, result, userDetail, myFilter, fmsScriptRunner);
            }
        }

        if (noRole && !noRoleChecks.isEmpty()) {
            result = true;
            for (Document noRoleDoc : noRoleChecks) {
                result = applyCheck(noRoleDoc, result, userDetail, myFilter, fmsScriptRunner);
            }
        }

        return Boolean.TRUE.equals(result);
    }

    private static boolean applyCheck(Document check, boolean result, UserDetail userDetail, Map myFilter, FmsScriptRunner fmsScriptRunner) throws RuntimeException {

        if (result == false) {
            return false;
        }

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

            List<Document> findFilters = doc.getList("query", Document.class);
            List<Document> countFilters = doc.getList("count-filter", Document.class);

            Document query = createQuery(findFilters, userDetail, myFilter);

            String decision = doc.getString("check");

            switch (decision) {
                case "existence":
                    result = result && fmsScriptRunner.findOne(db, table, query) != null;
                    break;
                case "non-existence":
                    result = result && fmsScriptRunner.findOne(db, table, query) == null;
                    break;
                case "count>0":
                    Document countQuery = createQuery(countFilters, userDetail, myFilter);
                    long x = fmsScriptRunner.count(db, table, query);
                    long y = fmsScriptRunner.count(db, table, countQuery);
                    result = (x > 0 && y > 0 && x == y);
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

            boolean hasStrValue = filter.containsKey("string-value");
            boolean hasArrayValue = filter.containsKey("array-value");
            boolean hasFmsValue = filter.containsKey(REPLACEABLE_KEY_FMS_VALUE);

            if (hasStrValue) {
                query.append(key, filter.getString("string-value"));
            } else if (hasArrayValue) {
                query.append(key, new Document(DOLAR_IN, filter.getList("array-value", String.class)));
            } else if (hasFmsValue) {
                String fmsValue = filter.getString(REPLACEABLE_KEY_FMS_VALUE);
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

                String type = filter.get("type", String.class);
                if (type == null) {
                    type = "string";
                }

                if (filter.get(VALUE) == null) {
                    query.put(key, null);
                } else {
                    switch (type) {
                        case "number":
                            query.put(key, filter.get(VALUE, Number.class));
                            break;
                        case "string":
                            query.put(key, filter.get(VALUE, String.class).replaceAll(DIEZ, DOLAR));
                            break;
                        case "in":
                            query.put(key, new Document(DOLAR_IN, Arrays.asList(filter.get(VALUE, String.class).replaceAll(DIEZ, DOLAR).split(","))));
                            break;
                        case "nin":
                            query.put(key, new Document(DOLAR_NIN, Arrays.asList(filter.get(VALUE, String.class).replaceAll(DIEZ, DOLAR).split(","))));
                            break;
                        case "ne":
                            query.put(key, new Document(DOLAR_NE, filter.get(VALUE, String.class).replaceAll(DIEZ, DOLAR)));
                            break;
                        case "regex":
                            query.put(key, new Document(DOLAR_REGEX, filter.get(VALUE, String.class).replaceAll(DIEZ, DOLAR)));
                            break;
                        default:
                            throw new UnsupportedOperationException("field.items.query.type is not supported  : " + type);
                    }
                }
            }
        }
        return query;
    }
}
