package tr.org.tspb.table;

import tr.org.tspb.datamodel.gui.FmsTableDataModel;
import tr.org.tspb.exceptions.NullNotExpectedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.bson.Document;
import static tr.org.tspb.constants.ProjectConstants.*;
import tr.org.tspb.converter.base.SelectOneStringConverter;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyFieldReportComparator;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.factory.qualifier.OgmCreatorQualifier;
import tr.org.tspb.factory.cp.OgmCreatorIntr;
import tr.org.tspb.service.FeatureService;

/**
 *
 * @author Telman Şahbazoğlu
 */
public abstract class FmsTableView extends AbstractViewer {

    @Inject
    private FeatureService featureService;

    @Inject
    @OgmCreatorQualifier
    private OgmCreatorIntr ogmCreator;

    private List<MyField> objectsColumnDataModel = new ArrayList<>();

    /**
     * @return the objectsColumnDataModel
     */
    public List<MyField> getObjectsColumnDataModel() {
        return Collections.unmodifiableList(objectsColumnDataModel);
    }

    public void drawGUI(FmsForm myForm) throws Exception {
        drawGUI(myForm, filterService.getBaseFilterCurrent());
    }

    public void drawGUI(FmsForm myForm, Document filter) throws Exception {

        if (myForm == null || myForm.getKey() == null || SelectOneStringConverter.NULL_VALUE.equals(myForm.getKey())) {
            throw new NullNotExpectedException("myForm = null");
        }

        if (myForm.getAccessControlLevelTwo() != null) {
            Document accessControlLevelTwo = myForm.getAccessControlLevelTwo();

            String db = (String) accessControlLevelTwo.get(FORM_DB);
            String collection = (String) accessControlLevelTwo.get(COLLECTION);
            String returnKey = (String) accessControlLevelTwo.get(RETURN_KEY);
            Document message = (Document) accessControlLevelTwo.get(MESSAGE);
            Document query = (Document) accessControlLevelTwo.get(QUERY);

            query = mongoDbUtil.replaceToDollar(query);

            Document result = mongoDbUtil.findOne(db, collection, query);

            if (result == null) {
                dialogController.showPopupInfo((String) message.get("norecord"), MESSAGE_DIALOG);
                return;
            }

            String returnValue = (String) result.get(returnKey);

            if (HAYIR.equals(returnValue)) {
                dialogController.showPopupInfo((String) message.get("noresponse"), MESSAGE_DIALOG);
            }
        }

        if (myForm.getFieldsKeySet() == null) {
            throw new NullNotExpectedException("form fields = null");
        }

        if (myForm.getDb() == null) {
            throw new NullNotExpectedException("db attribute is resolved to null");
        }

        this.objectsColumnDataModel = createColumnFields(myForm);

        setData(new FmsTableDataModel(this));

        featureService.getEsignDoor().initEsignCtrlV2(formService.getMyForm(), null, MULTIPLE);

    }

    private List<MyField> createColumnFields(final FmsForm myForm) {

        List<MyField> columnList = new ArrayList<>();

        if (myForm != null) {
            for (String key : myForm.getFieldsKeySet()) {
                MyField myField = myForm.getField(key);

                if (myField.isReportRendered()) {

                    String accesscontrol = myField.getAccesscontrol();
                    String visibleValue = myField.getVisible();

                    if (accesscontrol == null && visibleValue == null) {
                        continue;
                    }

                    if (loginController.isUserInRole(accesscontrol) || loginController.isUserInRole(visibleValue)) {
                        columnList.add(myField);
                    }
                }
            }
        }

        Collections.sort(columnList, new MyFieldReportComparator());

        return columnList;
    }

}
