package tr.org.tspb.table;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoConfigurationException;
import com.mongodb.client.model.Filters;
import tr.org.tspb.datamodel.gui.FmsTableDataModel;
import com.mongodb.gridfs.GridFSDBFile;
import java.io.ByteArrayInputStream;
import java.io.File;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.pojo.MyConstraintFormula;
import tr.org.tspb.pojo.MyControlResult;
import tr.org.tspb.pojo.MyCommandResult;
import tr.org.tspb.exceptions.UserException;
import tr.org.tspb.pojo.UysListOfMapElement;
import tr.org.tspb.service.CalcService;
import tr.org.tspb.service.CtrlService;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import static javax.xml.bind.Marshaller.JAXB_ENCODING;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import javax.xml.transform.TransformerException;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.xml.sax.SAXException;
import tr.org.tspb.util.stereotype.MyController;
import javax.inject.Inject;
import org.bson.Document;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.exceptions.LdapException;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.MoreThenOneInListException;
import tr.org.tspb.exceptions.RecursiveLimitExceedException;
import tr.org.tspb.common.qualifier.MyCtrlServiceQualifier;
import tr.org.tspb.common.qualifier.MyQualifier;
import tr.org.tspb.common.qualifier.ViewerController;
import java.text.ParseException;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.el.EvaluationException;
import javax.mail.MessagingException;
import javax.script.ScriptException;
import org.apache.commons.io.FileUtils;
import org.asciidoctor.Asciidoctor;
import static org.asciidoctor.Asciidoctor.Factory.create;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import tr.org.tspb.converter.base.SelectOneObjectIdConverter;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.outsider.FmsWorkFlow;
import tr.org.tspb.pojo.UserDetail;
import static tr.org.tspb.constants.ProjectConstants.*;
import tr.org.tspb.dao.TagActionsAction;
import tr.org.tspb.dao.MyActions;
import tr.org.tspb.dao.MyBaseRecord;
import tr.org.tspb.dao.MyItems;
import tr.org.tspb.dao.FmsFormProperty;
import tr.org.tspb.dao.TagEvent;
import tr.org.tspb.dp.nullobj.PlainRecordData;
import tr.org.tspb.factory.qualifier.OgmCreatorQualifier;
import tr.org.tspb.outsider.PaymentDoor;
import tr.org.tspb.outsider.qualifier.DefaultPaymentDoor;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.tools.MongoDbUtilIntr;
import tr.org.tspb.factory.cp.OgmCreatorIntr;
import tr.org.tspb.outsider.qualifier.MyWorkFlowQualifier;
import tr.org.tspb.pojo.DatabaseUser;
import tr.org.tspb.service.FeatureService;
import tr.org.tspb.tags.FmsCheck;

/*
 * @author Telman Şahbazoğlu
 */
@MyController
@MyQualifier(myEnum = ViewerController.twoDimModifyCtrl)
public class TwoDimModifyCtrl extends FmsTable implements ActionListener {

    @Inject
    @MyCtrlServiceQualifier
    private CtrlService ctrlService;

    @Inject
    private CalcService calcService;

    @Inject
    @MyWorkFlowQualifier
    private FmsWorkFlow fmsFlowCtrl;

    @Inject
    private FeatureService featureService;

    @Inject
    @DefaultPaymentDoor
    private PaymentDoor paymentDoor;

    @Inject
    @KeepOpenQualifier
    private MongoDbUtilIntr mongoDbUtil;

    @Inject
    @OgmCreatorQualifier
    private OgmCreatorIntr ogmCreator;

    public static final String SESSION_KEY = "SESSION_KEY__CRUD_2D_MB";

    private static final int EVENT_SIZE = 5;
    private boolean disabledSearchButton;
    private List<String> eventLog = new ArrayList<>(EVENT_SIZE);
    private String calculateFormulaId;
    private List<String> selectedFormMessages;
    private transient StreamedContent file;
    private transient List<Map<String, Object>> successList = new ArrayList<>();
    private transient List<Map<String, Object>> failList = new ArrayList<>();
    private transient Map<String, Object> selectedRow;
    private static final String FAIL_LIST = "failList";
    private static final String SUCCESS_LIST = "successList";
    private String asciidoctorContent = "";
    private Asciidoctor asciidoctor;

    private TreeNode root;

    @PostConstruct
    public void init() {
        asciidoctor = create();
    }

    public List<Map<String, Object>> getFailList() {
        return Collections.unmodifiableList(failList);
    }

    public StreamedContent getFile() {

        try {
            JAXBContext context = JAXBContext.newInstance(UysListOfMapElement.class);
            Marshaller marshallEmailData = context.createMarshaller();
            marshallEmailData.setProperty(JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshallEmailData.setProperty(JAXB_ENCODING, "UTF-8");
            StringWriter stringWriter = new StringWriter();

            List<Map> list = new ArrayList<>();

            for (MyField myField : formService.getMyForm().getFields().values()) {
                StringBuilder value = new StringBuilder();
                if (getMyObject().containsKey(myField.getKey())) {

                    Object obj = getMyObject().get(myField.getKey());

                    if (myField.getMyconverter() instanceof SelectOneObjectIdConverter && obj instanceof ObjectId) {

                        if (SelectOneObjectIdConverter.NULL_VALUE.equals(obj)) {
                            value.append("Lütfen Seçiniz ...");
                        } else {
                            Document Document = mongoDbUtil.findOne(myField.getItemsAsMyItems().getDb(), myField.getItemsAsMyItems().getTable(),
                                    new Document(MONGO_ID, obj));

                            if (Document == null) {
                                value.append(String.format("no record regarding to %s", obj.toString()));
                            } else {
                                for (String key : myField.getItemsAsMyItems().getView()) {
                                    value.append(Document.get(key).toString());
                                    value.append(" - ");
                                }
                            }
                        }
                    } else {
                        value.append(getMyObject().get(myField.getKey()).toString());
                    }

                }
                Map map = new HashMap();
                map.put("key", myField.getName());
                map.put("value", value.toString());
                list.add(map);
            }

            marshallEmailData.marshal(new UysListOfMapElement(list, list, list), stringWriter);

            String fileName = SIMPLE_DATE_FORMAT__5.format(new Date())
                    .concat("_")
                    .concat(((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId())
                    .concat("_")
                    .concat(String.valueOf((int) (Math.random() * 1000000)));

            String xmlFileName = baseService.getProperties().getTmpDownloadPath().concat(fileName).concat(".xml");

            try (FileWriter fw = new FileWriter(xmlFileName)) {
                fw.write(stringWriter.toString());
            }

            // start XML To PDF
            String pdftoolpath = baseService.getProperties().getPdfTool();
            String xslFileName = pdftoolpath.concat("uys_pdf_fop_general.xsl");
            String pdfFileName = baseService.getProperties().getTmpDownloadPath().concat(fileName).concat(FILE_EXTENSION_PDF);
            String cnfFileName = pdftoolpath.concat("fop.xconf");

            uysApplicationMB.createPdfFile(pdfFileName, cnfFileName, xslFileName, xmlFileName, fileName);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(pdfFileName)));
            file = new DefaultStreamedContent(byteArrayInputStream, "application/pdf", fileName.concat(FILE_EXTENSION_PDF));

        } catch (IOException | JAXBException | TransformerException | SAXException ex) {
            logger.error(ex.getMessage());
        }

        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public List<Map<String, Object>> getSuccessList() {
        return Collections.unmodifiableList(successList);
    }

    public String showEimza() {
        List<Map> list = new ArrayList();
        list.add(new Document(crudObject));

        featureService.getEsignDoor().initAndShowEsignDlg(list, formService.getMyForm(), "widgetVarToBeSignedDialog", MULTIPLE);

        return null;
    }

    public String payment() {
        Object amountObj = getMyObject().get(formService.getMyForm().getPosAmountField());
        if (amountObj instanceof Number) {
            String amount = String.valueOf(((Number) amountObj).intValue() * 100);
            paymentDoor.newOrder(amount);
            dialogController.showPopup("wv-dlg-payment");
        } else {
            dialogController.showPopupError("no amount");
        }
        return null;
    }

    public String showEimza22() {
        try {
            localShowEimza22();
        } catch (NullNotExpectedException ex) {
            logger.error(ex.getMessage());
            dialogController.showPopupError(ex.getMessage());
        }
        return null;
    }

    private void localShowEimza22() throws NullNotExpectedException {

        Document query = new Document();

        if (!loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminAndViewerRole())) {
            if (formService.getMyForm().getLoginFkField() == null) {
                throw new NullNotExpectedException("login foreign key had not been set");
            }
            query.put(formService.getMyForm().getLoginFkField(), loginController.getLoggedUserDetail().getLoginFkSearchMapInListOfValues());
        }

        query.put(FORMS, formService.getMyForm().getForm());

        if (formService.getMyForm().getField(PERIOD) != null) {
            query.put(PERIOD, getSearchObjectValue(PERIOD));
        }

        List<Map> list = repositoryService.list(formService.getMyForm().getDb(), formService.getMyForm().getTable(), query);

        if (list.isEmpty()) {
            //FIXME messagebundle
            throw new NullNotExpectedException("İmzalanacak Kayıtlı Veriniz Tespit Edilemedi.");
        }

        featureService.getEsignDoor().initAndShowEsignDlg(list, formService.getMyForm(), "widgetVarToBeSignedDialog", MULTIPLE);

    }

    public Map<String, Object> getSelectedRow() {
        if (selectedRow == null) {
            return null;
        }
        return Collections.unmodifiableMap(selectedRow);
    }

    public void setSelectedRow(Map<String, Object> selectedRow) {
        this.selectedRow = selectedRow;
    }

    public void createTreeNode(Document document, TreeNode folder) {

        for (Map.Entry entry : document.entrySet()) {

            FmsFormProperty formProperty;

            if (entry.getValue() instanceof Document) {
                formProperty = new FmsFormProperty(entry.getKey().toString(), entry.getValue(), false);
            } else {
                formProperty = new FmsFormProperty(entry.getKey().toString(), entry.getValue());
            }

            TreeNode childNode = new DefaultTreeNode(formProperty);

            folder.getChildren().add(childNode);

            if (entry.getValue() instanceof Document) {
                Document innerDoc = (Document) entry.getValue();
                createTreeNode(innerDoc, childNode);
            }

        }

    }

    // EVENT LISTENERS BEGIN
    public void selectListener(SelectEvent event) {

        try {
            if (formService.getMyForm().isJsonViewer()) {
                Document document = mongoDbUtil.findOne(formService.getMyForm().getDb(), formService.getMyForm().getTable(), new BasicDBObject());

//                StringBuilder sb = new StringBuilder("= Form");
//
//                for (String key : document.keySet()) {
//                    sb.append("\n");
//                    sb.append("* *".concat(key).concat("*"));
//                }
//                asciidoctorContent = asciidoctor.convert(sb.toString(), Collections.EMPTY_MAP);
                root = new DefaultTreeNode("Root", null);

                createTreeNode(document, root);

                dialogController.showPopup(DLG_CRUD_JSON);
            } else {
                retrieveObjectFromDB((Map) event.getObject(), true);

                formService.getMyForm().runAjaxBulk(getComponentMap(), crudObject, loginController.getRoleMap(), loginController.getLoggedUserDetail());

                formService.getMyForm().arrangeActions(loginController.getRoleMap(), filterService.getTableFilterCurrent(), crudObject);

                if (formService.getMyForm().isWorkFlowActive()) {
                    formService.getMyForm().getMyActions().reset();
                    fmsFlowCtrl.init(formService.getMyForm(), crudObject, filterService.getTableFilterCurrent());
                }

                dialogController.showPopup(CRUD_OPERATION_DIALOG2);
            }
        } catch (Exception ex) {
            logger.error("error occured", ex);
            dialogController.showPopupError(ex.getMessage());
        }
    }

    // GETTERS & SETTERS BEGIN
    public int getEventSize() {
        return EVENT_SIZE;
    }

    public List<String> getEventLog() {
        return Collections.unmodifiableList(eventLog);
    }

    public void setEventLog(List<String> eventLog) {
        this.eventLog = eventLog;
    }

    public String clearCache() {
        uysApplicationMB.clearCalculateFormula();
        uysApplicationMB.clearApplicationSearchResults();
        return null;
    }

    public String sendEmailToAll() {

        try {
            localSendEmailToAll();
        } catch (NullNotExpectedException | IOException ex) {
            logger.error(ex.getMessage());
            dialogController.showPopupError(ex.getMessage());
        }
        return null;
    }

    private void localSendEmailToAll() throws NullNotExpectedException, IOException {

        Object objectEmail = ((Document) formService.getMyForm().getActions()).get(EMAIL_TO_ALL);
        Code recipientListOfMembers2 = (Code) ((Document) objectEmail).get("recipientListOfMembers_2");

        Object msgFormatContent = getTransparentProperties().get("emailType");

        Document Document = mongoDbUtil.findOne(CONFIG_DB, COLLECTION_SYSTEM_MESSAGES,
                new Document(CODE, msgFormatContent));

        if (Document == null) {
            throw new NullNotExpectedException("content is not defined yet");
        }

        Document fileQuery = (Document) Document.get("contentQuery");

        String gridfsDb = (String) fileQuery.get(FORM_DB);
        String filename = (String) fileQuery.get("filename");

        List<GridFSDBFile> gridFSDBFiles = mongoDbUtil.findFiles(gridfsDb, filename);

        StringWriter sw = new StringWriter();

        InputStreamReader streamReader = new InputStreamReader(gridFSDBFiles.get(0).getInputStream());

        int c;
        while ((c = streamReader.read()) != -1) {
            sw.append((char) c);
        }

        Document commandResult = mongoDbUtil
                .runCommand(formService.getMyForm().getDb(), recipientListOfMembers2.getCode(), filterService.getTableFilterCurrent());

        List<Document> listOf = mongoDbUtil.find(
                baseService.getLoginDB(),
                baseService.getLoginTable(),
                Filters.in(MONGO_ID, commandResult.get(RETVAL)));

        for (Document loginRecord : listOf) {
            String email = (String) loginRecord.get(EMAIL);
            logger.info(String.format("%50s : %s", loginRecord.get(NAME), email));
        }

    }

    public void valueChangeListenerTableSearch(AjaxBehaviorEvent event) {
        search();
        resetActions();

    }

    public String actionSearchObject() {
        search();
        resetActions();

        return null;
    }

    private void resetActions() {
        MyActions myActions = ogmCreator
                .getMyActions(formService.getMyForm(), loginController.getRoleMap(), filterService.getTableFilterCurrent(), loginController.getLoggedUserDetail());
        formService.getMyForm().initActions(myActions);
    }

    public String resetFilter() {
        filterService.getGuiFilterCurrent().clear();
        search();
        resetActions();

        return null;
    }

    private int rowCount = 0;
    private int limit = 5000;

    private void search() {
        try {
            filterService.createTableFilterCurrent(formService.getMyForm());
            //FIXME 02.10.2019 Volkan&Telman : set row count also on drawGUI phase

            rowCount = findDataCount();

            if (rowCount > limit) {
                ((FmsTableDataModel) getData()).initRowCount(0);
                dialogController.showPopupInfo("sorgu sonucuna göre kayıt sayısı 5000 nin üzerinde. Filtre seçiminizi daraltınız", MESSAGE_DIALOG);
            } else {
                ((FmsTableDataModel) getData()).initRowCount(rowCount);
            }

        } catch (NullNotExpectedException ex) {

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

    public Boolean getSelectedFormUserNote() {
        return formService.getMyForm() != null
                && formService.getMyForm().getUserNote() != null
                && !formService.getMyForm().getUserNote().isEmpty();
    }

    public String getSelectedFormConstantNote() {
        return formService.getMyForm() == null ? " " : formService.getMyForm().getConstantNote();
    }

    public String getSelectedFormFuncNote() {
        if (formService.getMyForm().getName() != null && formService.getMyForm().getFuncNote() != null) {
            Document commandResult = mongoDbUtil
                    .findOne(formService.getMyForm().getDb(), formService.getMyForm().getFuncNote(), filterService.getTableFilterCurrent());
            return commandResult.getString(RETVAL);
        } else {
            return null;
        }
    }

    public String getSelectedFormReadOnlyNote() {
        return formService.getMyForm() == null ? " " : formService.getMyForm().getReadOnlyNote();
    }

    public String provideOverAllCrossCheckForAll() {
        try {
            localProvideOverAllCrossCheckForAll();
        } catch (NullNotExpectedException ex) {
            dialogController.showPopupError("period is required");
        }
        return null;
    }

    public void localProvideOverAllCrossCheckForAll() throws NullNotExpectedException {

        if (filterService.getTableFilterCurrent().get(PERIOD) == null) {
            throw new NullNotExpectedException("period is required");
        }

        if (filterService.getTableFilterCurrent().get(TEMPLATE) == null) {
            throw new NullNotExpectedException("template is required");
        }

        if (filterService.getTableFilterCurrent().get(formService.getMyForm().getLoginFkField()) != null
                && !SelectOneObjectIdConverter.NULL_VALUE
                        .equals(filterService.getTableFilterCurrent().get(formService.getMyForm().getLoginFkField()))) {
            provideOverAllCrossCheck();
        } else {

            Document query = new Document("status.code", "001");

            query.append(PERIOD, filterService.getTableFilterCurrent().get(PERIOD));
            query.append(TEMPLATE, filterService.getTableFilterCurrent().get(TEMPLATE));

            List<Document> cursor = mongoDbUtil.find(UYSDB, "dataBankOrganizationStatus", query);

            int i = 0;

            for (Document dbo : cursor) {
                i++;
                String message = String.valueOf(i).concat(", {").concat(formService.getMyForm().getLoginFkField())
                        .concat(" : ").concat(dbo.get(formService.getMyForm().getLoginFkField()).toString()).concat("}");
                logger.info(message);
                filterService.getTableFilterCurrent().put(formService.getMyForm().getLoginFkField(), dbo.getObjectId(formService.getMyForm().getLoginFkField()));
                filterService.getTableFilterCurrent().put(TEMPLATE, dbo.getObjectId(TEMPLATE));
                putSearchObjectValue(formService.getMyForm().getLoginFkField(), dbo.getObjectId(formService.getMyForm().getLoginFkField()));
                putSearchObjectValue(TEMPLATE, dbo.getObjectId(TEMPLATE));
                provideOverAllCrossCheck();
            }
        }
    }

    /*
    steps :
    1. run constraints formulas
    2. write the results of aforementiond step to the config pre-defined collection
    3. we are eligable to assyncronuously change the result of the step two outupt like in case of Data Change
    4. check the result to focus on sendForm 
     */
    public String provideOverAllCrossCheck() {
        ctrlService.init(formService.getMyForm().getMyProject().getConfigTable());
        try {
            Document filterClone = new Document(filterService.getTableFilterCurrent());
            ctrlService.crossCheck(filterClone);
            callAdditionalAction(filterClone, formService.getMyForm().getMyActions().getCheckAllAction());
            resetActions();
            ((FmsTableDataModel) getData()).initRowCount(findDataCount());
        } catch (Exception ex) {
            logger.error("error occured", ex);
            dialogController.showPopupError(ex.getMessage());
        }
        return null;
    }

    public void callAdditionalAction(Document filter, TagActionsAction fmsAction) {

        ctrlService.init(formService.getMyForm().getMyProject().getConfigTable());

        if (fmsAction.isEnable() && fmsAction.getActionFunc() != null) {
            mongoDbUtil.runCommand(fmsAction.getDb(), fmsAction.getActionFunc(), filter, loginController.getRolesAsSet());
        }

        if (fmsAction.isEnable() && fmsAction.getOperations() != null) {

            List<FmsCheck> fmsChecks = fmsAction.getCheckList();

            Boolean ifcase = null;

            if (fmsChecks != null) {
                for (FmsCheck fmsCheck : fmsChecks) {
                    ifcase = (ifcase == null) ? fmsCheck.execute() : ifcase && fmsCheck.execute();
                }
            }

            for (TagActionsAction.Operation operation : fmsAction.getOperations()) {
                if (ifcase == null || operation.getIfcase() == ifcase) {
                    switch (operation.getOp()) {
                        case "upsert":
                            mongoDbUtil.upsertOne(operation.getDb(), operation.getTable(), operation.getFilter(), operation.getSet());
                            break;
                        case "update":
                            mongoDbUtil.updateMany(operation.getDb(), operation.getTable(), operation.getFilter(), operation.getSet());
                            break;
                    }
                }
            }

        }
    }

    public String chooseEditor() {
        return null;
    }

    public String actionEmail() {
        try {
            actionEmail(crudObject);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            dialogController.showPopupError(ex.getMessage());
        }
        return null;
    }

    public String gonder() {
        gonder(crudObject);
        return null;
    }

    public String saveObject() {
        try {
            saveObject(null);
            ((FmsTableDataModel) getData()).initRowCount(findDataCount());
            ((FmsTableDataModel) getData()).emptyListOfData();
            resetActions();
        } catch (UserException ex) {
            logger.error("error occured", ex);
            dialogController.showPopupError(ex.getMessage());
        } catch (FormConfigException | LdapException | MongoOrmFailedException
                | MoreThenOneInListException | NullNotExpectedException
                | RecursiveLimitExceedException
                | NoSuchMethodException | ParseException | MessagingException | ScriptException | net.sourceforge.jeval.EvaluationException ex) {
            logger.error(ex.getMessage());
            dialogController.showPopupError(ex.toString());
        }
        return null;
    }

    public String saveObject(MyCommandResult smControlResult)
            throws UserException, NullNotExpectedException, FormConfigException, MessagingException, LdapException, ScriptException, NoSuchMethodException, EvaluationException, MongoOrmFailedException, ParseException, MoreThenOneInListException, RecursiveLimitExceedException, net.sourceforge.jeval.EvaluationException {

        if (runEventPreSave(filterService.getTableFilterCurrent(), crudObject)) {
            return null;
        }

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

        ObjectId returnID = saveObject(formService.getMyForm(), loginController, crudObject);

        if (returnID != null) {
            retrieveObjectFromDB(new Document(MONGO_ID, returnID), true);

            Map<String, List> map = internalCheck();
            successList = map.get(SUCCESS_LIST);
            failList = map.get(FAIL_LIST);

            callAdditionalAction(filterService.getTableFilterCurrent(), formService.getMyForm().getMyActions().getSaveAction());

            formService.getMyForm().runAjaxBulk(getComponentMap(), crudObject, loginController.getRoleMap(), loginController.getLoggedUserDetail());

            dialogController.showPopup(CRUD_OPERATION_DIALOG2);

        }

        return null;
    }

    public String saveAsObject() {
        try {

            if (crudObject.get(MONGO_ID) == null) {
                saveObject();
                return null;
            }

            MyMap crud = (MyMap) crudObject.clone();

            StringBuilder message = new StringBuilder();
            if (crud.get(NOTE) != null) {
                message.append(crud.get(NOTE).toString());
            }
            message.append(". kopyalanarak oluşturuldu. original ID : ");
            message.append(crud.get(MONGO_ID).toString());

            crud.remove(MONGO_ID);

            crud.put(NOTE, message.toString());

            if (runEventPreSave(filterService.getTableFilterCurrent(), crud)) {
                return null;
            }

            Object loginFkFieldValue = crud.get(formService.getMyForm().getLoginFkField());

            boolean ok = loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminRole());
            ok = ok || loginController.getLoggedUserDetail().getDbo().getObjectId().equals(loginFkFieldValue);

            for (UserDetail.EimzaPersonel ep : loginController.getLoggedUserDetail().getEimzaPersonels()) {
                if (ep.getDelegatingMember() != null && ep.getDelegatingMember().equals(loginFkFieldValue)) {
                    ok = true;
                    break;
                }
            }

            if (!ok) {
                throw new Exception("Sisteme girş yapan kullanıcı yalnızca kendisine ait veri ekleyip değiştirebilir.");
            }

            ObjectId returnID = saveObject(formService.getMyForm(), loginController, crud);

            retrieveObjectFromDB(new Document(MONGO_ID, returnID), true);

            Map<String, List> map = internalCheck();
            successList = map.get(SUCCESS_LIST);
            failList = map.get(FAIL_LIST);

            callAdditionalAction(filterService.getTableFilterCurrent(), formService.getMyForm().getMyActions().getSaveAction());

            formService.getMyForm().runAjaxBulk(getComponentMap(), crudObject, loginController.getRoleMap(), loginController.getLoggedUserDetail());
            dialogController.showPopup(CRUD_OPERATION_DIALOG2);
        } catch (UserException ex) {
            logger.error(ex.getMessage());
            dialogController.showPopup(ex.getTitle(), ex.getMessage(), MESSAGE_DIALOG);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            dialogController.showPopupError(ex.toString());
        }
        return null;
    }

    public Map requiredMap() {
        return mapRequired;
    }

    private MyMap retrieveObjectFromDB(Map map, Boolean recall) throws FormConfigException {

        if (recall) {
            map = mongoDbUtil
                    .findOne(formService.getMyForm().getDb(),
                            formService.getMyForm().getTable(),
                            new Document(MONGO_ID, map.get(MONGO_ID)));
        }

        ctrlService.checkRecordConverterValueType(new Document(map), formService.getMyForm());

        crudObject = prepareCrudObject(map);

        prepareJsfComponentMap(formService.getMyForm());

        refreshUploadedFileList();

        List<Map> listOfCruds = new ArrayList();
        listOfCruds.add(new Document(crudObject));

        featureService.getEsignDoor().initEsignCtrl(formService.getMyForm(), listOfCruds, null, UNIQUE);

        //FIXME  : make this snippet selectedForm dependable
        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);

        mapRequired = new HashMap();
        if (HAYIR.equals(crudObject.get("is_operated"))) {
            mapRequired.put("period", true);
            mapRequired.put("is_operated", true);
            mapRequired.put("continent", false);
            mapRequired.put("country", false);
            mapRequired.put("stock_market", false);
            mapRequired.put("clearing_house_name", false);
            mapRequired.put("trading_volume_client", false);
            mapRequired.put("trading_volume_portfolio", false);
            httpSession.setAttribute(HTTP_SESSION_ATTR_MAP_REQURED_CONTROL, mapRequired);
        } else {
            httpSession.removeAttribute(HTTP_SESSION_ATTR_MAP_REQURED_CONTROL);
        }

        return crudObject;
    }

    protected Map<String, List> internalCheck()
            throws ScriptException, NoSuchMethodException, MongoOrmFailedException, NullNotExpectedException, FormConfigException, ParseException, MoreThenOneInListException, RecursiveLimitExceedException, net.sourceforge.jeval.EvaluationException {
        Map<String, List> returnMap = new HashMap();
        returnMap.put(SUCCESS_LIST, new ArrayList<>());
        returnMap.put(FAIL_LIST, new ArrayList<>());

        Document docConstraintItems = formService.getMyForm().getConstraintItems();

        if (docConstraintItems == null) {
            return returnMap;
        }

        List<Document> constraintCursor = ctrlService.createConstraintCursor(docConstraintItems, filterService.getTableFilterCurrent());

        for (Document next : constraintCursor) {
            MyConstraintFormula myConstraintFormula = new MyConstraintFormula(next);
            Document myCrudObject = new Document(crudObject);
            // we remove it bacuase of MyForm class cannot be serialized for mongo.doEval
            myCrudObject.remove(INODE);

            //  try {
            ctrlService.init(formService.getMyForm().getMyProject().getConfigTable());

            List<Map> resultListOfMap = ctrlService.runConstraintCrossCheck(myConstraintFormula, true,
                    new Object[]{filterService.getTableFilterCurrent(), loginController.getRolesAsSet(), myCrudObject},
                    null, filterService.getTableFilterCurrent());

            for (Map map : resultListOfMap) {
                myConstraintFormula.setControlResult(new MyControlResult(map));
                if (myConstraintFormula.getControlResult().isResult()) {
                    returnMap.get(SUCCESS_LIST).add(myConstraintFormula);
                } else {
                    returnMap.get(FAIL_LIST).add(myConstraintFormula);
                }
            }
//            } catch (Exception e) {
//                throw new Exception(MessageFormat.format("exception:{0}<br/>transferOrder:{1}<br/>name:{2}<br/>constraint:{3}<br/>",
//                        e.getMessage(),
//                        myConstraintFormula.getTransferOrder(),
//                        myConstraintFormula.getName(),
//                        myConstraintFormula), e);
//            }
        }

        return returnMap;
    }

    public String copyObject() {
        try {
            copyObject(formService.getMyForm(), loginController, crudObject);
        } catch (UserException ex) {
            logger.error(ex.getMessage());
            dialogController.showPopup(ex.getTitle(), ex.getMessage(), MESSAGE_DIALOG);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            dialogController.showPopupError(ex.toString());
        }
        return null;
    }

    public String performCancel() {
        try {
            FmsForm chosenMyForm = ogmCreator.getMyFormLarge(null,
                    formService.getMyForm().getMyProject().getConfigTable(),
                    new Document(FORM, "ion_form_1020"),
                    null,
                    loginController.getRoleMap(),
                    loginController.getLoggedUserDetail());

            this.crudObject = newObject(chosenMyForm);

            prepareJsfComponentMap(chosenMyForm);

            dialogController.showPopup(CRUD_OPERATION_DIALOG2);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    public String deleteObject() {
        try {
            deleteObject(loginController, formService.getMyForm(), crudObject);
            ((FmsTableDataModel) getData()).initRowCount(findDataCount());
            ((FmsTableDataModel) getData()).emptyListOfData();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            dialogController.showPopupError(ex.toString());
        }
        return null;
    }

    public String getSelectFormShortName() {
        return "Yeni Ekle";
    }

    public String newObject() {
        try {

            crudObject = newObject(formService.getMyForm());

            for (MyField myField : formService.getMyForm().getFieldsAsList()) {

                if (myField != null && myField.isAutoComplete()) {

                    MyItems myItems = myField.getItemsAsMyItems();

                    Document doc = mongoDbUtil.findOne(myItems.getDb(), myItems.getTable(), Filters.eq(MONGO_ID, myField.getKey()));

                    crudObject.put(myField.getKey(), PlainRecordData.getPlainRecord(doc, myItems));
                }
            }

            formService.getMyForm()
                    .arrangeActions(loginController.getRoleMap(), filterService.getTableFilterCurrent(), crudObject);

            prepareJsfComponentMap(formService.getMyForm());

            listFileData = new ArrayList<>();

            formService.getMyForm().runAjaxBulk(getComponentMap(), crudObject,
                    loginController.getRoleMap(), loginController.getLoggedUserDetail());

            resetHistory();

            dialogController.showPopup(CRUD_OPERATION_DIALOG2);

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            dialogController.showPopupError(ex.toString());
        }
        return null;
    }

    public boolean isDisabledSearchButton() {
        return disabledSearchButton;
    }

    public String doNotSave() {

        resetMyObject();

        try {
            loginController.logout();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        return null;

    }

    public String saveAndLogout() {
        saveObject();
        resetMyObject();

        try {
            loginController.logout();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        return null;

    }

    /* **************************** GETTER/SETTER *************************** */
    public String getCalculateFormulaId() {
        return calculateFormulaId;
    }

    public void setCalculateFormulaId(String calculateFormulaId) {
        this.calculateFormulaId = calculateFormulaId;
    }

    public void setDisabledSearchButton(boolean disabledSearchButton) {
        this.disabledSearchButton = disabledSearchButton;
    }

    public List<String> getSelectedFormMessages() {
        return Collections.unmodifiableList(selectedFormMessages);
    }

    public void drawGUI(FmsForm myForm) throws Exception {
        drawGUI(myForm, filterService.getBaseFilterCurrent());
        formService.getMyForm().runAjaxBulk(getComponentMap(), crudObject,
                loginController.getRoleMap(), loginController.getLoggedUserDetail());

        TagEvent trigger = myForm.getEventFormSelection();
        if (trigger != null && TagEvent.TagEventType.showWarnErrPopup.equals(trigger.getType())) {
            dialogController.showPopupInfoWithOk(trigger.getMsg(), MESSAGE_DIALOG);
        }

    }

    @Override
    public void drawGUI(FmsForm myForm, Document filter) throws Exception {

        crudObject = ogmCreator.getCrudObject();

        super.drawGUI(myForm, filter);

        fmsFlowCtrl.reset();

        if (myForm.getUniqueIndexList() != null) {
            for (Object obj : myForm.getUniqueIndexList()) {
                Document dbo = (Document) obj;
                mongoDbUtil.createIndexUnique(formService.getMyForm(), dbo);
            }
        }

        selectedFormMessages = new ArrayList<>();

        if (formService.getMyForm() != null) {
            if (formService.getMyForm().getConstantNote() != null && !formService.getMyForm().getConstantNote().isEmpty()) {
                selectedFormMessages.add(formService.getMyForm().getConstantNote());
            }

            if (formService.getMyForm().getUserConstantNoteList() instanceof List) {
                for (String message : formService.getMyForm().getUserConstantNoteList()) {
                    selectedFormMessages.add(message);
                }
            }

            if (!formService.getMyForm().getMyActions().isSave() && formService.getMyForm().getReadOnlyNote() != null) {
                selectedFormMessages.add(formService.getMyForm().getReadOnlyNote());
            }
            if (formService.getMyForm().getFuncNote() != null) {
                Document commandResult = mongoDbUtil.runCommand(formService.getMyForm().getDb(), formService.getMyForm().getFuncNote(), filter);
                String commandResultValue = commandResult.getString(RETVAL);
                if (commandResultValue != null) {
                    selectedFormMessages.add(commandResultValue);
                }
            }
        }

        if ("debug".equals(baseService.getProperties().getDebugMode())) {
            dialogController.showPopupInfo(new StringBuilder()
                    .append("You see this message because of server properties DEBUG_MODE is set to debug.")
                    .append(formService.getMyForm() == null ? "selectedForm is null" : formService.getMyForm().printToConfigAnalyze("smth"))
                    .append("<br/>")
                    .append("<u>query :</u>")
                    .append("<br/><br/>")
                    .append(filterService.getTableFilterCurrent().toString())
                    .toString(), MESSAGE_DIALOG);
        }

        search();

    }

    @Deprecated
    public void processActionExternal(ActionEvent ae) {
        try {
            processAction(ae);
        } catch (Exception ex) {
            dialogController.showPopupError("Hata Oluştu");
        }
    }

    @Deprecated
    @Override
    public void processAction(ActionEvent ae) throws AbortProcessingException {
        /*
        armActionAtrrs();

        String failMessage = (String) eventAttrs.get(FAIL_MESSAGE);
        try {

            //Only java native variables can be pass through jsf attributes
            String successMessage = (String) eventAttrs.get(SUCCESS_MESSAGE);

            TimeZone timeZone = TimeZone.getTimeZone("Asia/Istanbul");
            SIMPLE_DATE_FORMAT__3.setTimeZone(timeZone);

            String projectBasedPeriodColectionName = formService.getMyForm().getField(PERIOD).getItemsAsMyItems().getTable();

            successMessage = String.format(successMessage, SIMPLE_DATE_FORMAT__3.format(new Date()),
                    uysApplicationMB.getApplicationSearchResults(
                            new Document()
                                    .append(FORM_DB, formService.getMyForm().getDb())
                                    .append(COLLECTION, projectBasedPeriodColectionName)
                                    .append(FORMS, PERIOD)
                                    .append(MONGO_ID, getSearchObjectValue(PERIOD))).get(0).get(NAME));

            String myActionType = (String) eventAttrs.get(MY_ACTION_TYPE);
            String javaFunc = (String) eventAttrs.get(JAVA_FUNC);
            String code = (String) eventAttrs.get(MYACTION);

            if (myActionType == null) {
                if (code == null) {
                    dialogController.showPopupWarning("Bu olay üzerinde eylem tanımlı değil.<br/>Sistem yöneticisi ile iletişime geçiniz.", MESSAGE_DIALOG);
                } else {
                    Document mySearchObject = repositoryService.expandCrudObject(formService.getMyForm(), getSearchObjectAsDbo());

                    for (MyField myField : formService.getMyForm().getAutosetFields()) {
                        if (mySearchObject.get(myField.getKey()) == null
                                || SelectOneObjectIdConverter.NULL_VALUE.equals(mySearchObject.get(myField.getKey()))) {
                            throw new Exception(
                                    MessageFormat.format("arama kriterlerinde {0} belirsiz.", myField.getKey()));
                        }
                    }

                    mongoDbUtil.runCommand(formService.getMyForm().getDb(), code, mySearchObject, null);

                    putSearchObjectValue(PERIOD, SelectOneObjectIdConverter.NULL_VALUE);

                    armActionAtrrs();

                    MyActions myActions = ogmCreator
                            .getMyActions(formService.getMyForm(), loginController.getRoleMap(),
                                    filterService.getTableFilterCurrent(), loginController.getLoggedUserDetail());

                    formService.getMyForm().initActions(myActions);

                    actionSearchObject();

                    dialogController.showPopupInfo(successMessage, MESSAGE_DIALOG);
                }
            } else if ("java".equals(myActionType)) {
                Method method = this.getClass().getMethod(javaFunc, new Class[]{});
                method.invoke(this, new Object[]{});
            }
        } catch (Exception ex) {
            dialogController.showPopupInfo(failMessage, MESSAGE_DIALOG);
        }
         */

    }

    public String norecord() {

        try {
            String successMessage = formService.getMyForm().getMyActions().getNorecordAction().getEnableResult().getSuccessMessage();

            String code = formService.getMyForm().getMyActions().getNorecordAction().getEnableResult().getMyaction();
            List<TagActionsAction.Operation> list = formService.getMyForm().getMyActions().getNorecordAction().getOperations();

            if (code != null) {
                Document mySearchObject = repositoryService.expandCrudObject(formService.getMyForm(), getSearchObjectAsDbo());

                for (MyField myField : formService.getMyForm().getAutosetFields()) {
                    if (mySearchObject.get(myField.getKey()) == null
                            || SelectOneObjectIdConverter.NULL_VALUE.equals(mySearchObject.get(myField.getKey()))) {
                        throw new Exception(
                                MessageFormat.format("arama kriterlerinde {0} belirsiz.", myField.getKey()));
                    }
                }
                mongoDbUtil.runCommand(formService.getMyForm().getDb(), code, mySearchObject, null);
            } else if (list != null) {
                for (TagActionsAction.Operation operation : list) {
                    if (operation.getOp().equals("upsert")) {
                        mongoDbUtil.upsertOne(operation.getDb(), operation.getTable(), operation.getFilter(), operation.getSet());
                    }
                }
            }

            resetActions();

            actionSearchObject();

            dialogController.showPopupInfo(successMessage, MESSAGE_DIALOG);

        } catch (Exception ex) {
            String failMessage = formService.getMyForm().getMyActions().getNorecordAction().getEnableResult().getFailMessage();
            dialogController.showPopupInfo(failMessage, MESSAGE_DIALOG);
        }
        return null;
    }

    public String sendForm() {

        String failMessage = formService.getMyForm().getMyActions().getSendFormAction().getEnableResult().getFailMessage();

        try {

            //Only java native variables can be pass through jsf attributes
            String successMessage = formService.getMyForm().getMyActions().getSendFormAction().getEnableResult().getSuccessMessage();

            TimeZone timeZone = TimeZone.getTimeZone("Asia/Istanbul");
            SIMPLE_DATE_FORMAT__3.setTimeZone(timeZone);

            MyField periodField = formService.getMyForm().getFields().get(PERIOD);

            String periodName = null;

            if (periodField != null) {
                periodName = mongoDbUtil.findOne(
                        periodField.getItemsAsMyItems().getDb(),
                        periodField.getItemsAsMyItems().getTable(),
                        Filters.and(
                                Filters.eq(FORMS, PERIOD),
                                Filters.eq(MONGO_ID, getSearchObjectValue(PERIOD)))).getString(NAME);
            }

            successMessage = String.format("%s - ".concat(successMessage).concat(". Gönderim Tarihi :  %s"),
                    periodName, SIMPLE_DATE_FORMAT__3.format(new Date()));

            String myActionType = formService.getMyForm().getMyActions().getSendFormAction().getEnableResult().getMyActionType();
            String javaFunc = formService.getMyForm().getMyActions().getSendFormAction().getEnableResult().getJavaFunc();
            String code = formService.getMyForm().getMyActions().getSendFormAction().getEnableResult().getMyaction();

            List<TagActionsAction.Operation> operaiotns = formService.getMyForm().getMyActions().getSendFormAction().getOperations();

            if (operaiotns != null && !operaiotns.isEmpty()) {
                callAdditionalAction(filterService.getTableFilterCurrent(), formService.getMyForm().getMyActions().getSendFormAction());
                putSearchObjectValue(PERIOD, SelectOneObjectIdConverter.NULL_VALUE);
                resetActions();
                actionSearchObject();
                dialogController.showPopupInfo(successMessage, MESSAGE_DIALOG);
            } else if (myActionType == null) {
                if (code == null) {
                    dialogController.showPopupWarning("Bu olay üzerinde eylem tanımlı değil.<br/>Sistem yöneticisi ile iletişime geçiniz.", MESSAGE_DIALOG);
                } else {
                    Document mySearchObject = repositoryService.expandCrudObject(formService.getMyForm(), getSearchObjectAsDbo());

                    for (MyField myField : formService.getMyForm().getAutosetFields()) {
                        if (mySearchObject.get(myField.getKey()) == null
                                || SelectOneObjectIdConverter.NULL_VALUE.equals(mySearchObject.get(myField.getKey()))) {
                            throw new Exception(
                                    MessageFormat.format("arama kriterlerinde {0} belirsiz.", myField.getKey()));
                        }
                    }

                    mongoDbUtil.runCommand(formService.getMyForm().getDb(), code, mySearchObject, null);

                    putSearchObjectValue(PERIOD, SelectOneObjectIdConverter.NULL_VALUE);

                    resetActions();

                    actionSearchObject();

                    dialogController.showPopupInfo(successMessage, MESSAGE_DIALOG);
                }
            } else if ("java".equals(myActionType)) {
                Method method = this.getClass().getMethod(javaFunc, new Class[]{});
                method.invoke(this, new Object[]{});
            }
        } catch (Exception ex) {
            dialogController.showPopupInfo(failMessage, MESSAGE_DIALOG);
        }
        return null;

    }

    public void showPopupError(String creditCardMsg) {
        dialogController.showPopupError(creditCardMsg);
    }

    public void dateChange(SelectEvent event) {
    }

    private List<String> newroles = new ArrayList<>();

    public String newLdapUser() {
        try {
            ldapService.createUser(
                    crudObject.get("kullanici").toString(),
                    crudObject.get("adi").toString(),
                    null,
                    "to be set",
                    "12345678"
            );
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            dialogController.showPopupError(ex.getMessage());
        }
        return null;
    }

    public String updateLdapPswd() {
        try {
            ldapService.updatePswd(crudObject.get("kullanici").toString(), "12345678");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            dialogController.showPopupError(ex.getMessage());
        }
        return null;
    }

    public String addUserToRole() {
        try {
            ldapService.addUserToRole(crudObject.get("kullanici").toString(), newroles);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            dialogController.showPopupError(ex.getMessage());
        }
        return null;
    }

    public String removeUserToRole() {
        try {
            ldapService.removeUserToRole(crudObject.get("kullanici").toString(), newroles);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }

    public List<String> getNewroles() {
        return newroles;
    }

    public void setNewroles(List<String> newroles) {
        this.newroles = newroles;
    }

    private MyMap newObject(FmsForm myForm) throws Exception {

        MyMap crudObject = ogmCreator.getCrudObject();

        crudObject.clear();

        for (MyField myField : myForm.getAutosetFields()) {
            crudObject.put(myField.getKey(), filterService.getTableFilterCurrent().get(myField.getKey()));
        }

        String userDB = baseService.getLoginDB();
        String userTable = baseService.getLoginTable();
        String userLdapUID = baseService.getLoginUsernameField();

        if (userDB.equals(myForm.getDb()) && userTable.equals(myForm.getTable()) && myForm.getField(userLdapUID) != null) {
            crudObject.put(MONGO_LDAP_UID, generateLdapUID(null));
        }

        if (!loginController.isUserInRole(myForm.getMyProject().getAdminRole())) {
            DatabaseUser loginRecord = loginController.getLoggedUserDetail().getDbo();
            if (loginRecord != null) {
                crudObject.put(formService.getMyForm().getLoginFkField(), loginRecord.getObjectId());
            }
        }

        crudObject.put(INODE, myForm);

        return crudObject;

    }

    public String showApi() {
        dialogController.showPopup("wvRestCurlDlg");
        return null;
    }

    private String generateLdapUID(String memberType) {
        String ldapUID = "Yeni Kurum tanımlamalarında \"Üye Tipi\" seçimine göre bu alan otomatik değişecektir. Lütfen Üye Tipi seçiniz ...";
        if (memberType != null) {
            ldapUID = memberType;
        }
        return ldapUID;
    }

    private void prepareJsfComponentMap(FmsForm inodeMyForm) {

        setHeaderTitle(inodeMyForm.getName());

        setComponentMap(new HashMap());

        for (String key : inodeMyForm.getFieldsKeySet()) {
            if (crudObject.get(key) instanceof Document) {
                ObjectId id = (ObjectId) ((Document) crudObject.get(key)).get(MONGO_ID);
                if (id != null) {
                    crudObject.put(key, id);
                }
            }
        }

        if (crudObject.isEmpty()) {
            return;
        }

        for (String key : inodeMyForm.getFieldsKeySet()) {

            MyField fieldStructure = inodeMyForm.getField(key);

            // recalculate rendered property
            fieldStructure.calcWfRendered(crudObject, loginController.getRoleMap(), filterService.getTableFilterCurrent());

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

            if (getInputFile().equals(fieldStructure.getComponentType())) {
                setFileLimit(fieldStructure.getFileLimit());
                if (fieldStructure.getFileType() != null) {
                    switch (fieldStructure.getFileType()) {
                        case "pdf":
                            setMongoUploadFileType("/(\\.|\\/)(pdf)$/");
                            setInvalidFileMessage("Geçersiz Dosya Tipi (Sadece PDF dosyalar eklenebilir) : ");
                            break;
                        case "image":
                            setMongoUploadFileType("/(\\.|\\/)(jpg|png|JPEG|JPG|PNG)$/");
                            setInvalidFileMessage("Geçersiz Dosya Tipi (Sadece resim [jpg, png, JPEG, JPG, PNG] formatında dosyalar eklenebilir) : ");
                            break;
                        default:
                            setMongoUploadFileType("/(\\.|\\/)(pdf)$/");
                            setInvalidFileMessage("Geçersiz Dosya Tipi (Sadece PDF dosyalar eklenebilir) : ");
                            break;
                    }
                }
            }

            if (!getAutoComplete().equals(fieldStructure.getComponentType())) {
                fieldStructure.createSelectItems(
                        filterService.getTableFilterCurrent(),
                        crudObject,
                        loginController.getRoleMap(),
                        loginController.getLoggedUserDetail(),
                        false
                );
            }

            fieldStructure.setCrudRecord(crudObject);

            fieldStructure.setReadonly(fieldStructure.isReadonly() || !formService.getMyForm().getMyActions().isSave());

            addComponent(key, fieldStructure);
        }
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
            String ajaxAction = myField.getAjax().getAction();

            if (ajaxAction == null) {
                return;
            }
            HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

            switch (ajaxAction) {
                case "ypi_refresh_list_of_country_and_empty_stock_market":
                    break;
                case "ypi_override_required_check":
                    mapRequired = new HashMap();
                    if (HAYIR.equals(crudObject.get("is_operated"))) {
                        mapRequired.put("period", true);
                        mapRequired.put("is_operated", true);
                        mapRequired.put("continent", false);
                        mapRequired.put("country", false);
                        mapRequired.put("stock_market", false);
                        mapRequired.put("clearing_house_name", false);
                        mapRequired.put("trading_volume_client", false);
                        mapRequired.put("trading_volume_portfolio", false);
                        httpSession.setAttribute(HTTP_SESSION_ATTR_MAP_REQURED_CONTROL, mapRequired);
                    } else {
                        httpSession.removeAttribute(HTTP_SESSION_ATTR_MAP_REQURED_CONTROL);
                    }
                    break;
                case "uys_member_generate_ldapUID":
                    formService.getMyForm().runAjax__uys_member_generate_ldapUID(crudObject);
                    throw new UnsupportedOperationException("review ajax functionality");
                case "render":
                    formService.getMyForm().runAjaxRender(myField, getComponentMap(), formService.getMyForm(), crudObject,
                            loginController.getRoleMap(), loginController.getLoggedUserDetail(), filterService.getTableFilterCurrent());
                    break;
                case "render-ref":
                    formService.getMyForm().runAjaxRenderRef(myField, getComponentMap(), formService.getMyForm(), crudObject,
                            loginController.getRoleMap(), loginController.getLoggedUserDetail(), filterService.getTableFilterCurrent());
                    break;
                case "list":
                    formService.getMyForm().runAjaxList(myField, getComponentMap(), formService.getMyForm(), crudObject,
                            loginController.getRoleMap(), loginController.getLoggedUserDetail(), filterService.getTableFilterCurrent());
                    break;
                default:
                     ;
            }
        } catch (Exception ex) {
            addMessage(null, null, ex.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    private final String DLG_DESC = "wvDescDlg";

    public String showMyFieldDesc() {
        dialogController.showPopup(DLG_DESC);
        return null;
    }

    @Override
    public List<Map> findLazyData(int startRow, int maxResults) throws NullNotExpectedException {

        if (rowCount > limit) {
            return new ArrayList<>();
        }

        FmsForm myForm = formService.getMyForm();
        if (myForm == null || myForm.getKey() == null) {
            return Collections.emptyList();
        }

        HashMap sortMap = new HashMap();
        if (myForm.getDefaultSortField() != null) {
            for (Map.Entry entry : (Set<Map.Entry>) myForm.getDefaultSortField().entrySet()) {
                sortMap.put(entry.getKey(), entry.getValue());
            }
        }

        List<Map> list = mongoDbUtil.find(formService.getMyForm(),
                myForm.getTable(), filterService.getTableFilterCurrent(), null, startRow, maxResults,
                sortMap,
                null);

        // FIXME : Generalize it : Ortaklık Yapısı
        if ("OY".equals(formService.getMyForm().getForm())) {
            double sum = 0D;
            for (Map<String, Object> map : list) {
                Object obj = map.get(SHARE_AMOUNT);
                if (obj instanceof Number) {
                    sum += ((Number) obj).doubleValue();
                }
            }
            for (Map<String, Object> map : list) {
                Object obj = map.get(SHARE_AMOUNT);
                if (obj instanceof String) {
                    obj = 0D;
                }
                if (obj != null && sum != 0D) {
                    map.put("sharePercent", 100 * ((Number) obj).doubleValue() / sum);
                }
            }

            Map<String, Object> calculcateSum = new HashMap<>();
            calculcateSum.put(MONGO_ID, "dummy_sum_id");
            calculcateSum.put(formService.getMyForm().getLoginFkField(), "");
            calculcateSum.put(PERIOD, "");
            calculcateSum.put("partnerNameTitle", "Toplam");
            calculcateSum.put(SHARE_AMOUNT, sum);
            calculcateSum.put("sharePercent", 100);
            calculcateSum.put(CALCULATE, true);
            calculcateSum.put(STYLE, "background:paleGreen;text-align:right");
            list.add(calculcateSum);
        }

        return list;

    }

    @Override
    public int findDataCount() throws NullNotExpectedException {
        if (!loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminAndViewerRole())) {
            if (formService.getMyForm().getLoginFkField() == null) {
                throw new NullNotExpectedException("login foreign key had not been set");
            }
        }
        return (int) mongoDbUtil
                .count(formService.getMyForm().getDb(), formService.getMyForm().getTable(), filterService.getTableFilterCurrent());
    }

    public String getAsciidoctorContent() {
        return asciidoctorContent;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

}
