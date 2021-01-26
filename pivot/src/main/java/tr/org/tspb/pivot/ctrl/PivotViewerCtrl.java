package tr.org.tspb.pivot.ctrl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.event.AjaxBehaviorEvent;
import org.bson.Document;
import tr.org.tspb.common.pojo.CellMultiDimensionKey;
import static tr.org.tspb.constants.ProjectConstants.ADMIN_QUERY;
import static tr.org.tspb.constants.ProjectConstants.HISTORY_QUERY;
import tr.org.tspb.common.qualifier.ViewerController;
import tr.org.tspb.common.qualifier.MyQualifier;
import tr.org.tspb.common.util.CustomOlapHashMap;
import tr.org.tspb.converter.base.MapConverter;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.MoreThenOneInListException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.exceptions.UserException;
import tr.org.tspb.pivot.datamodel.PivotDataModel;
import tr.org.tspb.pivot.datamodel.PivotDataModelReadonly;
import tr.org.tspb.util.stereotype.MyController;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyController
@MyQualifier(myEnum = ViewerController.viewerPivot)
public class PivotViewerCtrl extends PivotImpl {

    private MapConverter myMapConverter;
    private String calculateFormulaId;
    private PivotDataModel pivotDataModel;
    private Map<CellMultiDimensionKey, List<CustomOlapHashMap>> pivotData;

    public String getCalculateFormulaId() {
        return calculateFormulaId;
    }

    public void setCalculateFormulaId(String calculateFormulaId) {
        this.calculateFormulaId = calculateFormulaId;
    }

    public MapConverter getMyMapConverter() {
        if (myMapConverter == null) {
            myMapConverter = new MapConverter();
        }
        return myMapConverter;
    }

    public String getQuery() {
        if (loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminAndViewerRole())) {
            return ADMIN_QUERY;
        }
        return HISTORY_QUERY;
    }

    public void valueChangeListenerZet(AjaxBehaviorEvent event) {
        filterService.createPivotFilterHistory();
        try {
//            prepareJsfComponentMap(formService.getMyForm());
//            refreshRowData(formService.getMyForm());
            createDimensionIksIgrek(formService.getMyForm());
            refreshPivotData();
        } catch (NullNotExpectedException | MongoOrmFailedException | MoreThenOneInListException | UserException | FormConfigException ex) {
            logger.error("error occured", ex);
        }
    }

    public Map<String, String> drawGUI() throws Exception {

        createDimensionZet(formService.getMyForm());

        createDimensionIksIgrek(formService.getMyForm());

        refreshPivotData();

        return null;
    }

    private void refreshPivotData() throws NullNotExpectedException, MongoOrmFailedException,
            MoreThenOneInListException, UserException, FormConfigException {

        pivotData = dataService.createPivotData(formService.getMyForm(), getFilter(),
                getIksDimension(), getIgrekDimension(), getMapOfMeasureField());

        calcService.applyCalcCoordinates(pivotData, formService.getMyForm(), getFilter());

        cellControl = ctrlService.createPivotCtrlData(getFilter());

        pivotDataModel = new PivotDataModelReadonly(
                formService.getMyForm().getHandsonColWidths(),
                formService.getMyForm().getHandsonRowHeaderWidth(),
                getIksDimension(),
                getIgrekDimension(),
                pivotData,
                snapshotMapMultiDimension,
                cellControl,
                snapshotNullHandler);

        if (formService.getMyForm().getSnapshotCollection() != null && false) {
            snapshotMapMultiDimension = dataService.createPivotDataSnapshot(formService.getMyForm(), getFilter(),
                    getIksDimension(), getIgrekDimension(), getMapOfMeasureField());

            calcService.provideSnapshotCalculatedValues(snapshotMapMultiDimension, pivotData, formService.getMyForm(), getFilter());

            snapshotNullHandler = mongoDbUtil.findOne(formService.getMyForm().getDb(), "snapshotNullHandler",
                    new Document(getFilter()));
        }

    }

    public PivotDataModel getPivotDataModel() {
        return pivotDataModel;
    }

    public void cleanPivotData() {
        pivotData = new HashMap<>();
    }

    @Override
    public Document getFilter() {
        return filterService.getPivotFilterHistory();
    }

    public Map<CellMultiDimensionKey, List<CustomOlapHashMap>> getPivotData() {
        return pivotData;
    }

}
