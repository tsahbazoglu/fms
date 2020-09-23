package tr.org.tspb.service;

import tr.org.tspb.common.services.LoginController;
import tr.org.tspb.util.stereotype.MyServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.slf4j.Logger;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.pojo.ui.TwoDimDlgUi;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import tr.org.tspb.dao.MyFieldComparator;
import tr.org.tspb.exceptions.FormConfigException;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyServices
public class GuiService implements Serializable {

    @Inject
    @MyLoginQualifier
    private LoginController loginController;

    @Inject
    private ModelService modelService;

    @Inject
    private DataService dataService;

    @Inject
    private Logger logger;

    private String projectAndFormKey;
    private TwoDimDlgUi twoDimDlgUi;
    private MyForm selectedForm;

    public String createAndView() throws FormConfigException {
        try {
            selectedForm = modelService.createForm(projectAndFormKey);
            twoDimDlgUi = createTwoDimUi(selectedForm);
            dataService.create();
        } catch (NullNotExpectedException ex) {
            logger.error("error occured", ex);
        } catch (MongoOrmFailedException ex) {
            logger.error("error occured", ex);
        }
        return null;
    }

    private TwoDimDlgUi createTwoDimUi(MyForm myForm) {

        TwoDimDlgUi twoDimDlgUi = new TwoDimDlgUi();

        twoDimDlgUi.setHeader(myForm.getName());

        Map<String, MyField> ui = new HashMap();
        List< MyField> list = new ArrayList<>();

        twoDimDlgUi.setComponentMap(ui);
        twoDimDlgUi.setComponentList(list);

        for (String key : myForm.getFieldsKeySet()) {

            MyField fieldStructure = myForm.getField(key);

            if (Tag.INPUT_FILE.getComponentName().equals(fieldStructure.getComponentType())) {
                twoDimDlgUi.setFileLimit(fieldStructure.getFileLimit());
                if (fieldStructure.getFileType() != null) {
                    switch (fieldStructure.getFileType()) {
                        case "pdf":
                            twoDimDlgUi.setMongoUploadFileType("/(\\.|\\/)(pdf)$/");
                            twoDimDlgUi.setInvalidFileMessage("Geçersiz Dosya Tipi (Sadece PDF dosyalar eklenebilir) : ");
                            break;
                        case "image":
                            twoDimDlgUi.setMongoUploadFileType("/(\\.|\\/)(jpg|png|JPEG|JPG|PNG)$/");
                            twoDimDlgUi.setInvalidFileMessage("Geçersiz Dosya Tipi (Sadece resim [jpg, png, JPEG, JPG, PNG] formatında dosyalar eklenebilir) : ");
                            break;
                        default:
                            twoDimDlgUi.setMongoUploadFileType("/(\\.|\\/)(pdf)$/");
                            twoDimDlgUi.setInvalidFileMessage("Geçersiz Dosya Tipi (Sadece PDF dosyalar eklenebilir) : ");
                            break;
                    }
                }
            }

            if (!Tag.AUTO_COMPLETE.getComponentName().equals(fieldStructure.getComponentType())) {
                fieldStructure.createSelectItems(
                        new HashMap(),
                        new MyMap(),
                        loginController.getRoleMap(), loginController.getLoggedUserDetail(),
                        false);
            }

            fieldStructure.setReadonly(fieldStructure.isReadonly() || !myForm.getMyActions().isSave());

            ui.put(key, fieldStructure);
            list.add(fieldStructure);
        }

        Collections.sort(list, new MyFieldComparator());

        return twoDimDlgUi;
    }

    public TwoDimDlgUi getTwoDimDlgUi() {
        return twoDimDlgUi;
    }

    public void setTwoDimDlgUi(TwoDimDlgUi twoDimDlgUi) {
        this.twoDimDlgUi = twoDimDlgUi;
    }

    public MyForm getSelectedForm() {
        return selectedForm;
    }

    public void setSelectedForm(MyForm selectedForm) {
        this.selectedForm = selectedForm;
    }

    private enum Tag {
        INPUT_FILE("inputFile"),
        AUTO_COMPLETE("inputFile");

        private String componentName;

        private Tag(String componentName) {
            this.componentName = componentName;

        }

        public String getComponentName() {
            return componentName;
        }
    }

    public String getProjectAndFormKey() {
        return projectAndFormKey;
    }

    public void setProjectAndFormKey(String projectAndFormKey) {
        this.projectAndFormKey = projectAndFormKey;
    }

}
