package tr.org.tspb.pivot.ctrl;

import tr.org.tspb.pivot.datamodel.PivotDataModelHandson;
import tr.org.tspb.pivot.datamodel.PivotDataModelReadonly;
import tr.org.tspb.pivot.datamodel.PivotDataModel;
import tr.org.tspb.service.RepositoryService;
import static tr.org.tspb.constants.ProjectConstants.ADMIN_QUERY;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_SET;
import static tr.org.tspb.constants.ProjectConstants.FIELDS_ROW;
import static tr.org.tspb.constants.ProjectConstants.PERIOD;
import static tr.org.tspb.constants.ProjectConstants.QUERY;
import com.google.gson.Gson;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.common.util.CustomOlapHashMap;
import tr.org.tspb.common.qualifier.ViewerController;
import tr.org.tspb.common.qualifier.MyQualifier;
import java.text.NumberFormat;
import java.util.*;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import org.bson.types.ObjectId;
import tr.org.tspb.util.stereotype.MyController;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Code;
import tr.org.tspb.common.pojo.CellMultiDimensionKey;
import tr.org.tspb.common.services.LoginController;
import static tr.org.tspb.constants.ProjectConstants.DIEZ;
import static tr.org.tspb.constants.ProjectConstants.DOLAR;
import static tr.org.tspb.constants.ProjectConstants.FORMS;
import static tr.org.tspb.constants.ProjectConstants.FORM_DB;
import static tr.org.tspb.constants.ProjectConstants.MEASURE;
import static tr.org.tspb.constants.ProjectConstants.MESSAGE;
import static tr.org.tspb.constants.ProjectConstants.MESSAGE_DIALOG;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import static tr.org.tspb.constants.ProjectConstants.ON_USER_ROLE;
import static tr.org.tspb.constants.ProjectConstants.OPERATOR_LDAP_UID;
import static tr.org.tspb.constants.ProjectConstants.RESULT;
import static tr.org.tspb.constants.ProjectConstants.RETVAL;
import static tr.org.tspb.constants.ProjectConstants.TYPE;
import static tr.org.tspb.constants.ProjectConstants.UPSERT_DATE;
import static tr.org.tspb.constants.ProjectConstants.VALUE;
import tr.org.tspb.converter.base.MoneyConverter;
import tr.org.tspb.converter.base.NumberConverter;
import tr.org.tspb.converter.base.SelectOneObjectIdConverter;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.MoreThenOneInListException;
import tr.org.tspb.exceptions.UserException;
import tr.org.tspb.pojo.MyConstraintFormula;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyController
@MyQualifier(myEnum = ViewerController.crudPivot)
public class PivotModifierCtrl extends PivotImpl {

    private List<MyConstraintFormula> successList;
    private List<MyConstraintFormula> failList = new ArrayList<>();

    private static final String LICENSE = "license";
    private static final String DATA_STATUS = "dataStatus";
    private static final String SECINIZ = " seçiniz";
    List<String> selectedFormMessages;

    private PivotDataModel pivotDataModelEdit;
    private PivotDataModel pivotDataModelRead;

    private boolean editable = false;

    protected Map<CellMultiDimensionKey, List<CustomOlapHashMap>> pivotData;

    @Inject
    RepositoryService repositoryService;

    private String saveObject() throws Exception {

        calcService.createPivotCalcData(pivotData, true, true, formService.getMyForm(), getFilter());

        Map<String, Object> autosetMapValues = new HashMap<>();

        for (MyField myField : formService.getMyForm().getAutosetFields()) {

            Object autosetKeyValue = getSearchObjectValue(myField.getKey());

            if (autosetKeyValue == null) {
                throw new UserException("Lütfen ".concat(myField.getName()).concat(SECINIZ));
            }

            if (autosetKeyValue instanceof ObjectId) {
                if (SelectOneObjectIdConverter.NULL_VALUE.equals(autosetKeyValue)) {
                    throw new UserException("Lütfen ".concat(myField.getName()).concat(SECINIZ));
                }
                autosetMapValues.put(myField.getKey(), autosetKeyValue);
            } else {
                throw new UserException("Lütfen ".concat(myField.getName()).concat(SECINIZ));
            }
        }

        Object member = autosetMapValues.get(formService.getMyForm().getLoginFkField());

        if (member == null) {
            throw new UserException("Veri ile Kurum ilişkilendirililemedi.");
        }

        /**
         * //FIXME burada uysdb değeri selectedForm üzerinden değil
         * db.ldapMatch.findOne() üzerinden yapılmalı.
         *
         * Cünkü farklı projeler farklı kullnıcı listesine sahip olabilir
         * ileride ama şimdilik hepsi uysdb projesi ile ortak
         */
        if (!loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminRole())//
                && (!loginController.getLoggedUserDetail().getDbo().getObjectId().equals(member))) {
            //FIXME messagebundle
            throw new UserException("Seçilen Kurum Sizin Kurum Değil");
        }

        // We need to save only Dimension-"Dimension" coordinates 
        // Below loop logic eliminate possibility of Dimension-"Measure" coordinates
        Map<CellMultiDimensionKey, Document> eliminatedToPureDimesnionCoordianteMap = new HashMap();

        for (CellMultiDimensionKey cellMultiKey : pivotData.keySet()) {

            List<CustomOlapHashMap> list = pivotData.get(cellMultiKey);

            if (list.size() > 1) {
                String errorMessage = "More than one record had been deteced on the cell. " + cellMultiKey;
                throw new Exception(errorMessage);
            }

            CellMultiDimensionKey cellMultiDimensionKeyMap = eliminateMeasureCoordinate(cellMultiKey);

            /**
             * we provide mapReduce emit phase by NULL checking
             */
            if (eliminatedToPureDimesnionCoordianteMap.get(cellMultiDimensionKeyMap) == null) {
                eliminatedToPureDimesnionCoordianteMap.put(cellMultiDimensionKeyMap, new Document());
            }

            CustomOlapHashMap customOlapHashMap = list.get(0);

            /**
             * if it is a calculate field then calculated values should be saved
             * for reports and "QUICK" constraint formulas
             *
             * if (Boolean.TRUE.equals(customOlapHaspMap.get(CALCULATE))) {
             *
             * logger.info("calculated value going to be
             * saved".concat(customOlapHaspMap.toString())); //continue; }
             */
            if (customOlapHashMap.getField() == null) {
                throw new Exception(customOlapHashMap.toString()//
                        .concat(" does not have a \"field\" key<br/>")//
                        .concat("It seems the new record Map missed the \"field\" key."));
            }

            eliminatedToPureDimesnionCoordianteMap.get(cellMultiDimensionKeyMap).putAll(customOlapHashMap.toDocument());

        }

        /**
         * prepare and save
         */
        Date upsertDate = new Date();

        for (CellMultiDimensionKey cellMultiDimensionKey : eliminatedToPureDimesnionCoordianteMap.keySet()) {
            Document recordMapValue = eliminatedToPureDimesnionCoordianteMap.get(cellMultiDimensionKey);
            Document indexRecord = new Document();

            /**
             * Should be merged with its map from mapReduce
             */
            for (MyField myField : cellMultiDimensionKey.getCoordinates()) {
                String fieldKey = myField.getKey();
                indexRecord.put(fieldKey, myField.getId());
                //FIXME we just hope that forms and field key have the same value
                if (indexRecord.get(fieldKey) == null && myField.getItemsAsMyItems() == null) {
                    indexRecord.put(fieldKey, myField.getDefaultValue());
                }
            }

            indexRecord.put(FORMS, formService.getMyForm().getKey());
            indexRecord.putAll(autosetMapValues);

            recordMapValue.putAll(indexRecord);
            recordMapValue.put(OPERATOR_LDAP_UID, loginController.getLoggedUserDetail().getUsername());
            recordMapValue.put(UPSERT_DATE, upsertDate);

            if (recordMapValue.get(MONGO_ID, ObjectId.class) == null) {
                Document index = new Document();
                for (String key : indexRecord.keySet()) {
                    index.put(key, true);
                }
                mongoDbUtil.createIndex(formService.getMyForm(), index);
                mongoDbUtil.updateMany(formService.getMyForm(), indexRecord, new Document(recordMapValue),
                        new UpdateOptions().upsert(true));
            } else {
                Bson searchPart = Filters.eq(MONGO_ID, recordMapValue.get(MONGO_ID, ObjectId.class));
//                    if (true) {//tarihçe alınsın mı .detect from databankOrganizationStatus
//                        //retrieve current value from db
//                        DBObject dbObject = dbCollection.findOne(searchPart);
//                        Object currentValue = dbObject.get("tradedVolume");
//                        Object nextValue = recordMapValue.get("tradedVolume");
//                        //is there any change
//                        if (nextValue != null && !nextValue.equals(currentValue)) {
//                            updatePart.put("$push", new Document("cellVersion", currentValue));
//                        }
//                    }
                mongoDbUtil.updateMany(formService.getMyForm(), searchPart, recordMapValue);
            }
        }

        Document trigger = formService.getMyForm().getEventPostSave();

        if (trigger == null || !"application".equals(trigger.get(TYPE))) {
            mongoDbUtil.trigger(new Document(getFilter()), trigger, loginController.getRolesAsList());
        }

        refreshPivotData();

        return null;
    }

    private CellMultiDimensionKey eliminateMeasureCoordinate(CellMultiDimensionKey cellMultiKey) {
        /**
         *
         * Runtime MapReduce base idea here is an idea that all record with the
         * data dimension combination object assessed as a new record
         *
         * by excluding DIMENSION_AND_MESURE coordinate from cellMultiKey we can
         * easily achieve MAP PHASE of the "MapReduce Idea"
         */
        /**
         * MAP PHASE
         */
        List<MyField> coordinates = new ArrayList<>(cellMultiKey.getCoordinates());
        for (MyField coordinate : cellMultiKey.getCoordinates()) {
            String ndType = coordinate.getNdType();
            if (MEASURE.equals(ndType)) {
                coordinates.remove(coordinate);
            }
        }
        return new CellMultiDimensionKey(coordinates);
    }

    protected String deleteObject(LoginController loginMB, MyForm myForm, MyMap crudObject) throws Exception {
        String collection = myForm.getTable();
        mongoDbUtil.deleteMany(formService.getMyForm().getDb(), collection, new Document(getFilter()));

        refreshPivotData();

        return null;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void cleanPivotData() {
        pivotData = new HashMap<>();
    }

    private void refreshPivotData() throws NullNotExpectedException, MongoOrmFailedException,
            MoreThenOneInListException, UserException, FormConfigException {

        dataService.cleanMapReduce(getFilter());

        pivotData = dataService.createPivotData(formService.getMyForm(), getFilter(),
                getIksDimension(), getIgrekDimension(), getMapOfMeasureField());

        calcService.applyCalcCoordinates(pivotData, formService.getMyForm(), getFilter());

        cellControl = ctrlService.createPivotCtrlData(getFilter());

        createPivotDataModel();

        if (formService.getMyForm().getSnapshotCollection() != null && false) {
            snapshotMapMultiDimension = dataService.createPivotDataSnapshot(formService.getMyForm(), getFilter(),
                    getIksDimension(), getIgrekDimension(), getMapOfMeasureField());
            calcService.provideSnapshotCalculatedValues(snapshotMapMultiDimension, pivotData, formService.getMyForm(), getFilter());
            snapshotNullHandler = mongoDbUtil.findOne(formService.getMyForm().getDb(), "snapshotNullHandler",
                    new Document(getFilter()));
        }

    }

    public String showEimza22() {

        Document dBObject = new Document();
        dBObject.put(FORMS, formService.getMyForm().getForm());
        dBObject.put(formService.getMyForm().getLoginFkField(), getSearchObjectValue(formService.getMyForm().getLoginFkField()));

        if (formService.getMyForm().getField(PERIOD) != null) {
            dBObject.put(PERIOD, getSearchObjectValue(PERIOD));
        }

        List<Map> list = repositoryService
                .list(formService.getMyForm().getDb(), formService.getMyForm().getTable(), dBObject);

        if (list.isEmpty()) {
            //FIXME messagebundle
            dialogController.showPopupWarning("İmzalanacak Kayıtlı Veriniz Tespit Edilemedi.", "2d_dialogControlAndSave");
        } else {
            List<Document> sortReferns = mongoDbUtil.find("iondb", "setting_license_type_deneme");
            final Map<ObjectId, Integer> sortMap = new HashMap();
            for (Document nextElement : sortReferns) {
                sortMap.put((ObjectId) nextElement.get(MONGO_ID), ((Number) nextElement.get("orderno")).intValue());
            }

            Collections.sort(list, new Comparator<Map>() {
                @Override
                public int compare(Map t1, Map t2) {

                    Integer order1 = sortMap.get(t1.get(LICENSE));
                    Integer order2 = sortMap.get(t2.get(LICENSE));
                    if (order1 == null) {
                        order1 = 0;
                    }
                    if (order2 == null) {
                        order2 = 0;
                    }

                    return order1.compareTo(order2);
                }
            });

            esignDoor.initAndShowEsignDlg(list, formService.getMyForm(), "eimzaDialogND", null);

        }

        return null;
    }

    public Boolean getSelectedFormUserNote() {
        return formService.getMyForm() != null //
                && formService.getMyForm().getUserNote() != null //
                && !formService.getMyForm().getUserNote().isEmpty();
    }

    public void valueChangeListenerZet(AjaxBehaviorEvent event) {
        filterService.createPivotFilterCurrent();
        try {
            createDimensionIksIgrek(formService.getMyForm());
            refreshPivotData();
        } catch (NullNotExpectedException | MongoOrmFailedException | MoreThenOneInListException | UserException | FormConfigException ex) {
            logger.error("error occured", ex);
        }
    }

    private void createPivotDataModel() {
        if (editable) {
            pivotDataModelEdit = new PivotDataModelHandson(
                    formService.getMyForm().getHandsonColWidths(),
                    formService.getMyForm().getHandsonRowHeaderWidth(),
                    getIksDimension(),
                    getIgrekDimension(),
                    pivotData,
                    cellControl);
        } else {
            pivotDataModelRead = new PivotDataModelReadonly(
                    formService.getMyForm().getHandsonColWidths(),
                    formService.getMyForm().getHandsonRowHeaderWidth(),
                    getIksDimension(),
                    getIgrekDimension(),
                    pivotData,
                    snapshotMapMultiDimension,
                    cellControl,
                    snapshotNullHandler);
        }
    }

    public Map<String, String> drawGUI() throws Exception {

        createDimensionZet(formService.getMyForm());

        createDimensionIksIgrek(formService.getMyForm());

        accesControlLevel2();

        // There is a requirenmenet to split editable and saveble properties
        setEditable(formService.getMyForm().getMyActions().isSave());

        selectedFormMessages = createFormMsg(formService.getMyForm());

        Document trigger = formService.getMyForm().getEventFormSelection();
        if (trigger != null && "showWarnErrPopup".equals(trigger.get(TYPE))) {
            dialogController.showPopupInfoWithOk(trigger.get(MESSAGE).toString(), MESSAGE_DIALOG);
        }

        refreshPivotData();

        return null;
    }

    private void accesControlLevel2() {
        if (formService.getMyForm().getAccessControlLevelTwo() != null) {

            Document accessControlLevelTwo = formService.getMyForm().getAccessControlLevelTwo();
            if (loginController.isUserInRole(accessControlLevelTwo.get(ON_USER_ROLE))) {
                String db = (String) accessControlLevelTwo.get(FORM_DB);
                Code func = (Code) accessControlLevelTwo.get("func");

                if (func != null) {
                    func = new Code(func.getCode().replace(DIEZ, DOLAR));
                    Document commandResult = mongoDbUtil.runCommand(db, func.getCode(), getFilter());

                    Document value = (Document) commandResult.get(RETVAL);

                    int result = ((Number) value.get(RESULT)).intValue();
                    String message = value.getString(MESSAGE);

                    if (result == 1) {
//                            throw new UserException(//
//                                    ERROR,
//                                    "",
//                                    message);
                        dialogController.showPopupInfo(message, MESSAGE_DIALOG);

                    } else if (result == 2) {

                        dialogController.showPopupInfo(message, MESSAGE_DIALOG);
                    }
                }
            }
        }
    }

    public String showAllNote() {
        if (formService.getMyForm() != null) {
            String text = formService.getMyForm().getUserNote();
            if (text != null) {
                dialogController.showPopupInfo(text, MESSAGE_DIALOG);
            }
        }
        return null;
    }

    public List findList(MyForm myForm, Document searcheDBObject) throws Exception {
        for (String key : new HashSet<>(searcheDBObject.keySet())) {
            if (myForm.getField(key) == null) {
                searcheDBObject.remove(PERIOD);
            }
        }
        return repositoryService.findList(myForm, searcheDBObject, 1000);
    }

    public String getReportWebOzetPath() {
        return baseService.getProperties().getTmpDownloadPath();
    }

    public String downloadCsvFile() {
//        downloadService.downloadCsvFilePivot(getIksDimension(), getIgrekDimension(),
//                mapMultiDimension, pivotViewerController.mapMultiDimension);
        return null;
    }

    private void cleanData() {
        for (CellMultiDimensionKey cellMultiDimensionKey : pivotData.keySet()) {
            List<CustomOlapHashMap> customOlapHaspMaps = pivotData.get(cellMultiDimensionKey);
            for (CustomOlapHashMap customOlapHaspMap : customOlapHaspMaps) {
                customOlapHaspMap.resetValue();
            }
        }
        pivotDataModelEdit = new PivotDataModelHandson(
                formService.getMyForm().getHandsonColWidths(),
                formService.getMyForm().getHandsonRowHeaderWidth(),
                getIksDimension(),
                getIgrekDimension(),
                pivotData,
                cellControl);
    }

    public void submitRowData(AjaxBehaviorEvent event) {
        try {
            if ("001".equals(event.getComponent().getAttributes().get(VALUE))) {// FIXME move to engine-config pattern
                cleanData();
                setEditable(false);
            } else if ("000".equals(rowObject.get(DATA_STATUS))) {
                refreshPivotData();
                setEditable(formService.getMyForm().getMyActions().isSave());
            } else {
                setEditable(false);
                //FIXME messagebundle
                dialogController.showPopupError("\"Bildirime konu veri bulunuyor mu?\" alanı zorunlu alandır");
            }
        } catch (Exception ex) {
            logger.error("error occured", ex);
        }
    }

    public String provideCrossCheck() {

        if (formService.getMyForm().getFieldsRowKeys() != null && !formService.getMyForm().getFieldsRowKeys().isEmpty()) {

            if (!"001".equals(rowObject.get(DATA_STATUS)) && !"000".equals(rowObject.get(DATA_STATUS))) {
                //FIXME messagebundle
                dialogController.showPopupError("\"Bildirime konu veri bulunuyor mu?\" alanı zorunlu alandır");
                return null;
            }

            rowObject.putAll(getFilter());
            rowObject.put(FORMS, FIELDS_ROW);

            if (rowObject.get(MONGO_ID) != null) {
                mongoDbUtil.updateMany(formService.getMyForm(), new Document(MONGO_ID, rowObject.get(MONGO_ID)), new Document(rowObject));
            } else {
                Document srchobj = new Document(getFilter());
                srchobj.put(FORMS, FIELDS_ROW);

                mongoDbUtil.updateMany(formService.getMyForm(), srchobj, new Document(DOLAR_SET, new Document(rowObject)));
            }

            //FIXME generalize it. move to engine-config pattern
            if ("001".equals(rowObject.get(DATA_STATUS))) {

                if (!(loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminRole()) //
                        || loginController.getLoggedUserDetail().getDbo().getObjectId()
                                .equals(getSearchObjectValue(formService.getMyForm().getLoginFkField())))) {
                    return null;
                }

                mongoDbUtil.deleteMany(formService.getMyForm().getDb(), formService.getMyForm().getTable(),
                        new Document(getFilter()).append(FORMS, formService.getMyForm().getKey()));
                try {
                    refreshPivotData();
                } catch (Exception ex) {
                    logger.error("error occured", ex);
                }
                return null;
            } else {
                //FIXME messagebundle
                dialogController.showPopupError("\"Bildirime konu veri bulunuyor mu?\" alanı zorunlu alandır");
                return null;
            }
        }

        try {
            String data = pivotDataModelEdit.getJsonDataToModel();
            if (data == null || data.trim().isEmpty()) {
                //FIXME messagebundle
                dialogController.showPopupError("Kaydedilecek bir değişiklik tespit edilemedi");
                return null;
            }

            Gson gson = new Gson();
            List<List> dataList = gson.fromJson(data, List.class
            );

            pivotDataModelEdit.setJsonDataToModel("");
            int i = 0;

            for (MyField coordinateRow : getIksDimension()) {
                int j = 0;
                for (MyField coordinateColumn : getIgrekDimension()) {
                    List<MyField> coordinates = new ArrayList<>();
                    coordinates.add(coordinateColumn);
                    coordinates.add(coordinateRow);
                    CellMultiDimensionKey cellMultiDimensionKey = new CellMultiDimensionKey(coordinates);

                    List<CustomOlapHashMap> customOlapHaspMaps = pivotData.get(cellMultiDimensionKey);

                    if (customOlapHaspMaps != null && !customOlapHaspMaps.isEmpty()) {
                        CustomOlapHashMap customOlapHaspMap = customOlapHaspMaps.get(0);
                        Object converter = customOlapHaspMap.getConverter();

                        Object value = dataList.get(i).get(j);

                        Locale locale = new Locale("tr", "TR");
                        NumberFormat toModel = NumberFormat.getInstance(locale);

                        if (value == null || value instanceof String && value.toString().trim().isEmpty()) {
                            customOlapHaspMap.resetValue();//veritabanına kuruş olarak gonderiyoruz
                        } else if (converter instanceof MoneyConverter) {
                            if (value instanceof Number) {
                                //100 e çarpmak kuruşa çevirmek için dir. kuruş kısmınız atmaz
                                //long cast olayı sadece doubledaki kuruş kısmının virgulsuz yazılması içindir.
                                //kuruşlar kalıyor ama yinede ve 100 e bolundugunde ortaya çıkyor.
                                //kaydetme esnasında Kuruş kısmını tamamen atmak için round ettikten sonra 100 e çarpılmalı.
                                customOlapHaspMap.setValue((long) (Math.round(((Number) value).doubleValue()) * 100));//veritabanına kuruş olarak gonderiyoruz
                            } else {
                                try {
                                    customOlapHaspMap.setValue((long) (Math.round(toModel.parse(value.toString()).doubleValue()) * 100));
                                } catch (Exception ex) {
                                    customOlapHaspMap.setValue((long) 0);
                                }
                            }
                            if ("kpbdb".equals(formService.getMyForm().getDb())) {
                                Number lastCheck = (Number) customOlapHaspMap.getValue();
                                if (lastCheck.doubleValue() < 0) {
                                    //FIXME messagebundle
                                    throw new Exception("Değerler pozitif olmalı");
                                }
                            }
                        } else if (converter instanceof NumberConverter) {
                            /* 
                             
                             MoneyConverterde tek farklı durum şu:
                             Round edilip sonra 100 ile çarpılıyor. 
                             Bu şekilde kuruşlar ATILIYOR.
                              
                             NumberConverterda ise :
                             100 yada tanımlı diğer bir div'e çarpılıp sonra round ediliyor.
                             Bu şekilde kuruşlar KALIYOR.

                             */
                            int div = ((NumberConverter) converter).getDiv();

                            if (customOlapHaspMap.getUysformat() != null) {
                                div = ((NumberConverter) converter).getDiv(customOlapHaspMap.getUysformat());
                            }

                            if (value instanceof Number) {
                                value = Math.round(((Number) value).doubleValue() * div);//veritabanına kuruş olarak gonderiyoruz
                                value = Double.valueOf(value.toString());
                                customOlapHaspMap.setValue(value);
                            } else {
                                try {
                                    value = Math.round(toModel.parse(value.toString()).doubleValue() * div);
                                    value = Double.valueOf(value.toString());
                                    customOlapHaspMap.setValue(value);
                                } catch (Exception ex) {
                                    customOlapHaspMap.resetValue();
                                }
                            }

                            if ("kpbdb".equals(formService.getMyForm().getDb())) {
                                Number lastCheck = (Number) customOlapHaspMap.getValue();
                                if (lastCheck.doubleValue() < 0) {
                                    //FIXME messagebundle
                                    throw new Exception("Değerler pozitif olmalı");
                                }
                            }
                        } else {
                            customOlapHaspMap.setValue(value);
                        }
                    }
                    j++;
                }
                i++;
            }

            saveObject();

            Map<String, List<MyConstraintFormula>> map = ctrlService.constraintCrossCheckVersionTwo(getFilter(), pivotData);

            successList = map.get("successList");
            failList = map.get("failList");
            dialogController.showPopupWarning("", "dialogControlAndSave");

            //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "dialogControlAndSave" + DOT + "show();");
        } catch (UserException ex) {
            logger.error("error occured", ex);
            dialogController.showPopup(ex.getTitle(), ex.getMessage(), MESSAGE_DIALOG);
        } catch (Exception ex) {
            logger.error("error occured", ex);
            dialogController.showPopupError(ex.toString());
        }
        return null;
    }

    public String doNotSave() {

        try {
            loginController.logout();
        } catch (Exception ex) {
            logger.error("error occured", ex);
            dialogController.showPopupError(ex.toString());
        }
        return null;
    }

    public String getQuery() {
        if (loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminAndViewerRole())) {
            return ADMIN_QUERY;
        }
        return QUERY;
    }

    /* **************************  GETTER/SETTER  *************************** */
    //
    /**
     * @return
     */
    public List<MyConstraintFormula> getFailList() {
        return Collections.unmodifiableList(failList);
    }

    public List<MyConstraintFormula> getSuccessList() {
        return Collections.unmodifiableList(successList);
    }

    public List<String> getSelectedFormMessages() {
        return Collections.unmodifiableList(selectedFormMessages);
    }

    public List<String> createFormMsg(MyForm myForm) {
        List<String> selectedFormMessages = new ArrayList<>();

        if (myForm.getConstantNote() != null && !myForm.getConstantNote().isEmpty()) {
            selectedFormMessages.add(myForm.getConstantNote());
        }
        if (myForm.getUserConstantNoteList() != null) {
            for (String message : myForm.getUserConstantNoteList()) {
                selectedFormMessages.add(message);
            }
        }
        if (myForm.getMyActions().isSave() && myForm.getReadOnlyNote() != null) {
            selectedFormMessages.add(myForm.getReadOnlyNote());
        }
        if (myForm.getFuncNote() != null) {

            String code = myForm.getFuncNote().getCode();

            Document commandResult = mongoDbUtil.runCommand(myForm.getDb(), code, getFilter());

            String commandResultValue = commandResult.getString(RETVAL);
            if (commandResultValue != null) {
                selectedFormMessages.add(commandResultValue);
            }
        }
        return selectedFormMessages;
    }

    @Override
    public Document getFilter() {
        return filterService.getPivotFilterCurrent();
    }

    public PivotDataModel getPivotDataModelEdit() {
        return pivotDataModelEdit;
    }

    public PivotDataModel getPivotDataModelRead() {
        return pivotDataModelRead;
    }

}
