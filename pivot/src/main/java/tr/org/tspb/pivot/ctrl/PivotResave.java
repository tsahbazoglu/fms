package tr.org.tspb.pivot.ctrl;

import com.jaspersoft.mongodb.connection.MongoDbConnection;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_GTE;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_IN;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_LTE;
import static tr.org.tspb.constants.ProjectConstants.FORM;
import static tr.org.tspb.constants.ProjectConstants.MEASURE;
import static tr.org.tspb.constants.ProjectConstants.MEMBER;
import static tr.org.tspb.constants.ProjectConstants.OPERATOR_LDAP_UID;
import static tr.org.tspb.constants.ProjectConstants.PERIOD;
import static tr.org.tspb.constants.ProjectConstants.TEMPLATE;
import static tr.org.tspb.constants.ProjectConstants.TYPE;
import static tr.org.tspb.constants.ProjectConstants.UPSERT_DATE;
import static tr.org.tspb.constants.ProjectConstants.VALUE;
import com.mongodb.BasicDBList;
import com.mongodb.client.model.Filters;
import java.io.File;
import tr.org.tspb.exceptions.UserException;
import tr.org.tspb.common.util.CustomOlapHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.bson.Document;
import org.bson.types.ObjectId;
import tr.org.tspb.common.pojo.CellMultiDimensionKey;
import tr.org.tspb.converter.base.SelectOneObjectIdConverter;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.constants.ProjectConstants;
import static tr.org.tspb.constants.ProjectConstants.COMMON;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_NE;
import static tr.org.tspb.constants.ProjectConstants.FILE_EXTENSION_JASPER;
import static tr.org.tspb.constants.ProjectConstants.FILE_EXTENSION_PDF;
import static tr.org.tspb.constants.ProjectConstants.FORMS;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import static tr.org.tspb.constants.ProjectConstants.MONGO_LDAP_UID;
import static tr.org.tspb.constants.ProjectConstants.NAME;
import static tr.org.tspb.constants.ProjectConstants.UYSDB;
import tr.org.tspb.factory.qualifier.OgmCreatorQualifier;
import tr.org.tspb.service.FilterService;
import tr.org.tspb.factory.cp.OgmCreatorIntr;
import tr.org.tspb.util.stereotype.MyController;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyController
public class PivotResave extends PivotImpl {

    @Inject
    protected FilterService filterService;

    @Inject
    @OgmCreatorQualifier
    private OgmCreatorIntr ogmCreator;

    private static final String SECINIZ = " seçiniz";
    private static final String PARAMETER_PAGE_NUMBER = "parameterPageNumber";
    private static final String DATA_BANK_TEMPLATE = "dataBankTemplate";

    private List<String> reportWebOzetMemberType;

    private String[] resaveForm = {"B2", "B3", "C1", "C2", "D1", "D2", "H1", "H2", "H3", "H4", "H5", "F11"};
    private String resaveMemberStr;
    private int resavePeriodStartStr;
    private int resavePeriodEndStr;
    private String resaveTemplateStr;
    private boolean reportWebOzetSelectAll;
    private Map<String, String> availaibleReportWebOzetMemberType;
    List<SelectItem> reportWebOzetMemberTypes;
    List<SelectItem> resaveAllPeriods;
    List<SelectItem> resaveAllTemplates;
    List<SelectItem> resaveForms;
    List<SelectItem> resaveMembers;

    //    
    protected Map<CellMultiDimensionKey, List<CustomOlapHashMap>> pivotData;
    private Integer jasperParameterPageNumber;
    @Resource(lookup = ProjectConstants.CUSTOM_RESOURCE_MONGO_URL)
    private String resourceMongoUrl;

    @Resource(lookup = ProjectConstants.CUSTOM_RESOURCE_MONGO_ADMIN_PSWD)
    private String resourceMongoAdminPswd;

    public List<SelectItem> getReportWebOzetMemberTypes() {
        return Collections.unmodifiableList(reportWebOzetMemberTypes);
    }

    public List<SelectItem> getResaveForms() {
        if (resaveForms == null) {
            resaveForms = new ArrayList<>();
            //
            resaveForms.add(new SelectItem("B2", "B2 : İnternet İşlemleri"));
            resaveForms.add(new SelectItem("D1", "D1 : Personel Sayısı - Departman"));
            resaveForms.add(new SelectItem("H3", "H3 : Bilanço Detay"));
            //
            resaveForms.add(new SelectItem("B3", "B3 : Çağrı Merkezi İşlemleri"));
            resaveForms.add(new SelectItem("D2", "D2 : Personel Sayısı - Yaş"));
            resaveForms.add(new SelectItem("H4", "H4 : Gelir Tablosu Detay"));
            //
            resaveForms.add(new SelectItem("C1", "C1 : İşlem Hacmi - Yatırımcı"));
            resaveForms.add(new SelectItem("H1", "H1 : Bilanço Özet"));
            resaveForms.add(new SelectItem("H5", "H5 : Bilanço TCMB Bildirimi"));
            //
            resaveForms.add(new SelectItem("C2", "C2 : İşlem Hacmi - Departman"));
            resaveForms.add(new SelectItem("H2", "H2 : Gelir Tablosu Özet"));
            resaveForms.add(new SelectItem("F11", "F11Varlık Yönetimi (PYS)"));
        }
        return Collections.unmodifiableList(resaveForms);
    }

    public List<SelectItem> getResaveAllTemplates() {
        if (resaveAllTemplates == null) {
            resaveAllTemplates = new ArrayList<>();
            List<Document> cursor = mongoDbUtil.find(UYSDB, DATA_BANK_TEMPLATE, new Document(FORMS, TEMPLATE), new Document(NAME, 1), null);

            for (Document object : cursor) {
                resaveAllTemplates.add(new SelectItem(object.get(MONGO_ID).toString(), (String) object.get(NAME)));
            }
        }
        return Collections.unmodifiableList(resaveAllTemplates);
    }

    public List<SelectItem> getResaveMembers() {
        if (resaveMembers == null) {
            resaveMembers = new ArrayList<>();
            List<Document> cursor = mongoDbUtil.find(UYSDB, COMMON,
                    Filters.and(Filters.eq(FORMS, baseService.getReportProperties().loginFormKey), Filters.eq(MONGO_LDAP_UID, new Document(DOLAR_NE, "empty"))),
                    new Document(MONGO_LDAP_UID, 1), null);

            resaveMembers.add(new SelectItem("none", "Lütfen Seçiniz ..."));

            for (Document object : cursor) {
                resaveMembers.add(new SelectItem(object.get(MONGO_ID).toString(), //
                        String.format("%s - %s", object.get(MONGO_LDAP_UID), object.get(NAME))));
            }
        }
        return Collections.unmodifiableList(resaveMembers);
    }

    public List<SelectItem> getResaveAllPeriods() {
        if (resaveAllPeriods == null) {
            resaveAllPeriods = new ArrayList<>();

            List<Document> cursor = mongoDbUtil.find(UYSDB, COMMON, new Document(FORMS, PERIOD), new Document(NAME, 1), null);

            for (Document object : cursor) {
                resaveAllPeriods.add(new SelectItem(((Number) object.get(VALUE)).intValue(), (String) object.get(NAME)));
            }
        }
        return Collections.unmodifiableList(resaveAllPeriods);
    }

    @PostConstruct
    public void init() {
        availaibleReportWebOzetMemberType = new HashMap<>();
        availaibleReportWebOzetMemberType.put("AK", "Aracı Kurumlar (AK)");
        availaibleReportWebOzetMemberType.put("B", "Bankalar (B)");
        availaibleReportWebOzetMemberType.put("KTB", "Katılım Bankaları (KTB)");
        availaibleReportWebOzetMemberType.put("MB", "Mevduat Bankaları (MB)");
        availaibleReportWebOzetMemberType.put("YB", "Yatırım Bankaları (YB)");
        availaibleReportWebOzetMemberType.put("KB", "Kalkınma Bankaları (KB)");
        availaibleReportWebOzetMemberType.put("PYŞ", "Portföy Yönetim Şirketleri (PYŞ)");
        availaibleReportWebOzetMemberType.put("MKYO", "Menkul Kıymetler Yatırım Ortaklığı (MKYO)");
        availaibleReportWebOzetMemberType.put("GYO", "Gayri Menkul Yatırım Ortaklığı (GYO)");
        availaibleReportWebOzetMemberType.put("GSYO", "Girişim Sermayesi Yatırım Ortaklığı (GSYO)");

        reportWebOzetMemberTypes = new ArrayList<>();
        for (Map.Entry<String, String> entry : availaibleReportWebOzetMemberType.entrySet()) {
            reportWebOzetMemberTypes.add(new SelectItem(entry.getKey(), entry.getValue()));
        }
    }

    public String webOzet() {
        try {

            String jasperPath = ProjectConstants.PATH_JASPER_JRXML
                    .concat("/allInOne")
                    .concat(FILE_EXTENSION_JASPER);

            File file = new File(jasperPath);

            if (!file.exists()) {
                throw new Exception("Dosya Bulunamadı : <br/>".concat(jasperPath));
            }

            try (MongoDbConnection connection = connect()) {

                Map parameterMap = new HashMap();
                parameterMap.put("REPORT_CONNECTION", connection);
                parameterMap.put("SUBREPORT_DIR", ProjectConstants.PATH_JASPER_JRXML.concat("/"));
                parameterMap.put(PARAMETER_PAGE_NUMBER, jasperParameterPageNumber);
                parameterMap.put("member_types", getReportWebOzetMemberType());
                parameterMap.put("REPORT_LOCALE", new Locale("tr", "TR"));

                StringBuilder membertypes = new StringBuilder();
                for (String memberType : getReportWebOzetMemberType()) {
                    membertypes.append(memberType).append("_");
                }

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameterMap);

                String destFileName = baseService.getProperties().getTmpDownloadPath().concat("allInOne__")
                        .concat(membertypes.toString()).concat(FILE_EXTENSION_PDF);

                String jasperVersion = "XXX";

                if ("6.3.0".equals(jasperVersion)) {
                    JRPdfExporter pdfExporter = new JRPdfExporter();
                    pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                    pdfExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                            baseService.getProperties().getTmpDownloadPath().concat("allInOne__").concat(membertypes.toString()).concat(FILE_EXTENSION_PDF));
                    pdfExporter.exportReport();
                } else {
                    JasperExportManager.exportReportToPdfFile(jasperPrint, destFileName);
                }
            }
        } catch (Exception ex) {
            dialogController.showPopupError(ex.getMessage());
        }

        return null;
    }

    private MongoDbConnection connect() throws JRException {
        String url = "mongodb://".concat(resourceMongoUrl).concat(":27017/uysdb");
        return new MongoDbConnection(url, "tspb-db-admin", resourceMongoAdminPswd);
    }

    public String webOzetSeparatedly() {
        try {
            List<Document> cursor = mongoDbUtil.find(UYSDB, "reportMembers");

            for (Document dBObject : cursor) {

                String memberName = (String) dBObject.get(NAME);

                String jasperPath = ProjectConstants.PATH_JASPER_JRXML
                        .concat("/allInOneParameter")
                        .concat(FILE_EXTENSION_JASPER);
                File file = new File(jasperPath);

                if (!file.exists()) {
                    throw new Exception("Dosya Bulunamadı : <br/>".concat(jasperPath));
                }

                try (MongoDbConnection connection = connect()) {

                    Map parameterMap = new HashMap();
                    parameterMap.put(PARAMETER_PAGE_NUMBER, 1);
                    parameterMap.put("REPORT_CONNECTION", connection);
                    parameterMap.put("SUBREPORT_DIR", ProjectConstants.PATH_JASPER_JRXML.concat("/"));
                    parameterMap.put("master_member_name", memberName);
                    parameterMap.put("REPORT_LOCALE", new Locale("tr", "TR"));

                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameterMap);

                    String destFileName = baseService.getProperties().getTmpDownloadPath().concat(memberName).concat(FILE_EXTENSION_PDF);

                    String jasperVersion = "XXX";

                    if ("6.3.0".equals(jasperVersion)) {
                        // PdfFont font = new PdfFont("Symbol", "CP1252", true);
                        JRPdfExporter pdfExporter = new JRPdfExporter();
                        pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                        pdfExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                                baseService.getProperties().getTmpDownloadPath().concat(memberName).concat(FILE_EXTENSION_PDF));
                        pdfExporter.exportReport();
                    } else {
                        JasperExportManager.exportReportToPdfFile(jasperPrint, destFileName);
                    }
                }
            }
        } catch (Exception ex) {
            dialogController.showPopupError(ex.getMessage());
        }

        return null;
    }

    /**
     *
     * @param key
     * @param value
     */
    public void putSearchObjectValue(String key, ObjectId value) {
        getFilter().put(key, value);

    }

    public String saveAgainAll() {
        try {
            reSaveAll();
        } catch (Exception ex) {
            logger.error("error occured", ex);
        }
        return null;
    }

    public String reSaveAll() throws Exception {
        String[] forms = resaveForm;

        final Map<ObjectId, String> cacheMapMember = new HashMap();
        final Map<ObjectId, String> cacheMapPeriod = new HashMap();

        List<Document> cursorMembers = mongoDbUtil.findWithProjection(UYSDB, COMMON,
                new Document(FORMS, MEMBER),
                new Document(MONGO_ID, true).append(NAME, true));
        for (Document member : cursorMembers) {
            cacheMapMember.put((ObjectId) member.get(MONGO_ID), (String) member.get(NAME));
        }

        List<Document> cursorPeriods = mongoDbUtil.findWithProjection(UYSDB, COMMON,
                new Document(FORMS, PERIOD),
                new Document(MONGO_ID, true).append(NAME, true));
        for (Document period : cursorPeriods) {
            cacheMapPeriod.put((ObjectId) period.get(MONGO_ID), (String) period.get(NAME));
        }

        for (String form : forms) {
            //FIXME remove hard coded "graph"
            MyForm myForm = ogmCreator
                    .getMyFormLarge(null, "graph", new Document(FORM, form), getFilter(), loginController.getRoleMap(), loginController.getLoggedUserDetail());

            BasicDBList statusList = new BasicDBList();
            statusList.add("ONAYLANDI");
            statusList.add("İNCELENİYOR");

            Document periodQuery = new Document()
                    .append(VALUE, new Document().append(DOLAR_GTE, resavePeriodStartStr).append(DOLAR_LTE, resavePeriodEndStr));

            List<Document> cursorPeriod = mongoDbUtil.find(UYSDB, COMMON, periodQuery);

            BasicDBList periodList = new BasicDBList();

            for (Document dBObject : cursorPeriod) {
                periodList.add(dBObject.get(MONGO_ID));
            }

            Document query = new Document()//
                    .append("workflowStatus.name", new Document(DOLAR_IN, statusList))//
                    .append(PERIOD, new Document(DOLAR_IN, periodList));

            if (resaveTemplateStr != null) {
                query.append(TEMPLATE, new ObjectId(resaveTemplateStr));
            }

            if (resaveMemberStr != null && !"none".equals(resaveMemberStr)) {
                query.append(MEMBER, new ObjectId(resaveMemberStr));
            }

            List<Document> orgStatusList = mongoDbUtil.find(UYSDB, "dataBankOrganizationStatus", query);

            Collections.sort(orgStatusList, new Comparator<Document>() {
                @Override
                public int compare(Document t1, Document t2) {
                    String order1 = cacheMapMember.get(t1.get(MEMBER));
                    String order2 = cacheMapMember.get(t2.get(MEMBER));
                    return order1.compareToIgnoreCase(order2);
                }
            });

            int i = 0;

            for (Document organizationStatus : orgStatusList) {

                /*
                 if (organizationStatus.get(MY_FORM_GROUPS) instanceof List) {
                 boolean result1 = ((List) organizationStatus.get(MY_FORM_GROUPS)).contains(myForm.getForm());
                 boolean result2 = ((List) organizationStatus.get(MY_FORM_GROUPS)).contains(myForm.getGroup());
                 if (!(result1 || result2)) {
                 continue;
                 }
                 }
                 */
                ObjectId memberID = (ObjectId) organizationStatus.get(formService.getMyForm().getLoginFkField());
                ObjectId periodID = (ObjectId) organizationStatus.get(PERIOD);
                ObjectId templateID = (ObjectId) organizationStatus.get(TEMPLATE);

                Document member = mongoDbUtil.findOne(UYSDB, COMMON,
                        new Document(FORMS, formService.getMyForm().getLoginFkField()).append(MONGO_ID, memberID));

                Document period = mongoDbUtil.findOne(UYSDB, COMMON,
                        new Document(FORMS, PERIOD).append(MONGO_ID, periodID));

                Document template = mongoDbUtil.findOne(UYSDB, "dataBankTemplate",
                        new Document(FORMS, TEMPLATE).append(MONGO_ID, templateID));

                logger.info(String.valueOf(i++)
                        .concat(" : ")
                        .concat(period.getString(NAME))
                        .concat(" : ")
                        .concat(template.getString(NAME))
                        .concat(" : ")
                        .concat(form).concat(" - ").concat(member.getString(NAME)));

                putSearchObjectValue(MEMBER, memberID);
                putSearchObjectValue(PERIOD, periodID);
                putSearchObjectValue(TEMPLATE, templateID);

                filterService.initSearchMap(memberID, periodID, templateID, myForm);

                if (!myForm.getFieldsKeySet().contains(TEMPLATE)) {
                    removeFilterAttr(TEMPLATE);
                }

                /*this step prepare ndAxisMap */
                createDimensionIksIgrek(myForm);

                pivotData = dataService.createPivotData(myForm, cellControl, getIksDimension(), getIgrekDimension(), getMapOfMeasureField());

                calcService.createPivotCalcData(pivotData, true, true, myForm, getFilter());

                Map<CellMultiDimensionKey, List<Document>> mapReduce = new HashMap();

                /**
                 * Convert Two Dimension Map to Collection
                 */
                for (CellMultiDimensionKey cellMultiKey : pivotData.keySet()) {
                    /**
                     *
                     * Runtime MapReduce base idea here is an idea that all
                     * record with the data dimension combination object
                     * assessed as a new record
                     *
                     * by excluding DIMENSION_AND_MESURE coordinate from
                     * cellMultiKey we can easily achieve MAP PHASE of the
                     * "MapReduce Idea"
                     */
                    /**
                     * MAP PHASE
                     */
                    List<MyField> listOfMappedCoordinates = new ArrayList<>(cellMultiKey.getCoordinates());

                    for (MyField coordinate : cellMultiKey.getCoordinates()) {
                        String ndType = coordinate.getNdType();
                        if (MEASURE.equals(ndType)) {
                            listOfMappedCoordinates.remove(coordinate);
                        }
                    }

                    CellMultiDimensionKey cellMultiDimensionKeyMap = new CellMultiDimensionKey(listOfMappedCoordinates);

                    Document recordMap = new Document();

                    List<CustomOlapHashMap> list = pivotData.get(cellMultiKey);

                    for (CustomOlapHashMap customOlapHaspMap : list) {
                        /**
                         * is it calculate field ? calculated values should be
                         * saved for reports and "QUICK" constraint formulas
                         *
                         * if
                         * (Boolean.TRUE.equals(customOlapHaspMap.get(CALCULATE)))
                         * {
                         *
                         * logger.info("calculated value going to be
                         * saved".concat(customOlapHaspMap.toString()));
                         * //continue; }
                         */
                        /**
                         * we provide emit step by this NULL check
                         */
                        if (mapReduce.get(cellMultiDimensionKeyMap) == null) {
                            mapReduce.put(cellMultiDimensionKeyMap, new ArrayList<Document>());
                        }

                        String measureField = customOlapHaspMap.getField();
                        ObjectId prevID = customOlapHaspMap.getId();

                        if (measureField == null) {
                            throw new Exception(customOlapHaspMap.toString()
                                    .concat(" does not have a \"field\" key<br/>")
                                    .concat("It seems the new record Map missed the \"field\" key."));
                        }

                        recordMap.append(measureField, customOlapHaspMap.getValue());

                        if (recordMap.get(MONGO_ID) != null && !recordMap.get(MONGO_ID).equals(customOlapHaspMap.getId())) {
                            String errorMessage = "We do not expect more than one value in olap view at the save state. "
                                    + "More than one record._id had been deteced on the cell." + cellMultiKey;
                            throw new Exception(errorMessage);
                        }

                        if (prevID != null) {
                            recordMap.put(MONGO_ID, prevID);
                        }
                        /**
                         * ON FLY REDUCE PHASE
                         */
                        List<Document> mappedList = mapReduce.get(cellMultiDimensionKeyMap);

                        if (mappedList.isEmpty()) {
                            mappedList.add(recordMap);
                        } else {
                            Document prevRecordMap = mappedList.get(mappedList.size() - 1);
                            prevRecordMap.putAll(recordMap);
                        }
                    }
                }

                // prepare and save
                for (CellMultiDimensionKey cellMultiDimensionKey : mapReduce.keySet()) {
                    for (Document recordMapValue : mapReduce.get(cellMultiDimensionKey)) {
                        Document indexRecord = new Document();

                        /**
                         * Should be merged with it map from mapReduce
                         */
                        for (MyField coordinate : cellMultiDimensionKey.getCoordinates()) {
                            // DBObject Document = new Document();
                            // Document.put(_ID, coordinate.getDimensionOrMeasure().get(_ID));
                            // Document.put(CODE, coordinate.getDimensionOrMeasure().get(CODE));
                            // Document.put(NAME, coordinate.getShortName()getName()DimensionOrMeasure().get(NAME));
                            // the code field is important for balnceAbstract calculation
                            // it drives the idea for general relation and need to be distributed over all the code
                            indexRecord.put(coordinate.getKey(), coordinate.getId());
                            // FIXME we just hope that forms and field key have the same value
                        }

                        indexRecord.put(FORMS, myForm.getKey());
                        indexRecord.put(formService.getMyForm().getLoginFkField(), memberID);
                        indexRecord.put(PERIOD, periodID);
                        indexRecord.put(TEMPLATE, templateID);

                        if (!myForm.getFieldsKeySet().contains(TEMPLATE)) {
                            indexRecord.put(TEMPLATE, templateID);
                        }

                        //   Map<String, Object> autosetMapValues = new HashMap<String, Object>();
                        recordMapValue.putAll(indexRecord);

                        ObjectId objectID = (ObjectId) recordMapValue.get(MONGO_ID);

                        if (objectID == null) {
                            Document index = new Document();
                            for (String key : indexRecord.keySet()) {
                                index.put(key, 1);
                            }

                            mongoDbUtil.createIndex(myForm, index);
                            mongoDbUtil.updateMany(myForm, indexRecord, new Document(recordMapValue));
                        } else {
                            //recordMapValue.remove(_ID);
                            mongoDbUtil.updateOne(myForm.getDb(), myForm.getTable(), Filters.eq(MONGO_ID, objectID), recordMapValue);
                        }
                    }
                }
            }
        }
        return null;
    }

    protected String saveObject() throws Exception {

        calcService.createPivotCalcData(pivotData, true, true, formService.getMyForm(), getFilter());

        String collection = formService.getMyForm().getTable();

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
         * burada uysdb değeri selectedForm üzerinden değil
         * db.ldapMatch.findOne() üzerinden yapılmalı.
         *
         * Cünkü farklı projeler farklı kullnıcı listesine sahip olabilir
         * ileride ama şimdilik hepsi uysdb projesi ile ortak
         */
        Document loginUserDBObject = (Document) mongoDbUtil.findOne(UYSDB, "common",
                new Document(MONGO_LDAP_UID, loginController.getLoggedUserDetail().getUsername()));

        if (!loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminRole())//
                && (loginUserDBObject == null || !loginUserDBObject.get(MONGO_ID).equals(member))) {
            throw new UserException("Seçilen Kurum Sizin Kurum Değil");
        }

        Map<CellMultiDimensionKey, List<Document>> mapReduce = new HashMap();

        /**
         * Convert Two Dimension Map to Collection
         */
        for (CellMultiDimensionKey cellMultiKey : pivotData.keySet()) {
            /**
             *
             * Runtime MapReduce base idea here is an idea that all record with
             * the data dimension combination object assessed as a new record
             *
             * by excluding DIMENSION_AND_MESURE coordinate from cellMultiKey we
             * can easily achieve MAP PHASE of the "MapReduce Idea"
             */
            /**
             * MAP PHASE
             */
            List<MyField> listOfMappedCoordinates = new ArrayList<>(cellMultiKey.getCoordinates());

            for (MyField coordinate : cellMultiKey.getCoordinates()) {
                String ndType = coordinate.getNdType();
                if (MEASURE.equals(ndType)) {
                    listOfMappedCoordinates.remove(coordinate);
                }
            }

            CellMultiDimensionKey cellMultiDimensionKeyMap = new CellMultiDimensionKey(listOfMappedCoordinates);

            Document recordMap = new Document();

            List<CustomOlapHashMap> list = pivotData.get(cellMultiKey);

            for (CustomOlapHashMap customOlapHaspMap : list) {
                /**
                 * is it calculate field ? calculated values should be saved for
                 * reports and "QUICK" constraint formulas
                 *
                 * if (Boolean.TRUE.equals(customOlapHaspMap.get(CALCULATE))) {
                 *
                 * logger.info( "calculated value going to be
                 * saved".concat(customOlapHaspMap.toString())); //continue; }
                 */
                /**
                 * we provide emit step by this NULL check
                 */
                if (mapReduce.get(cellMultiDimensionKeyMap) == null) {
                    mapReduce.put(cellMultiDimensionKeyMap, new ArrayList<Document>());
                }

                String measureField = (String) customOlapHaspMap.getField();
                ObjectId prevID = customOlapHaspMap.getId();

                if (measureField == null) {
                    throw new Exception(customOlapHaspMap.toString()//
                            .concat(" does not have a \"field\" key<br/>")//
                            .concat("It seems the new record Map missed the \"field\" key."));
                }

                recordMap.append(measureField, customOlapHaspMap.getValue());

                if (recordMap.get(MONGO_ID) != null && !recordMap.get(MONGO_ID).equals(customOlapHaspMap.getId())) {
                    String errorMessage = "We do not expect more than one value in olap view at the save state. "
                            + "More than one record._id had been deteced on the cell." + cellMultiKey;
                    throw new Exception(errorMessage);
                }

                if (prevID != null) {
                    recordMap.put(MONGO_ID, prevID);
                }
                /**
                 * ON FLY REDUCE PHASE
                 */
                List<Document> mappedList = mapReduce.get(cellMultiDimensionKeyMap);

                if (mappedList.isEmpty()) {
                    mappedList.add(recordMap);
                } else {
                    Document prevRecordMap = mappedList.get(mappedList.size() - 1);
                    prevRecordMap.putAll(recordMap);
                }
            }
        }

        /**
         * prepare and save
         */
        Date upsertDate = new Date();

        for (CellMultiDimensionKey cellMultiDimensionKey : mapReduce.keySet()) {
            for (Document recordMapValue : mapReduce.get(cellMultiDimensionKey)) {
                Document indexRecord = new Document();

                /**
                 * Should be merged with it map from mapReduce
                 */
                for (MyField coordinate : cellMultiDimensionKey.getCoordinates()) {
                    String fieldKey = coordinate.getKey();
                    indexRecord.put(fieldKey, coordinate.getId());
                    //FIXME we just hope that forms and field key have the same value
                    if (indexRecord.get(fieldKey) == null && coordinate.getItemsAsMyItems() == null) {
                        indexRecord.put(fieldKey, coordinate.getDefaultValue());
                    }
                }

                indexRecord.put(FORMS, formService.getMyForm().getKey());
                indexRecord.putAll(autosetMapValues);

                recordMapValue.putAll(indexRecord);

                ObjectId objectID = (ObjectId) recordMapValue.get(MONGO_ID);

                recordMapValue.put(OPERATOR_LDAP_UID, loginController.getLoggedUserDetail().getUsername());
                recordMapValue.put(UPSERT_DATE, upsertDate);

                if (objectID == null) {
                    Document index = new Document();
                    for (String key : indexRecord.keySet()) {
                        index.put(key, 1);
                    }
                    mongoDbUtil.createIndex(formService.getMyForm(), index);
                    mongoDbUtil.updateMany(formService.getMyForm(), indexRecord, new Document(recordMapValue));
                } else {
                    Document searchPart = new Document(MONGO_ID, objectID);
                    Document updatePart = new Document();
                    recordMapValue.remove(MONGO_ID);
                    updatePart.put("$set", new Document(recordMapValue));

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
                    mongoDbUtil.updateMany(formService.getMyForm(), searchPart, updatePart);
                }
            }
        }

        Document trigger = formService.getMyForm().getEventPostSave();

        if (trigger == null || !"application".equals(trigger.get(TYPE))) {
            mongoDbUtil.trigger(new Document(getFilter()), trigger, loginController.getRolesAsList());
        }

        return null;
    }

    public String[] getResaveForm() {
        return resaveForm;
    }

    public void setResaveForm(String[] resaveForm) {
        this.resaveForm = resaveForm;
    }

    public String getResaveMemberStr() {
        return resaveMemberStr;
    }

    public void setResaveMemberStr(String resaveMemberStr) {
        this.resaveMemberStr = resaveMemberStr;
    }

    public int getResavePeriodEndStr() {
        return resavePeriodEndStr;
    }

    public void setResavePeriodEndStr(int resavePeriodEndStr) {
        this.resavePeriodEndStr = resavePeriodEndStr;
    }

    public String getResaveTemplateStr() {
        return resaveTemplateStr;
    }

    public void setResaveTemplateStr(String resaveTemplateStr) {
        this.resaveTemplateStr = resaveTemplateStr;
    }

    public int getResavePeriodStr() {
        return resavePeriodStartStr;
    }

    public void setResavePeriodStr(int resavePeriodStr) {
        this.resavePeriodStartStr = resavePeriodStr;
    }

    /**
     *
     * @return
     */
    public List<String> getReportWebOzetMemberType() {
        return reportWebOzetMemberType;
    }

    /**
     *
     * @param reportWebOzetMemberType
     */
    public void setReportWebOzetMemberType(List<String> reportWebOzetMemberType) {
        this.reportWebOzetMemberType = reportWebOzetMemberType;
    }

    public Integer getJasperParameterPageNumber() {
        return jasperParameterPageNumber;
    }

    public void setJasperParameterPageNumber(Integer jasperParameterPageNumber) {
        this.jasperParameterPageNumber = jasperParameterPageNumber;
    }

    public void onSelectAll() {
        setReportWebOzetMemberType(reportWebOzetSelectAll ? new ArrayList<>(availaibleReportWebOzetMemberType.keySet()) : null);
    }

    public boolean isReportWebOzetSelectAll() {
        return reportWebOzetSelectAll;
    }

    public void setReportWebOzetSelectAll(boolean reportWebOzetSelectAll) {
        this.reportWebOzetSelectAll = reportWebOzetSelectAll;
    }

    @Override
    public Document getFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
