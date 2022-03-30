package tr.org.tspb.datamodel.gui;

import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import org.bson.Document;
import org.junit.experimental.theories.FromDataPoints;
import static tr.org.tspb.constants.ProjectConstants.FORM_KEY;
import static tr.org.tspb.constants.ProjectConstants.PROJECT_KEY;
import static tr.org.tspb.constants.ProjectConstants.NAME;
import static tr.org.tspb.constants.ProjectConstants.MENU_ORDER;
import tr.org.tspb.dao.MyProject;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class ModuleItem {

    private final String projectKey;
    private final String moduleKey;
    private final String name;
    private List<FormDef> myLinks;
    private final boolean rowSelected;
    private final int menuOrder;
    private MyProject project;

    public ModuleItem(Map<String, Object> moduleDoc) {
        this.projectKey = moduleDoc.get(PROJECT_KEY).toString();
        this.moduleKey = moduleDoc.get(FORM_KEY).toString();
        this.name = moduleDoc.get(NAME).toString();
        this.rowSelected = false;
        this.menuOrder = ((Number) moduleDoc.get(MENU_ORDER)).intValue();
    }

    public ModuleItem(Document moduleDoc, MyProject myProject) {
        this(moduleDoc);
        this.project = myProject;
    }

    public int getMenuOrder() {
        return menuOrder;
    }

    public String getModuleKey() {
        return moduleKey;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void createList(List<FormDef> forms) {
        this.myLinks = forms;
    }

    public List<FormDef> getMyLinks() {
        return myLinks;
    }

    public boolean isRowSelected() {
        return rowSelected;
    }

    public String getName() {
        return name;
    }

    public MyProject getProject() {
        return project;
    }

}
