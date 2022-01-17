/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bson.Document;
import org.bson.types.ObjectId;
import tr.org.tspb.constants.ProjectConstants;
import static tr.org.tspb.constants.ProjectConstants.DIEZ;
import static tr.org.tspb.constants.ProjectConstants.DOLAR;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_IN;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_NE;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_REGEX;
import static tr.org.tspb.constants.ProjectConstants.VALUE;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.dao.TagItemsQueryRef;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;
import tr.org.tspb.pojo.RoleMap;

/**
 *
 * @author telman
 */
public class FmsQuery {

    public static Document build(Document d, Map filter,
            FmsScriptRunner fmsScriptRunner, ObjectId loginMemberId) {

        Document q = new Document();

        String key = d.get("key", String.class);

        Document refValue = d.get("ref-value", Document.class);
        Document inRef = d.get("in-ref", Document.class);
        String fmsValue = d.get("fms-value", String.class);
        Boolean booleanValue = d.get("boolean-value", Boolean.class);
        String strValue = d.get("string-value", String.class);
        Number numberValue = d.get("number-value", Number.class);
        List<String> listOfString = d.getList("array-value", String.class);
        List<Number> listOfNumber = d.getList("array-number", Number.class);
        Document aggregateValue = d.get("aggregate-value", Document.class);

        if (aggregateValue != null) {
            q.put(key, aggregate(aggregateValue, fmsScriptRunner, loginMemberId));

        } else if (refValue != null) {
            q.put(key, new TagItemsQueryRef(refValue, filter, fmsScriptRunner, loginMemberId)
                    .value());
        } else if (inRef != null) {
            q.put(key, new Document(DOLAR_IN, new TagItemsQueryRef(inRef, filter, fmsScriptRunner, loginMemberId)
                    .values()));
        } else if (fmsValue != null) {
            switch (fmsValue) {
                case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_PERIOD:
                    q.put(key, filter.get("period") == null ? "no result" : filter.get("period"));
                    break;
                case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_TEMPLATE:
                    q.put(key, filter.get("template") == null ? "no result" : filter.get("template"));
                    break;
                case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID:
                    q.put(key, loginMemberId == null ? "no result" : loginMemberId);
                    break;
                default:
                    throw new RuntimeException("could not find replaceble word");
            }
        } else if (booleanValue != null) {
            q.put(key, booleanValue);
        } else if (strValue != null) {
            q.put(key, strValue);
        } else if (numberValue != null) {
            q.put(key, numberValue);
        } else if (listOfString != null) {
            q.put(key, new Document(DOLAR_IN, listOfString));
        } else if (listOfNumber != null) {
            q.put(key, new Document(DOLAR_IN, listOfNumber));
        } else {
            String type = d.get("type", String.class);
            if (type == null) {
                type = "string";
            }
            switch (type) {
                case "number":
                    q.put(key, d.get(VALUE, Number.class));
                    break;
                case "string":
                    q.put(key, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR));
                    break;
                case "in":
                    q.put(key, new Document(DOLAR_IN, Arrays.asList(d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR).split(","))));
                    break;
                case "ne":
                    q.put(key, new Document(DOLAR_NE, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR)));
                    break;
                case "regex":
                    q.put(key, new Document(DOLAR_REGEX, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR)));
                    break;
                default:
                    throw new UnsupportedOperationException("field.items.query.type is not supported  : " + type);
            }
        }
        return q;
    }

    public static Document buildListQuery(List<Document> listOfFilter, Map filter,
            FmsScriptRunner fmsScriptRunner, ObjectId loginMemberId) throws RuntimeException {

        Document result = new Document();
        if (listOfFilter != null) {
            for (Document d : listOfFilter) {
                result.putAll(FmsQuery.build(d, filter, fmsScriptRunner, loginMemberId));
            }
        }
        return result;
    }

    public static Document buildListQueryAjax(List<Document> listOfFilter, Map filter,
            FmsScriptRunner fmsScriptRunner, ObjectId loginMemberId,
            MyMap crud, RoleMap roleMap) throws RuntimeException {

        Document result = new Document();

        for (Document d : listOfFilter) {
            result.putAll(FmsQuery.buildAjax(d, filter, fmsScriptRunner, loginMemberId, crud, roleMap));
        }
        return result;
    }

    static Pattern pattern_fms_crud = Pattern.compile("fms_crud\\{\\{(.*?)\\}\\}");

    public static Document buildAjax(Document d, Map filter,
            FmsScriptRunner fmsScriptRunner, ObjectId loginMemberId,
            MyMap crud, RoleMap roleMap) {

        Document q = new Document();

        String key = d.get("key", String.class);

        List roles = d.getList("roles", String.class);
        if (roles != null && !roleMap.isUserInRole(roles)) {
            return new Document();
        }

        Document refValue = d.get("ref-value", Document.class);
        Document inRef = d.get("in-ref", Document.class);
        String fmsValue = d.get("fms-value", String.class);
        Boolean booleanValue = d.get("boolean-value", Boolean.class);
        String strValue = d.get("string-value", String.class);
        Number numberValue = d.get("number-value", Number.class);
        List<String> listOfString = d.getList("array-value", String.class);
        List<Number> listOfNumber = d.getList("array-number", Number.class);

        if (refValue != null) {
            q.put(key, new TagItemsQueryRef(refValue, filter, fmsScriptRunner, loginMemberId)
                    .value());
        } else if (inRef != null) {
            q.put(key, new Document(DOLAR_IN, new TagItemsQueryRef(inRef, filter, fmsScriptRunner, loginMemberId)
                    .values()));
        } else if (fmsValue != null) {

            Matcher m = pattern_fms_crud.matcher(fmsValue);
            if (m.find()) {
                Object crudValue = crud.get(m.group(1));
                q.put(key, crudValue == null ? "no result" : crudValue);
            } else {
                switch (fmsValue) {
                    case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_PERIOD:
                        q.put(key, filter.get("period") == null ? "no result" : filter.get("period"));
                        break;
                    case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_TEMPLATE:
                        q.put(key, filter.get("template") == null ? "no result" : filter.get("template"));
                        break;
                    case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID:
                        q.put(key, loginMemberId == null ? "no result" : loginMemberId);
                        break;
                    default:
                        throw new RuntimeException("could not find replaceble word");
                }
            }

        } else if (booleanValue != null) {
            q.put(key, booleanValue);
        } else if (strValue != null) {
            q.put(key, strValue);
        } else if (numberValue != null) {
            q.put(key, numberValue);
        } else if (listOfString != null) {
            q.put(key, new Document(DOLAR_IN, listOfString));
        } else if (listOfNumber != null) {
            q.put(key, new Document(DOLAR_IN, listOfNumber));
        } else {
            String type = d.get("type", String.class);
            if (type == null) {
                type = "string";
            }
            switch (type) {
                case "number":
                    q.put(key, d.get(VALUE, Number.class));
                    break;
                case "string":
                    q.put(key, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR));
                    break;
                case "in":
                    q.put(key, new Document(DOLAR_IN, Arrays.asList(d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR).split(","))));
                    break;
                case "ne":
                    q.put(key, new Document(DOLAR_NE, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR)));
                    break;
                case "regex":
                    q.put(key, new Document(DOLAR_REGEX, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR)));
                    break;
                default:
                    throw new UnsupportedOperationException("field.items.query.type is not supported  : " + type);
            }
        }
        return q;
    }

    private static Object aggregate(Document aggregateValue, FmsScriptRunner fmsScriptRunner, ObjectId loginMemberId) {
        String db = aggregateValue.getString("db");
        String table = aggregateValue.getString("table");
        List<Document> pipline = aggregateValue.getList("pipline", Document.class);
        String projection = aggregateValue.getString("projection");

        List<Document> modifiedPipline = new ArrayList<>();

        for (Document doc : pipline) {
            Document matchDoc = doc.get("$match", Document.class);
            if (matchDoc != null) {
                for (String key : matchDoc.keySet()) {
                    Object value = matchDoc.get(key);
                    if (ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID.equals(value)) {
                        matchDoc.put(key, loginMemberId);
                    } else {
                        matchDoc.put(key, value);
                    }
                }
            }
            modifiedPipline.add(doc);
        }

        List<Document> result = fmsScriptRunner.aggreagate(db, table, pipline);
        if (result.size() > 0) {
            return result.get(0).get(projection);
        }
        return null;
    }

}
