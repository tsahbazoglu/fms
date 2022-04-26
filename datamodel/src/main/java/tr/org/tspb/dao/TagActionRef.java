package tr.org.tspb.dao;

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
import static tr.org.tspb.constants.ProjectConstants.MEMBER;
import static tr.org.tspb.constants.ProjectConstants.PERIOD;
import static tr.org.tspb.constants.ProjectConstants.REPLACEABLE_KEY_FMS_ID_VALUE;
import static tr.org.tspb.constants.ProjectConstants.REPLACEABLE_KEY_FMS_VALUE;
import static tr.org.tspb.constants.ProjectConstants.TEMPLATE;
import static tr.org.tspb.constants.ProjectConstants.VALUE;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;
import tr.org.tspb.pojo.UserDetail;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class TagActionRef {

    static Pattern pattern_fms_crud = Pattern.compile("fms_crud\\{\\{(.*?)\\}\\}");
    static Pattern pattern_fms_filter = Pattern.compile("fms_filter\\{\\{(.*?)\\}\\}");

    static Boolean calc(Document ref, Map filter, UserDetail userDetail, FmsScriptRunner fmsScriptRunner, MyMap crudObject) {

        String db = ref.get("db", String.class);
        String table = ref.get("table", String.class);

        Document query = new Document();

        String func = ref.get("query", Document.class).getString("func");
        List<Document> list = ref.get("query", Document.class).getList("list", Document.class);

        for (Document d : list) {

            String key = d.get("key", String.class);

            String fmsValue = d.get(REPLACEABLE_KEY_FMS_VALUE, String.class);
            String fmsIdValue = d.get(REPLACEABLE_KEY_FMS_ID_VALUE, String.class);
            String strValue = d.get("string-value", String.class);
            Number numberValue = d.get("number-value", Number.class);

            if (fmsIdValue != null) {
                Matcher crudMatcher = pattern_fms_crud.matcher(fmsIdValue);
                if (crudMatcher.find()) {
                    Object crudValue = crudObject == null ? null : crudObject.get(crudMatcher.group(1));
                    query.put(key, crudValue instanceof ObjectId ? "no result" : crudValue);
                } else {
                    Object crudValue;
                    switch (fmsIdValue) {
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_MEMBER:
                            crudValue = filter.get(MEMBER);
                            break;
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_PERIOD:
                            crudValue = filter.get(PERIOD);
                            break;
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_TEMPLATE:
                            crudValue = filter.get(TEMPLATE);
                            break;
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID:
                            crudValue = userDetail.getDbo().getObjectId();
                            break;
                        default:
                            throw new RuntimeException("could not find replaceble word");
                    }
                    query.put(key, crudValue instanceof ObjectId ? crudValue : "no result");
                }
            } else if (fmsValue != null) {
                Matcher m;
                Object crudValue = null;
                if ((m = pattern_fms_crud.matcher(fmsValue)).find()) {
                    crudValue = crudObject == null ? null : crudObject.get(m.group(1));
                } else if ((m = pattern_fms_filter.matcher(fmsValue)).find()) {
                    crudValue = filter == null ? null : filter.get(m.group(1));
                } else {
                    switch (fmsValue) {
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_MEMBER:
                            crudValue = filter.get(MEMBER);
                            break;
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_PERIOD:
                            crudValue = filter.get(PERIOD);
                            break;
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_TEMPLATE:
                            crudValue = filter.get(TEMPLATE);
                            break;
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID:
                            crudValue = userDetail.getDbo().getObjectId();
                            break;
                        default:
                            throw new RuntimeException("could not find replaceble word");
                    }
                }
                query.put(key, crudValue == null ? "no result" : crudValue);
            } else if (strValue != null) {
                query.put(key, strValue);
            } else if (numberValue != null) {
                query.put(key, numberValue);
            } else {

                String type = d.get("type", String.class);
                if (type == null) {
                    type = "string";
                }
                switch (type) {
                    case "number":
                        query.put(key, d.get(VALUE, Number.class));
                        break;
                    case "string":
                        query.put(key, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR));
                        break;
                    case "in":
                        query.put(key, new Document(DOLAR_IN, Arrays.asList(d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR).split(","))));
                        break;
                    case "ne":
                        query.put(key, new Document(DOLAR_NE, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR)));
                        break;
                    case "regex":
                        query.put(key, new Document(DOLAR_REGEX, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR)));
                        break;
                    default:
                        throw new UnsupportedOperationException("field.items.query.type is not supported  : " + type);
                }
            }
        }

        Document doc = fmsScriptRunner.findOne(db, table, query);

        return doc != null;

    }

}
