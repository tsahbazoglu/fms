package tr.org.tspb.table;

import com.google.gson.Gson;
import static tr.org.tspb.constants.ProjectConstants.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import tr.org.tspb.util.tools.MongoDbVersion;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.common.services.LoginController;
import tr.org.tspb.util.crypt.RandomString;
import tr.org.tspb.common.services.LdapService;
import tr.org.tspb.common.services.MailService;
import tr.org.tspb.service.RepositoryService;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import com.mongodb.client.model.Filters;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.exceptions.LdapException;
import tr.org.tspb.exceptions.UserException;
import tr.org.tspb.pojo.MyLdapUser;
import java.util.Set;
import org.bson.conversions.Bson;
import tr.org.tspb.converter.props.MessageBundleLoaderv1;
import tr.org.tspb.converter.base.BsonConverter;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyItems;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.dao.MyMerge;
import tr.org.tspb.dao.MyNotifies;
import tr.org.tspb.dp.nullobj.PlainRecordData;
import tr.org.tspb.pojo.ExcellColumnDef;
import tr.org.tspb.pojo.UserDetail;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import javax.annotation.PostConstruct;
import tr.org.tspb.common.qualifier.MyCtrlServiceQualifier;
import tr.org.tspb.dao.ChildFilter;
import tr.org.tspb.dao.MyBaseRecord;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.factory.qualifier.OgmCreatorQualifier;
import tr.org.tspb.service.CtrlService;
import tr.org.tspb.factory.cp.OgmCreatorIntr;
import tr.org.tspb.pojo.PostSaveResult;
import tr.org.tspb.service.CalcService;

/**
 *
 * @author Telman Şahbazoğlu
 */
public abstract class FmsTable extends FmsTableView {

    @Inject
    LdapService ldapService;

    @Inject
    MailService mailService;

    @Inject
    RepositoryService repositoryService;

    @Inject
    @MyCtrlServiceQualifier
    CtrlService ctrlService;

    @Inject
    @OgmCreatorQualifier
    private OgmCreatorIntr ogmCreator;

    @Inject
    protected CalcService calcService;

    protected MyMap crudObject;

    private Map<String, MyField> componentMap = new HashMap();
    private Map transparentProperties = new HashMap();
    private Map emailData = new HashMap();
    private String crudObjectTextViewer;
    private String imz64;
    private UploadedFile uploadedFile;
    private UploadedFile uploadedFileKpbDb;

    private Map<String, Object> uploadedMergeObject = new HashMap<>();

    // mapRequired
    protected Map mapRequired = new HashMap();

    private final static String WRITE_TO = "GRIDFS_DB";
    private List<Map<String, String>> myImzas;

    private int fileLimit = 1;
    private String mongoUploadFileType = "/(\\.|\\/)(pdf)$/";
    private String invalidFileMessage = "Geçersiz Dosya Tipi (Sadece PDF dosyalar eklenebilir) : ";
    private List<Document> listOfToBeUpsert = new ArrayList<>();
    private String toBeDeletedFileID;
    private boolean enableHistoryOnSave = true;
    private String headerTitle;
    protected List<Map<String, String>> listFileData = new ArrayList<>();
    protected List<Map<String, String>> fileListAll = new ArrayList<>();
    private List versionHistory = new ArrayList<>();
    private List historyColumnModel = new ArrayList();

    private static final String SUBJECT = "subject";
    private static final String DEADLINE = "deadline";

    public static final String CRUD_OPERATION_DIALOG2 = "crudOperationDialog2";
    public static final String DLG_CRUD_JSON = "wv-dlg-crud-json";

    Gson gsonConverter = new Gson();
    Type gsonType = new TypeToken<List<String>>() {
    }.getType();

    @PostConstruct
    public void init() {
        crudObject = ogmCreator.getCrudObject();
    }

    public Map<String, MyField> getComponentMap() {
        return Collections.unmodifiableMap(componentMap);
    }

    public void addComponent(String key, MyField myField) {
        this.componentMap.put(key, myField);
    }

    public void setComponentMap(Map<String, MyField> componentMap) {
        this.componentMap = componentMap;
    }

    public boolean runEventPreSave(Map query) {

        if (formService.getMyForm().getEventPreSave() == null) {
            return false;
        }

        String eventPreSaveDB = formService.getMyForm().getEventPreSave().getDb();

        if (eventPreSaveDB == null) {
            //FIXME messagebundle
            dialogController.showPopupInfoWithOk("<ul>"
                    + "<li><font color='red'>Kaydetme İşlemi Gerçekleştirilemedi.</font></li>"
                    + "<li>Konfigürasyon Hatası : db tanımlı değil.</li>"
                    + "</ul>", MESSAGE_DIALOG);
            return true;
        }

        Document myCrudObject = new Document(crudObject);
        myCrudObject.remove(INODE);// we remove it bacuase of MyForm class cannot be serialized for mongo.doEval

        String code = formService.getMyForm().getEventPreSave().getJsFunction();
        Document commandResult = mongoDbUtil.runCommand(eventPreSaveDB, code, query, myCrudObject);
        Object result = commandResult.get(RETVAL);

        if (Boolean.TRUE.equals(result)) {
            //FIXME messagebundle
            dialogController.showPopupInfoWithOk("<ul>"
                    + "<li><font color='red'>Kaydetme İşlemi Gerçekleştirilemedi.</font></li>"
                    + "<li>\"Birlik Temsilcisi\" yalnız bir defa seçilebilmektedir. <br/>Daha önce seçim yaptınız.</li>"
                    + "</ul>", MESSAGE_DIALOG);
            return true;
        }

        if (result instanceof Document) {
            Document resultJSON = (Document) result;
            if ("facesMessage".equals(resultJSON.get("gui"))) {
                String mssssage = resultJSON.get("facesMessage").toString();
                FacesMessage.Severity severity;
                switch (resultJSON.get("facesMessageSeverity").toString()) {
                    case "error":
                        severity = FacesMessage.SEVERITY_ERROR;
                        break;
                    case "info":
                        severity = FacesMessage.SEVERITY_INFO;
                        break;
                    case "warn":
                        severity = FacesMessage.SEVERITY_WARN;
                        break;
                    default:
                        severity = FacesMessage.SEVERITY_INFO;
                        break;
                }
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(severity, mssssage, "*"));
            } else {
                String mssssage = resultJSON.get("popupMessage").toString();
                dialogController.showPopupInfoWithOk(mssssage, MESSAGE_DIALOG);
            }
            return true;
        }

        return false;
    }

    public String getInvalidFileMessage() {
        return invalidFileMessage;
    }

    public void setInvalidFileMessage(String invalidFileMessage) {
        this.invalidFileMessage = invalidFileMessage;
    }

    public Map<String, Object> getUploadedMergeObject() {
        return Collections.unmodifiableMap(uploadedMergeObject);
    }

    public void setUploadedMergeObject(Map<String, Object> uploadedMergeObject) {
        this.uploadedMergeObject = uploadedMergeObject;
    }

    /**
     * @return the mongoUploadFileType
     */
    public String getMongoUploadFileType() {
        return mongoUploadFileType;
    }

    /**
     * @param mongoUploadFileType the mongoUploadFileType to set
     */
    public void setMongoUploadFileType(String mongoUploadFileType) {
        this.mongoUploadFileType = mongoUploadFileType;
    }

    public int getFileLimit() {
        return fileLimit;
    }

    public void setFileLimit(int fileLimit) {
        this.fileLimit = fileLimit;
    }

    public List<Map<String, String>> getMyEimzas() {
        return Collections.unmodifiableList(myImzas);
    }

    public String getCrudObjectTextViewer() {
        return crudObjectTextViewer;
    }

    public String getImz64() {
        return imz64;
    }

    public void setImz64(String imz64) {
        this.imz64 = imz64;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
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

                    GridFSInputFile gridFSInputFile = mongoDbUtil
                            .createFile(baseService.getProperties().getUploadTable(), uploadedFile.getInputstream());

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

    public void uploadExcell(FileUploadEvent event) {

        uysApplicationMB.initKpbMemberCache();

        listOfToBeUpsert = new ArrayList<>();

        uploadedFileKpbDb = event.getFile();

        try {
            MyMerge myMerge = formService.getMyForm().getUploadMerge();

            if (myMerge == null) {
                dialogController.showPopupError("upload merge config has not been defined");
                return;
            }

            InputStream is = uploadedFileKpbDb.getInputstream();

            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            XSSFSheet sheet = xssfWorkbook.getSheetAt(0);

            int rowcount = sheet.getLastRowNum() + 1;
            int startrow = myMerge.getWorkbookSheetStartRow();

            List<String> listOfNotFoundMembers = new ArrayList<>();

            for (int i = startrow; i < rowcount; i++) {
                XSSFRow row = sheet.getRow(i);
                //empty rows is resolved to null
                if (row == null) {
                    continue;
                }

                TreeMap<String, ExcellColumnDef> sortedExcellColumns = new TreeMap<>();

                sortedExcellColumns.putAll(myMerge.getWorkbookSheetColumnMap());

                Document record = new Document().append(FORMS, formService.getMyForm().getKey());

                int columnn = 0;
                XSSFCell cellll;

                for (String key : sortedExcellColumns.keySet()) {

                    ExcellColumnDef excellColumnDef = myMerge.getWorkbookSheetColumnMap().get(key);

                    cellll = row.getCell(columnn++);

                    if (cellll == null) {
                        record.append(excellColumnDef.getToMyField().getKey(), null);
                        continue;
                    }

                    if (cellll.getCellType() == Cell.CELL_TYPE_BLANK) {
                        record.append(excellColumnDef.getToMyField().getKey(), "");
                        continue;
                    }

                    Object obtainedValue = null;

                    if (cellll.getCellType() == Cell.CELL_TYPE_STRING) {
                        switch (excellColumnDef.getToMyField().getValueType()) {
                            case JAVAUTIL_DATE:
                                obtainedValue = cellll.getDateCellValue();
                                break;
                            case JAVALANG_DATE:
                                obtainedValue = cellll.getDateCellValue();
                                break;
                            case JAVALANG_STRING:
                                obtainedValue = cellll.getStringCellValue();
                                break;
                            default:
                                obtainedValue = cellll.getStringCellValue();
                                break;
                        }
                    } else if (cellll.getCellType() == Cell.CELL_TYPE_NUMERIC) {
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
                            default:
                                obtainedValue = cellll.getNumericCellValue();
                                break;
                        }
                    }

                    Object resolvedValue = obtainedValue;

                    if (excellColumnDef.getConverter() != null) {

                        Document commandResult = mongoDbUtil.runCommand(formService.getMyForm().getUploadMerge().getToDb(),
                                excellColumnDef.getConverter().getCode(), resolvedValue);

                        resolvedValue = commandResult.get(RETVAL);

                        if (resolvedValue instanceof Document) {
                            String type = ((Document) resolvedValue).get(TYPE).toString();
                            switch (type) {
                                case "objectid":
                                    resolvedValue = new ObjectId(((Document) resolvedValue).get(VALUE).toString());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    record.append(excellColumnDef.getToMyField().getKey(), resolvedValue);

                }
                listOfToBeUpsert.add(record);
            }

            if (!listOfNotFoundMembers.isEmpty()) {
                showNOKMessage("Kayıtlı üye listesinde aşağıdaki kurum isimleri tespit edilemedi : ");
            }
            for (String message : listOfNotFoundMembers) {
                showNOKMessage(message);
            }

        } catch (Exception ex) {
            logger.error("error occured", ex);
            dialogController.showPopupError(ex.getMessage());
        }

    }

    public String previewUpload() {

        try {
            localPreviewUpload();
            dialogController.showPopup("id_kpb_bulk_preview");
        } catch (NullNotExpectedException ex) {
            dialogController.showPopupError(ex.getMessage());
        }
        return null;
    }

    public void localPreviewUpload() throws NullNotExpectedException {

        if (formService.getMyForm().getUploadMerge() == null) {
            throw new NullNotExpectedException("form.".concat(UPLOAD_CONFIG).concat(". has not been defined."));
        }

        if (listOfToBeUpsert == null) {
            throw new NullNotExpectedException("Dosya Yükleme İşlemi Yapılmamış");
        }

        if (formService.getMyForm().getUploadMerge().isInsert()) {
            for (Document dbo : listOfToBeUpsert) {
                dbo.putAll(uploadedMergeObject);
                dbo.put(STYLE, "css-new");
            }
        } else if (formService.getMyForm().getUploadMerge().isUpdate()) {
            for (Document dbo : listOfToBeUpsert) {

                Document searchDBObject = new Document();
                searchDBObject.put(formService.getMyForm().getLoginFkField(), dbo.get(formService.getMyForm().getLoginFkField()));

                searchDBObject.putAll(uploadedMergeObject);

                Document record = mongoDbUtil.findOne(formService.getMyForm().getUploadMerge().getToDb(),
                        formService.getMyForm().getUploadMerge().getToCollection(), searchDBObject);

                dbo.putAll(uploadedMergeObject);
                dbo.put(STYLE, record == null ? "css-new" : "css-update");
            }
        }
    }

    public String bulkLoadExcell() {
        try {
            bulkLoadExcell();
        } catch (Exception ex) {
            logger.error("error occured", ex);
            dialogController.showPopupError(ex.getMessage());
        }
        return null;
    }

    public void localBulkLoadExcell() throws Exception {

        List<String> upsertKeys = null;
        if (!formService.getMyForm().getUploadMerge().getUpsertFields().isEmpty()) {
            upsertKeys = new ArrayList<>();
            for (MyField field : formService.getMyForm().getUploadMerge().getUpsertFields()) {
                upsertKeys.add(field.getKey());
            }
        }

        if (formService.getMyForm().getUploadMerge().isInsert()) {
            for (Document dbo : listOfToBeUpsert) {
                dbo.putAll(uploadedMergeObject);
                MyMap mm = ogmCreator.getCrudObject();
                mm.putAll(dbo);
                saveObject(formService.getMyForm(), loginController, mm);
            }
        } else if (formService.getMyForm().getUploadMerge().isUpdate()) {
            for (Document dbo : listOfToBeUpsert) {
                dbo.putAll(uploadedMergeObject);
                Document search = new Document(FORMS, formService.getMyForm().getKey());
                if (upsertKeys != null) {
                    for (String key : upsertKeys) {
                        search.put(key, dbo.get(key));
                    }
                } else {
                    search.putAll(dbo);
                }
                mongoDbUtil.updateMany(formService.getMyForm().getUploadMerge().getToDb(),
                        formService.getMyForm().getUploadMerge().getToCollection(),
                        search, dbo);
            }
        }
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

    protected void refreshUploadedFileList() {
        ObjectId objectId = null;

        if (crudObject.get(MONGO_ID) instanceof ObjectId) {
            objectId = (ObjectId) crudObject.get(MONGO_ID);
        } else if (crudObject.get(MONGO_ID) instanceof MyBaseRecord) {
            objectId = ((MyBaseRecord) crudObject.get(MONGO_ID)).getObjectId();
        }

        listFileData = repositoryService.findGridFsFileList(objectId);
        refreshUploadedFileListAll();
    }

    public void refreshUploadedFileListAll() {
        this.fileListAll = repositoryService.findGridFsFileList(formService.getMyForm());
    }

    public void resetMyObject() {
        this.crudObject = ogmCreator.getCrudObject();
    }

    public MyMap getMyObject() {
        return crudObject;
    }

    public void setMyObject(MyMap myObject) {
        this.crudObject = myObject;
    }

    public Map getEmailData() {
        return Collections.unmodifiableMap(emailData);
    }

    public void setEmailData(Map emailData) {
        this.emailData = emailData;
    }

    public Map getTransparentProperties() {
        return transparentProperties;
    }

    public void setTransparentProperties(Map transparentProperties) {
        this.transparentProperties = transparentProperties;
    }

    public boolean isEnableHistoryOnSave() {
        return enableHistoryOnSave;
    }

    public List<SelectItem> getEmailTypes() {
        List<SelectItem> list = new ArrayList<>();
        list.add(new SelectItem("PLEASE_SELECT", "Lütfen Seçiniz ..."));

        Document objectEmail = (Document) ((Document) formService.getMyForm().getActions()).get(EMAIL);

        Object emailTypes = objectEmail.get("emailTypes");

        if (emailTypes != null) {
            for (Object obj : (Iterable<? extends Object>) emailTypes) {
                Document dbo = (Document) obj;
                list.add(new SelectItem(dbo.get(VALUE), (String) dbo.get(LABEL)));
            }
        }
        return list;
    }

    public void setEnableHistoryOnSave(boolean enableHistoryOnSave) {
        this.enableHistoryOnSave = enableHistoryOnSave;
    }

    public String actionEmail(MyMap crudObject) throws IOException, Exception {

        String emailType = (String) transparentProperties.get("emailType");
        String email;
        String content = null;
        String subject;
        byte[] attachment = null;

        Map<String, Object> updateMap = new HashMap(crudObject);

        Object objectEmail = ((Document) formService.getMyForm().getActions()).get(EMAIL);

        if (!(objectEmail instanceof Document)) {
            throw new Exception("email field is not instance of DBObject");
        }

        String applicationJavaFunction = ((Document) objectEmail)//
                .getString("applicationJavaFunction");

        if ("updateLdapRecord(updateMap)".equals(applicationJavaFunction)) {
            ldapService.updatePswd(new MyLdapUser(updateMap).getUid());
        }

        Document enrichedCrudObject = new Document();
        for (String key : updateMap.keySet()) {
            if (formService.getMyForm().getField(key) != null) { //"approve" durumu
                if (formService.getMyForm().getField(key).getMyconverter() instanceof BsonConverter) {
                    try {
                        Document dbObject = Document.parse((String) updateMap.get(key));
                        enrichedCrudObject.put(key, dbObject);
                    } catch (Exception e) {
                        enrichedCrudObject.put(key, updateMap.get(key));
                    }
                } else if (updateMap.get(key) instanceof ObjectId) {
                    Object obj = mongoDbUtil.findOne(formService.getMyForm().getDb(), formService.getMyForm().getField(key).getItemsAsMyItems().getTable(),
                            new Document(MONGO_ID, updateMap.get(key)));
                    enrichedCrudObject.put(key, obj);
                } else {
                    enrichedCrudObject.put(key, updateMap.get(key));
                }
            } else {
                enrichedCrudObject.put(key, updateMap.get(key));
            }
        }

        Object msgFormatContent = ((Document) objectEmail).get("msgFormatContent");
        if (msgFormatContent instanceof Code) {
            String code = ((Code) msgFormatContent).getCode();
            //begin
            //otherwise we get undefined on crudObject.workflowStatus.name

            //end
            Document commandResult = mongoDbUtil.runCommand(formService.getMyForm().getDb(), code, enrichedCrudObject);
            msgFormatContent = commandResult.getString(RETVAL);
        }

        if (!"PLEASE_SELECT".equals(emailType)) {
            msgFormatContent = emailType;
        }

        Document Document = mongoDbUtil.findOne(CONFIG_DB, COLLECTION_SYSTEM_MESSAGES, new Document(CODE, msgFormatContent));

        if (Document == null) {
            throw new Exception("Bu duruma uygun gönderilcek email içeriği tespit edilemedi.");
        }

        Object to = ((Document) objectEmail).get("to");
        if (to instanceof Code) {
            String code = ((Code) to).getCode();
            //begin
            //otherwise we get undefined on crudObject.workflowStatus.name

            //end
            Document commandResult = mongoDbUtil.runCommand(formService.getMyForm().getDb(), code, enrichedCrudObject);
            email = commandResult.getString(RETVAL);
        } else {
            throw new Exception("\"to\" alanı tanımlı değil");
        }

        subject = Document.getString(SUBJECT);

        List attrs = new ArrayList();
        List msgFormatAttrs = (List) Document.get("msgFormatAttrs");

        if (msgFormatAttrs != null) {
            for (Object key : msgFormatAttrs) {
                if ("new Date()".equals(key)) {
                    attrs.add(SIMPLE_DATE_FORMAT__0.format(new Date()));
                } else if ("new Date()+1".equals(key)) {
                    Date nextDate = calculateDayIntervalLater(1);
                    attrs.add(String.format("%s tarihli saat %s",//
                            SIMPLE_DATE_FORMAT__0.format(nextDate),
                            SIMPLE_DATE_FORMAT__7.format(nextDate)//
                    ));
                    emailData.put(DEADLINE, nextDate);
                } else {
                    String[] subKeys = ((String) key).split("[.]");
                    Object value = enrichedCrudObject;
                    for (String subKey : subKeys) {
                        value = ((Map) value).get(subKey);
                    }
                    if (value instanceof Number) {
                        NumberFormat nf = NumberFormat.getNumberInstance();
                        nf.setGroupingUsed(false); //otgherwise it put comma. we dont want comma here
                        value = nf.format(value);
                    }
                    attrs.add(value);
                }
            }
        }

        if (Document != null && Document.get("contentQuery") != null) {

            Document fileQuery = (Document) Document.get("contentQuery");

            String gridfsDb = (String) fileQuery.get(FORM_DB);
            String filename = (String) fileQuery.get("filename");

            List<GridFSDBFile> gridFSDBFiles = mongoDbUtil.findFiles(gridfsDb, filename);

            if (gridFSDBFiles.isEmpty()) {
                throw new Exception("no email file on gridfs with name ".concat(filename));
            }

            StringWriter sw = new StringWriter();

            InputStreamReader streamReader = new InputStreamReader(gridFSDBFiles.get(0).getInputStream());

            int c;
            while ((c = streamReader.read()) != -1) {
                sw.append((char) c);
            }

            content = MessageFormat.format(sw.toString(), attrs.toArray());
            attachment = mailService.createAttachment(Document, gridfsDb);
        }

        emailData.put(EMAIL, email);
        emailData.put(SUBJECT, subject);
        emailData.put("content", content);
        emailData.put("attachment", attachment);

        dialogController.showPopup("Email Önizleme", "dummy", "emailPreview");

        return null;
    }

    public Date calculateDayIntervalLater(int dayInterval) {

        List offDays = Arrays.asList("30.8");//dd.MM bayram gunleri

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        while (dayInterval != 0) {
            dayInterval--;
            cal.add(Calendar.DAY_OF_YEAR, 1);
            while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY//
                    || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY//
                    || offDays.contains(//
                            MessageFormat.format("{0}.{1}", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1))) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 0);

        return cal.getTime();
    }

    public String gonder(MyMap crudObject) {
        String subject = (String) emailData.get(SUBJECT);
        String content = (String) emailData.get("content");
        String email = (String) emailData.get(EMAIL);
        byte[] attachment = (byte[]) emailData.get("attachment");
        Date deadline = (Date) emailData.get(DEADLINE);

        try {
            mailService.sendMail(subject, content, email, attachment);
            if (deadline != null) {
                crudObject.put(DEADLINE, deadline);
                if (this instanceof TwoDimModifyCtrl) {
                    ((TwoDimModifyCtrl) this).saveObject();
                }
            }
        } catch (MessagingException ex) {
            logger.error("error occured", ex);
        }

        return null;
    }

    public String prepareQuery(MyMap crudObject) {
        return null;
    }

    public List<Map<String, String>> getFileData() {
        return Collections.unmodifiableList(listFileData);
    }

    public List<Map<String, String>> getFileListAll() {
        return Collections.unmodifiableList(fileListAll);
    }

    public ObjectId saveObjectFromPaymentService(Document operatedObject, String username, MyForm myForm, String ip) throws Exception {
        if (!"payment-service-user".equals(username)) {
            throw new RuntimeException("Upsss ...");
        }
        return saveOneDimensionObject(operatedObject, username, myForm, ip, "payment-service-no-session");
    }

    private ObjectId saveOneDimensionObject(Document operatedObject, String username, MyForm myForm, String ip, String sessionId)
            throws MessagingException, NullNotExpectedException, LdapException, FormConfigException, MongoOrmFailedException {

        MyForm inode = (MyForm) operatedObject.get(INODE);
        operatedObject.remove(INODE);//just to sutisfy the icefaces
        if (inode == null) {
            inode = myForm;
        }

        ctrlService.checkRecordConverterValueType(operatedObject, myForm);

        if (myForm.isHasAttachedFiles() && (listFileData == null || listFileData.isEmpty())) {
            for (MyField field : myForm.getFields().values()) {
                if (field.isRequired() && getInputFile().equals(field.getComponentType())) {
                    FacesMessage facesMessageRequired = new FacesMessage(//
                            FacesMessage.SEVERITY_ERROR, //
                            MessageFormat.format("[{0}] {1}", field.getShortName(), MessageBundleLoaderv1.getMessage("requiredMessage")),//
                            "*");
                    FacesContext.getCurrentInstance().addMessage(null, facesMessageRequired);
                    return operatedObject.getObjectId(MONGO_ID);
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
                 eğer formun eki ajax olarak schemadan kaldırıldıysa (ornegin : ion_form_1170 )
                 kayederken daha oceden eklenen ekleri sil. aksi takdirde bunlar eimzaya yansıyor
                 */

                mongoDbUtil.removeFile(baseService.getProperties().getUploadTable(),
                        new BasicDBObject().append("metadata.crud_object_id", result.get(MONGO_ID))//
                                .append("metadata.username", username));
            }
        }
        //end : provide uploaded file relation

        PostSaveResult postSaveResult = repositoryService.runEventPostSave(operatedObject, myForm, null);

        if (postSaveResult.getMsg() != null) {
            dialogController.showPopupInfoWithOk(postSaveResult.getMsg(), MESSAGE_DIALOG);
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

    protected String deleteObject(LoginController loginMB, MyForm myForm, MyMap crudObject) throws Exception {
        String collection = myForm.getTable();

        ObjectId objectID = (ObjectId) crudObject.get(MONGO_ID);
        if (objectID != null) {

            Document toBeDeleted = repositoryService.expandCrudObject(myForm, new Document(crudObject));

            mongoDbUtil.trigger(//
                    toBeDeleted,//
                    myForm.getEventPreDelete(), loginController.getRolesAsList());

            List<ChildFilter> childFilters = myForm.getChilds();

            for (ChildFilter childFilter : childFilters) {
                if (mongoDbUtil.findOne(childFilter.getDb(), childFilter.getTable(), Filters.eq(childFilter.getFieldKey(), objectID)) != null) {
                    throw new Exception(childFilter.print(objectID));
                }
            }

            mongoDbUtil.deleteMany(myForm.getDb(), collection, new Document(MONGO_ID, objectID));

            mongoDbUtil.trigger(repositoryService.expandCrudObject(myForm, new Document(crudObject)), myForm.getEventPostDelete(), loginController.getRolesAsList());

            crudObject = ogmCreator.getCrudObject();

        }
        dialogController.hidePopup(CRUD_OPERATION_DIALOG2);

        return null;
    }

    public String copyObject(MyForm myForm, LoginController loginMB, MyMap crudObject) throws Exception {

        Document operatedObject = new Document(crudObject);

        operatedObject.remove(MONGO_ID);

        if (!(loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminRole()) //
                || crudObject.get(formService.getMyForm().getLoginFkField()) == null
                || loginMB.getLoggedUserDetail().getDbo().getObjectId().equals(operatedObject.get(formService.getMyForm().getLoginFkField())))) {
            throw new Exception("Sisteme girş yapan kullanıcı yalnızca kendisine ait veri ekleyip değiştirebilir.");
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String sessionId = ((HttpSession) facesContext.getExternalContext().getSession(false)).getId();

        saveOneDimensionObject(operatedObject, loginMB.getLoggedUserDetail().getUsername(), formService.getMyForm(), request.getRemoteAddr(), sessionId);

        crudObject.put(STATE, "saved");
        return null;
    }

    public ObjectId saveObject(MyForm myForm, LoginController loginMB, MyMap crudObject)
            throws UserException, MessagingException, NullNotExpectedException, LdapException, FormConfigException, MongoOrmFailedException {

        Object loginFkFieldValue = crudObject.get(formService.getMyForm().getLoginFkField());

        if (loginFkFieldValue instanceof MyBaseRecord) {
            loginFkFieldValue = ((MyBaseRecord) loginFkFieldValue).getObjectId();
        }

        boolean ok = loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminRole());

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
                formService.getMyForm(), request.getRemoteAddr(), sessionId);
        crudObject.put(STATE, "saved");

        return returnID;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public void fillSelectItems(DBCursor cursor, List<SelectItem> selectItems, Document viewObject) throws MongoException {

        while (cursor.hasNext()) {
            //FIXME Proivide this abilitiy with converter
            //select One get the to String as value
            Document object = (Document) cursor.next();

            StringBuilder value = new StringBuilder();
            Iterator<String> iterator = viewObject.keySet().iterator();
            while (iterator.hasNext()) {
                String field = iterator.next();
                Object fieldValue = object.get(field);
                if (fieldValue instanceof Date) {
                    value.append(SIMPLE_DATE_FORMAT__0.format(fieldValue));
                } else {
                    value.append(fieldValue);
                }
                if (iterator.hasNext()) {
                    value.append(" - ");
                }
            }
            selectItems.add(new SelectItem(object.get(MONGO_ID), value.toString()));
        }
    }

    public List getVersionHistory() {
        return Collections.unmodifiableList(versionHistory);
    }

    public List getHistoryColumnModel() {
        return Collections.unmodifiableList(historyColumnModel);
    }

    protected MyMap prepareCrudObject(Map rowData) {

        MyMap crudObject = ogmCreator.getCrudObject();

        crudObject.clear();

        //FIXME Why?
        if (Boolean.TRUE.equals(rowData.get(CALCULATE))) {
            return null;
        }

        crudObject.putAll(rowData);

        for (String key : (Set<String>) rowData.keySet()) {

            MyField myField = formService.getMyForm().getField(key);

            if (myField != null && myField.isAutoComplete()) {

                MyItems myItems = myField.getItemsAsMyItems();

                Document doc = mongoDbUtil.findOne(myItems.getDb(), myItems.getTable(), Filters.eq(MONGO_ID, rowData.get(key)));

                crudObject.put(key, PlainRecordData.getPlainRecord(doc, myItems));
            }
        }

        if (formService.getMyForm().getVersionCollection() != null) {

            ObjectId objectId = null;

            if (crudObject.get(MONGO_ID) instanceof ObjectId) {
                objectId = (ObjectId) crudObject.get(MONGO_ID);
            } else if (crudObject.get(MONGO_ID) instanceof MyBaseRecord) {
                objectId = ((MyBaseRecord) crudObject.get(MONGO_ID)).getObjectId();
            }

            Map<String, List> map = MongoDbVersion.instance(mongoDbUtil).fetch(//
                    formService.getMyForm(),//
                    formService.getMyForm().getDb(), //
                    formService.getMyForm().getVersionCollection(), //
                    objectId,
                    formService.getMyForm().getVersionFields());

            historyColumnModel = map.get(COLUMN_LIST);
            versionHistory = map.get(ROW_LIST);
        }
        return crudObject;
    }

    public void resetHistory() {
        historyColumnModel = new ArrayList();
        versionHistory = new ArrayList();
    }

    public UploadedFile getUploadedFileKpbDb() {
        return uploadedFileKpbDb;
    }

    public void setUploadedFileKpbDb(UploadedFile uploadedFileKpbDb) {
        this.uploadedFileKpbDb = uploadedFileKpbDb;
    }

    public List<Document> getListOfToBeUpsert() {
        return Collections.unmodifiableList(listOfToBeUpsert);
    }

    public void setListOfToBeUpsert(List<Document> listOfToBeUpsert) {
        this.listOfToBeUpsert = listOfToBeUpsert;
    }

}
