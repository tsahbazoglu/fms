package tr.org.tspb.service;

import static tr.org.tspb.constants.ProjectConstants.*;
import tr.org.tspb.pojo.MyCalcCoordinate;
import tr.org.tspb.util.stereotype.MyServices;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import net.sourceforge.jeval.EvaluationException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Code;
import tr.org.tspb.common.pojo.CellMultiDimensionKey;
import tr.org.tspb.common.qualifier.MyCtrlServiceQualifier;
import tr.org.tspb.common.util.CustomOlapHashMap;
import tr.org.tspb.converter.base.MoneyConverter;
import tr.org.tspb.converter.base.NumberConverter;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.MoreThenOneInListException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.exceptions.RecursiveLimitExceedException;
import tr.org.tspb.exceptions.UserException;
import tr.org.tspb.pojo.MyCalcDef;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyServices
public class CalcService extends CommonSrv {

    @Inject
    @MyCtrlServiceQualifier
    protected CtrlService ctrlService;

    private Map<Map, Map> mapCalculateCache;

    Map<Map, List<Document>> applicationSearchResults = new HashMap<>();
    private HashMap<Map, Map> snapshotMapCalculateCache = new HashMap<>();

    public Map createCalculateMapKey(Map searchedMap, String form, MyCalcCoordinate myCalcCoordinate) {
        Map cachedCalculatedCoordinateValueMap = new HashMap();
        cachedCalculatedCoordinateValueMap.putAll(searchedMap);
        cachedCalculatedCoordinateValueMap.put(FORMS, form);
        cachedCalculatedCoordinateValueMap.put(X_CODE, myCalcCoordinate.getXcode());
        cachedCalculatedCoordinateValueMap.put(Y_CODE, myCalcCoordinate.getYcode());
        return cachedCalculatedCoordinateValueMap;
    }

    public boolean doesCalcCacheContainKey(Map key) {
        return mapCalculateCache != null && mapCalculateCache.get(key) != null;
    }

    public void resetMapCalculateCache() {
        mapCalculateCache = new HashMap<>();
    }

    public void put(Map key, Map value) {
        mapCalculateCache.put(key, value);
    }

    public Map get(Map key) {
        return mapCalculateCache.get(key);
    }

    public void createPivotCalcData(Map<CellMultiDimensionKey, List<CustomOlapHashMap>> inputMap,
            boolean calculate, boolean mark, MyForm myForm, Map filter) throws UserException, EvaluationException, FormConfigException {

        for (String key : myForm.getZetDimension()) {
            if (!filter.containsKey(key)) {
                return;
            }
        }

        resetMapCalculateCache();

        if (myForm.getCalculateCollection() == null && myForm.getCalculateQuery() == null) {
            return;
        }

        Map calculateAreaSearchMap = new HashMap();

        Object calculateQuery = myForm.getCalculateQuery();

        if (calculateQuery == null || calculateQuery instanceof String) {
            calculateAreaSearchMap.put(FORM_DB, myForm.getDb());
            calculateAreaSearchMap.put(RELATIONS, myForm.getForm());
        }

        if (calculateQuery == null) {
            calculateAreaSearchMap.put(COLLECTION, myForm.getCalculateCollection());
        } else if (calculateQuery instanceof String) {
            calculateAreaSearchMap.put(COLLECTION, myForm.getCalculateQuery());
        } else if (calculateQuery instanceof Code) {
            Code code = (Code) calculateQuery;
            Document commandResult = mongoDbUtil.runCommand(myForm.getDb(), code.getCode(), filter, null);
            calculateAreaSearchMap.putAll(((Document) commandResult.get(RETVAL)));
        } else if (calculateQuery instanceof Document) {
            calculateAreaSearchMap.putAll(((Document) calculateQuery));
        }

        List<Document> calculateFormulasRelatedToForm;

        if (Boolean.TRUE.equals(calculateAreaSearchMap.get("cache"))) {
            calculateFormulasRelatedToForm = getApplicationSearchResults(calculateAreaSearchMap);
        } else {
            Document searchObj = new Document(calculateAreaSearchMap);
            String collection = (String) calculateAreaSearchMap.get(COLLECTION);
            searchObj.remove(FORM_DB);
            searchObj.remove(COLLECTION);
            searchObj.remove(SORT);
            searchObj.remove(LIMIT);
            searchObj.remove("cache");

            calculateFormulasRelatedToForm = mongoDbUtil.find(myForm.getDb(), collection, searchObj);

        }

        for (Document calcDBO : calculateFormulasRelatedToForm) {
            /**
             * The below commented code is stayed for test cases
             */
//            if (!Boolean.TRUE.equals(calculateDefinition.get(ACTIVE))) {
//                continue;
//            }
            MyCalcDef myCalcDef = new MyCalcDef(calcDBO);

            MyCalcCoordinate myCalcCoordinate = myCalcDef.getDroolsRuleCoordinate();

            if (myCalcCoordinate == null) {
                continue;
            }

            CellMultiDimensionKey cellMultiDimensionKey = new CellMultiDimensionKey();
            cellMultiDimensionKey.put(X_CODE, myCalcCoordinate.getXcode());
            cellMultiDimensionKey.put(Y_CODE, myCalcCoordinate.getYcode());

            List<CustomOlapHashMap> list = inputMap.get(cellMultiDimensionKey);

            if (list == null) {
                continue;
            }

            String style = myCalcDef.getStyle();

            CustomOlapHashMap cellValue = list.get(0);
            cellValue.setCalculate(true);//will be used during the save
            cellValue.setReadonly(true);
            cellValue.setRendered(true);
            cellValue.setStyle(style);
            cellValue.setCalculateFormulaId(myCalcDef.getId());//for easy debug via website

            String converter = myCalcDef.getConverter();
            switch (converter) {
                case CONVERTER_MONEY_CONVERTER:
                    cellValue.setConverter(new MoneyConverter());//will be used during the save
                    break;
                case CONVERTER_NUMBER_CONVERTER:
                case CONVERTER_INTEGER_CONVERTER:
                    cellValue.setConverter(new NumberConverter());
                    break;
                case "#.###.###,##":
                case "#.###.###,###":
                case "#.###.###,####":
                case "#.###.###,#####":
                    cellValue.setConverter(new NumberConverter());
                    cellValue.setUysformat(converter);
                    break;
                default:
                    break;
            }

            /**
             * THIS CHECK PREVENT THE CALCULATION ON ONLOAD PHASE AND BY THIS
             * PROVIDE PERFOMANS WHEN USER JUST OBSERVE THE PIVOT TABLE
             *
             * NO NEED TO RECALCULATE THE PRECALCULATED VALUES
             */
            if (calculate) {
                try {
                    Map cachedCalculatedCoordinateValueMap = createCalculateMapKey(filter,
                            myForm.getKey(), myCalcCoordinate);

                    if (!doesCalcCacheContainKey(cachedCalculatedCoordinateValueMap)) {
                        ctrlService.jevalCalculateAndStore(filter, myCalcDef, myForm.getForm(), 0, true, inputMap,
                                myForm, myForm.getMyProject().getConfigTable(), filter, myForm.getDb());
                    }

                    String measureKey = myCalcDef.getDroolsMeasureKey();
                    cellValue.setValue(get(cachedCalculatedCoordinateValueMap).get(measureKey));

                } catch (MongoOrmFailedException | MoreThenOneInListException | NullNotExpectedException | RecursiveLimitExceedException ex) {
                    logger.error("error occured", ex);
                    String errorMessage = myCalcDef.getName() + " : " + ex.getMessage() + "</br>";
                    throw new UserException("Hesaplma Esnasında Hata oluştu", errorMessage, ex);
                }
            }
        }
    }

    public void applyCalcCoordinates(Map<CellMultiDimensionKey, List<CustomOlapHashMap>> inputMap,
            MyForm myForm, Map filter) throws UserException {

        for (String key : myForm.getZetDimension()) {
            if (!filter.containsKey(key)) {
                return;
            }
        }

        resetMapCalculateCache();

        if (myForm.getCalculateCollection() == null && myForm.getCalculateQuery() == null) {
            return;
        }

        Map calculateAreaSearchMap = new HashMap();

        Object calculateQuery = myForm.getCalculateQuery();

        if (calculateQuery == null || calculateQuery instanceof String) {
            calculateAreaSearchMap.put(FORM_DB, myForm.getDb());
            calculateAreaSearchMap.put(RELATIONS, myForm.getForm());
        }

        if (calculateQuery == null) {
            calculateAreaSearchMap.put(COLLECTION, myForm.getCalculateCollection());
        } else if (calculateQuery instanceof String) {
            calculateAreaSearchMap.put(COLLECTION, myForm.getCalculateQuery());
        } else if (calculateQuery instanceof Code) {
            Code code = (Code) calculateQuery;
            Document commandResult = mongoDbUtil.runCommand(myForm.getDb(), code.getCode(), filter, null);
            calculateAreaSearchMap.putAll(((Document) commandResult.get(RETVAL)));
        } else if (calculateQuery instanceof Document) {
            calculateAreaSearchMap.putAll(((Document) calculateQuery));
        }

        List<Document> calculateFormulasRelatedToForm;

        if (Boolean.TRUE.equals(calculateAreaSearchMap.get("cache"))) {
            calculateFormulasRelatedToForm = getApplicationSearchResults(calculateAreaSearchMap);
        } else {
            Document searchObj = new Document(calculateAreaSearchMap);
            String collection = (String) calculateAreaSearchMap.get(COLLECTION);
            searchObj.remove(FORM_DB);
            searchObj.remove(COLLECTION);
            searchObj.remove(SORT);
            searchObj.remove(LIMIT);
            searchObj.remove("cache");

            calculateFormulasRelatedToForm = mongoDbUtil.find(myForm.getDb(), collection, searchObj);

        }

        for (Document calcDBO : calculateFormulasRelatedToForm) {

            MyCalcDef myCalcDef = new MyCalcDef(calcDBO);

            MyCalcCoordinate myCalcCoordinate = myCalcDef.getDroolsRuleCoordinate();

            if (myCalcCoordinate == null) {
                continue;
            }

            CellMultiDimensionKey cellMultiDimensionKey = new CellMultiDimensionKey();
            cellMultiDimensionKey.put(X_CODE, myCalcCoordinate.getXcode());
            cellMultiDimensionKey.put(Y_CODE, myCalcCoordinate.getYcode());

            List<CustomOlapHashMap> list = inputMap.get(cellMultiDimensionKey);

            if (list == null) {
                continue;
            }

            String style = myCalcDef.getStyle();

            CustomOlapHashMap cellValue = list.get(0);
            cellValue.setCalculate(true);//will be used during the save
            cellValue.setReadonly(true);
            cellValue.setRendered(true);
            cellValue.setStyle(style);
            cellValue.setCalculateFormulaId(myCalcDef.getId());//for easy debug via website

            String converter = myCalcDef.getConverter();
            switch (converter) {
                case CONVERTER_MONEY_CONVERTER:
                    cellValue.setConverter(new MoneyConverter());//will be used during the save
                    break;
                case CONVERTER_NUMBER_CONVERTER:
                case CONVERTER_INTEGER_CONVERTER:
                    cellValue.setConverter(new NumberConverter());
                    break;
                case "#.###.###,##":
                case "#.###.###,###":
                case "#.###.###,####":
                case "#.###.###,#####":
                    cellValue.setConverter(new NumberConverter());
                    cellValue.setUysformat(converter);
                    break;
                default:
                    break;
            }
        }
    }

    public void provideSnapshotCalculatedValues(
            Map<CellMultiDimensionKey, List<CustomOlapHashMap>> inputMap,
            Map<CellMultiDimensionKey, List<CustomOlapHashMap>> mapMultiDimension,
            MyForm myForm, Map filter)
            throws UserException {

        if (true) {
            return;
        }

        snapshotMapCalculateCache = new HashMap<>();

        if (myForm.getSnapshotCollection() == null) {
            return;
        }

        Map calculateAreaSearchMap = new HashMap();
        calculateAreaSearchMap.put(FORM_DB, myForm.getDb());
        calculateAreaSearchMap.put(COLLECTION, myForm.getSnapshotCollection());
        calculateAreaSearchMap.put(RELATIONS, myForm.getForm());

        List<Document> calculateArea = getApplicationSearchResults(calculateAreaSearchMap);

        for (Document calcDbo : calculateArea) {

            MyCalcDef calculateDefinition = new MyCalcDef(calcDbo);

            /**
             * The below commented code is stayed for test cases
             */
//            if (!Boolean.TRUE.equals(calculateDefinition.get(ACTIVE))) {
//                continue;
//            }
            MyCalcCoordinate calculatedCellCoordinates = calculateDefinition.getDroolsRuleCoordinate();

            if (calculatedCellCoordinates == null) {
                continue;
            }

            CellMultiDimensionKey cellMultiDimensionKey = new CellMultiDimensionKey();
            cellMultiDimensionKey.put(X_CODE, calculatedCellCoordinates.getXcode());
            cellMultiDimensionKey.put(Y_CODE, calculatedCellCoordinates.getYcode());

            List list = inputMap.get(cellMultiDimensionKey);

            if (list == null) {
                continue;
            }

            String style = (String) calculateDefinition.getStyle();

            CustomOlapHashMap cellValue;

            cellValue = (CustomOlapHashMap) list.get(0);
            cellValue.setCalculate(true);//will be used during the save
            cellValue.setReadonly(true);
            cellValue.setRendered(true);
            cellValue.setStyle(style);
            //           }

            cellValue.setCalculateFormulaId(calculateDefinition.getId());//for easy debug via website

            String converter = (String) calculateDefinition.getConverter();

            if (CONVERTER_MONEY_CONVERTER.equals(converter)) {
                cellValue.setConverter(new MoneyConverter());//will be used during the save
            } else if (CONVERTER_NUMBER_CONVERTER.equals(converter) || CONVERTER_INTEGER_CONVERTER.equals(converter)) {
                cellValue.setConverter(new NumberConverter());
            }

            try {
                Map cachedCalculatedCoordinateValueMap = new HashMap();
                cachedCalculatedCoordinateValueMap.put(myForm.getLoginFkField(), filter.get(myForm.getLoginFkField()));
                cachedCalculatedCoordinateValueMap.put(PERIOD, filter.get(PERIOD));
                cachedCalculatedCoordinateValueMap.put(FORMS, myForm.getKey());
                cachedCalculatedCoordinateValueMap.putAll(cellMultiDimensionKey);
                if (snapshotMapCalculateCache == null || snapshotMapCalculateCache.get(cachedCalculatedCoordinateValueMap) == null) {
                    ctrlService.jevalCalculateAndStore(filter, calculateDefinition, myForm.getForm(), 0,
                            true, mapMultiDimension, myForm, myForm.getMyProject().getConfigTable(), filter, myForm.getDb());
                }

                String measureKey = (String) calculateDefinition.getDroolsMeasureKey();
                cellValue.setValue(snapshotMapCalculateCache.get(cachedCalculatedCoordinateValueMap).get(measureKey));

            } catch (Exception ex) {
                logger.error("error occured", ex);
                String errorMessage = calculateDefinition.getName() + " : " + ex.getMessage() + "</br>";
                throw new UserException("Hesaplma Esnasında Hata oluştu", errorMessage, ex);
            }
        }

//        System.out.println(i + "   " + j);
    }

    private List<Document> getApplicationSearchResults(Map search) {
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

    private final ScriptEngineManager mgr = new ScriptEngineManager();
    private final ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");

    public Object calculateValue(Map crudObject, String fieldKey, FacesContext context) {

        if (crudObject == null || crudObject.isEmpty()) {
            return null;
        }

        MyForm myFrom = (MyForm) context.getExternalContext().getSessionMap().get(SESSION_ATTR_SELECTED_FORM);

        MyField field = myFrom.getField(fieldKey);

        Code calculateOnClient = field.getCalculateOnClient();

        String calculateEngine = field.getCalculateEngine();

        Document b = new Document(crudObject);

        try {
            Object value;
            if ("clientSideJS".equals(calculateEngine) && calculateOnClient != null) {
                calculateOnClient = new Code(calculateOnClient.getCode().replace(DIEZ, DOLAR));
                // String jsScriptString = "calculate=" + calculateOnClient.toString();
                String jsScriptString = "calculate=" + calculateOnClient.getCode();
                jsEngine.eval(jsScriptString);
                Invocable inv = (Invocable) jsEngine;
                value = inv.invokeFunction("calculate", b.toJson());
            } else {
                Code code = field.getCalculate();
                b.put("calculate", code);

                String codeString = "function(bsonObject){return  bsonObject.calculate()}";
                String db = myFrom.getDb();

                Document commandResult = mongoDbUtil.runCommand(db, codeString, b);

                value = commandResult.get("retval");
            }

            return value;
        } catch (Exception ex) {
            logger.error("error occured", ex);
        }
        return null;
    }

}
