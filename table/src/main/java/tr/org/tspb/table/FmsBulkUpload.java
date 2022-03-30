/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.table;

import java.io.InputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import org.bson.types.ObjectId;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import tr.org.tspb.common.qualifier.MyQualifier;
import tr.org.tspb.common.qualifier.ViewerController;
import tr.org.tspb.common.services.LoginController;
import static tr.org.tspb.constants.ProjectConstants.FORMS;
import static tr.org.tspb.constants.ProjectConstants.JAVALANG_DATE;
import static tr.org.tspb.constants.ProjectConstants.JAVALANG_STRING;
import static tr.org.tspb.constants.ProjectConstants.JAVAUTIL_DATE;
import static tr.org.tspb.constants.ProjectConstants.JAVAUTIL_OBJECTID;
import static tr.org.tspb.constants.ProjectConstants.RETVAL;
import static tr.org.tspb.constants.ProjectConstants.STATE;
import static tr.org.tspb.constants.ProjectConstants.STYLE;
import static tr.org.tspb.constants.ProjectConstants.TYPE;
import static tr.org.tspb.constants.ProjectConstants.UPLOAD_CONFIG;
import static tr.org.tspb.constants.ProjectConstants.VALUE;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.dao.MyBaseRecord;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.dao.MyMerge;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.exceptions.LdapException;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.exceptions.UserException;
import tr.org.tspb.factory.cp.OgmCreatorIntr;
import tr.org.tspb.factory.qualifier.OgmCreatorQualifier;
import tr.org.tspb.pojo.ExcellColumnDef;
import tr.org.tspb.pojo.UserDetail;
import tr.org.tspb.service.RepositoryService;
import tr.org.tspb.util.stereotype.MyController;

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
@MyQualifier(myEnum = ViewerController.fmsBulkUpload)
public class FmsBulkUpload extends FmsTable implements Serializable {

    @Inject
    @MyQualifier(myEnum = ViewerController.twoDimModifyCtrl)
    private TwoDimModifyCtrl twoDimModifyCtrl;

    private String invalidSizeMessage = "Dosya Boyutu 2MB dan büyük olmamalı : ";
    private String invalidFileMessage = "Geçersiz Dosya Tipi (Sadece *.xlsx dosyalar eklenebilir) : ";
    private String fileLimitMessage = "En fazla 2MB dosya eklenebilir.";
    private int fmsFileLimit = 1;
    private int fmsSizeLimit = 2097152;

    public int getFmsSizeLimit() {
        return fmsSizeLimit;
    }

    public void setFmsSizeLimit(int fmsSizeLimit) {
        this.fmsSizeLimit = fmsSizeLimit;
    }

    public int getFmsFileLimit() {
        return fmsFileLimit;
    }

    public void setFmsFileLimit(int fmsFileLimit) {
        this.fmsFileLimit = fmsFileLimit;
    }

    public String getInvalidSizeMessage() {
        return invalidSizeMessage;
    }

    public void setInvalidSizeMessage(String invalidSizeMessage) {
        this.invalidSizeMessage = invalidSizeMessage;
    }

    public String getFileLimitMessage() {
        return fileLimitMessage;
    }

    public void setFileLimitMessage(String fileLimitMessage) {
        this.fileLimitMessage = fileLimitMessage;
    }

    public String getInvalidFileMessage() {
        return invalidFileMessage;
    }

    public void setInvalidFileMessage(String invalidFileMessage) {
        this.invalidFileMessage = invalidFileMessage;
    }

    @Inject
    protected Logger logger;

    @Inject
    RepositoryService repositoryService;

    @Inject
    @OgmCreatorQualifier
    private OgmCreatorIntr ogmCreator;

    private List<Document> listOfToBeUpsert = new ArrayList<>();
    private Map<String, Object> uploadedMergeObject = new HashMap<>();

    public Map<String, Object> getUploadedMergeObject() {
        return uploadedMergeObject;
    }

    public void setUploadedMergeObject(Map<String, Object> uploadedMergeObject) {
        this.uploadedMergeObject = uploadedMergeObject;
    }

    public List<Document> getListOfToBeUpsert() {
        return Collections.unmodifiableList(listOfToBeUpsert);
    }

    public void setListOfToBeUpsert(List<Document> listOfToBeUpsert) {
        this.listOfToBeUpsert = listOfToBeUpsert;
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

    public String bulkLoadExcell() {
        try {
            localBulkLoadExcell();
            twoDimModifyCtrl.refreshDataTable();
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
                Thread.sleep(200);
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

    public void uploadExcell(FileUploadEvent event) {

        uysApplicationMB.initKpbMemberCache();

        listOfToBeUpsert = new ArrayList<>();

        UploadedFile uploadedFileKpbDb = event.getFile();

        try {
            MyMerge myMerge = formService.getMyForm().getUploadMerge();

            if (myMerge == null) {
                dialogController.showPopupError("upload merge config has not been defined");
                return;
            }

            InputStream is = uploadedFileKpbDb.getInputStream();

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

                Document record = new Document().append(FORMS, formService.getMyForm().getKey());

                int columnn = 0;
                XSSFCell cellll;

                for (ExcellColumnDef excellColumnDef : myMerge.getWorkbookSheetColumnList()) {

                    cellll = row.getCell(columnn++);

                    if (cellll == null) {
                        if (excellColumnDef.getToMyField().isRequired()) {
                            throw new Exception(
                                    String.format("satır '%d' : sütun '%d' : %s' alanı zorunlu alandır", i, columnn, excellColumnDef.getToMyField().getName()));
                        }
                        record.append(excellColumnDef.getToMyField().getKey(), null);
                        continue;
                    }

                    if (cellll.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.BLANK) {
                        if (excellColumnDef.getToMyField().isRequired()) {
                            throw new Exception(
                                    String.format("satır '%d' : sütun '%d' : '%s' alanı zorunlu alandır", i, columnn, excellColumnDef.getToMyField().getName()));
                        }
                        record.append(excellColumnDef.getToMyField().getKey(), "");
                        continue;
                    }

                    Object obtainedValue = null;

                    if (cellll.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.STRING) {
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
                            default:
                                obtainedValue = cellll.getNumericCellValue();
                                break;
                        }
                    }

                    Object resolvedValue = obtainedValue;

                    if (excellColumnDef.getConverter() != null) {

                        try {
                            Document commandResult = mongoDbUtil.runCommand(formService.getMyForm().getUploadMerge().getToDb(),
                                    excellColumnDef.getConverter().getCode(), resolvedValue);

                            resolvedValue = commandResult.get(RETVAL);

                        } catch (Exception ex) {
                            throw new Exception(
                                    String.format("satır '%d' : sütun '%d' : '%s' alanının tespiti esnasında hata oluştu", i, columnn, excellColumnDef.getToMyField().getName()));
                        }

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
                    }

                    record.append(excellColumnDef.getToMyField().getKey(), resolvedValue);

                }

                if (!loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminAndViewerRole())) {
                    record.put(formService.getMyForm().getLoginFkField(), loginController.getLoggedUserDetail().getDbo().getObjectId());
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
            listOfToBeUpsert = new ArrayList<>();
            logger.error("error occured", ex);
            dialogController.showPopupError("Dosya Yükleme Esnasında hata oluştu.<br/><br/>".concat(ex.getMessage()));
        }

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

    public ObjectId saveObject(FmsForm myForm, LoginController loginMB, MyMap crudObject)
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

    @Override
    public List<Map> findLazyData(int first, int pageSize) throws NullNotExpectedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int findDataCount() throws NullNotExpectedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private transient UploadedFile uploadedFileKpbDb;

    public UploadedFile getUploadedFileKpbDb() {
        return uploadedFileKpbDb;
    }

    public void setUploadedFileKpbDb(UploadedFile uploadedFileKpbDb) {
        this.uploadedFileKpbDb = uploadedFileKpbDb;
    }

    public void uploadSimple() {
        String fileName = uploadedFileKpbDb.getFileName();
        String contentType = uploadedFileKpbDb.getContentType();
        byte[] contents = uploadedFileKpbDb.getContent(); // Or getInputStream()
        // ... Save it, now!
    }

}
