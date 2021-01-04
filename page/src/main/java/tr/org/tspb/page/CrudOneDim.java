package tr.org.tspb.page;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoConfigurationException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.component.UISelectOne;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import static tr.org.tspb.constants.ProjectConstants.*;
import tr.org.tspb.util.tools.MongoDbVersion;
import tr.org.tspb.util.stereotype.MyController;
import tr.org.tspb.common.qualifier.MyQualifier;
import tr.org.tspb.common.qualifier.ViewerController;
import tr.org.tspb.common.qualifier.MyCtrlServiceQualifier;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import tr.org.tspb.common.services.AppScopeSrvCtrl;
import tr.org.tspb.common.services.BaseService;
import tr.org.tspb.common.services.LdapService;
import tr.org.tspb.common.services.LoginController;
import tr.org.tspb.common.services.MailService;
import tr.org.tspb.service.RepositoryService;
import tr.org.tspb.converter.base.SelectOneObjectIdConverter;
import tr.org.tspb.converter.base.SelectOneStringConverter;
import tr.org.tspb.converter.props.MessageBundleLoaderv1;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.dao.MyNotifies;
import tr.org.tspb.dao.TagEvent;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.exceptions.LdapException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.exceptions.UserException;
import tr.org.tspb.factory.qualifier.OgmCreatorQualifier;
import tr.org.tspb.outsider.EsignDoor;
import tr.org.tspb.outsider.ReportDoor;
import tr.org.tspb.outsider.qualifier.DefaultReportDoor;
import tr.org.tspb.pojo.ComponentType;
import tr.org.tspb.pojo.UserDetail;
import tr.org.tspb.service.CtrlService;
import tr.org.tspb.service.FilterService;
import tr.org.tspb.service.FormService;
import tr.org.tspb.util.crypt.RandomString;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.service.DlgCtrl;
import tr.org.tspb.util.tools.MongoDbUtilIntr;
import tr.org.tspb.factory.cp.OgmCreatorIntr;
import tr.org.tspb.pojo.DatabaseUser;
import tr.org.tspb.pojo.PostSaveResult;
import tr.org.tspb.pojo.PreSaveResult;
import tr.org.tspb.service.CalcService;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyController
@MyQualifier(myEnum = ViewerController.crudOneDim)
public class CrudOneDim implements ValueChangeListener, Serializable {

    @Inject
    private EsignDoor esignDoor;

    @Inject
    @DefaultReportDoor
    private ReportDoor reportDoor;

    @Inject
    private FormService formService;

    @Inject
    private DlgCtrl dialogController;

    @Inject
    private FilterService filterService;

    @Inject
    @MyLoginQualifier
    private LoginController loginController;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private BaseService baseService;

    @Inject
    private LdapService ldapService;

    @Inject
    private MailService mailService;

    @Inject
    private AppScopeSrvCtrl uysApplicationMB;

    @Inject
    @MyCtrlServiceQualifier
    protected CtrlService ctrlService;

    @Inject
    private Logger logger;

    @Inject
    @KeepOpenQualifier
    private MongoDbUtilIntr mongoDbUtil;

    @Inject
    @OgmCreatorQualifier
    private OgmCreatorIntr ogmCreator;

    @Inject
    private CalcService calcService;

    private List<Map<String, String>> listFileData = new ArrayList<>();
    private List<String> selectedFormMessages;
    private List versionHistory = new ArrayList();
    private List historyColumnModel = new ArrayList();
    private String mongoUploadFileType;
    private String invalidFileMessage;
    private String toBeDeletedFileID;
    private int fileLimit;
    private final boolean enableHistoryOnSave = true;
    private final static String WRITE_TO = "GRIDFS_DB";
    private final static String DLG_DESC = "wvDescDlg";
    private MyField selectedField;
    private UploadedFile uploadedFile;
    private MyMap crudObject;
    private Map<String, MyField> componentMap = new HashMap();

    @PostConstruct
    public void init() {
        crudObject = ogmCreator.getCrudObject();
    }

    public boolean isEnableHistoryOnSave() {
        return enableHistoryOnSave;
    }

    public MyField getSelectedField() {
        return selectedField;
    }

    public void setSelectedField(MyField selectedField) {
        this.selectedField = selectedField;
    }

    public String showMyFieldDesc() {
        dialogController.showPopup(DLG_DESC);
        return null;
    }

    public static ELContext getELContext() {
        return FacesContext.getCurrentInstance().getELContext();
    }

    public static ExpressionFactory getExpressionFactory() {
        return getApplication().getExpressionFactory();
    }

    public static Application getApplication() {
        return FacesContext.getCurrentInstance().getApplication();
    }

    public StreamedContent getFile() {
        return reportDoor.getFile(formService.getMyForm(), crudObject);
    }

    public List<String> getSelectedFormMessages() {
        return Collections.unmodifiableList(selectedFormMessages);
    }

    public String getToBeDeletedFileID() {
        return toBeDeletedFileID;
    }

    public void setToBeDeletedFileID(String toBeDeletedFileID) {
        this.toBeDeletedFileID = toBeDeletedFileID;
    }

    public String deleteFile() {
        if (formService.getMyForm().getMyActions().isDelete()) {
            mongoDbUtil.removeFile(baseService.getProperties().getUploadTable(), new ObjectId(toBeDeletedFileID));
            refreshUploadedFileList();
        }
        return null;
    }

    public String deleteObject() {
        //FIXME messagebundle
        dialogController.showPopupError("Tek boyutlu form lar üzerinde silme eylemi bloke edildi.");
        return null;
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

    public Boolean getSelectedFormUserNote() {
        return formService.getMyForm() != null
                && formService.getMyForm().getUserNote() != null
                && !formService.getMyForm().getUserNote().isEmpty();
    }

    public String showEimza() {

        Document dBObject = new Document();
        dBObject.put(FORMS, formService.getMyForm().getForm());
        dBObject.put(formService.getMyForm().getLoginFkField(), filterService.getTableFilterCurrent()
                .get(formService.getMyForm().getLoginFkField()));

        if (formService.getMyForm().getField(PERIOD) != null) {
            dBObject.put(PERIOD, filterService.getTableFilterCurrent().get(PERIOD));
        }

        List<Map> listOfCruds = repositoryService
                .list(formService.getMyForm().getDb(), formService.getMyForm().getTable(), dBObject);

        if (listOfCruds.isEmpty()) {
            //FIXME messagebundle
            dialogController.showPopupWarning("İmzalanacak Kayıtlı Veriniz Tespit Edilemedi.", MESSAGE_DIALOG);
        } else {
            esignDoor.iniAndShowEsignDlgV1(new TreeMap<Integer, String>(), listOfCruds, formService.getMyForm(), "widgetVarToBeSignedDialog", UNIQUE);
        }

        return null;
    }

    public String getSelectedFormConstantNote() {
        return formService.getMyForm() == null ? " " : formService.getMyForm().getConstantNote();
    }

    public void drawGUI(MyForm myForm) throws Exception {
        drawGUI(myForm, filterService.getTableFilterCurrent());
        formService.getMyForm().runAjaxBulk(getComponentMap(), crudObject,
                loginController.getRoleMap(), loginController.getLoggedUserDetail());

    }

    public void drawGUI(MyForm myForm, Document filter) throws Exception {

        if (myForm.getUniqueIndexList() != null) {
            for (Object obj : myForm.getUniqueIndexList()) {
                Document dbo = (Document) obj;
                mongoDbUtil.createIndexUnique(myForm, dbo);
            }
        }

        armCrudRecord(myForm, filter);

        if (myForm.getVersionCollection() != null) {

            Map<String, List> map = MongoDbVersion.instance(mongoDbUtil).fetch(
                    myForm,
                    myForm.getDb(),
                    myForm.getVersionCollection(),
                    (ObjectId) crudObject.get(MONGO_ID),
                    myForm.getVersionFields());

            historyColumnModel = map.get(COLUMN_LIST);
            versionHistory = map.get(ROW_LIST);
        }

        drawPopupGUI();

        selectedFormMessages = createFormMsg(myForm);

        TagEvent trigger = myForm.getEventFormSelection();
        if (trigger != null && TagEvent.TagEventType.showWarnErrPopup.equals(trigger.getType())) {
            dialogController.showPopupInfoWithOk(trigger.getMsg(), MESSAGE_DIALOG);
        }

        if ("debug".equals(baseService.getProperties().getDebugMode())) {
            dialogController.showPopupInfo(new StringBuilder()
                    .append("You see this message because of server properties DEBUG_MODE is set to debug.")
                    .append(myForm.printToConfigAnalyze("smth"))
                    .append("<br/>")
                    .append("<u>query :</u>")
                    .append("<br/><br/>")
                    .append("filter : ")
                    .append(filterService.getTableFilterCurrent().toString())
                    .toString(), MESSAGE_DIALOG);
        }

    }

    private void armCrudRecord(MyForm myForm, Document modifiedSearchObject) throws FormConfigException {

        if (crudObject == null) {
            crudObject = ogmCreator.getCrudObject();
        }

        crudObject.clear();

        Document dboRecord = mongoDbUtil.findOne(myForm.getDb(), myForm.getTable(), modifiedSearchObject);

        if (dboRecord != null) {
            crudObject.putAll(dboRecord);
        }

        for (String key : myForm.getFieldsKeySet()) {

            armDefaultValues(key, myForm, modifiedSearchObject);

            if (crudObject.get(key) instanceof Document && ((Document) crudObject.get(key)).get(MONGO_ID) != null) {
                crudObject.put(key, ((Document) crudObject.get(key)).get(MONGO_ID));
            }
        }

        if (crudObject.get(myForm.getLoginFkField()) == null) {
            throw new FormConfigException(myForm.getLoginFkField().concat(" had not been set. review default/autoset setting."));
        }

    }

    private void armDefaultValues(String key, MyForm myForm, Document modifiedSearchObject)
            throws tr.org.tspb.exceptions.FormConfigException {
        if (crudObject.get(key) == null) {

            MyField myField = myForm.getField(key);

            Object defaultValueObject = myField.getDefaultValue();

            if (defaultValueObject instanceof String) {
                crudObject.put(key, defaultValueObject);
            } else if (defaultValueObject instanceof ObjectId) {
                crudObject.put(key, defaultValueObject);
            } else if (defaultValueObject instanceof Code) {
                String code = ((Code) defaultValueObject).getCode();
                try {
                    Document commandResult = mongoDbUtil.runCommand(myForm.getDb(), code, modifiedSearchObject, loginController.getRolesAsSet());
                    crudObject.put(key, commandResult.getString(RETVAL));
                } catch (Exception ex) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<br/>");
                    sb.append("<br/>");
                    sb.append("form key : ".concat(myForm.getKey()));
                    sb.append("<br/>");
                    sb.append("field : ".concat(myField.getName()));
                    sb.append("<br/>");
                    sb.append("method : caclulate default value");
                    sb.append("<br/>");
                    sb.append("<br/>");
                    sb.append(ex.getMessage());
                    throw new tr.org.tspb.exceptions.FormConfigException(sb.toString(), ex);
                }
            }
        }

        for (MyField myField : myForm.getAutosetFields()) {
            crudObject.put(myField.getKey(), filterService.getTableFilterCurrent().get(myField.getKey()));
        }

        if (!loginController.isUserInRole(myForm.getMyProject().getAdminRole())) {
            DatabaseUser loginRecord = loginController.getLoggedUserDetail().getDbo();
            if (loginRecord != null) {
                crudObject.put(formService.getMyForm().getLoginFkField(), loginRecord.getObjectId());
            }
        }

    }

    public List getVersionHistory() {
        return Collections.unmodifiableList(versionHistory);
    }

    public List getHistoryColumnModel() {
        return Collections.unmodifiableList(historyColumnModel);
    }

    private void drawPopupGUI() {

        componentMap = new HashMap();

        boolean hasInputFile = false;

        for (String key : formService.getMyForm().getFieldsKeySet()) {

            MyField myField = formService.getMyForm().getField(key);

            // recalculate rendered property
            myField.calcWfRendered(crudObject, loginController.getRoleMap(), filterService.getTableFilterCurrent());

            // recalculate defaultValue property
            Object defaultValueObject = formService.getMyForm().getField(key).getDefaultValue();

            if (defaultValueObject != null && crudObject.get(key) == null) {

                if (defaultValueObject instanceof String) {
                    crudObject.put(key, defaultValueObject);
                } else if (defaultValueObject instanceof List) {
                    crudObject.put(key, defaultValueObject);
                } else if (defaultValueObject instanceof Number) {
                    crudObject.put(key, defaultValueObject);
                } else if (defaultValueObject instanceof ObjectId) {
                    crudObject.put(key, defaultValueObject);
                } else if (defaultValueObject instanceof Code) {
                    String code = ((Code) defaultValueObject).getCode();
                    Document commandResult = mongoDbUtil.runCommand(formService.getMyForm().getDb(),
                            code, filterService.getTableFilterCurrent(), loginController.getRolesAsList());
                    crudObject.put(key, commandResult.get(RETVAL));
                }
            }

            if (INPUT_FILE.equals(myField.getComponentType())) {
                if (myField.getFileType() != null) {
                    setFileLimit(myField.getFileLimit());
                    //FIXME messagebundle
                    setMongoUploadFileType("/(\\.|\\/)(pdf)$/");
                    setInvalidFileMessage("Geçersiz Dosya Tipi (Sadece PDF dosyalar eklenebilir) : ");

                    switch (myField.getFileType()) {
                        case "pdf":
                            setMongoUploadFileType("/(\\.|\\/)(pdf)$/");
                            setInvalidFileMessage("Geçersiz Dosya Tipi (Sadece PDF dosyalar eklenebilir) : ");
                            break;
                        case "image":
                            setMongoUploadFileType("/(\\.|\\/)(jpg|png|JPEG|JPG|PNG)$/");
                            setInvalidFileMessage("Geçersiz Dosya Tipi (Sadece resim [jpg, png, JPEG, JPG, PNG] formatında dosyalar eklenebilir) : ");
                            break;
                        default:
                            break;
                    }
                    hasInputFile = true;
                }
            }

            if (!getAutoComplete().equals(myField.getComponentType())) {
                myField.createSelectItems(filterService.getTableFilterCurrent(),
                        crudObject,
                        loginController.getRoleMap(),
                        loginController.getLoggedUserDetail(),
                        false);
            }

            componentMap.put(key, myField);
        }

        if (hasInputFile) {
            refreshUploadedFileList();
        }

        List<Map> listOfCruds = new ArrayList();
        listOfCruds.add(new Document(crudObject));

        esignDoor.initAndFindEsignsV1(formService.getMyForm(), null, listOfCruds, UNIQUE);

    }

    public String saveObject() {

        try {

            Object loginFkfiledValue = crudObject.get(formService.getMyForm().getLoginFkField());

            if (loginFkfiledValue != null) {
                if (!(loginController.getLoggedUserDetail().getDbo().getObjectId().equals(loginFkfiledValue))) {
                    if (!(loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminRole()))) {
                        throw new Exception("Sisteme girş yapan kullanıcı yalnızca kendisine ait veri ekleyip değiştirebilir.");
                    }
                }
            }

            PreSaveResult preSaveResult = repositoryService
                    .runEventPreSave(filterService.getTableFilterCurrent(), formService.getMyForm(), crudObject);

            if (preSaveResult.isResult()) {
                throw new Exception(preSaveResult.getMsg());
            }

            saveObject(formService.getMyForm(), loginController, crudObject);

            drawGUI(formService.getMyForm(), filterService.getTableFilterCurrent());

            formService.getMyForm().runAjaxBulk(getComponentMap(), crudObject,
                    loginController.getRoleMap(), loginController.getLoggedUserDetail());

        } catch (UserException ex) {
            logger.error("error occured", ex);
            dialogController.showPopupError(ex.getMessage());
        } catch (Exception ex) {
            logger.error("error occured", ex);
            dialogController.showPopupError("Beklenmeyen Hata Oluştu.");
        }
        return null;
    }

    public MyMap getMyObject() {
        return crudObject;
    }

    public void setMyObject(MyMap myObject) {
        this.crudObject = myObject;
    }

    /**
     * This function is responsible on updating the SelectItems on SelectOneMenu
     * component
     *
     *
     * @param form
     * @param observerKey
     * @param observableKey
     * @param observableValue
     */
    public void updateSelectItems(Document form, String observerKey, String observableKey, Object observableValue) {

        String observerClientId = SelectOneObjectIdConverter.mapClientIdPerMongoKey.get(observerKey);

        UIComponent uiComponent = FacesContext.getCurrentInstance().getViewRoot();

        if (observerClientId == null) {
            logger.error("observerClientId==null");
        }

        uiComponent = uiComponent.findComponent(observerClientId);

        if (uiComponent instanceof UISelectOne) {
            List list = new ArrayList();

            Document searchDBObject = new Document(FORMS, observerKey);

            if (observableValue instanceof ObjectId) {
                list.add(new SelectItem(SelectOneObjectIdConverter.NULL_VALUE, SELECT_PLEASE));
                searchDBObject.put(observableKey.concat(DOT).concat(MONGO_ID), observableValue);
            } else {
                list.add(new SelectItem(SelectOneStringConverter.NULL_VALUE, SELECT_PLEASE));
                searchDBObject.append(observableKey, observableValue);
            }

            List<Document> cursor = mongoDbUtil
                    .find((String) form.get(FORM_DB), (String) form.get(COLLECTION), searchDBObject, new Document(NAME, 1), null);

            for (Document object : cursor) {
                list.add(new SelectItem(object.get(MONGO_ID), object.getString(NAME)));
            }

            UISelectItems items = new UISelectItems();

            Collections.sort(list, new Comparator() {
                @Override
                public int compare(Object t1, Object t2) {
                    if (t1 instanceof SelectItem) {
                        String m1 = ((SelectItem) t1).getLabel();
                        String m2 = ((SelectItem) t2).getLabel();

                        if ("diğer".equalsIgnoreCase(m1) || "diğer, diğer".equalsIgnoreCase(m1)) {
                            return 1;
                        }

                        return m2.compareToIgnoreCase(m2);
                    }
                    return 0;
                }
            });

            items.setValue(list);
            uiComponent.getChildren().clear();
            uiComponent.getChildren().add(items);
            ((ValueHolder) uiComponent).setConverter(new SelectOneObjectIdConverter());
        }
    }

    @Override
    public void processValueChange(ValueChangeEvent event) throws AbortProcessingException {
        throw new UnsupportedOperationException("CrudOneDim.processValueChange()");
//        String field = (String) event.getComponent().getAttributes().get("mongoField");
//        crudObject.put(field, event.getComponent().getAttributes().get("value"));
//        UysObservable uysObservable = field == null ? null : getMapObservables(field);
//        if (uysObservable != null) {
//            uysObservable.notifyObservers(event.getComponent().getAttributes().get("value"));
//        }
    }

    public void dateChange(SelectEvent event) {
    }

    //move to util
    public List<String> createFormMsg(MyForm myForm) {
        List<String> msgs = new ArrayList<>();

        if (myForm.getConstantNote() != null && !myForm.getConstantNote().isEmpty()) {
            msgs.add(myForm.getConstantNote());
        }
        if (myForm.getUserConstantNoteList() != null) {
            for (String message : myForm.getUserConstantNoteList()) {
                msgs.add(message);
            }
        }
        if (myForm.getMyActions().isSave() && myForm.getReadOnlyNote() != null) {
            msgs.add(myForm.getReadOnlyNote());
        }
        if (myForm.getFuncNote() != null) {
            Document commandResult = mongoDbUtil.runCommand(myForm.getDb(), myForm.getFuncNote(), filterService.getTableFilterCurrent());
            String commandResultValue = commandResult.getString(RETVAL);
            if (commandResultValue != null) {
                msgs.add(commandResultValue);
            }
        }
        return msgs;
    }

    public int getFileLimit() {
        return fileLimit;
    }

    public void setFileLimit(int fileLimit) {
        this.fileLimit = fileLimit;
    }

    public String getMongoUploadFileType() {
        return mongoUploadFileType;
    }

    public void setMongoUploadFileType(String mongoUploadFileType) {
        this.mongoUploadFileType = mongoUploadFileType;
    }

    public String getInvalidFileMessage() {
        return invalidFileMessage;
    }

    public void setInvalidFileMessage(String invalidFileMessage) {
        this.invalidFileMessage = invalidFileMessage;
    }

    protected void refreshUploadedFileList() {
        ObjectId myCommonRecordID = (ObjectId) crudObject.get(MONGO_ID);
        listFileData = repositoryService.findGridFsFileList(myCommonRecordID);
    }

    public ObjectId saveObject(MyForm myForm, LoginController loginMB, MyMap crudObject)
            throws UserException, MessagingException, NullNotExpectedException, LdapException, FormConfigException {

        Object loginFkFieldValue = crudObject.get(formService.getMyForm().getLoginFkField());

        boolean ok = loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminRole());
        ok = ok || loginController.getLoggedUserDetail().getDbo().getObjectId().equals(loginFkFieldValue);

        for (UserDetail.EimzaPersonel ep : loginController.getLoggedUserDetail().getEimzaPersonels()) {
            if (ep.getDelegatingMember() != null && ep.getDelegatingMember().equals(loginFkFieldValue)) {
                ok = true;
                break;
            }
        }

        if (!ok) {
            //FIXME messagebundle
            throw new UserException("Sisteme girş yapan kullanıcı yalnızca kendisine ait veri ekleyip değiştirebilir.");
        }

        Document operatedObject = new Document(crudObject);

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String sessionId = ((HttpSession) facesContext.getExternalContext().getSession(false)).getId();

        ObjectId returnID = saveOneDimensionObject(operatedObject, loginMB.getLoggedUserDetail().getUsername(),
                formService.getMyForm(), request.getRemoteAddr(), sessionId);
        crudObject.put(STATE, "saved");

        return returnID;
    }

    private ObjectId saveOneDimensionObject(Document operatedObject, String username, MyForm myForm, String ip, String sessionId)
            throws MessagingException, NullNotExpectedException, LdapException, FormConfigException, UserException {

        MyForm inode = (MyForm) operatedObject.get(INODE);
        operatedObject.remove(INODE);//just to sutisfy the icefaces
        if (inode == null) {
            inode = myForm;
        }

        ctrlService.checkRecordConverterValueType(operatedObject, myForm);

        if (myForm.isHasAttachedFiles() && (listFileData == null || listFileData.isEmpty())) {
            for (MyField field : myForm.getFields().values()) {
                if (field.isRequired() && ComponentType.inputFile.name().equals(field.getComponentType())) {
                    FacesMessage facesMessageRequired = new FacesMessage(//
                            FacesMessage.SEVERITY_ERROR, //
                            MessageFormat.format("[{0}] {1}", field.getShortName(), MessageBundleLoaderv1.getMessage("requiredMessage")),//
                            "*");
                    FacesContext.getCurrentInstance().addMessage(null, facesMessageRequired);
                    throw new UserException("<br/><br/> Dosya Eksik. 'Ekli Dosyalar' sekmesinden talep edilen belge(leri) ekleyiniz");
                }
            }
        }

        operatedObject = repositoryService.expandCrudObject(myForm, operatedObject);

        operatedObject.put(OPERATOR_LDAP_UID, username);
        operatedObject.put(FORMS, myForm.getForm());

        if (myForm.getFindAndSaveFilter() != null) {
            operatedObject.putAll(myForm.getFindAndSaveFilter());
        }

        Document uysAdditionalMetaData = (Document) operatedObject.get(ADMIN_METADATA);
        if (uysAdditionalMetaData == null) {
            uysAdditionalMetaData = new Document();
            operatedObject.put(ADMIN_METADATA, uysAdditionalMetaData);
            uysAdditionalMetaData.put(CREATE_USER, username);
            uysAdditionalMetaData.put(CREATE_DATE, new Date());
            uysAdditionalMetaData.put(CREATE_SESSIONID, sessionId);
        } else {
            uysAdditionalMetaData.put(UPDATE_DATE, new Date());
            uysAdditionalMetaData.put(UPDATE_USER, username);
        }

        for (MyField myField : inode.getAutosetFields()) {
            Object value = operatedObject.get(myField.getKey());
            if (value == null) {
                operatedObject.put(myField.getKey(), filterService.getTableFilterCurrent().get(myField.getKey()));
            }
        }

        for (String fieldKey : operatedObject.keySet()) {
            MyField fieldStriucture = myForm.getField(fieldKey);
            if (fieldStriucture == null) {
                continue;
            }
            Object fieldValue = operatedObject.get(fieldKey);
            Object defaultValue = fieldStriucture.getDefaultValue();
            if (defaultValue != null && (fieldValue == null || "".equals(fieldValue))) {
                operatedObject.put(fieldKey, defaultValue);
            }
        }

        for (String fieldKey : myForm.getFieldsKeySet()) {
            MyField fieldStriucture = myForm.getField(fieldKey);
            if (fieldStriucture.getCalculateOnSave()) {
                operatedObject.put(fieldKey, calcService.calculateValue(operatedObject, fieldKey, FacesContext.getCurrentInstance()));
            }
        }

        String operatorLdapUID = username;

        Document result;

        if (operatedObject.get(MONGO_ID) != null) {

            Bson query = Filters.eq(MONGO_ID, operatedObject.get(MONGO_ID));

            mongoDbUtil.updateOne(inode.getDb(), inode.getTable(), query, operatedObject);

            result = mongoDbUtil.findOne(inode.getDb(), inode.getTable(), query);
        } else {
            // still no way to get the just inserted object id. 
            // we dont wont to create id on java side. we want to leave this job to mongodb.
            // for ease retrieving the just inserted object we add an additonal retrieve InsertId to object
            // it can be easly removed later.

            String toBeRetrivedValue = String.format("s:%s_r:%s_t:%s_u:%s_c:%s",
                    sessionId,//
                    new RandomString(32).nextString(),//
                    new Date().getTime(),//
                    username,//
                    myForm.getTable()
            );

            Document record = new Document(operatedObject).append(UYS_EASY_FIND_KEY, toBeRetrivedValue);

            try {

                for (String key : record.keySet()) {
                    if (record.get(key) instanceof Object[]) {
                        Object[] multiValues = (Object[]) record.get(key);
                        List<String> list = new ArrayList<>();
                        for (Object obj : multiValues) {
                            list.add(obj.toString());
                        }
                        record.put(key, list);
                    }
                }
                mongoDbUtil.insertOne(inode.getDb(), inode.getTable(), record);
            } catch (MongoWriteException ex) {
                throw new FormConfigException(ex.getMessage(), ex);
            }

            result = mongoDbUtil.findOne(inode.getDb(), inode.getTable(), new Document(UYS_EASY_FIND_KEY, toBeRetrivedValue));

            operatedObject.append(MONGO_ID, record.get(MONGO_ID));

        }

        for (String field : myForm.getFieldsKeySet()) {
            MyField fieldStriucture = myForm.getField(field);
            if (fieldStriucture.getCalculateAfterSave()) {
                result.put(field, calcService.calculateValue(operatedObject, field, FacesContext.getCurrentInstance()));
            }
        }

        if (enableHistoryOnSave) {
            try {
                MongoDbVersion.instance(mongoDbUtil).archive(//
                        inode.getDb(),//
                        inode.getVersionCollection(),//
                        myForm.getKey(),//
                        result, //
                        ip, //
                        operatorLdapUID,//
                        inode.getVersionFields());
            } catch (Exception ex) {
                logger.error("error occured", ex);
            }
        }

        //begin : provide uploaded file relation
        if (!listFileData.isEmpty()) {
            List<ObjectId> listOfFileIDs = new ArrayList<>();

            for (Map map : listFileData) {
                listOfFileIDs.add(new ObjectId((String) map.get(FILE_ID)));
            }

            mongoDbUtil.updateMany(baseService.getProperties().getUploadTable(), "fs.files",
                    new Document(MONGO_ID, new Document(DOLAR_IN, listOfFileIDs)),
                    new Document("metadata.crud_object_id", result.get(MONGO_ID)));

            if ("iondb".equals(myForm.getDb()) && !myForm.isHasAttachedFiles()) {
                /*
                 eimza bidirim formlarında 
                 eğer formun eki ajax olarak schemadan kaldırıldıysa
                 kayderken daha önceden eklenen ekleri sil. aksi takdirde bunlar eimzaya yansıyor
                 */

                mongoDbUtil.removeFile(baseService.getProperties().getUploadTable(),
                        new BasicDBObject().append("metadata.crud_object_id", result.get(MONGO_ID))//
                                .append("metadata.username", username));
            }
        }
        //end : provide uploaded file relation

        try {
            PostSaveResult postSaveResult = repositoryService
                    .runEventPostSave(operatedObject, formService.getMyForm(), crudObject);
            //FIXME messagebundle
            if (postSaveResult.getMsg() != null) {
                dialogController.showPopupInfoWithOk(postSaveResult.getMsg(), MESSAGE_DIALOG);
            }
        } catch (Exception ex) {
            logger.error("error occured", ex);
            dialogController.showPopupError(ex.toString());
        }

        if (formService.getMyForm().getMyNotifies() != null) {
            for (MyNotifies myNotifies : formService.getMyForm().getMyNotifies().getList()) {
                myNotifies.reEnable(crudObject);
                myNotifies.reTo(crudObject);
                myNotifies.reSubject(crudObject);
                myNotifies.reContent(crudObject);
                if (myNotifies.isEnable() && myNotifies.isEmail()) {
                    mailService.sendMail(myNotifies.getSubject(), myNotifies.getContent(), myNotifies.getTo());
                }
            }
        }

        return (ObjectId) result.get(MONGO_ID);
    }

    public void upload(FileUploadEvent event) {
        uploadedFile = event.getFile();

        try {
            switch (WRITE_TO) {
                case "GRIDFS_DB":
                    BasicDBObject metadata = new BasicDBObject();
                    // null points to the fact that this record(file) is not related yet.
                    // we will use this NULL state during the search over all other forms
                    metadata.put("crud_object_id", null);
                    metadata.put("selectFormKey", formService.getMyForm().getKey());
                    metadata.put("selectFormName", formService.getMyForm().getName());
                    metadata.put("username", loginController.getLoggedUserDetail().getUsername());

                    GridFSInputFile gridFSInputFile = mongoDbUtil.createFile(baseService.getProperties().getUploadTable(),
                            uploadedFile.getInputStream());

                    gridFSInputFile.setFilename(uploadedFile.getFileName());
                    gridFSInputFile.setMetaData(metadata);
                    gridFSInputFile.save();
                    break;
                case "FILESISTEM":
                    break;
                case "FILESISTEM_AND_DATABASE":
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            logger.error("error occured", ex);
            dialogController.showPopupError(ex.toString());
        }

        refreshUploadedFileList();

    }

    protected void addMessage(String componentId, String summary, String message, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(componentId, new FacesMessage(severity, summary, message));
    }

    public void someaction(final AjaxBehaviorEvent event) {
        try {

            String fieldKey = null;

            if (event == null) {//it is when p:selectOneMenu is place inside ui:include
                fieldKey = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(FIELD_KEY);
            } else {
                fieldKey = (String) event.getComponent().getAttributes().get(FIELD_KEY);
            }

            if (fieldKey == null) {
                throw new MongoConfigurationException("fieldKey aattribute missed on ajax component");
            }

            MyField myField = formService.getMyForm().getField(fieldKey);
            String ajaxAction = myField.getAjaxAction();

            if (ajaxAction == null) {
                return;
            }
            HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

            switch (ajaxAction) {
                case "render":
                    formService.getMyForm().runAjaxRender(myField, componentMap, formService.getMyForm(), crudObject,
                            loginController.getRoleMap(), loginController.getLoggedUserDetail(), filterService.getTableFilterCurrent());
                case "render-ref":
                    formService.getMyForm().runAjaxRenderRef(myField, componentMap, formService.getMyForm(), crudObject,
                            loginController.getRoleMap(), loginController.getLoggedUserDetail(), filterService.getTableFilterCurrent());
                default:
                    break;
            }
        } catch (Exception ex) {
            logger.error("error occured", ex);
            addMessage(null, null, ex.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public String getInputText() {
        return ComponentType.inputText.name();
    }

    public String getInputFile() {
        return ComponentType.inputFile.name();
    }

    public String getInputTextarea() {
        return ComponentType.inputTextarea.name();
    }

    public String getInputMask() {
        return ComponentType.inputMask.name();
    }

    public String getInputDate() {
        return ComponentType.inputDate.name();
    }

    public String getAutoComplete() {
        return "autoComplete";
    }

    public String getSelectOneMenu() {
        return ComponentType.selectOneMenu.name();
    }

    public String getSelectManyListbox() {
        return ComponentType.selectManyListbox.name();
    }

    public String getSelectOneRadio() {
        return ComponentType.selectOneRadio.name();
    }

    public String getSelectBooleanCheckbox() {
        return ComponentType.selectBooleanCheckbox.name();
    }

    public List<Map<String, String>> getListFileData() {
        return listFileData;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public Map<String, MyField> getComponentMap() {
        return componentMap;
    }

}
