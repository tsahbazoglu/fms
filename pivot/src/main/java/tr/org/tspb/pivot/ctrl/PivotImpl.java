package tr.org.tspb.pivot.ctrl;

import static tr.org.tspb.constants.ProjectConstants.*;
import java.io.Serializable;
import java.text.Collator;
import tr.org.tspb.common.util.CustomOlapHashMap;
import tr.org.tspb.common.qualifier.MyCtrlServiceQualifier;
import tr.org.tspb.service.CalcService;
import tr.org.tspb.service.CtrlService;
import tr.org.tspb.service.MapReduceService;
import java.util.*;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import org.bson.Document;
import org.bson.types.Code;
import org.slf4j.Logger;
import tr.org.tspb.common.pojo.CellMultiDimensionKey;
import tr.org.tspb.common.qualifier.MyQualifier;
import tr.org.tspb.common.qualifier.ViewerController;
import tr.org.tspb.converter.base.JsFunctionConverter;
import tr.org.tspb.converter.base.MoneyConverter;
import tr.org.tspb.converter.base.NumberConverter;
import tr.org.tspb.converter.base.SelectOneDBObjectConverter;
import tr.org.tspb.converter.base.SelectOneObjectIdConverter;
import tr.org.tspb.converter.base.SelectOneStringConverter;
import tr.org.tspb.converter.base.TelmanStringConverter;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyItems;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import tr.org.tspb.common.services.AppScopeSrvCtrl;
import tr.org.tspb.common.services.BaseService;
import tr.org.tspb.service.FilterService;
import tr.org.tspb.common.services.LoginController;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.outsider.EsignDoor;
import tr.org.tspb.service.DataService;
import tr.org.tspb.service.FormService;
import tr.org.tspb.util.service.DlgCtrl;
import tr.org.tspb.dao.MyFieldComparator;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.tools.MongoDbUtilIntr;
import tr.org.tspb.factory.cp.OgmCreatorIntr;
import tr.org.tspb.factory.qualifier.OgmCreatorQualifier;
import tr.org.tspb.service.FeatureService;

/**
 *
 * @author Telman Şahbazoğlu
 */
public abstract class PivotImpl implements Serializable, PivotApi {

    @Inject
    protected Logger logger;

    @Inject
    protected AppScopeSrvCtrl appScopeSrvCtrl;

    @Inject
    @MyCtrlServiceQualifier
    protected CtrlService ctrlService;

    @Inject
    protected CalcService calcService;

    @Inject
    protected MapReduceService mapReduceService;

    @Inject
    @MyLoginQualifier
    protected LoginController loginController;

    @Inject
    protected DlgCtrl dialogController;

    @Inject
    protected FilterService filterService;

    @Inject
    protected DataService dataService;

    @Inject
    protected FeatureService featureService;

    @Inject
    @MyQualifier(myEnum = ViewerController.viewerPivot)
    protected PivotViewerCtrl pivotViewerController;

    @Inject
    protected FormService formService;

    @Inject
    @KeepOpenQualifier
    protected MongoDbUtilIntr mongoDbUtil;

    @Inject
    @OgmCreatorQualifier
    private OgmCreatorIntr ogmCreator;

    @Inject
    protected BaseService baseService;

    //
    private List<MyField> dimensionZet;
    private List<MyField> dimensionIks;
    private List<MyField> dimensionIgrek;
    //
    private Map<String, MyField> mapOfMeasureField;
    private Map<String, Map<String, Object>> componentMap;
    private Converter zetAxisConverter;
    //
    protected MyMap rowObject = new MyMap();
    protected Map cellControl = new HashMap();
    protected Map<CellMultiDimensionKey, List<CustomOlapHashMap>> snapshotMapMultiDimension;
    protected Object snapshotNullHandler;

    public void removeFilterAttr(String key) {
        getFilter().remove(key);
    }

    /**
     *
     * @param myForm
     * @param docItems
     * @param myField
     * @return
     * @throws Exception
     */
    private List<MyField> createDimensionFieldItems(MyForm myForm, MyField myField) throws MongoOrmFailedException {

        StringBuilder sb = new StringBuilder();
        sb.append(myForm.getKey());
        sb.append(" : ");
        sb.append(myField.getKey());
        sb.append(" : ");
        sb.append(myField.getItemsAsMyItems());
        sb.append(" : ");
        sb.append(getFilter());
        sb.append(" : ");
        sb.append(loginController.getRolesAsSet());
        String cacheKey = org.apache.commons.codec.digest.DigestUtils.sha256Hex(sb.toString());

        List<MyField> listOfMap = appScopeSrvCtrl.cacheAndGetDimensionItems(cacheKey);

        // if (listOfMap != null) {
        //  return listOfMap;
        //}
        listOfMap = new ArrayList<>();

        myField.createSelectItems(getFilter(), rowObject,
                loginController.getRoleMap(),
                loginController.getLoggedUserDetail(), true);

        if (myField.getItemsAsMyItems() == null) {
            listOfMap.add(myField);
            appScopeSrvCtrl.putCacheDimensionItems(cacheKey, listOfMap);
            return listOfMap;
        }

        Document enrichSearchObject = new Document(getFilter());

        Object collectionName = myField.getItemsAsMyItems().getTable();

        if (collectionName instanceof Code) {
            Document commandResult = mongoDbUtil
                    .runCommand(myForm.getDb(), ((Code) collectionName).getCode(), enrichSearchObject, null);
            collectionName = commandResult.get(RETVAL);
        }

        List<Document> cursor = mongoDbUtil
                .findProjectLookup(myForm.getDb(), (String) collectionName,
                        mongoDbUtil.expandQuery(myField.getItemsAsMyItems().getQuery(), getFilter()),
                        myField.getItemsAsMyItems().getSort(),
                        myField.getItemsAsMyItems().getLimit(),
                        myField.getItemsAsMyItems().getQueryProjection(),
                        myField.getItemsAsMyItems().getLookup(),
                        myField.getItemsAsMyItems().getResultProjection());

        List<String> listOfCode = new ArrayList<>();

        for (Document dbObject : cursor) {
            String code = (String) dbObject.get(CODE);

            if (FORM_DIMENSION.equals(myField.getNdType()) && (code == null || code.isEmpty() || listOfCode.contains(code))) {
                throw new MongoOrmFailedException(String.format("<br/>A dimesion field does not have a \"code\" on its record list or duplicate it."
                        + "<br/>Detail : fieldKey=%s, data=%s", myField.getKey(), dbObject.get(NAME)));
            } else {
                listOfCode.add(code);
            }

            dbObject.put(FIELD, myField.getKey());//will be used during save two to one dimension phase
            dbObject.put(FORM_KEY, myField.getKey());//will be used during save two to one dimension phase
            dbObject.put(ND_AXIS, myField.getNdAxis());//will be used during save two to one dimension phase
            dbObject.put(ND_TYPE, myField.getNdType());//will be used during save two to one dimension phase

            StringBuilder name = new StringBuilder();
            for (String keyy : myField.getItemsAsMyItems().getView()) {
                name.append("\t|\t");
                if (dbObject.get(keyy) instanceof Number) {
                    name.append(((Number) dbObject.get(keyy)).intValue());
                } else {
                    name.append(dbObject.get(keyy));
                }
            }

            dbObject.put(NAME, name.substring(3));

            listOfMap.add(new MyField.Builder(dbObject)
                    .maskId()
                    .maskField()
                    .maskKey()
                    .maskNdTypeAndNdAxis()
                    .maskOrders()
                    .maskCode()
                    .maskName()
                    .build());

        }

        appScopeSrvCtrl.putCacheDimensionItems(cacheKey, listOfMap);

        return listOfMap;
    }

    /**
     *
     * @param myForm
     */
    /*   DONT REMOVE  */
    public void refreshRowData(MyForm myForm) {

        Document dbo = mongoDbUtil.findOne(myForm.getDb(), myForm.getTable(), new Document(getFilter()).append(FORMS, FIELDS_ROW));

        if (rowObject
                == null) {
            rowObject = new MyMap();
        } else {
            rowObject.clear();//"continent => {$oid:'4e8c63ad51f9ef80a7ce30b7'}"
        }
        if (dbo
                != null) {
            rowObject.putAll(dbo);

//            if ("001".equals(rowObject.get("dataStatus"))) { // FIXME move to engine-config pattern
//                setEditable(false);
//            } else if (this instanceof PivotModifierCtrl) {
//                setEditable(Boolean.TRUE.equals(getDependOnSelectedFormRenderedMap().get(SAVE)));
//            }
        }
    }

    /**
     *
     * @param myField
     * @return
     */
    public List<SelectItem> createItems(MyField myField) {
        List<SelectItem> listSelectItems = new ArrayList();

        if (MyItems.ItemType.list.equals(myField.getItemsAsMyItems().getItemType())) {
            List docItems = myField.getItemsAsMyItems().getList();
            listSelectItems.add(new SelectItem(SelectOneStringConverter.NULL_VALUE, SELECT_PLEASE));
            for (Object itemValue : docItems) {
                if (itemValue instanceof Document) {
                    listSelectItems.add(new SelectItem(((Document) itemValue).get(CODE), (String) ((Document) itemValue).get(NAME)));
                }
            }
        }
        return listSelectItems;
    }

    /**
     *
     * @param inodeMyForm
     */
    public void prepareJsfComponentMap(MyForm inodeMyForm) {

        componentMap = new HashMap();

        for (String key : inodeMyForm.getFieldsRowKeys()) {

            MyField fieldStructure = inodeMyForm.getFieldRow(key);

            List<SelectItem> items = createItems(fieldStructure);

            Converter myconverter = null;
            String converterFormat = fieldStructure.getConverterFormat();
            Boolean calculateOnCrudView = fieldStructure.getCalculateOnCrudView();
            Boolean calculateOnSave = fieldStructure.getCalculateOnSave();

            if (fieldStructure.getMyconverter() != null) {
                if (fieldStructure.getMyconverter() instanceof MoneyConverter) {
                    myconverter = new MoneyConverter();
                } else if (fieldStructure.getMyconverter() instanceof NumberConverter) {
                    myconverter = new NumberConverter();
                } else if (fieldStructure.getMyconverter() instanceof SelectOneDBObjectConverter) {
                    myconverter = new SelectOneObjectIdConverter();
                    Map<String, String> itemMap = new HashMap();
                    for (SelectItem selectItem : items) {
                        itemMap.put(selectItem.getValue().toString(), selectItem.getLabel());
                    }
                    ((SelectOneObjectIdConverter) myconverter).setItemMap(itemMap);
                } else if (fieldStructure.getMyconverter() instanceof SelectOneStringConverter) {
                    myconverter = new SelectOneStringConverter();
                } else if (fieldStructure.getMyconverter() instanceof JsFunctionConverter) {
                    myconverter = new JsFunctionConverter();
                } else if (fieldStructure.getMyconverter() instanceof TelmanStringConverter) {
                    myconverter = new TelmanStringConverter();
                }
            }

            Object rendered = fieldStructure.isRendered();

            if (Boolean.FALSE.equals(rendered)) {
                continue;
            }
            if (rendered instanceof Document && !loginController.isUserInRole(((Document) rendered).get(ON_USER_ROLE))) {
                continue;
            }

            componentMap.put(key, new HashMap());
            componentMap.get(key).put(LABEL, fieldStructure.getName());
            if (fieldStructure.getStyle() != null) {
                componentMap.get(key).put(STYLE, fieldStructure.getStyle());
            }

            componentMap.get(key).put(REQUIRED, Boolean.TRUE.equals(fieldStructure.isRequired()));
            componentMap.get(key).put(RENDERED, Boolean.TRUE.equals(fieldStructure.isRendered()));
            componentMap.get(key).put(COMPONENTTYPE, fieldStructure.getComponentType());
            componentMap.get(key).put(ITEMS, items);
            componentMap.get(key).put(CONVERTER, myconverter);
            componentMap.get(key).put(CONVERTER_INSTANCE, fieldStructure.getConverterInstance());
            componentMap.get(key).put(CONVERTER_FORMAT, converterFormat);
            componentMap.get(key).put(CALCULATE_ON_SAVE, calculateOnSave);
            componentMap.get(key).put(CALCULATE_ON_CRUD_VIEW, calculateOnCrudView);
            componentMap.get(key).put(IS_AJAX, Boolean.TRUE.equals(fieldStructure.isAjax()));
            if (fieldStructure.getAjaxEffectedKeys() != null) {
                String ajaxUpdate = "";
                for (String ajaxKey : fieldStructure.getAjaxEffectedKeys()) {
                    ajaxUpdate += ":crud2dForm:label_".concat(ajaxKey).concat(COMMA);
                    ajaxUpdate += ":crud2dForm:component_".concat(ajaxKey).concat(COMMA);
                }
                componentMap.get(key).put(AJAX_UPDATE, ajaxUpdate);
            }

            componentMap.get(key).put(READONLY, true);
            componentMap.get(key).put(MAX_VALUE, fieldStructure.getMaxValue());
            componentMap.get(key).put(SHOULD_CHECK_NEGOTIF, fieldStructure.getShouldCheckNegative());

        }
    }

    /**
     *
     * @param myForm
     * @throws tr.org.tspb.exceptions.MongoOrmFailedException
     */
    protected void createDimensionIksIgrek(MyForm myForm) throws MongoOrmFailedException {

        mapOfMeasureField = new HashMap<>();
        /**
         * DONT REMOVE. FIXME planned for the future(SOON) should be nice for
         * nice coding
         */

        dimensionIks = new ArrayList<>();
        dimensionIgrek = new ArrayList<>();

        for (Map.Entry<String, MyField> entry : myForm.getFields().entrySet()) {

            MyField myField = entry.getValue();

            if (!myField.isRendered()) {
                continue;
            }

            if (!loginController.isUserInRole(myField.getAccesscontrol())) {
                continue;
            }

            if (MEASURE.equals(myField.getNdType())) {
                mapOfMeasureField.put(myField.getKey(), myField);
            }

            if (myField.getNdAxis() != null) {
                switch (myField.getNdAxis()) {
                    case X_IKS:
                        dimensionIks.addAll(createDimensionFieldItems(myForm, myField));
                        break;
                    case Y_IGREK:
                        dimensionIgrek.addAll(createDimensionFieldItems(myForm, myField));
                        break;
                }
            }
        }

        Collections.sort(dimensionIks, new MyFieldComparator());
        Collections.sort(dimensionIgrek, new MyFieldComparator());

    }

    /**
     *
     * @param myForm
     */
    protected void createDimensionZet(MyForm myForm) throws FormConfigException {

        dimensionZet = new ArrayList<>();

        for (String key : myForm.getZetDimension()) {

            MyField myField = myForm.getField(key);

            if (!myField.isRendered()) {
                continue;
            }

            if (!loginController.isUserInRole(myField.getAccesscontrol())) {
                continue;
            }

            String field = myField.getField();

            Document map = new Document();

            switch (myField.getItemsAsMyItems().getItemType()) {
                case list:
                    List<SelectItem> listDimensionZAXIS = new ArrayList<>();
                    Iterator it = ((Iterable<Object>) myField.getItemsAsMyItems().getList()).iterator();
                    while (it.hasNext()) {
                        Object object = it.next();
                        listDimensionZAXIS.add(new SelectItem(object, (String) object));
                    }

                    map.put(FORM_KEY, key);
                    map.put(FIELD, field);
                    map.put(SHORT_NAME, myForm.getField(key).getShortName());
                    map.put(ND_AXIS, myForm.getField(key).getNdAxis());
                    map.put(LIST_OF_VALUES, listDimensionZAXIS);
                    map.put(MY_CONVERTER, new SelectOneStringConverter());

                    dimensionZet.add(ogmCreator
                            .getMyFieldPivot(myForm, new Document(map), getFilter(),
                                    loginController.getRoleMap(), loginController.getLoggedUserDetail()));
                    break;
                case doc:

                    StringBuilder sb = new StringBuilder();
                    sb.append(myForm.getKey());
                    sb.append(" : ");
                    sb.append(myField.getKey());
                    sb.append(" : ");
                    sb.append(myField.getField());
                    sb.append(" : ");
                    sb.append(myField.getItemsAsMyItems());
                    sb.append(" : ");
                    sb.append(getFilter());
                    sb.append(" : ");
                    sb.append(loginController.getRolesAsSet());
                    String cacheKey = org.apache.commons.codec.digest.DigestUtils.sha256Hex(sb.toString());

                    List<SelectItem> zetDimensionItems = appScopeSrvCtrl.getCacheZetDimensionItems(cacheKey);

                    if (zetDimensionItems == null) {
                        MyItems myItems = myField.getItemsAsMyItems();

                        List<Document> cursor = filterService
                                .createDocuments(myItems, myForm, getFilter(), this instanceof PivotViewerCtrl);

                        Document modifiedView = new Document();

                        for (String viewKey : myField.getItemsAsMyItems().getView()) {

                            modifiedView.put(viewKey, true);

                        }

                        Document sortObject = myField.getItemsAsMyItems().getSort();
                        String locale = myField.getItemsAsMyItems().getLocale();

                        String labelStringFormat = myField.getItemsAsMyItems().getLabelStringFormat();

                        zetDimensionItems = createSelectItems(cursor, modifiedView, sortObject, labelStringFormat, true, locale);

                        appScopeSrvCtrl.putCacheZetDimensionItems(cacheKey, zetDimensionItems);
                    }

                    if (myForm.getField(key).getMyconverter() != null) {
                        if (myForm.getField(key).getMyconverter() instanceof SelectOneObjectIdConverter) {
                            zetAxisConverter = new SelectOneObjectIdConverter();
                        } else if (myForm.getField(key).getMyconverter() instanceof NumberConverter) {
                            zetAxisConverter = new NumberConverter();
                        } else if (myForm.getField(key).getMyconverter() instanceof MoneyConverter) {
                            zetAxisConverter = new MoneyConverter();
                        }
                    }

                    map.put(FORM_KEY, key);
                    map.put(FIELD, field);
                    map.put(SHORT_NAME, myForm.getField(key).getShortName());
                    map.put(ND_AXIS, myForm.getField(key).getNdAxis());
                    map.put(LIST_OF_VALUES, zetDimensionItems);
                    map.put(MY_CONVERTER, zetAxisConverter);
                    dimensionZet.add(ogmCreator//
                            .getMyFieldPivot(myForm, new Document(map), getFilter(),
                                    loginController.getRoleMap(), loginController.getLoggedUserDetail()));
                    break;

            }

        }
    }

    /**
     *
     * @param cursor
     * @param viewObject
     * @param sortObject
     * @param labelStringFormat
     * @param all
     * @param locale
     * @return
     */
    private List<SelectItem> createSelectItems(List<Document> cursor, Document viewObject, Document sortObject, String labelStringFormat,
            boolean all, String locale) {

        List<SelectItem> selectItems = new ArrayList<>();
        List<Document> lists = new ArrayList<>();

        for (Document doc : cursor) {
            lists.add(doc);
        }

        if (sortObject == null || locale != null) {
            Collections.sort(lists, new Comparator() {
                Collator collator = Collator.getInstance(new Locale("tr", "TR"));

                @Override
                public int compare(Object t1, Object t2) {
                    String order1 = (String) ((Document) t1).get(NAME);
                    String order2 = (String) ((Document) t2).get(NAME);
                    return SELECT_PLEASE.equals(order1) ? 0 : collator.compare(order1, order2);
                }
            });
        }

        if (all) {
            selectItems.add(new SelectItem(SelectOneObjectIdConverter.NULL_VALUE, SELECT_PLEASE));
        }

        for (Document object : lists) {
            //FIXME Proivide this abilitiy with converter
            //select One get the to String as value
            StringBuilder format = new StringBuilder("%s");
            Iterator<String> iterator = viewObject.keySet().iterator();

            List toBeVieweeValues = new ArrayList();

            while (iterator.hasNext()) {

                String field = iterator.next();
                Object fieldValue = object.get(field);
                if (fieldValue instanceof Date) {
                    toBeVieweeValues.add(SIMPLE_DATE_FORMAT__0.format(fieldValue));
                } else {
                    toBeVieweeValues.add(fieldValue == null ? "" : fieldValue);
                }
                if (iterator.hasNext()) {
                    format.append(" - %6s");
                }
            }
            String label = "could not be set";
            try {
                label = String.format(labelStringFormat == null ? format.toString() : labelStringFormat, toBeVieweeValues.toArray());
            } catch (Exception ex) {
                logger.error("stop here");
            }
            selectItems.add(new SelectItem(object.get(MONGO_ID), label));
        }
        return selectItems;
    }

    /**
     *
     * @param key
     * @return
     */
    public Object getSearchObjectValue(String key) {
        return getFilter().get(key);
    }

    /**
     *
     * @return
     */
    public List<MyField> getZetDimension() {
        return dimensionZet;
    }

    /**
     *
     * @param zetDimension
     */
    public void setZetDimension(List<MyField> zetDimension) {
        this.dimensionZet = zetDimension;
    }

    /**
     *
     * @return
     */
    public Converter getZetAxisConverter() {
        return zetAxisConverter;
    }

    /**
     *
     * @param zetAxisConverter
     */
    public void setZetAxisConverter(Converter zetAxisConverter) {
        this.zetAxisConverter = zetAxisConverter;
    }

    /**
     *
     * @return
     */
    public List<MyField> getIksDimension() {
        return dimensionIks;
    }

    /**
     *
     * @param dimensionIks
     */
    public void setIksDimension(List<MyField> dimensionIks) {
        this.dimensionIks = dimensionIks;
    }

    /**
     *
     * @return
     */
    public List<MyField> getIgrekDimension() {
        return dimensionIgrek;
    }

    /**
     *
     * @param dimensionIgrek
     */
    public void setIgrekDimension(List<MyField> dimensionIgrek) {
        this.dimensionIgrek = dimensionIgrek;
    }

    /**
     *
     * @return
     */
    public Map<String, Map<String, Object>> getComponentMap() {
        return Collections.unmodifiableMap(componentMap);
    }

    /**
     *
     * @return
     */
    public Map getRowObject() {
        return Collections.unmodifiableMap(rowObject);
    }

    /**
     *
     * @param rowObject
     */
    public void setRow(MyMap rowObject) {
        this.rowObject = rowObject;
    }

    /**
     *
     * @return
     */
    public Map<String, MyField> getMapOfMeasureField() {
        return mapOfMeasureField;
    }

}
