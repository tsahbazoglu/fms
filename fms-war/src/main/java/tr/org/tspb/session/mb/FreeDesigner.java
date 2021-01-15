package tr.org.tspb.session.mb;

import com.mongodb.client.model.Filters;
import tr.org.tspb.util.service.DlgCtrl;
import tr.org.tspb.common.services.LoginController;
import static tr.org.tspb.constants.ProjectConstants.*;
import tr.org.tspb.common.qualifier.ViewerController;
import tr.org.tspb.common.qualifier.MyQualifier;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import org.bson.Document;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import tr.org.tspb.util.stereotype.MyController;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import tr.org.tspb.service.RepositoryService;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.common.services.AppScopeSrvCtrl;
import tr.org.tspb.common.services.BaseService;
import tr.org.tspb.dao.MyBaseRecord;
import tr.org.tspb.dao.refs.PlainRecord;
import tr.org.tspb.dp.nullobj.PlainRecordData;
import tr.org.tspb.pojo.PostSaveResult;
import tr.org.tspb.service.FeatureService;
import tr.org.tspb.util.tools.DocumentRecursive;
import tr.org.tspb.uys.freedesign.MyLicense;
import tr.org.tspb.uys.freedesign.MyRecord;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyController
@MyQualifier(myEnum = ViewerController.freeDesigner)
public class FreeDesigner implements Serializable {

    @Inject
    @MyLoginQualifier
    private LoginController loginController;

    @Inject
    DlgCtrl dialogController;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private AppScopeSrvCtrl appScopeSrvCtrl;

    @Inject
    private FeatureService featureService;

    @Inject
    private BaseService baseService;

    private MyBaseRecord searchLoginMemberID;

    private List<String> selectedFormMessages;
    public static final String MB__NAME = "#{freeDesignMB}";
    private final static String STORE_LOCATION__DB = "00";
    private final static String STORE_LOCATION__FILESYSTEM = "01";
    private final static String STORE_LOCATION__FILESYSTEM_AND_DB = "10";
    public static final String JOIN_ID = "joinId";
    public static final String FIELD_GROUP = "field_group";
    public final static String FIELD_GROUP_COMMON = "common";
    public static final String FIELD_GROUP_LICENSE = "license";
    protected MyForm selectedForm;
    private MyRecord myRecord;
    private Map<String, Boolean> actionsMap;
    protected List<Map<String, String>> listFileData;
    private String toBeDeletedFileID;

    protected Set<String> roles;
    //
    private MyRecord selectedRow;

    private LazyDataModel<MyRecord> myRecords;

    public String search() {

        if (searchLoginMemberID == null || searchLoginMemberID.isEmpty()) {
            searchedMap.remove(MEMBER);
        } else {
            searchedMap.put(MEMBER, searchLoginMemberID.getObjectId());
        }

        return null;
    }

    public List<PlainRecord> completeMethod(String query) {
        List<PlainRecord> plainRecords = new ArrayList<>();
        List<DocumentRecursive> list;

        if (query != null && !query.isEmpty()) {
            list = repositoryService
                    .findList("uysdb", "common", null,
                            Filters.regex(NAME, query.toUpperCase(new Locale("tr", "TR")), "i"), 10);
        } else {
            list = repositoryService.findList("uysdb", "common", null, new Document(), 10);
        }

        plainRecords.add(PlainRecordData.getNullPlainRecord());

        for (DocumentRecursive documentRecursive : list) {
            plainRecords
                    .add(PlainRecordData
                            .getPlainRecord(new SelectItem(documentRecursive.get(MONGO_ID), (String) documentRecursive.get(NAME))));
        }
        return plainRecords;
    }

    Map searchedMap = new HashMap();

    public List load(String sortColumnName, boolean sortAscending, int startRow, int maxResults, MyForm myForm) {

        searchedMap.put(FIELD_GROUP, FIELD_GROUP_COMMON);

        if (myForm != null && myForm.getKey() != null) {
            try {

                Map sortMap = new HashMap();
                Map defaultSortField = myForm.getDefaultSortField();
                if (defaultSortField != null) {
                    sortMap.putAll(defaultSortField);
                }

                List<Map> cursor = repositoryService
                        .findSkipLimitAsLoggedUser(selectedForm.getDb(), myForm.getTable(), searchedMap, startRow, maxResults);

                List<MyRecord> list = new ArrayList<>();

                for (Map doc : cursor) {
                    list.add(new MyRecord(
                            loginController.getRoleMap(),
                            loginController.getLoggedUserDetail(),
                            doc,
                            myForm,
                            appScopeSrvCtrl.memberCacheNameById()));
                }

                return list;

            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public MyRecord getRowData(String rowKey) {
        Map record = repositoryService.oneById(selectedForm.getDb(), selectedForm.getTable(), rowKey);

        return new MyRecord(loginController.getRoleMap(), loginController.getLoggedUserDetail(),
                record, selectedForm, appScopeSrvCtrl.memberCacheNameById());
    }

    private void refreshLazy() {

        myRecords = new LazyDataModel<MyRecord>() {

            @Override
            public List<MyRecord> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                return FreeDesigner.this.load(null, false, first, pageSize, selectedForm);
            }

            @Override
            public List<MyRecord> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, FilterMeta> filterBy) {
                return FreeDesigner.this.load(null, false, first, pageSize, selectedForm);
            }

            @Override
            public MyRecord getRowData(String rowKey) {
                return FreeDesigner.this.getRowData(rowKey);
            }

            @Override
            public String getRowKey(MyRecord myRecord) {
                return myRecord.getMongoIdAsString();
            }

        };

        myRecords.setRowCount(3000000);

    }

    public void init(MyForm selectedForm) {
        this.selectedForm = selectedForm;
        this.roles = loginController.getRolesAsSet();

        if (loginController.isUserInRole(selectedForm.getMyProject().getAdminAndViewerRole())) {
            refreshLazy();
            Map searchedMap = new HashMap();
            searchedMap.put(FIELD_GROUP, FIELD_GROUP_COMMON);
            this.myRecords.setRowCount((int) repositoryService.count(selectedForm.getDb(), selectedForm.getTable(), searchedMap));
        } else {
            this.myRecord = findOne();
            if (selectedForm.getFieldsKeySet().contains("ekler")) {
                refreshUploadedFileList();
            }

        }

        this.actionsMap = new HashMap();

        if (loginController.isUserInRole(selectedForm.getMyProject().getAdminAndViewerRole())) {
            this.actionsMap.put(ACTION_SAVE, Boolean.FALSE);
            this.actionsMap.put("esign", Boolean.FALSE);
        } else if (loginController.isUserInRole(selectedForm.getMyProject().getUserRole())) {
            this.actionsMap.put(ACTION_SAVE, Boolean.TRUE);
            this.actionsMap.put("esign", Boolean.TRUE);
        }

        featureService.getEsignDoor().initAndFindEsigns(selectedForm, null);

    }

    public String showEimza() {

        List<Map> listOfCruds = repositoryService.listAsLoggedUser(selectedForm);

        if (listOfCruds.isEmpty()) {
            dialogController.showPopup("Uyarı", "İmzalanacak Kayıtlı Veriniz Tespit Edilemedi.", MESSAGE_DIALOG);
        } else {

            featureService.getEsignDoor().iniAndShowEsignDlgV1(new TreeMap<Integer, String>(), listOfCruds, selectedForm, "widgetVarToBeSignedDialog", UNIQUE);

        }

        return null;
    }

    public void upload(FileUploadEvent event) {
        myRecord.setUploadedFile(event.getFile());

        String writeLocation = STORE_LOCATION__DB;

        try {
            switch (writeLocation) {
                case STORE_LOCATION__DB:
                    repositoryService.createFile(selectedForm, myRecord);
                    break;
                case STORE_LOCATION__FILESYSTEM:
                    break;
                case STORE_LOCATION__FILESYSTEM_AND_DB:
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);

        }
        refreshUploadedFileList();
    }

    public String deleteFile() {

        repositoryService.removeFileById(toBeDeletedFileID);

        refreshUploadedFileList();
        return null;
    }

    private void refreshUploadedFileList() {
        if (myRecord != null) {
            listFileData = repositoryService.findGridFsFileList(myRecord.getMongoIdAsString());
        }
    }

    public List<Map<String, String>> getFileData() {
        return Collections.unmodifiableList(listFileData);
    }

    public String save() {
        saveAsLoggedUser(myRecord);
        refreshUploadedFileList();
        dialogController.showPopupInfoWithOk(PostSaveResult.MSG, MESSAGE_DIALOG);
        return null;
    }

    public LoginController getLoginMB() {
        return loginController;
    }

    public void setLoginMB(LoginController loginMB) {
        this.loginController = loginMB;
    }

    public MyForm getSelectedForm() {
        return selectedForm;
    }

    public void setSelectedForm(MyForm selectedForm) {
        this.selectedForm = selectedForm;
    }

    public MyRecord getMyRecord() {
        return myRecord;
    }

    public void setMyRecord(MyRecord myRecord) {
        this.myRecord = myRecord;
    }

    public String getToBeDeletedFileID() {
        return toBeDeletedFileID;
    }

    public void setToBeDeletedFileID(String toBeDeletedFileID) {
        this.toBeDeletedFileID = toBeDeletedFileID;
    }

    private List<MyLicense> wrap(List<MyLicense> list) {

        List<MyLicense> wrappedListOfMyLicense = new ArrayList<>();

        if (loginController.isUserInRole("pyuser")) {
            for (MyLicense myLicense : MyLicense.getLicenceParentPys().values()) {
                wrappedListOfMyLicense.add(new MyLicense(myLicense, roles));
            }
        } else {
            for (MyLicense myLicense : MyLicense.getLicenceParent().values()) {
                wrappedListOfMyLicense.add(new MyLicense(myLicense, roles));
            }
        }

        Collections.sort(wrappedListOfMyLicense, new Comparator<MyLicense>() {
            @Override
            public int compare(MyLicense t1, MyLicense t2) {
                int order1 = t1.getOrderno();
                int order2 = t2.getOrderno();
                return Integer.compare(order1, order2);
            }
        });

        for (MyLicense myLicense : list) {

            int index = wrappedListOfMyLicense.indexOf(myLicense);

            MyLicense wrap = wrappedListOfMyLicense.get(index);
            wrap.setIn(myLicense.getIn());
            wrap.setOut(myLicense.getOut());
            wrap.setInout(myLicense.getInout());
        }
        return wrappedListOfMyLicense;
    }

    public void saveAsLoggedUser(MyRecord myRecord) {

        if (loginController.getLoggedUserDetail().getDbo().notFound()) {
            return;
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();
        String sessionId = ((HttpSession) facesContext.getExternalContext().getSession(false)).getId();

        String memberUserName = loginController.getLoggedUserDetail().getUsername();

        int joinId = (int) (Math.random() * 1000000);

        for (MyLicense myLicense : myRecord.getListOfLicense()) {
            if (myLicense.getSaveToDb()) {
                Map searchForUpdate = new HashMap();
                searchForUpdate.put(FORM, selectedForm.getTable());
                searchForUpdate.put(MyLicense.LICENSE_KEY, myLicense.getLicense());
                searchForUpdate.put(FIELD_GROUP, FIELD_GROUP_LICENSE);

                myLicense.setAdminMetaData(loginController.getLoggedUserDetail().getUsername(), new Date(), sessionId);

                repositoryService.updateManyMyLicenseAsUser(selectedForm, searchForUpdate, myLicense, joinId);
            }
        }

        Map searchForUpdate = new HashMap();
        searchForUpdate.put(FORM, selectedForm.getTable());
        searchForUpdate.put(FIELD_GROUP, FIELD_GROUP_COMMON);

        Document uysAdditionalMetaData = (Document) myRecord.getCrudMap().get(ADMIN_METADATA);
        if (uysAdditionalMetaData == null) {
            uysAdditionalMetaData = new Document();
            myRecord.getCrudMap().put(ADMIN_METADATA, uysAdditionalMetaData);
            uysAdditionalMetaData.put(CREATE_USER, loginController.getLoggedUserDetail().getUsername());
            uysAdditionalMetaData.put(CREATE_DATE, new Date());
            uysAdditionalMetaData.put(CREATE_SESSIONID, sessionId);
        } else {
            uysAdditionalMetaData.put(UPDATE_DATE, new Date());
            uysAdditionalMetaData.put(UPDATE_USER, loginController.getLoggedUserDetail().getUsername());
        }

        repositoryService.updateManyMyRecordAsUser(selectedForm, searchForUpdate, myRecord, joinId);

        // begin : provide uploaded file relation
        Map dbo = repositoryService.one(selectedForm.getDb(), selectedForm.getTable(), searchForUpdate);

        Map searchMap = new HashMap();
        searchMap.put("metadata.crud_object_id", null);
        searchMap.put("metadata.username", memberUserName);

        Map updateMap = new HashMap();
        updateMap.put("metadata.crud_object_id", dbo.get(MONGO_ID));
        updateMap.put(JOIN_ID, joinId);

        repositoryService
                .updateMany(baseService.getProperties().getUploadTable(), "fs.files", searchMap, updateMap, true);

        //end : provide uploaded file relation
    }

    public MyRecord findOne() {

        Map searchMap = new HashMap();
        searchMap.put(FIELD_GROUP, FIELD_GROUP_LICENSE);

        List<Map> cursor = repositoryService.listAsUser(selectedForm, searchMap);

        List<MyLicense> list = new ArrayList<>();

        for (Map next : cursor) {
            list.add(new MyLicense(next));
        }

        Map searchMap1 = new HashMap();
        searchMap1.put(FIELD_GROUP, FIELD_GROUP_COMMON);

        Map next = repositoryService.oneAsUserOrDefault(selectedForm.getDb(), selectedForm.getTable(), searchMap1);

        MyRecord record = new MyRecord(loginController.getRoleMap(), loginController.getLoggedUserDetail(), next, selectedForm, appScopeSrvCtrl.memberCacheNameById());

        record.setListOfLicense(wrap(list));

        if (loginController.isUserInRole("pyuser")) {
            record.setRenderedInOut(Boolean.TRUE);
        }

        return record;
    }

    public Map getActionsMap() {
        return Collections.unmodifiableMap(this.actionsMap);
    }

    public List<String> getSelectedFormFieldKeySetAsList() {
        List<String> list = new ArrayList<>();
        for (String key : selectedForm.getFieldsKeySet()) {
            if (loginController.isUserInRole(selectedForm.getField(key).getAccesscontrol())) {
                list.add(key);
            }
        }
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String t, String t1) {
                String nameCurrent = selectedForm.getField(t).getOrder().toString();
                String nameNext = selectedForm.getField(t1).getOrder().toString();
                return nameNext.compareTo(nameCurrent);
            }
        });
        return list;
    }

    public MyRecord getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(MyRecord selectedRow) {
        this.selectedRow = selectedRow;
    }

    public LazyDataModel<MyRecord> getMyRecords() {
        return myRecords;
    }

    public void setMyRecords(LazyDataModel<MyRecord> myRecords) {
        this.myRecords = myRecords;
    }

    public List<String> getSelectedFormMessages() {
        return selectedFormMessages;
    }

    public MyBaseRecord getSearchLoginMemberID() {
        return searchLoginMemberID;
    }

    public void setSearchLoginMemberID(MyBaseRecord searchLoginMemberID) {
        this.searchLoginMemberID = searchLoginMemberID;
    }

}
