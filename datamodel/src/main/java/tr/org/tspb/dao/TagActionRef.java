package tr.org.tspb.dao;

import java.util.Arrays;
import java.util.Map;
import org.bson.Document;
import tr.org.tspb.constants.ProjectConstants;
import static tr.org.tspb.constants.ProjectConstants.DIEZ;
import static tr.org.tspb.constants.ProjectConstants.DOLAR;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_IN;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_NE;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_REGEX;
import static tr.org.tspb.constants.ProjectConstants.MEMBER;
import static tr.org.tspb.constants.ProjectConstants.PERIOD;
import static tr.org.tspb.constants.ProjectConstants.TEMPLATE;
import static tr.org.tspb.constants.ProjectConstants.VALUE;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;
import tr.org.tspb.pojo.UserDetail;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class TagActionRef {

    static Boolean calc(Document ref, Map filter, UserDetail userDetail, FmsScriptRunner fmsScriptRunner) {

        String db = ref.get("db", String.class);
        String table = ref.get("table", String.class);

        Document query = new Document();

        for (Document d : ref.get("query", Document.class).getList("list", Document.class)) {

            String key = d.get("key", String.class);

            String fmsValue = d.get("fms-value", String.class);
            String strValue = d.get("string-value", String.class);
            Number numberValue = d.get("number-value", Number.class);

            if (fmsValue != null) {
                switch (fmsValue) {
                    case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_MEMBER:
                        query.put(key, filter.get(MEMBER) == null ? "no result" : filter.get(MEMBER));
                        break;
                    case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_PERIOD:
                        query.put(key, filter.get(PERIOD) == null ? "no result" : filter.get(PERIOD));
                        break;
                    case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_TEMPLATE:
                        query.put(key, filter.get(TEMPLATE) == null ? "no result" : filter.get(TEMPLATE));
                        break;
                    case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID:
                        query.put(key, userDetail.getDbo().getObjectId() == null ? "no result" : userDetail.getDbo().getObjectId());
                        break;
                    default:
                        throw new RuntimeException("could not find replaceble word");
                }
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
