package tr.org.tspb.service;

import com.mongodb.client.model.Filters;
import tr.org.tspb.common.services.LoginController;
import tr.org.tspb.util.stereotype.MyServices;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import static tr.org.tspb.constants.ProjectConstants.DIEZ;
import static tr.org.tspb.constants.ProjectConstants.DOLAR;
import static tr.org.tspb.constants.ProjectConstants.RETVAL;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyItems;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import static tr.org.tspb.constants.ProjectConstants.FORMS;
import static tr.org.tspb.constants.ProjectConstants.FORM_KEY;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import static tr.org.tspb.constants.ProjectConstants.PERIOD;
import static tr.org.tspb.constants.ProjectConstants.TEMPLATE;
import static tr.org.tspb.constants.ProjectConstants.ZET_DIMENSION;
import tr.org.tspb.dao.MyFormXs;
import tr.org.tspb.dao.refs.PlainRecord;
import tr.org.tspb.dp.nullobj.PlainRecordData;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.service.util.FilterUtil;
import tr.org.tspb.util.service.DlgCtrl;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyServices
public class FilterService extends CommonSrv {

    @Inject
    @MyLoginQualifier
    private LoginController loginController;

    @Inject
    protected DlgCtrl dialogController;

    @Inject
    private FormService formService;

    private final Map<String, List<MyField>> filtersCache = new HashMap<>();

    //
    private Map<String, Object> guiFiltersCurrent;
    private Map<String, Object> guiFiltersHistory;
    //
    private Document baseFilterCurrent;
    private Document baseFilterHistory;

    //
    private Document tableFilterCurrent;
    private Document tableFilterHistory;
    //
    private Document pivotFilterCurrent;
    private Document pivotFilterHistory;

    private List<MyField> advanceFilters = new ArrayList<>();
    private List<MyField> quickFilters = new ArrayList<>();

    @PostConstruct
    public void init() {
        this.guiFiltersCurrent = new HashMap<>();
        this.guiFiltersHistory = new HashMap<>();
    }

    public void createBaseFilter(MyFormXs myFormXs) {
        baseFilterCurrent = new Document(myFormXs.getDefaultCurrentQuery());
        baseFilterHistory = new Document(myFormXs.getDefaultHistoryQuery());
    }

    public String bringFilters() {

        String key = createCacheKey("advanced-filter");

        advanceFilters = filtersCache.get(key);

        if (advanceFilters == null) {
            advanceFilters = FilterUtil.instance(mongoDbUtil, ogmCreatorIntr)
                    .createCurrentFilters(formService.getMyForm(),
                            loginController.getRoleMap(),
                            loginController.getLoggedUserDetail(),
                            tableFilterCurrent);
            filtersCache.put(key, advanceFilters);
        }

        for (MyField myField : advanceFilters) {
            if (myField.isAutoComplete()) {
                if (!(guiFiltersCurrent.get(myField.getKey()) instanceof PlainRecord)) {
                    MyItems myItems = myField.getItemsAsMyItems();

                    Document doc = mongoDbUtil.findOne(myItems.getDb(), myItems.getTable(),
                            Filters.eq(MONGO_ID, guiFiltersCurrent.get(myField.getKey())));
                    guiFiltersCurrent.put(myField.getKey(), PlainRecordData.getPlainRecord(doc, myItems));
                }
            }
        }

        dialogController.showPopup("wv-dlg-filter");
        return null;
    }

    private String createCacheKey(String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append(" : ");
        sb.append(formService.getMyForm().getKey());
        sb.append(" : ");
        sb.append(loginController.getRoleMap());
        sb.append(" : ");
        sb.append(loginController.getLoggedUserDetail());
        sb.append(" : ");
        sb.append(tableFilterCurrent);
        return org.apache.commons.codec.digest.DigestUtils.sha256Hex(sb.toString());
    }

    public void createPivotCurrentAndHistoryFilters() throws FormConfigException {
        createPivotFilterCurrent();
        createPivotFilterHistory();
    }

    public void createTableCurrentAndHistoryFilters(MyForm myForm) throws NullNotExpectedException {
        createTableFilterCurrent(myForm);
        createTableFilterHistory(myForm);
    }

    public void createPivotFilterCurrent() throws FormConfigException {

        pivotFilterCurrent = new Document();
        MyForm myForm = formService.getMyForm();

        if (myForm.getZetDimension() == null) {
            throw new FormConfigException(ZET_DIMENSION.concat(" is resolved to null"));
        }

        for (String key : myForm.getZetDimension()) {
            // take into account an alternativechannels form's 
            // periodFilter and period. 
            // key and field could be different
            String fieldName = myForm.getField(key).getField();

            if (guiFiltersCurrent.get(fieldName) != null) {
                pivotFilterCurrent.put(fieldName, guiFiltersCurrent.get(fieldName));
            } else if (baseFilterCurrent.get(fieldName) != null) {
                pivotFilterCurrent.put(fieldName, baseFilterCurrent.get(fieldName));
                guiFiltersCurrent.put(fieldName, baseFilterCurrent.get(fieldName));
            } else {
                pivotFilterCurrent.put(fieldName, new ObjectId());
            }
        }
        pivotFilterCurrent.put(FORMS, formService.getMyForm().getKey());

    }

    public void createPivotFilterHistory() {
        pivotFilterHistory = new Document();
        MyForm myForm = formService.getMyForm();
        for (String key : formService.getMyForm().getZetDimension()) {
            String fieldName = myForm.getField(key).getField();
            if (guiFiltersHistory.get(fieldName) != null) {
                pivotFilterHistory.put(fieldName, guiFiltersHistory.get(fieldName));
            } else if (baseFilterCurrent.get(fieldName) != null) {
                pivotFilterHistory.put(fieldName, baseFilterHistory.get(fieldName));
                guiFiltersHistory.put(fieldName, baseFilterHistory.get(fieldName));
            } else {
                pivotFilterHistory.put(fieldName, new ObjectId());
            }
        }
        pivotFilterHistory.put(FORMS, formService.getMyForm().getKey());
    }

    public void resetColumnDataModel() {
        this.advanceFilters = new ArrayList<>();
    }

    public void init(Map searchMap, Map currentFilter) {
        this.guiFiltersCurrent = currentFilter;
        this.tableFilterCurrent = new Document(searchMap);
    }

    public void initSearchMap(ObjectId memberID, ObjectId periodID, ObjectId templateID, MyForm myForm) {
        tableFilterCurrent = new Document();
        tableFilterCurrent.put(myForm.getLoginFkField(), memberID);
        tableFilterCurrent.put(PERIOD, periodID);
        tableFilterCurrent.put(TEMPLATE, templateID);
        tableFilterCurrent.put(FORMS, myForm.getKey());
    }

    public Bson getFormFilter(String formKey) {
        return Filters.eq(FORM_KEY, formKey);
    }

    public void createTableFilterCurrent(MyForm myForm) throws NullNotExpectedException {
        if (MyForm.SCHEMA_VERSION_110.equals(myForm.getSchemaVersion())) {
            this.tableFilterCurrent = FilterUtil.instance(mongoDbUtil, ogmCreatorIntr).createTableFilterSchemaVersion110(myForm,
                    baseFilterCurrent,
                    guiFiltersCurrent,
                    loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminAndViewerRole()),
                    loginController.getLoggedUserDetail(),
                    loginController.getRoleMap());
        } else {
            this.tableFilterCurrent = FilterUtil.instance(mongoDbUtil, ogmCreatorIntr).createTableFilter(myForm,
                    baseFilterCurrent,
                    guiFiltersCurrent,
                    loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminAndViewerRole()),
                    loginController.getLoggedUserDetail(),
                    loginController.getRoleMap());
        }
    }

    public void createTableFilterHistory(MyForm myForm) throws NullNotExpectedException {

        if (MyForm.SCHEMA_VERSION_110.equals(myForm.getSchemaVersion())) {
            this.tableFilterHistory = FilterUtil.instance(mongoDbUtil, ogmCreatorIntr).createTableHistoryScemaVersion110(myForm,
                    baseFilterHistory,
                    guiFiltersHistory,
                    loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminAndViewerRole()),
                    loginController.getLoggedUserDetail(),
                    loginController.getRoleMap());
        } else {
            this.tableFilterHistory = FilterUtil.instance(mongoDbUtil, ogmCreatorIntr).createTableHistory(myForm,
                    baseFilterHistory,
                    guiFiltersHistory,
                    loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminAndViewerRole()),
                    loginController.getLoggedUserDetail(),
                    loginController.getRoleMap());
        }
    }

    public List<Document> createDocuments(MyItems myItems, MyForm selectedForm, Map<String, Object> filter, boolean history) {
        String collectionName = myItems.getTable();
        String database = myItems.getDb();

        Object queryObject = history ? myItems.getHistoryQuery() : myItems.getQuery();

        if (queryObject == null) {

            logger.warn("query object is resolved to null");

            queryObject = myItems.getQuery();
        }

        if (queryObject instanceof Code) {
            Code func = new Code(((Code) queryObject).getCode().replace(DIEZ, DOLAR));

            Document commandResult = mongoDbUtil.runCommand(selectedForm.getDb(), func.getCode(), filter, null);

            queryObject = commandResult.get(RETVAL);
        }

        Document queryDoc = mongoDbUtil.replaceToDollar((Document) queryObject);

        Document sortObject = myItems.getSort();

        List<Document> cursor = mongoDbUtil.find(database == null ? selectedForm.getDb() : database, collectionName, queryDoc, sortObject, null);

        return cursor;
    }

    public Map getGuiFilterCurrent() {
        return guiFiltersCurrent;
    }

    public Map getGuiFilterHistory() {
        return guiFiltersHistory;
    }

    //
    public Document getPivotFilterCurrent() {
        return pivotFilterCurrent;
    }

    public Document getPivotFilterHistory() {
        return pivotFilterHistory;
    }

    //
    public Document getTableFilterCurrent() {
        return tableFilterCurrent;
    }

    public Document getTableFilterHistory() {
        return tableFilterHistory;
    }

    public List<MyField> getAdvanceFilters() {
        return Collections.unmodifiableList(advanceFilters);
    }

    public Document getBaseFilterCurrent() {
        return baseFilterCurrent;
    }

    public Document getBaseFilterPast() {
        return baseFilterHistory;
    }

    public List<MyField> getQuickFilters() {
        return quickFilters;
    }

    public void initQuickFilters() {
        String key = createCacheKey("quick-filter");
        quickFilters = filtersCache.get(key);
        if (quickFilters == null) {
            quickFilters = FilterUtil.instance(mongoDbUtil, ogmCreatorIntr)
                    .createCurrentQuickFilters(formService.getMyForm(), loginController.getRoleMap(), loginController.getLoggedUserDetail(), tableFilterCurrent);
        }

        for (MyField myField : quickFilters) {
            if (myField.isAutoComplete()) {
                if (!(guiFiltersCurrent.get(myField.getKey()) instanceof PlainRecord)) {
                    MyItems myItems = myField.getItemsAsMyItems();

                    Document doc = mongoDbUtil.findOne(myItems.getDb(), myItems.getTable(),
                            Filters.eq(MONGO_ID, guiFiltersCurrent.get(myField.getKey())));
                    guiFiltersCurrent.put(myField.getKey(), PlainRecordData.getPlainRecord(doc, myItems));
                }
            }
        }
    }

}
