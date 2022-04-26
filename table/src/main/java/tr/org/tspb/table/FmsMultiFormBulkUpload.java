/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.table;

import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import tr.org.tspb.common.qualifier.MyCtrlServiceQualifier;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import tr.org.tspb.common.qualifier.MyQualifier;
import tr.org.tspb.common.qualifier.ViewerController;
import tr.org.tspb.common.services.AppScopeSrvCtrl;
import tr.org.tspb.common.services.BaseService;
import tr.org.tspb.common.services.LoginController;
import tr.org.tspb.common.services.MailService;
import static tr.org.tspb.constants.ProjectConstants.ADMIN_METADATA;
import static tr.org.tspb.constants.ProjectConstants.CREATE_DATE;
import static tr.org.tspb.constants.ProjectConstants.CREATE_SESSIONID;
import static tr.org.tspb.constants.ProjectConstants.CREATE_USER;
import static tr.org.tspb.constants.ProjectConstants.FORMS;
import static tr.org.tspb.constants.ProjectConstants.INODE;
import static tr.org.tspb.constants.ProjectConstants.JAVALANG_DATE;
import static tr.org.tspb.constants.ProjectConstants.JAVALANG_INTEGER;
import static tr.org.tspb.constants.ProjectConstants.JAVALANG_STRING;
import static tr.org.tspb.constants.ProjectConstants.JAVAUTIL_DATE;
import static tr.org.tspb.constants.ProjectConstants.JAVAUTIL_OBJECTID;
import static tr.org.tspb.constants.ProjectConstants.MESSAGE_DIALOG;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import static tr.org.tspb.constants.ProjectConstants.OPERATOR_LDAP_UID;
import static tr.org.tspb.constants.ProjectConstants.RETVAL;
import static tr.org.tspb.constants.ProjectConstants.STATE;
import static tr.org.tspb.constants.ProjectConstants.TYPE;
import static tr.org.tspb.constants.ProjectConstants.UPDATE_DATE;
import static tr.org.tspb.constants.ProjectConstants.UPDATE_USER;
import static tr.org.tspb.constants.ProjectConstants.UYS_EASY_FIND_KEY;
import static tr.org.tspb.constants.ProjectConstants.VALUE;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.dao.MyBaseRecord;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.dao.MyMerge;
import tr.org.tspb.dao.MyProject;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.exceptions.LdapException;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.exceptions.UserException;
import tr.org.tspb.factory.cp.OgmCreatorIntr;
import tr.org.tspb.factory.qualifier.OgmCreatorQualifier;
import tr.org.tspb.pojo.ExcellColumnDef;
import tr.org.tspb.pojo.PostSaveResult;
import tr.org.tspb.pojo.UserDetail;
import tr.org.tspb.service.CalcService;
import tr.org.tspb.service.CtrlService;
import tr.org.tspb.service.FilterService;
import tr.org.tspb.service.RepositoryService;
import tr.org.tspb.util.crypt.RandomString;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.service.DlgCtrl;
import tr.org.tspb.util.stereotype.MyController;
import tr.org.tspb.util.tools.MongoDbUtilIntr;
import tr.org.tspb.util.tools.MongoDbVersion;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;

/**
 *
 * @author telman
 * <!-- 1024*1024*1 = 1048576 = 1MB -->
 * <!-- 1024*1024*2 = 2097152 = 2MB -->
 * <!-- 1024*1024*3 = 3145728 = 3MB -->
 * <!-- 1024*1024*4 = 4194304 = 4MB -->
 * <!-- 1024*1024*5 = 5242880 = 5MB -->
 * <!-- 1024*1024*6 = 6291456 = 6MB -->
 * <!-- 1024*1024*7 = 7340032 = 7MB -->
 */
@MyController
@MyQualifier(myEnum = ViewerController.fmsMultiFormBulkUpload)
public class FmsMultiFormBulkUpload implements Serializable {

    @Inject
    protected DlgCtrl dialogController;

    @Inject
    @KeepOpenQualifier
    protected MongoDbUtilIntr mongoDbUtil;

    @Inject
    @MyLoginQualifier
    protected LoginController loginController;

    @Inject
    protected AppScopeSrvCtrl uysApplicationMB;

    @Inject
    protected Logger logger;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    protected BaseService baseService;

    @Inject
    @MyCtrlServiceQualifier
    CtrlService ctrlService;

    @Inject
    protected CalcService calcService;

    @Inject
    MailService mailService;

    @Inject
    protected FilterService filterService;

    private boolean enableHistoryOnSave = true;

    private final int fileLimit = 1;
    private final String invalidFileMessage = "Geçersiz Dosya Tipi (Sadece *.xslx uzantılı Excel dosyalar eklenebilir)";
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");

    private UploadedFile uploadedFile;
    private List<Document> listOfToBeUpsert1 = new ArrayList<>();
    private List<Document> listOfToBeUpsert2 = new ArrayList<>();
    private List<Document> listOfToBeUpsert3 = new ArrayList<>();
    private List<Document> listOfToBeUpsert4 = new ArrayList<>();
    private List<Document> listOfToBeUpsert5 = new ArrayList<>();
    private List<Document> listOfToBeUpsert6 = new ArrayList<>();

    private FmsForm myForm1;
    private FmsForm myForm2;
    private FmsForm myForm3;
    private FmsForm myForm4;
    private FmsForm myForm5;
    private FmsForm myForm6;

    private static final long TIMEOUT = 30;

    private Cache<String, Object> tokens;

    @PostConstruct
    public void init() {
        tokens = CacheBuilder.newBuilder().expireAfterWrite(TIMEOUT, TimeUnit.DAYS).build();
    }

    public String createToken(Object data) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, data);
        return token;
    }

    public Boolean isTokenValid(String token) {
        return tokens.getIfPresent(token) != null;
    }

    private void resetForms() throws NullNotExpectedException, MongoOrmFailedException {
        MyProject myProject = uysApplicationMB.getProject("gyonadvd");
        myForm1 = repositoryService.getMyFormLarge(myProject, "varlik_turu_bir");
        myForm2 = repositoryService.getMyFormLarge(myProject, "varlik_turu_iki");
        myForm3 = repositoryService.getMyFormLarge(myProject, "varlik_turu_uc");
        myForm4 = repositoryService.getMyFormLarge(myProject, "varlik_turu_dort");
        myForm5 = repositoryService.getMyFormLarge(myProject, "varlik_turu_bes");
        myForm6 = repositoryService.getMyFormLarge(myProject, "varlik_turu_alti");
    }

    private Map<String, Object> uploadedMergeObject = new HashMap<>();

    @Inject
    @OgmCreatorQualifier
    private OgmCreatorIntr ogmCreator;

    public String bulkLoadExcell() {
        try {
            localBulkLoadExcell();
        } catch (Exception ex) {

            dialogController.showPopupError(ex.getMessage());
        }
        return null;
    }

    public void uploadExcell(FileUploadEvent event) {

        uysApplicationMB.initKpbMemberCache();

        UploadedFile uploadedFile = event.getFile();

        try {

            InputStream is = uploadedFile.getInputStream();
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);

            resetForms();
            resetUpsertList();

            StringBuilder sb = new StringBuilder();

            writeToList(sb, listOfToBeUpsert1, myForm1, xssfWorkbook, 0);
            writeToList(sb, listOfToBeUpsert2, myForm2, xssfWorkbook, 1);
            writeToList(sb, listOfToBeUpsert3, myForm3, xssfWorkbook, 2);
            writeToList(sb, listOfToBeUpsert4, myForm4, xssfWorkbook, 3);
            writeToList(sb, listOfToBeUpsert5, myForm5, xssfWorkbook, 4);
            writeToList(sb, listOfToBeUpsert6, myForm6, xssfWorkbook, 5);

            if (!sb.toString().isEmpty()) {
                dialogController.showPopupError("Dosya Yükleme Esnasında oluşan hatalar :<br/><br/>".concat(sb.toString()));
            }

        } catch (Exception ex) {
            resetUpsertList();
            logger.error("error occured", ex);
            dialogController.showPopupError("Dosya Yükleme Esnasında hata oluştu.<br/><br/>".concat(ex.getMessage()));
        }

    }

    public String localBulkLoadExcell() {
        try {
            localBulkLoadExcellBy(myForm1, listOfToBeUpsert1);
            localBulkLoadExcellBy(myForm2, listOfToBeUpsert2);
            localBulkLoadExcellBy(myForm3, listOfToBeUpsert3);
            localBulkLoadExcellBy(myForm4, listOfToBeUpsert4);
            localBulkLoadExcellBy(myForm5, listOfToBeUpsert5);
            localBulkLoadExcellBy(myForm6, listOfToBeUpsert6);
        } catch (Exception ex) {
            logger.error("error occured", ex);
            dialogController.showPopupError("Dosya Yükleme Esnasında hata oluştu.<br/><br/>".concat(ex.getMessage()));
        }
        return null;
    }

    public void localBulkLoadExcellBy(FmsForm fmsForm, List<Document> listOfToBeUpsert) throws Exception {

        List<String> upsertKeys = null;
        if (!fmsForm.getUploadMerge().getUpsertFields().isEmpty()) {
            upsertKeys = new ArrayList<>();
            for (MyField field : fmsForm.getUploadMerge().getUpsertFields()) {
                upsertKeys.add(field.getKey());
            }
        }

        if (fmsForm.getUploadMerge().isInsert()) {
            for (Document dbo : listOfToBeUpsert) {
                dbo.putAll(uploadedMergeObject);
                MyMap mm = ogmCreator.getCrudObject();
                mm.putAll(dbo);
                saveObject(fmsForm, loginController, mm, fmsForm);
            }
        } else if (fmsForm.getUploadMerge().isUpdate()) {
            for (Document dbo : listOfToBeUpsert) {
                dbo.putAll(uploadedMergeObject);
                Document search = new Document(FORMS, fmsForm.getKey());
                if (upsertKeys != null) {
                    for (String key : upsertKeys) {
                        search.put(key, dbo.get(key));
                    }
                } else {
                    search.putAll(dbo);
                }
                mongoDbUtil.updateMany(fmsForm.getUploadMerge().getToDb(),
                        fmsForm.getUploadMerge().getToCollection(),
                        search, dbo, new UpdateOptions().upsert(true));
                try {
                    PostSaveResult postSaveResult = repositoryService.runEventPostSave(dbo, fmsForm, null);
                    //FIXME messagebundle
                    if (postSaveResult.getMsg() != null) {
                        dialogController.showPopupInfoWithOk(postSaveResult.getMsg(), MESSAGE_DIALOG);
                    }
                } catch (Exception ex) {
                    logger.error("error occured", ex);
                    StringBuilder dlgSb = new StringBuilder();
                    dlgSb.append("Kayıt Sonrası tetikleyici çalıştırılıyor iken bir hata oluştu. ");
                    dlgSb.append("<br/><br/>");
                    dlgSb.append("Lütfen bu durumu sistem yöneticisine bildiriniz.");
                    // dialogController.showPopupError(dlgSb.toString());
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                    "Hata",
                                    dlgSb.toString().replace("<br/>", "")));
                }

            }
        }
    }

    private void resetUpsertList() {
        listOfToBeUpsert1 = new ArrayList<>();
        listOfToBeUpsert2 = new ArrayList<>();
        listOfToBeUpsert3 = new ArrayList<>();
        listOfToBeUpsert4 = new ArrayList<>();
        listOfToBeUpsert5 = new ArrayList<>();
        listOfToBeUpsert6 = new ArrayList<>();
    }

    private void writeToList(StringBuilder sb, List<Document> listOfToBeUpsert, FmsForm myForm, XSSFWorkbook xssfWorkbook, int sheetNo)
            throws IOException, Exception {

        MyMerge myMerge = myForm.getUploadMerge();
        if (myMerge == null) {
            throw new Exception("upload merge config has not been defined");
        }

        XSSFSheet sheet = xssfWorkbook.getSheetAt(sheetNo);

        int rowcount = sheet.getLastRowNum() + 1;
        int startrow = myMerge.getWorkbookSheetStartRow();
        List<String> listOfNotFoundMembers = new ArrayList<>();
        for (int i = startrow; i < rowcount; i++) {
            XSSFRow row = sheet.getRow(i);
            //empty rows is resolved to null
            if (row == null) {
                continue;
            }

            Document record = new Document().append(FORMS, myForm.getKey());
            try {
                appendField(myMerge, row, sheet, i, record, myForm);
            } catch (Exception e) {
                sb.append(e.getMessage().concat("</br>"));
                continue;
            }

            if (!loginController.isUserInRole(myForm.getMyProject().getAdminAndViewerRole())) {
                record.put(myForm.getLoginFkField(), loginController.getLoggedUserDetail().getDbo().getObjectId());
            }

            listOfToBeUpsert.add(record);
        }
        if (!listOfNotFoundMembers.isEmpty()) {
            throw new Exception("Kayıtlı üye listesinde aşağıdaki kurum isimleri tespit edilemedi");
        }
        for (String message : listOfNotFoundMembers) {
            throw new Exception(message);
        }
    }

    private void appendField(MyMerge myMerge, XSSFRow row, XSSFSheet sheet, int i, Document record, FmsForm myForm) throws Exception {

        XSSFCell cellll;
        int columnn = 0;

        for (ExcellColumnDef excellColumnDef : myMerge.getWorkbookSheetColumnList()) {

            cellll = row.getCell(columnn++);

            if (cellll == null || cellll.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.BLANK) {
                if (excellColumnDef.getToMyField().isRequired()) {
                    throw new Exception(
                            String.format("sayfa '%s' : satır '%d' : sütun '%d' : %s' alanı zorunlu alandır", sheet.getSheetName(), i + 1, columnn, excellColumnDef.getToMyField().getName()));
                }
                record.append(excellColumnDef.getToMyField().getKey(), null);
                continue;
            }

            Object obtainedValue = null;

            if (cellll.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.STRING) {
                switch (excellColumnDef.getToMyField().getValueType()) {
                    case JAVAUTIL_DATE:
                    case JAVALANG_DATE:
                        obtainedValue = dateFormat.parse(cellll.getStringCellValue());
                        break;
                    case JAVALANG_STRING:
                        obtainedValue = cellll.getStringCellValue();
                        break;
                    default:
                        obtainedValue = cellll.getStringCellValue();
                        break;
                }
            } else if (cellll.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                switch (excellColumnDef.getToMyField().getValueType()) {
                    case JAVAUTIL_DATE:
                        obtainedValue = cellll.getDateCellValue();
                        break;
                    case JAVALANG_DATE:
                        obtainedValue = cellll.getDateCellValue();
                        break;
                    case JAVALANG_STRING:
                        obtainedValue = String.valueOf(((Number) cellll.getNumericCellValue()).longValue());
                        break;
                    case JAVAUTIL_OBJECTID:
                        obtainedValue = cellll.getNumericCellValue();
                        break;
                    case JAVALANG_INTEGER:
                        obtainedValue = new Double(cellll.getNumericCellValue()).intValue();
                        break;
                    default:
                        obtainedValue = cellll.getNumericCellValue();
                        break;
                }
            }

            Object resolvedValue = null;

            if (excellColumnDef.getConverter() != null && excellColumnDef.getConverter().getCode() != null) {

                try {
                    String token = sheet.getSheetName()
                            .concat("__").concat(Integer.toString(columnn)
                            .concat("__").concat(obtainedValue.toString()));
                    
                    if ((resolvedValue = tokens.getIfPresent(token)) == null) {
                        Document commandResult = mongoDbUtil.runCommand(myForm.getUploadMerge().getToDb(),
                                excellColumnDef.getConverter().getCode(), obtainedValue);
                        resolvedValue = commandResult.get(RETVAL);

                        if (resolvedValue instanceof Document) {
                            Document resolvedValueDoc = (Document) resolvedValue;
                            String type = resolvedValueDoc.getString(TYPE);
                            switch (type) {
                                case "objectid":
                                    resolvedValue = new ObjectId(resolvedValueDoc.getString(VALUE));
                                    break;
                                default:
                                    break;
                            }
                        }
                        tokens.put(token, resolvedValue);
                    }

                } catch (Exception ex) {
                    throw new Exception(
                            String.format("sayfa %s : satır '%d' : sütun '%d' : '%s' = %s alanının tespiti esnasında hata oluştu",
                                    sheet.getSheetName(), i, columnn, excellColumnDef.getToMyField().getName(), obtainedValue));
                }

            } else {
                resolvedValue = obtainedValue;
            }

            record.append(excellColumnDef.getToMyField().getKey(), resolvedValue);

        }
    }

    /**
     * @return the fileLimit
     */
    public int getFileLimit() {
        return fileLimit;
    }

    /**
     * @return the invalidFileMessage
     */
    public String getInvalidFileMessage() {
        return invalidFileMessage;
    }

    /**
     * @return the uploadedFile
     */
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    /**
     * @param uploadedFile the uploadedFile to set
     */
    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    protected void showOKMessage(String okMessage) {
        addMessage(null, MessageFormat.format("{0}", okMessage),
                okMessage,
                FacesMessage.SEVERITY_INFO);
    }

    protected void showNOKMessage(String nokMessage) {
        addMessage(null, MessageFormat.format("{0}", nokMessage),
                nokMessage,
                FacesMessage.SEVERITY_ERROR);
    }

    protected void addMessage(String componentId, String summary, String message, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(componentId, new FacesMessage(severity, summary, message));
    }

    public ObjectId saveObject(FmsForm myForm, LoginController loginMB, MyMap crudObject, FmsForm fmsForm)
            throws UserException, MessagingException, NullNotExpectedException, LdapException, FormConfigException, MongoOrmFailedException {

        Object loginFkFieldValue = crudObject.get(fmsForm.getLoginFkField());

        if (loginFkFieldValue instanceof MyBaseRecord) {
            loginFkFieldValue = ((MyBaseRecord) loginFkFieldValue).getObjectId();
        }

        boolean ok = loginController.isUserInRole(fmsForm.getMyProject().getAdminRole());

        ok = ok || loginController.getLoggedUserDetail().getDbo().getObjectId().equals(loginFkFieldValue);

        if (!ok) {
            for (UserDetail.EimzaPersonel ep : loginController.getLoggedUserDetail().getEimzaPersonels()) {
                if (ep.getDelegatingMember() != null && ep.getDelegatingMember().equals(loginFkFieldValue)) {
                    ok = true;
                    break;
                }
            }
        }

        if (!ok) {
            throw new UserException("Sisteme girş yapan kullanıcı yalnızca kendisine ait veri ekleyip değiştirebilir.");
        }

        Document operatedObject = new Document(crudObject);

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String sessionId = ((HttpSession) facesContext.getExternalContext().getSession(false)).getId();

        ObjectId returnID = saveOneDimensionObject(operatedObject, loginMB.getLoggedUserDetail().getUsername(),
                fmsForm, request.getRemoteAddr(), sessionId);
        crudObject.put(STATE, "saved");

        return returnID;
    }

    public ObjectId saveOneDimensionObject(Document operatedObject, String username,
            FmsForm myForm, String ip, String sessionId)
            throws MessagingException, NullNotExpectedException, LdapException, FormConfigException, MongoOrmFailedException, UserException {

        FmsForm inode = (FmsForm) operatedObject.get(INODE);
        operatedObject.remove(INODE);//just to sutisfy the icefaces
        if (inode == null) {
            inode = myForm;
        }

        ctrlService.checkRecordConverterValueType(operatedObject, myForm);

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
            MyField myField = myForm.getField(fieldKey);
            if (myField.getCalculateOnSave()) {
                operatedObject.put(fieldKey, calcService.calculateValue(operatedObject, myField, FacesContext.getCurrentInstance()));
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

        for (String fieldKey : myForm.getFieldsKeySet()) {
            MyField myField = myForm.getField(fieldKey);
            if (myField.getCalculateAfterSave()) {
                result.put(fieldKey, calcService.calculateValue(operatedObject, myField, FacesContext.getCurrentInstance()));
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

        try {
            PostSaveResult postSaveResult = repositoryService.runEventPostSave(operatedObject, myForm, null);
            //FIXME messagebundle
            if (postSaveResult.getMsg() != null) {
                dialogController.showPopupInfoWithOk(postSaveResult.getMsg(), MESSAGE_DIALOG);
            }
        } catch (Exception ex) {
            logger.error("error occured", ex);
            StringBuilder dlgSb = new StringBuilder();
            dlgSb.append("Kayıt Sonrası tetikleyici çalıştırılıyor iken bir hata oluştu. ");
            dlgSb.append("<br/><br/>");
            dlgSb.append("Lütfen bu durumu sistem yöneticisine bildiriniz.");
//            dialogController.showPopupError(dlgSb.toString());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL,
                            "Hata",
                            dlgSb.toString().replace("<br/>", "")));
        }

//        if (myForm.getMyNotifies() != null) {
//            for (MyNotifies myNotifies : myForm.getMyNotifies().getList()) {
//                myNotifies.reEnable(crudObject);
//                myNotifies.reTo(crudObject);
//                myNotifies.reSubject(crudObject);
//                myNotifies.reContent(crudObject);
//                if (myNotifies.isEnable() && myNotifies.isEmail()) {
//                    mailService.sendMail(myNotifies.getSubject(), myNotifies.getContent(), myNotifies.getTo());
//                }
//            }
//        }
        return (ObjectId) result.get(MONGO_ID);
    }

}
