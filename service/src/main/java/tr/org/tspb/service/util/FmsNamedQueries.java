package tr.org.tspb.service.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.bson.Document;
import tr.org.tspb.constants.ProjectConstants;
import static tr.org.tspb.constants.ProjectConstants.CREATE_SESSIONID;
import static tr.org.tspb.constants.ProjectConstants.DIEZ;
import static tr.org.tspb.constants.ProjectConstants.DOLAR;
import static tr.org.tspb.constants.ProjectConstants.INCLUDE;
import static tr.org.tspb.constants.ProjectConstants.MONGO_LDAP_UID;
import static tr.org.tspb.constants.ProjectConstants.RETVAL;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.pojo.RoleMap;
import tr.org.tspb.pojo.UserDetail;
import tr.org.tspb.util.tools.MongoDbUtilIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class FmsNamedQueries {

    private final Document filter;

    public FmsNamedQueries(FmsForm myForm, RoleMap roleMap, UserDetail userDetail, MongoDbUtilIntr mongoDbUtil) throws NullNotExpectedException {

        Document myNamedQueries = myForm.getMyNamedQueries();

        filter = new Document();

        if (null == myNamedQueries) {
            return;
        }

        List<Document> includeQuery = myNamedQueries.getList(INCLUDE, Document.class);

        if (includeQuery != null) {
            runLoopBreak(includeQuery, roleMap, userDetail, myForm, mongoDbUtil);
        }

    }

    private void runLoopBreak(List<Document> includeQuery, RoleMap roleMap, UserDetail userDetail, FmsForm myForm, MongoDbUtilIntr mongoDbUtil) throws RuntimeException {

        Document noRolesDoc = null;
        boolean noRole = true;

        for (Document doc : includeQuery) {

            List<String> roles = doc.get("roles", List.class);
            if (roles == null) {
                noRolesDoc = doc;
            } else if (roleMap.isUserInRole(roles)) {
                noRole = false;
                applyQuery(doc, userDetail, myForm, mongoDbUtil, roleMap);
            }

        }

        if (noRole && noRolesDoc != null) {
            applyQuery(noRolesDoc, userDetail, myForm, mongoDbUtil, roleMap);
        }

    }

    private void applyQuery(Document doc, UserDetail userDetail, FmsForm myForm, MongoDbUtilIntr mongoDbUtil, RoleMap roleMap) throws RuntimeException {
        Document queryDoc = doc.get("query", Document.class);

        List<Document> list = queryDoc.get("list", List.class);

        String func = queryDoc.get("func", String.class);

        if (list != null) {

            for (Document document : list) {
                String key = document.get("key", String.class);
                String fmsValue = document.get("fms-value", String.class);
                String stringValue = document.get("string-value", String.class);
                Number numberValue = document.get("number-value", Number.class);

                if (fmsValue != null) {
                    switch (fmsValue) {
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID:
                            filter.put(key, userDetail.getDbo().getObjectId());
                            break;
                        default:
                    }
                } else if (stringValue != null) {
                    filter.put(key, stringValue);
                } else if (numberValue != null) {
                    filter.put(key, numberValue);
                } else {
                    throw new RuntimeException("cannot set quey value");
                }
            }

        } else if (func != null) {

            func = func.replace(DIEZ, DOLAR);
            Map map = new HashMap();
            map.put(MONGO_LDAP_UID, userDetail.getUsername());
            map.put(myForm.getLoginFkField(), filter.get(myForm.getLoginFkField()));
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext != null) {
                map.put(CREATE_SESSIONID, ((HttpSession) facesContext.getExternalContext().getSession(false)).getId());
            }
            Document commandResult = mongoDbUtil.runCommand(myForm.getDb(), func, map, roleMap.keySet());
            filter.putAll(commandResult.get(RETVAL, Document.class));
        }
    }

    public Document filter() {
        return filter;
    }

}
