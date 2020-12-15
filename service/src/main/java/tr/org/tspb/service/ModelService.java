package tr.org.tspb.service;

import com.mongodb.client.model.Filters;
import tr.org.tspb.common.services.LoginController;
import tr.org.tspb.util.stereotype.MyServices;
import javax.inject.Inject;
import org.bson.Document;
import static tr.org.tspb.constants.ProjectConstants.FORM_KEY;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyProject;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import tr.org.tspb.common.services.BaseService;
import tr.org.tspb.constants.ProjectConstants;
import static tr.org.tspb.constants.ProjectConstants.CFG_TABLE_PROJECT;
import tr.org.tspb.dao.MyActions;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.factory.qualifier.OgmCreatorQualifier;
import tr.org.tspb.factory.cp.OgmCreatorIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyServices
public class ModelService extends CommonSrv {

    @Inject
    @MyLoginQualifier
    private LoginController loginController;

    @Inject
    @OgmCreatorQualifier
    private OgmCreatorIntr ogmCreator;

    @Inject
    private BaseService baseService;

    private String projectAndFormKey;

    private MyForm selectedForm;

    public MyForm createForm(String projectAndFormKey) throws NullNotExpectedException, MongoOrmFailedException, FormConfigException {

        String[] selectedFormInfos = projectAndFormKey.split("[,]");
        String formKey = selectedFormInfos[0];
        String projectKey = selectedFormInfos[1];

        MyProject myProject = ogmCreator
                .getMyProject(mongoDbUtil
                        .findOne(ProjectConstants.CONFIG_DB, CFG_TABLE_PROJECT, Filters.eq(FORM_KEY, projectKey)),
                        baseService.getTagLogin());

        MyForm myForm = ogmCreator
                .getMyFormLarge(myProject, myProject.getConfigTable(), Filters.eq(FORM_KEY, formKey), new Document(),
                        loginController.getRoleMap(), loginController.getLoggedUserDetail());

        MyActions myActions = ogmCreator
                .getMyActions(myForm, loginController.getRoleMap(), new Document(), loginController.getLoggedUserDetail());

        myForm.initActions(myActions);

        return myForm;
    }

    public MyForm getSelectedForm() {
        return selectedForm;
    }

    public void setSelectedForm(MyForm selectedForm) {
        this.selectedForm = selectedForm;
    }

    public String getProjectAndFormKey() {
        return projectAndFormKey;
    }

    public void setProjectAndFormKey(String projectAndFormKey) {
        this.projectAndFormKey = projectAndFormKey;
    }

}
