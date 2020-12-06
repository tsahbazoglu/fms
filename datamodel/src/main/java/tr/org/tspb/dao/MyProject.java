package tr.org.tspb.dao;

import java.util.List;
import static tr.org.tspb.constants.ProjectConstants.*;
import java.util.Map;
import org.bson.Document;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyProject {

    private String configCollection;
    private String adminRole;
    private String viewerRole;
    private String userRole;
    private String adminAndViewerRole;
    private String welcomePage;
    private final Document loginDetailQuery;
    private final String loginDetailDb;
    private final String loginDetailTable;
    private final String loginDetailLdapUID;
    private final String key;
    private Document registredFunctions;

    public MyProject(Map docProject, String loginDetailDb, String loginDetailTable, String loginDetailLdapUID, Document loginDetailQuery) {
        this.configCollection = docProject.get(CONFIG_COLLECTIONS).toString();
        this.adminRole = docProject.get(ADMIN_ROLE).toString();
        this.adminAndViewerRole = adminRole;
        if (docProject.get(VIEWER_ROLE) != null) {
            this.viewerRole = docProject.get(VIEWER_ROLE).toString();
            this.adminAndViewerRole = adminRole.concat(COMMA).concat(viewerRole);
        }
        if (docProject.get(USER_ROLE) != null) {
            this.userRole = docProject.get(USER_ROLE).toString();
        }
        this.welcomePage = docProject.get(WELCOME_PAGE).toString();

        this.key = docProject.get(FORM_KEY).toString();

        this.loginDetailDb = loginDetailDb;
        this.loginDetailTable = loginDetailTable;
        this.loginDetailLdapUID = loginDetailLdapUID;
        this.loginDetailQuery = loginDetailQuery;

        this.maskRegistredFunctions(new Document(docProject));

    }

    public Document getRegistredFunctions() {
        return registredFunctions;
    }

    public String getAdminRole() {
        return adminRole;
    }

    public String getConfigTable() {
        return configCollection;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getViewerRole() {
        return viewerRole;
    }

    public String getAdminAndViewerRole() {
        return adminAndViewerRole;
    }

    public String getWelcomePage() {
        return welcomePage;
    }

    public Document getLoginDetailQuery() {
        return loginDetailQuery;
    }

    public String getLoginDetailDb() {
        return loginDetailDb;
    }

    public String getLoginDetailTable() {
        return loginDetailTable;
    }

    public String getLoginDetailLdapUid() {
        return loginDetailLdapUID;
    }

    public String getKey() {
        return key;
    }

    public final void maskRegistredFunctions(Document docProject) {

        List<Document> listDoc = docProject.getList("registred-functions", Document.class);

        if (listDoc != null) {
            this.registredFunctions = new Document();
            for (Document document : listDoc) {
                this.registredFunctions.append(document.getString("key"), document.getString("value"));
            }
        }

    }

}
