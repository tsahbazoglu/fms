package tr.org.tspb.common.services;

import com.mongodb.client.model.Filters;
import static tr.org.tspb.constants.ProjectConstants.*;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.util.*;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.xml.sax.SAXException;
import tr.org.tspb.common.jmx.MyJmxMonitor;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import tr.org.tspb.dao.MyActions;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.dao.MyProject;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.factory.qualifier.OgmCreatorQualifier;
import tr.org.tspb.pojo.MyCalcDef;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.tools.MongoDbUtilIntr;
import tr.org.tspb.factory.cp.OgmCreatorIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
@Named
@ApplicationScoped
public class AppScopeSrvCtrl {

    @Inject
    @MyLoginQualifier
    LoginController loginController;

    @Inject
    private Logger logger;

    @Inject
    BaseService baseService;

    @Inject
    @KeepOpenQualifier
    private MongoDbUtilIntr mongoDbUtil;

    @Inject
    @OgmCreatorQualifier
    private OgmCreatorIntr ogmCreator;

    private static Map<String, MyProject> cacheProjects = new HashMap<>();
    private static Map<String, ObjectId> cacheIdByKpbMemberName;
    private static Map<String, ObjectId> cacheIdByLdapUid;
    private static Map<String, String> cacheIonSettingNotifyType;
    private static Map<String, MyFopFactory> mapOfPdfFactory = new HashMap<>();
    private final Map<String, List<SelectItem>> cacheZetDimensionItems = new HashMap<>();
    private final Map<String, List<MyField>> cacheDimensionItems = new HashMap<>();
    private final Map<String, FmsForm> cacheForm = new HashMap<>();
    private final Map<String, MyActions> cacheActions = new HashMap<>();

    public MyActions getCacheActions(String key) {
        return cacheActions.get(key);
    }

    public void putCacheActions(String key, MyActions myActions) {
        cacheActions.put(key, myActions);
    }

    public FmsForm getCacheForm(String key) {
        return cacheForm.get(key);
    }

    public void putCacheForm(String key, FmsForm myForm) {
        cacheForm.put(key, myForm);
    }

    public List<SelectItem> getCacheZetDimensionItems(String key) {
        return cacheZetDimensionItems.get(key);
    }

    public void putCacheZetDimensionItems(String key, List<SelectItem> zetDimensionItems) {
        cacheZetDimensionItems.put(key, zetDimensionItems);
    }

    public List<MyField> cacheAndGetDimensionItems(String key) {
        return cacheDimensionItems.get(key);
    }

    public void putCacheDimensionItems(String key, List<MyField> dimensionItems) {
        cacheDimensionItems.put(key, dimensionItems);
    }

    public MyProject getProject(String key, Document projectDoc) throws NullNotExpectedException, FormConfigException {
        MyProject myProject = cacheProjects.get(key);
        if (myProject == null) {
            myProject = ogmCreator.getMyProject(projectDoc, baseService.getTagLogin());
            cacheProjects.put(key, myProject);
        }
        return myProject;
    }

    public MyProject getProject(String key) throws NullNotExpectedException {
        return cacheProjects.get(key);
    }

    private static final String ION_SETTING_ACTIVITY_STATUS_001 = "ion_setting_activity_status_001";

    List<Document> listOfPeriods;
    private Map<String, FmsForm> mapForms = new HashMap<>();
    Map<Map, List<Document>> sessionSearchCacheMap = new HashMap<>();
    Map<Map, List<Document>> applicationSearchResults = new HashMap<>();
    Map<Map, Document> calculateFormula = new HashMap();
    Map<Map, Map> applicationSearchCachMap = new HashMap<>();
    String[] readyReport = {"mrPtvtSumReport", "mrPtvtOrderReport"};
    private Map<ObjectId, Document> mapPeriod = new HashMap<>();
    private Map<Integer, Document> mapPeriodByValue = new HashMap<>();
    private Map<String, Document> mapMemberByLdapUID = new HashMap<>();

    // FIXME move this to engine-config pattern
    private ObjectId activityStatusHayir;
    private String webContext;
    private Boolean databankRecalculateOnControl = false;
    private String testControlFormulaTransferOrders = "";
    ObjectId prevTemplateId = null;
    //FIXME should be genralized
    Map<Map, ObjectId> prevPeriodTemplateMap = new HashMap();

    private List<Map> moduleList;
    private boolean cacheModuleList = false;
    private Map<String, String> memberCacheNameById;

    public Map<String, String> memberCacheNameById() {
        if (memberCacheNameById == null) {
            memberCacheNameById = new HashMap();

            List<Document> cursor = mongoDbUtil.find(UYSDB, "common", Filters.eq(FORMS, MEMBER));

            for (Document next : cursor) {
                memberCacheNameById.put(next.get(MONGO_ID).toString(), next.get(NAME).toString());
            }
        }
        return memberCacheNameById;
    }

    public void setModuleslist(List<Map> modules) {
        modules = new ArrayList();
        moduleList.addAll(modules);

        //FIXME Provide panelCollapsible or tree aproach regarding to upperNode Value
        for (Map mapModule : moduleList) {
            mapModule.put("rowSelected", false);
        }

        Collections.sort(moduleList, new Comparator<Map>() {
            @Override
            public int compare(Map t1, Map t2) {
                Number menuOrder1 = (Number) t1.get(MENU_ORDER);
                Number menuOrder2 = (Number) t2.get(MENU_ORDER);
                int order1 = (menuOrder1 == null) ? 0 : menuOrder1.intValue();
                int order2 = (menuOrder2 == null) ? 0 : menuOrder2.intValue();
                return Integer.compare(order1, order2);
            }
        });
    }

    public void createPdfFile(String pdfFileName, String cnfFileName,
            String xslFileName, String xmlFileName, String fileName)
            throws TransformerException, IOException, SAXException {

        try (OutputStream outputStream = new FileOutputStream(new File(pdfFileName))) {
            Fop fop = getMyFopFactory(cnfFileName).getFopFactory()
                    .newFop(MimeConstants.MIME_PDF, outputStream);

            Source xslSrc = new StreamSource(new File(xslFileName));

            Transformer transformer = getMyFopFactory(cnfFileName).getFactory()
                    .newTransformer(xslSrc);

            Result res = new SAXResult(fop.getDefaultHandler());
            Source xmlSrc = new StreamSource(new File(xmlFileName));
            transformer.transform(xmlSrc, res);
            // end XML To PDF
        }
    }

    public AppScopeSrvCtrl() {

        try {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

            Set<ObjectInstance> instances = platformMBeanServer.queryMBeans(new ObjectName(MyJmxMonitor.objectName), null);

            if (instances != null && instances.iterator().hasNext()) {
                ObjectName name = new ObjectName(MyJmxMonitor.objectName);
                platformMBeanServer.unregisterMBean(name);
            }

            instances = platformMBeanServer.queryMBeans(new ObjectName(MyJmxMonitor.objectName), null);
            if (instances != null && !instances.iterator().hasNext()) {
                ObjectName name = new ObjectName(MyJmxMonitor.objectName);
                MyJmxMonitor helloMBean = new MyJmxMonitor();

                platformMBeanServer.registerMBean(helloMBean, name);

                //REPORT
                instances = platformMBeanServer.queryMBeans(new ObjectName(MyJmxMonitor.objectName), null);
            }

        } catch (InstanceNotFoundException | MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException ex) {
            logger.error("error occured", ex);
        }

    }

    public ObjectId getActivityStatusHayir() {
        //FIXME generalize  and remove this function
        if (activityStatusHayir == null) {
            activityStatusHayir = (ObjectId) mongoDbUtil
                    .findOne("iondb", FmsForm.ION_SETTING_ACTIVITY_STATUS, new Document(CODE, ION_SETTING_ACTIVITY_STATUS_001))
                    .get(MONGO_ID);
        }
        return activityStatusHayir;
    }

    public MyFopFactory getMyFopFactory(String keyFileName) throws SAXException, IOException, TransformerConfigurationException {
        if (mapOfPdfFactory.containsKey(keyFileName)) {
            return mapOfPdfFactory.get(keyFileName);
        }
        //FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        MyFopFactory myFopFactory = new MyFopFactory(FopFactory.newInstance(new File(keyFileName)), factory);
        mapOfPdfFactory.put(keyFileName, myFopFactory);
        return myFopFactory;
    }

    public Map<String, String> cacheIonSettingNotifyType() {
        if (cacheIonSettingNotifyType == null) {
            cacheIonSettingNotifyType = new HashMap();

            List<Document> cursor = mongoDbUtil.find("iondb", "ion_setting_notify_type");

            for (Document nextElement : cursor) {
                cacheIonSettingNotifyType.put(nextElement.get(MONGO_ID).toString(), nextElement.get(CODE).toString());
            }
        }
        return Collections.unmodifiableMap(cacheIonSettingNotifyType);

    }

    public String getWebContext() {
        if (webContext == null) {
            String protocol = FacesContext.getCurrentInstance().getExternalContext().getRequestScheme();
            String serverName = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
            String serverPort = ":".concat(String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort()));
            String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
            webContext = protocol.concat("://").concat(serverName).concat("https".equals(protocol) ? "" : serverPort).concat(contextPath);
        }
        return webContext;
    }

    public void initKpbMemberCache() {
        if (cacheIdByLdapUid == null) {
            cacheIdByLdapUid = new HashMap();
            cacheIdByKpbMemberName = new HashMap();

            Document view = new Document(NAME, 1).append(MONGO_LDAP_UID, 1);

            List<Document> cursor = mongoDbUtil.find(baseService.getLoginDB(),
                    baseService.getLoginTable(),
                    baseService.getTagLogin().getFilter());

            for (Document nextElement : cursor) {
                cacheIdByLdapUid.put(nextElement.get(MONGO_LDAP_UID).toString(), (ObjectId) nextElement.get(MONGO_ID));
                cacheIdByKpbMemberName.put(createKeyFoCacheIdByKpbMemberName(nextElement.get(NAME).toString()), (ObjectId) nextElement.get(MONGO_ID));
            }
        }
    }

    public String clearKpbMemberCache() {
        cacheIdByLdapUid = null;
        cacheIdByKpbMemberName = null;
        return null;
    }

    public String createKeyFoCacheIdByKpbMemberName(String memberName) {
        return memberName
                .trim()
                .replaceAll("İ", "I")
                .replaceAll("Ş", "S")
                .replaceAll("Ğ", "G")
                .replaceAll("Ö", "O")
                .replaceAll("Ü", "U")
                .replaceAll("DEGERLER", "")
                .replaceAll("KIYMETLER", "")
                .replaceAll("YATIRIM", "")
                .replaceAll("MENKUL", "")
                .replaceAll("MARKET", "")
                .replaceAll("DEG", "")
                .replaceAll("TIC", "")
                .replaceAll("VE", "")
                .replaceAll("I", "")
                .replaceAll(" ", "")
                .replaceAll("[.]", "")
                .replaceAll("-", "");
    }

    public String getTestControlFormulaTransferOrders() {
        return testControlFormulaTransferOrders;
    }

    public void setTestControlFormulaTransferOrders(String testControlFormulaTransferOrders) {
        this.testControlFormulaTransferOrders = testControlFormulaTransferOrders;
    }

    public Boolean getDatabankRecalculateOnControl() {
        return databankRecalculateOnControl;
    }

    public void setDatabankRecalculateOnControl(Boolean databankRecalculateOnControl) {
        this.databankRecalculateOnControl = databankRecalculateOnControl;
    }

    public String justSubmit() {
        return null;
    }

    public Boolean getKunyeReport() {
        //configdb.uyscrontab.update({"job": "eimza_datafill_report"}, {$set: {completion: false}}, {upsert: true});
        Document dbo = mongoDbUtil.findOne("configdb", "uyscrontab", new Document("job", "eimza_datafill_report"));
        return Boolean.TRUE.equals(dbo == null ? null : dbo.get("completion"));
    }

    public String getKunyeReportTitle() {
        //configdb.uyscrontab.update({"job": "eimza_datafill_report"}, {$set: {completion: false}}, {upsert: true});

        Document dbo = mongoDbUtil.findOne("configdb", "uyscrontab", new Document("job", "eimza_datafill_report"));

        if (dbo == null) {
            return "Daha önceden oluşturulan rapor yok";
        } else {
            return (String) dbo.get("description");
        }
    }

    private Document getPeriod(ObjectId periodId) {
        Document period = mapPeriod.get(periodId);
        if (period == null) {
            period = mongoDbUtil.findOne(UYSDB, COMMON, new Document(MONGO_ID, periodId));
            mapPeriod.put(periodId, period);
        }
        return period;
    }

    public Document cachePeriodByValue(Integer value) {
        Document period = mapPeriodByValue.get(value);
        if (period == null) {
            period = mongoDbUtil.findOne(UYSDB, COMMON, new Document(VALUE, value));
            mapPeriodByValue.put(value, period);
        }
        return period;
    }

    public Document cacheMemberByLdapUID(String ldapUID) {
        Document member = mapMemberByLdapUID.get(ldapUID);
        if (member == null) {
            member = mongoDbUtil.findOne(UYSDB, COMMON, new Document(MONGO_LDAP_UID, ldapUID));
            mapMemberByLdapUID.put(ldapUID, member);
        }
        return member;
    }

    private Document getTemplate(ObjectId templateId) {
        Document template = mapPeriod.get(templateId);
        if (template == null) {
            template = mongoDbUtil.findOne(UYSDB, "dataBankTemplate", new Document(MONGO_ID, templateId));
            mapPeriod.put(templateId, template);
        }
        return template;
    }

    public Map<Map, List<Document>> getSessionSearchCacheMap() {
        return Collections.unmodifiableMap(sessionSearchCacheMap);
    }

    public void removeApplicationSearchResults(Map search) {
        if (applicationSearchResults.get(search) != null) {
            applicationSearchResults.remove(search);
        }
    }

    public void clearApplicationSearchResults() {
        applicationSearchResults = new HashMap<>();
    }

    public void clearCalculateFormula() {
        calculateFormula = new HashMap();
    }

    public MyCalcDef getCalculateFormula(Map calculateFormulaSearchMap) {
        Document formula = calculateFormula.get(calculateFormulaSearchMap);
        if (formula == null) {
            formula = new Document();
            Document query = new Document();
            Document coordinate = new Document();
            coordinate.put(X_CODE, calculateFormulaSearchMap.get(X_CODE));
            coordinate.put(Y_CODE, calculateFormulaSearchMap.get(Y_CODE));

            query.put(RELATIONS, calculateFormulaSearchMap.get(RELATIONS));
            query.put("droolsRuleCoordinate", coordinate);

            Document period = getPeriod((ObjectId) calculateFormulaSearchMap.get(PERIOD));
            Document template = getTemplate((ObjectId) calculateFormulaSearchMap.get(TEMPLATE));

            if (period != null) {
                query.put("validPeriods", period.get(VALUE));
            }
            if (template != null) {
                query.put(TEMPLATE, template.get(VALUE));
            }

            Document findOneResult = mongoDbUtil.findOne(UYSDB, "dataBankCalculateFormulas", query);

            if (findOneResult != null) {
                formula.putAll(findOneResult);
            }

            calculateFormula.put(calculateFormulaSearchMap, formula);
        }
        return formula.isEmpty() ? null : new MyCalcDef(formula);
    }

    /**
     * search map must have the property db and collection
     *
     * @param search
     * @return
     */
    public List<Document> getApplicationSearchResults(Map search) {
        List<Document> results = applicationSearchResults.get(search);
        if (results == null) {

            String db = (String) search.get(FORM_DB);
            String collection = (String) search.get(COLLECTION);
            String sortField = (String) search.get(SORT);
            Integer limit = (Integer) search.get(LIMIT);

            Document searchObject = new Document(search);
            searchObject.remove(FORM_DB);
            searchObject.remove(COLLECTION);
            searchObject.remove(SORT);
            searchObject.remove(LIMIT);
            searchObject.remove("cache");

            Bson sortBson = null;
            if (sortField != null) {
                sortBson = new Document(sortField, 1);
            }

            List<Document> cursor = mongoDbUtil.find(db, collection, searchObject, sortBson, limit);

            results = new ArrayList<>();

            for (Document doc : cursor) {
                results.add(doc);
            }

            applicationSearchResults.put(search, results);
        }
        return results;
    }

    public List<Document> getListOfPeriods(String db) {

        if (listOfPeriods == null) {
            listOfPeriods = new ArrayList<>();

            List<Document> docs = mongoDbUtil.find(db, COMMON, new Document(FORMS, PERIOD), new Document(NAME, 1), null);

            for (Document doc : docs) {
                listOfPeriods.add(doc);
            }

        }
        return Collections.unmodifiableList(listOfPeriods);
    }

    public ObjectId getPrevTemplate(String db, String sortKey, int prevCount, ObjectId currentTemplateId, ObjectId currentPeriodID) throws NullNotExpectedException {

        Map searchMap = new HashMap();
        searchMap.put("period", currentPeriodID);
        searchMap.put("template", currentTemplateId);

        if (prevPeriodTemplateMap.get(searchMap) == null) {
            Document currentDBObject = mongoDbUtil.findOne(db, "dataBankPreviousTemplate",
                    new Document().append(PERIOD, currentPeriodID).append(TEMPLATE, currentTemplateId));

            if (currentDBObject == null) {
                throw new NullNotExpectedException("Önceki Dönem-Şablon İlişkisini Tespit Eden Fonksiyonda Hata Oluştu.");
            }

            int currentSummarizeOrder = ((Number) currentDBObject.get("summarize_order")).intValue();

            Document previousDBObject = mongoDbUtil.findOne(db, "dataBankPreviousTemplate", new Document("summarize_order", currentSummarizeOrder - 10));

            if (previousDBObject != null) {
                prevPeriodTemplateMap.put(searchMap, (ObjectId) previousDBObject.get(TEMPLATE));
            } else {
                prevPeriodTemplateMap.put(searchMap, currentTemplateId);
            }

        }

        return prevPeriodTemplateMap.get(searchMap);
    }

    public Document getPrevPeriod(String db, String sortKey, int prevCount, ObjectId currentId) {

        if (currentId == null) {
            return null;
        }

        Map<Object, Document> mapSortKey = new HashMap<>();
        Map<Object, Document> mapId = new HashMap<>();

        for (Document dBObject : getListOfPeriods(db)) {
            mapSortKey.put(dBObject.get(sortKey), dBObject);
            mapId.put(dBObject.get(MONGO_ID), dBObject);
        }

        List listOfKeys = new ArrayList(mapSortKey.keySet());

        Collections.sort(listOfKeys, new Comparator() {
            @Override
            public int compare(Object t1, Object t2) {
                String order1 = (String) t1;
                String order2 = (String) t2;
                return order1.compareTo(order2);
            }
        });

        String currentName = (String) mapId.get(currentId).get(sortKey);

        int currentIndex = listOfKeys.indexOf(currentName);
        if (currentIndex > 0) {
            String prevName = (String) listOfKeys.get(listOfKeys.indexOf(currentName) - 1);
            return mapSortKey.get(prevName);
        }
        return null;
    }

    public Document getLastQuaterOfPrevYear(String db, String sortKey, int prevCount, ObjectId currentId) {
        Map<Object, Document> mapSortKey = new HashMap<>();
        Map<Object, Document> mapId = new HashMap<>();

        for (Document dBObject : getListOfPeriods(db)) {
            mapSortKey.put(dBObject.get(sortKey), dBObject);
            mapId.put(dBObject.get(MONGO_ID), dBObject);
        }

        List listOfKeys = new ArrayList(mapSortKey.keySet());

        Collections.sort(listOfKeys, new Comparator() {
            @Override
            public int compare(Object t1, Object t2) {
                String order1 = (String) t1;
                String order2 = (String) t2;
                return order1.compareTo(order2);
            }
        });

        String currentName = (String) mapId.get(currentId).get(sortKey);

        //FIXME bu ne ya???
        if (currentName.contains("2009")) {
            return mapSortKey.get("2008 - 12");
        }
        if (currentName.contains("2010")) {
            return mapSortKey.get("2009 - 12");
        }
        if (currentName.contains("2011")) {
            return mapSortKey.get("2010 - 12");
        }
        if (currentName.contains("2012")) {
            return mapSortKey.get("2011 - 12");
        }
        if (currentName.contains("2013")) {
            return mapSortKey.get("2012 - 12");
        }
        if (currentName.contains("2014")) {
            return mapSortKey.get("2013 - 12");
        }
        if (currentName.contains("2015")) {
            return mapSortKey.get("2014 - 12");
        }
        if (currentName.contains("2016")) {
            return mapSortKey.get("2015 - 12");
        }
        if (currentName.contains("2017")) {
            return mapSortKey.get("2016 - 12");
        }
        if (currentName.contains("2018")) {
            return mapSortKey.get("2017 - 12");
        }
        if (currentName.contains("2019")) {
            return mapSortKey.get("2018 - 12");
        }

        int currentIndex = listOfKeys.indexOf(currentName);
        if (currentIndex > 0) {
            String prevName = (String) listOfKeys.get(listOfKeys.indexOf(currentName) - 1);
            return mapSortKey.get(prevName);
        }
        return null;
    }

    public FmsForm getFormDefinitionByForm(String configCollection, String form, Map searchObject)
            throws NullNotExpectedException, MongoOrmFailedException, FormConfigException {
        String cacheKey = configCollection.concat(":").concat(form);
        if (mapForms.get(cacheKey) == null) {
            MyProject myProject = ogmCreator
                    .getMyProject(mongoDbUtil
                            .findOne("configdb", CFG_TABLE_PROJECT, Filters.eq(CONFIG_COLLECTIONS, configCollection)),
                            baseService.getTagLogin());

            mapForms.put(cacheKey, ogmCreator
                    .getMyFormLarge(myProject, configCollection, new Document(FORM, form), searchObject,
                            loginController.getRoleMap(), loginController.getLoggedUserDetail()));
        }
        return mapForms.get(cacheKey);
    }

    public FmsForm getFormDefinitionByKey(String configCollection, String collectionKey, Map searchObject)
            throws NullNotExpectedException, MongoOrmFailedException, FormConfigException {
        /**
         * Burada esas amaç herhangi bir session mb yi elde etmek cunku
         * sözkonusu config collection session tabanlı olmasını istiyoruz.
         */
        String cacheKey = configCollection.concat(":").concat(collectionKey);

        if (mapForms.get(cacheKey) == null) {

            MyProject myProject = ogmCreator
                    .getMyProject(mongoDbUtil
                            .findOne("configdb", CFG_TABLE_PROJECT, Filters.eq(CONFIG_COLLECTIONS, configCollection)),
                            baseService.getTagLogin());

            mapForms.put(cacheKey, ogmCreator
                    .getMyFormLarge(myProject, configCollection, new Document(FORM_KEY, collectionKey), searchObject,
                            loginController.getRoleMap(), loginController.getLoggedUserDetail()));

        }
        return mapForms.get(cacheKey);
    }

    public List<Map> getModuleList() {
        return Collections.unmodifiableList(moduleList);
    }

    public class MyFopFactory {

        private FopFactory fopFactory;
        private TransformerFactory factory;

        private MyFopFactory() {
        }

        public MyFopFactory(FopFactory fopFactory, TransformerFactory factory) {
            this.fopFactory = fopFactory;
            this.factory = factory;
        }

        public FopFactory getFopFactory() {
            return fopFactory;
        }

        public TransformerFactory getFactory() {
            return factory;
        }

    }

}
