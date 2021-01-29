package tr.org.tspb.service;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import static tr.org.tspb.constants.ProjectConstants.*;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.MoreThenOneInListException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.exceptions.RecursiveLimitExceedException;
import tr.org.tspb.common.util.CustomOlapHashMap;
import tr.org.tspb.pojo.MyCalcCoordinate;
import tr.org.tspb.pojo.MyCalcDef;
import tr.org.tspb.pojo.MyCalcVar;
import tr.org.tspb.pojo.MyConstraintFormula;
import tr.org.tspb.pojo.MyConstraintVariable;
import tr.org.tspb.pojo.MyControlResult;
import tr.org.tspb.common.pojo.CellMultiDimensionKey;
import tr.org.tspb.common.qualifier.MyCtrlServiceQualifier;
import tr.org.tspb.util.stereotype.MyServices;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.inject.Inject;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import org.bson.Document;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import tr.org.tspb.common.services.AppScopeSrvCtrl;
import tr.org.tspb.common.services.LoginController;
import tr.org.tspb.constants.ProjectConstants;
import tr.org.tspb.converter.base.MoneyConverter;
import tr.org.tspb.converter.base.NumberConverter;
import tr.org.tspb.converter.base.SelectOneObjectIdConverter;
import tr.org.tspb.converter.base.TelmanStringConverter;
import tr.org.tspb.converter.base.UysStringConverter;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyServices
@MyCtrlServiceQualifier
public class CtrlService extends CommonSrv {

    @Inject
    private AppScopeSrvCtrl appScopeSrvCtrl;

    @Inject
    private CalcService calcService;

    @Inject
    @MyLoginQualifier
    private LoginController loginController;

    @Inject
    private MapReduceService mapReduceService;

    @Inject
    private DataService dataService;

    @Inject
    private FormService formService;

    private static final String FAIL_LIST = "failList";
    private static final String SUCCESS_LIST = "successList";

    private static final String SORT_KEY = "sortKey";
    private static final String CHECK_INDEX = "checkIndex";

    private final ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("JavaScript");

    private String configCollection;

    public void init(String configCollection) {
        this.configCollection = configCollection;

    }

    public List<Map> runConstraintCrossCheck(
            MyConstraintFormula myConstraintFormula,
            boolean isInternalCheck,
            Object[] parameters,
            Map<CellMultiDimensionKey, List<CustomOlapHashMap>> mapMultiDimension,
            Document filter)
            throws ScriptException, NoSuchMethodException, NullNotExpectedException, MongoOrmFailedException, FormConfigException, ParseException, MoreThenOneInListException, RecursiveLimitExceedException, EvaluationException {

        List<Map> resultListOfMap = new ArrayList<>();

        if (MyConstraintFormula.EngineType.MONGODB_FUNCTION.equals(myConstraintFormula.getEngineType())) {

            Object[] jsParameters = new Object[]{filter, loginController.getRolesAsSet()};

            if (parameters != null) {
                jsParameters = parameters;
            }

            Map resultMap = new HashMap();

            Document commandResult = mongoDbUtil
                    .runCommand(formService.getMyForm().getDb(), myConstraintFormula.getControlFunction(), jsParameters);

            Document returnObject = (Document) commandResult.get(RETVAL);
            resultMap.put(RESULT, returnObject.get(RESULT));
            resultMap.put(EXPRESSION, returnObject.get(EXPRESSION));
            resultListOfMap.add(resultMap);
            return resultListOfMap;
        }

        Map<String, Map> retrivedVariables = new HashMap<>();

        for (MyConstraintVariable myConstraintVariable : myConstraintFormula.getVariables()) {
            retrivedVariables.put(myConstraintVariable.getKey(),
                    retriveValueByCoordinate(myConstraintVariable,
                            isInternalCheck,
                            appScopeSrvCtrl.getDatabankRecalculateOnControl(),
                            mapMultiDimension, filter));
        }

        if (MyConstraintFormula.EngineType.JEVAL.equals(myConstraintFormula.getEngineType())) {

            for (Object checkExpression : myConstraintFormula.getCheckList()) {
                String jevalExpression = (String) checkExpression;
                String customNicePresenter = (String) checkExpression;

                for (Map.Entry<String, Map> entry : retrivedVariables.entrySet()) {
                    Object present = entry.getValue().get(PRESENT);
                    Object value = entry.getValue().get(VALUE);

                    if (present == null || present instanceof String && ((String) present).isEmpty()) {
                        present = "boş";
                    }
                    if (value == null || value instanceof String && ((String) value).isEmpty()) {
                        value = "0";
                    }

                    customNicePresenter = customNicePresenter.replaceAll(entry.getKey(), present.toString());
                    jevalExpression = jevalExpression.replaceAll(entry.getKey(), "(" + value.toString() + ")");
                }

                Map resultMap = new HashMap();
                Evaluator evaluator = new Evaluator();
                resultMap.put(RESULT, evaluator.getBooleanResult(jevalExpression));
                resultMap.put(EXPRESSION, customNicePresenter);

                resultListOfMap.add(resultMap);
            }
        } else if (MyConstraintFormula.EngineType.JAVA_SCRIPT.equals(myConstraintFormula.getEngineType())) {
            for (Object checkExpression : myConstraintFormula.getCheckList()) {
                String formula = (String) checkExpression;
                String customNicePresenter = (String) checkExpression;
                Map sessionCacheRetrivedVariables = new HashMap();

                for (MyConstraintVariable myConstraintVariable : myConstraintFormula.getVariables()) {

                    Object presentableFormOfValue = retrivedVariables.get(myConstraintVariable.getKey()).get(PRESENT);

                    if (presentableFormOfValue == null
                            || (presentableFormOfValue instanceof String) && ((String) presentableFormOfValue).isEmpty()) {
                        presentableFormOfValue = "boş";
                    }

                    customNicePresenter = customNicePresenter.replaceAll(myConstraintVariable.getKey(), presentableFormOfValue.toString());

                    String param = myConstraintVariable.getKey();
                    Object value = retrivedVariables.get(myConstraintVariable.getKey()).get(VALUE);

                    if (value instanceof Long) {
                        // https://bugs.openjdk.java.net/browse/JDK-8161665
                        value = value.toString();
                        sessionCacheRetrivedVariables.put(param, value == null ? 0 : value);
                        formula = formula.replaceAll(param, String.format("Number(input.%s)", param));
                    } else {
                        sessionCacheRetrivedVariables.put(myConstraintVariable.getKey(), value == null ? 0 : value);
                        formula = formula.replaceAll(param, "input.".concat(param));
                    }

                }

                StringBuilder javaScriptFunction = new StringBuilder();

                javaScriptFunction.append("function constraint(input){");
                //javaScriptFunction.append("input = eval('('+input+')');");//IMPORTANAT
                if (!formula.contains("return")) {
                    javaScriptFunction.append("return ");
                }
                javaScriptFunction.append(formula);
                javaScriptFunction.append("}");

                jsEngine.eval(javaScriptFunction.toString());

                Invocable inv = (Invocable) jsEngine;

                Object result = inv.invokeFunction("constraint", sessionCacheRetrivedVariables);

                Map resultMap = new HashMap();
                resultMap.put(RESULT, result);
                resultMap.put(EXPRESSION, customNicePresenter);

                resultListOfMap.add(resultMap);
            }
        }
        return resultListOfMap;
    }

    private Map retriveValueByCoordinate(MyConstraintVariable myConstraintVariable,
            boolean isInternalCheck, boolean recalculate,
            Map<CellMultiDimensionKey, List<CustomOlapHashMap>> mapMultiDimension,
            Document filter)
            throws NullNotExpectedException, MongoOrmFailedException, FormConfigException,
            ParseException, MoreThenOneInListException, RecursiveLimitExceedException, EvaluationException {

        String collectionKey;
        String collection;
        String valueKey;
        String form;

        Map returnMap = new HashMap();

        if (myConstraintVariable.isCode()) {
            Document commandResult = mongoDbUtil
                    .runCommand(formService.getMyForm().getDb(), myConstraintVariable.getCode().getCode(), filter);

            Object value = commandResult.get(RETVAL);

            returnMap.put(VALUE, value == null ? null : Math.round(((Number) value).doubleValue() / 100));
            returnMap.put(PRESENT, value == null ? null : new MoneyConverter().getAsString(null, null, value));
            return returnMap;
        }

        Map cashedPersistenceKey = new HashMap();
        Map cashedPreCalculatedKey;

        valueKey = myConstraintVariable.getValueKey();
        /*
         * FIXME generalize period attr name. 
         * It should be admin managable accross config
         */
        Document elementDefinedPeriod = myConstraintVariable.getPeriod();

        form = myConstraintVariable.getForm();

        // FIXME Move to engine constraint formula validator
        if (form == null) {
            form = formService.getMyForm().getForm();
            if (form == null) {
                throw new NullNotExpectedException("form is not defined on variable and could not be retrived from the current state");
            }
        }

        MyForm formDef = appScopeSrvCtrl.getFormDefinitionByForm(configCollection, form, filter);
        MyField fieldDef = formDef.getField(valueKey);

        if (fieldDef == null) {
            throw new FormConfigException(valueKey.concat(" form field is resolved to null"));
        }

        collection = formDef.getTable();
        if (collection == null) {
            collection = formService.getMyForm().getTable();
        }

        collectionKey = formDef.getKey();
        if (collectionKey == null) {
            collectionKey = formService.getMyForm().getTable();
        }

        for (Map.Entry entry : filter.entrySet()) {
            if (formDef.getField((String) entry.getKey()) != null) {
                cashedPersistenceKey.put(entry.getKey(), entry.getValue());
            }
        }

        cashedPersistenceKey.put(FORMS, collectionKey);

        Object value = null;

        /**
         * we check period == null because when there is a compare with old
         * period we have to retrieve this value from db
         *
         * <pre>
         *
         * Question:
         * Should this cooridnate value to be found in precalculatedMatris ?
         *
         * Answer:
         * It depends on coordinate itself wich can be detected from
         * db search on "cached" dataBankCalculateFormulas
         * </pre>
         */
        if (elementDefinedPeriod != null) {
            ObjectId currentPeriodID = (ObjectId) cashedPersistenceKey.get(PERIOD);
            ObjectId currentTemplateID = (ObjectId) cashedPersistenceKey.get(TEMPLATE);

            String sortKey = (String) elementDefinedPeriod.get(SORT_KEY);
            Document prevPeriodDBObject = appScopeSrvCtrl.getPrevPeriod(formService.getMyForm().getDb(), sortKey, 1, currentPeriodID);

            if (prevPeriodDBObject != null) {
                cashedPersistenceKey.put(PERIOD, prevPeriodDBObject.get(MONGO_ID));
            }

            if (currentTemplateID != null) {
                ObjectId prevTemplateId = appScopeSrvCtrl.getPrevTemplate(formService.getMyForm().getDb(),
                        null, 1, currentTemplateID, currentPeriodID);
                cashedPersistenceKey.put(TEMPLATE, prevTemplateId);
            }
        }

        CellMultiDimensionKey cellMultiDimensionKey = new CellMultiDimensionKey();
        cellMultiDimensionKey.put(X_CODE, myConstraintVariable.getxCode());
        cellMultiDimensionKey.put(Y_CODE, myConstraintVariable.getyCode());

        MyCalcDef calculateDefinition = null;
        if (recalculate) {
            Map calculateFormulaSearchMap = new HashMap();
            calculateFormulaSearchMap.put(RELATIONS, form);
            calculateFormulaSearchMap.put(X_CODE, myConstraintVariable.getxCode());
            calculateFormulaSearchMap.put(Y_CODE, myConstraintVariable.getyCode());
            calculateFormulaSearchMap.putAll(cashedPersistenceKey);
            calculateDefinition = appScopeSrvCtrl.getCalculateFormula(calculateFormulaSearchMap);
        }

        boolean shouldbeRetrievedFromDB = myConstraintVariable.isShouldbeRetrievedFromDB();
        if (shouldbeRetrievedFromDB) {
            double sum = 0D;

            List<Document> cursor = mongoDbUtil.find(formService.getMyForm().getDb(), collection, new Document(cashedPersistenceKey));

            for (Document dBObject : cursor) {
                if (dBObject.get(valueKey) != null && !dBObject.get(valueKey).toString().isEmpty()) {
                    sum += Double.valueOf(dBObject.get(valueKey).toString());
                }
            }
            value = Double.valueOf(sum);
        } else if (recalculate && calculateDefinition != null && elementDefinedPeriod == null) {

            MyForm formulaRelatedFormDef = appScopeSrvCtrl
                    .getFormDefinitionByForm(configCollection, calculateDefinition.getRelations(), filter);

            Map search = new HashMap();
            for (Map.Entry entry : filter.entrySet()) {
                if (formulaRelatedFormDef.getField(entry.getKey().toString()) != null) {
                    search.put(entry.getKey(), entry.getValue());
                }
            }

            cashedPreCalculatedKey = calcService
                    .createCalculateMapKey(search, formulaRelatedFormDef.getKey(), new MyCalcCoordinate(myConstraintVariable));

            if (!calcService.doesCalcCacheContainKey(cashedPreCalculatedKey)) {
                jevalCalculateAndStore(search, calculateDefinition, form, 0, isInternalCheck, mapMultiDimension, formService.getMyForm(), configCollection, filter, formService.getMyForm().getDb());
            }
            value = calcService.get(cashedPreCalculatedKey).get(valueKey);

        } else if (formService.getMyForm().getKey().equals(collectionKey) && elementDefinedPeriod == null) {

            List<CustomOlapHashMap> list = mapMultiDimension.get(cellMultiDimensionKey);

            if (list != null) {
                for (CustomOlapHashMap customOlapHaspMap : list) {
                    value = customOlapHaspMap.getValue();
                }
            } else {
                /**
                 * FIXME show check the exists of cellMultiDimensionKey in
                 * selectForm field keys
                 *
                 * this is state when user do not have consolidated but
                 * calculation formula take a place here and try to find them
                 *
                 */
                value = null;
            }
        } else {

            Map matris = dataService.cacheAndGetPivotData(cashedPersistenceKey, formDef);

            if (matris.get(cellMultiDimensionKey) == null) {
                value = 0D;
            } else {
                value = ((Map) matris.get(cellMultiDimensionKey)).get(valueKey);
            }
        }

        if (value == null) {
            returnMap.put(VALUE, null);
            returnMap.put(PRESENT, null);
            if (fieldDef.getMyconverter() instanceof UysStringConverter) {
                returnMap.put(VALUE, "");
            }
        } else if (fieldDef.getMyconverter() instanceof MoneyConverter) {
            if (value instanceof String) {
                if (((String) value).trim().isEmpty()) {
                    value = null;
                } else {
                    try {
                        value = Double.valueOf((String) value);
                    } catch (NumberFormatException ex) {
                        returnMap.put(VALUE, value);
                        returnMap.put(PRESENT, value);
                        return returnMap;
                    }
                }
            }
            returnMap.put(VALUE, value == null ? null : Math.round(((Number) value).doubleValue() / 100));
            returnMap.put(PRESENT, value == null ? null : new MoneyConverter().getAsString(null, null, value));
        } else if (fieldDef.getMyconverter() instanceof NumberConverter) {

            Locale locale = new Locale("tr", "TR");
            NumberFormat numberFormat = NumberFormat.getInstance(locale);

            if ("integer".equals(fieldDef.getConverterInstance())) {
                numberFormat = NumberFormat.getInstance(locale);
            }

            if (value instanceof Number) {
                returnMap.put(VALUE, value);
                returnMap.put(PRESENT, numberFormat.format(value));
            } else if (value instanceof String && !((String) value).isEmpty()) {
                value = numberFormat.parse((String) value);
                returnMap.put(VALUE, value);
                returnMap.put(PRESENT, numberFormat.format(value));
            }
        } else {
            returnMap.put(VALUE, value);
            returnMap.put(PRESENT, value);
        }

        return returnMap;
    }

    public Map jevalCalculateAndStore(Map searchedMap, MyCalcDef myCalcDef,
            String initForm, int recursiveDeepSizeLimit, boolean isInternalCheck,
            Map<CellMultiDimensionKey, List<CustomOlapHashMap>> mapMultiDimension,
            MyForm selectedForm, String configCollection, Map searchObject, String db)
            throws NullNotExpectedException, MongoOrmFailedException, RecursiveLimitExceedException, MoreThenOneInListException, EvaluationException, FormConfigException {

        MyForm initMyForm = appScopeSrvCtrl.getFormDefinitionByForm(configCollection, initForm, searchObject);

        recursiveDeepSizeLimit++;
        // to prevent wrong circles
        if (recursiveDeepSizeLimit > 100) {
            throw new RecursiveLimitExceedException("recursiveDeepSizeLimit achived on formula : " + myCalcDef.getName());
        }

        MyCalcCoordinate myCalcCoordinate = myCalcDef.getDroolsRuleCoordinate();

        Map resultMap = new HashMap();

        if (MyCalcDef.CalcType.FUNCTION.equals(myCalcDef.getCalcType())) {

            Document commandResult = mongoDbUtil.runCommand(db, myCalcDef.getFuncCode().getCode(), searchObject);

            Object value = commandResult.get(RETVAL);
            resultMap.put(RESULT, value);
            resultMap.put(EXPRESSION, "refer to dataBankPortfolioSize.summmary");

        } else {

            StringBuilder jevalExpression = new StringBuilder();

            for (MyCalcVar myCalcVar : myCalcDef.getJevalList()) {

                if (MyCalcVar.VarType.OP.equals(myCalcVar.getVarType())) {
                    jevalExpression.append(myCalcVar.getOp());
                    continue;
                }

                if (MyCalcVar.VarType.CELL.equals(myCalcVar.getVarType())) {

                    Document elementDefinedPeriod = myCalcVar.getPeriod();
                    String valueKey = myCalcVar.getValueKey();
                    String elementForm = myCalcVar.getForm();

                    if (elementForm == null) {
                        elementForm = initForm;
                    }

                    /**
                     * BEGIN Recursive Approach
                     */
                    Map calculateFormulaSearchMap = new HashMap();
                    calculateFormulaSearchMap.put(RELATIONS, elementForm);
                    calculateFormulaSearchMap.put(X_CODE, myCalcVar.getxCode());
                    calculateFormulaSearchMap.put(Y_CODE, myCalcVar.getyCode());
                    calculateFormulaSearchMap.put(PERIOD, searchedMap.get(PERIOD));
                    calculateFormulaSearchMap.put(TEMPLATE, searchedMap.get(TEMPLATE));

                    MyCalcDef reCalculateDefinition = appScopeSrvCtrl.getCalculateFormula(calculateFormulaSearchMap);

                    if (reCalculateDefinition != null && elementDefinedPeriod == null) {
                        jevalCalculateAndStore(searchedMap, reCalculateDefinition, elementForm,
                                recursiveDeepSizeLimit, isInternalCheck, mapMultiDimension, selectedForm, configCollection, searchObject, db);
                    }

                    if ("developer".equals(myCalcVar.getLevel())) {

                        Document cacheKey = myCalcVar.getCacheKey();
                        String cacheValue = myCalcVar.getCacheValue();

                        Map searchCacheKey = new HashMap();

                        for (String key : cacheKey.keySet()) {
                            if (PERIOD.equals(key)) {
                                searchCacheKey.put(key, searchedMap.get(PERIOD));
                            } else if (selectedForm.getLoginFkField().equals(key)) {
                                searchCacheKey.put(key, searchedMap.get(selectedForm.getLoginFkField()));
                            } else {
                                searchCacheKey.put(key, cacheKey.get(key));
                            }
                        }

                        resultMap = new HashMap();

                        List cachedResult = appScopeSrvCtrl
                                .getSessionSearchCacheMap()
                                .get(searchCacheKey);

                        resultMap.put(RESULT, cachedResult == null ? "0" : ((Map) cachedResult.get(0)).get(cacheValue));
                        resultMap.put(EXPRESSION, jevalExpression.toString());
                        return resultMap;
                    }

                    MyForm elementFormDef = appScopeSrvCtrl.getFormDefinitionByForm(configCollection, elementForm, searchObject);

                    String collectionKey = elementFormDef.getKey();

                    if (valueKey == null) {
                        valueKey = myCalcDef.getDroolsMeasureKey();
                    }

                    Map cashedPersistenceKey = new HashMap(searchedMap);
                    cashedPersistenceKey.put(FORMS, collectionKey);

                    Map coordinate = new HashMap();
                    coordinate.put(X_CODE, myCalcVar.getxCode());
                    coordinate.put(Y_CODE, myCalcVar.getyCode());

                    if (elementDefinedPeriod != null) {
                        Document periodDBObject;
                        if (SelectOneObjectIdConverter.NULL_VALUE.equals(cashedPersistenceKey.get(PERIOD))) {
                            periodDBObject = null;
                        } else {
                            String sortKey = (String) elementDefinedPeriod.get(SORT_KEY);
                            periodDBObject = appScopeSrvCtrl.getLastQuaterOfPrevYear(db, sortKey, 1,
                                    (ObjectId) cashedPersistenceKey.get(PERIOD));
                        }

                        if (periodDBObject != null) {
                            cashedPersistenceKey.put(PERIOD, periodDBObject.get(MONGO_ID));
                        } else {
                            /**
                             * If there is no previous period record then we
                             * want to take the current
                             */
                            coordinate.put(Y_CODE, myCalcCoordinate.getYcode());
                            coordinate.put(X_CODE, myCalcCoordinate.getXcode());
                        }
                    }

                    Map cachedPreCalculatedKey = new HashMap(cashedPersistenceKey);
                    cachedPreCalculatedKey.putAll(coordinate);

                    if (calcService.doesCalcCacheContainKey(cachedPreCalculatedKey)) {
                        Object value = calcService.get(cachedPreCalculatedKey).get(valueKey);
                        if (value instanceof Double) {
                            double myValue = ((Number) value).doubleValue();
                            if (Double.isInfinite(myValue) || Double.isNaN(myValue)) {
                                value = 0D;
                            }
                        }
                        if (value == null || value instanceof String && ((String) value).isEmpty()) {
                            value = 0D;
                        }

                        jevalExpression.append("(").append(value).append(")");
                    } else {

                        Object value;

                        if (isInternalCheck && elementDefinedPeriod == null && selectedForm.getKey().equals(collectionKey)) {

                            CellMultiDimensionKey cellMultiDimensionKey = new CellMultiDimensionKey();

                            cellMultiDimensionKey.putAll(coordinate);
                            List<CustomOlapHashMap> list = mapMultiDimension.get(cellMultiDimensionKey);
                            if (list == null || list.isEmpty()) {
                                // FIXME should be 1 in case of *
                                value = "0";
                            } else {
                                value = list.get(0).getValue();
                                if (value == null || value instanceof String && ((String) value).isEmpty()) {
                                    value = 0D;
                                }
                            }
                        } else {
                            // create map reduce result

                            Map matris = dataService.cacheAndGetPivotData(cashedPersistenceKey, elementFormDef);

                            if (matris.get(coordinate) == null) {
                                // FIXME should be 1 in case of *
                                value = "0";
                            } else {

                                value = ((Map) matris.get(coordinate)).get(valueKey);

                                if (value == null || value instanceof String && ((String) value).isEmpty()) {
                                    value = 0D;
                                }
                            }
                        }
                        /**
                         * Parantezzis is used to prevent an error in case of
                         * negative amount
                         */
                        jevalExpression.append("(").append(value).append(")");
                    }

                }
            }

            Evaluator evaluator = new Evaluator();
            String result = evaluator.evaluate(jevalExpression.toString());
            Object value = result == null ? null : Double.valueOf(result);

            if (myCalcDef.isJevalReturnTypeBool()) {
                value = evaluator.getBooleanResult(jevalExpression.toString());
            }

            resultMap.put(RESULT, value);
            resultMap.put(EXPRESSION, jevalExpression);

        }

        Map cashedCalculatedCoordinateValueMap = calcService.createCalculateMapKey(searchedMap, initMyForm.getKey(), myCalcCoordinate);

        HashMap storedCalculatedValue = new HashMap();
        storedCalculatedValue.put(myCalcDef.getDroolsMeasureKey(), resultMap.get(RESULT));

        calcService.put(cashedCalculatedCoordinateValueMap, storedCalculatedValue);

        return resultMap;

    }

    public void crossCheck(Document filter) throws Exception {

        dataService.resetMapReduceCache();

        if (filter.get(formService.getMyForm().getLoginFkField()) == null) {
            throw new NullNotExpectedException("Üye seçilmedi");
        }
        if (filter.get(PERIOD) == null) {
            throw new NullNotExpectedException("Dönem seçilmedi");
        }
        if (formService.getMyForm().getField(TEMPLATE) != null && filter.get(TEMPLATE) == null) {
            throw new NullNotExpectedException("Şablon seçilmedi");
        }

        List<Map<String, Object>> onFlyList = constraintCrossCheckVersionThree(filter);

        Document index = new Document();
        index.put(formService.getMyForm().getLoginFkField(), 1);
        index.put(PERIOD, 1);
        if (formService.getMyForm().getField(TEMPLATE) != null) {
            index.put(TEMPLATE, 1);
        }

        index.put(TRANSFER_ORDER, 1);
        index.put(CHECK_INDEX, 1);

        Document search = new Document();
        search.append(formService.getMyForm().getLoginFkField(), filter.get(formService.getMyForm().getLoginFkField()));
        search.append(PERIOD, filter.get(PERIOD));
        if (formService.getMyForm().getField(TEMPLATE) != null) {
            search.append(TEMPLATE, filter.get(TEMPLATE));
        }

        search.append(formService.getMyForm().getLoginFkField(), loginController.getLoggedUserDetail().getDbo().getObjectId());

        UpdateOptions upsert = new UpdateOptions().upsert(true);

        for (Map map : onFlyList) {
            /*
                     We do this because map come from constraintformulas where member is null.
                     The constraintFormulas's member ObjectId is null,
                     because it had been updated by admin who dont have member account.
                     so we garant here the current member and period
             */
            map.remove(formService.getMyForm().getLoginFkField());
            map.remove(PERIOD);
            map.remove(TEMPLATE);

            search.append(TRANSFER_ORDER, map.get(TRANSFER_ORDER));
            // this covers the state when more than one chek is performed bu only one formul definition
            search.append(CHECK_INDEX, map.get(CHECK_INDEX));

            mongoDbUtil.createIndex(formService.getMyForm().getDb(), formService.getMyForm().getTable(), index);
            mongoDbUtil.updateMany(
                    formService.getMyForm(),
                    search,
                    new Document(map).append(FORMS, formService.getMyForm().getKey()),
                    upsert);
        }

    }

    private List<Map<String, Object>> constraintCrossCheckVersionThree(Document filter) throws Exception {

        // clean up search map.
        // over all cross check appaers to aal collections
        // but all colllection don have the fields that belong to calculateCollection
        Set<String> keySet = new HashSet(filter.keySet());

        for (String object : keySet) {
            if (Arrays.asList(//
                    PERIOD,//
                    formService.getMyForm().getLoginFkField(),//
                    TEMPLATE//
            ).contains(object)) {
                continue;
            }
            filter.remove(object);
        }

        List<Map<String, Object>> all = new ArrayList<>();

        MyForm constraintFormDef = appScopeSrvCtrl.getFormDefinitionByKey(configCollection, "constraint", filter);

        String overAllCheck = constraintFormDef.getMyNamedQueries().get("overAllCheck", String.class);

        /*
         overAllCheck: function(searchObject){
         month = db.common.findOne({
         _id:searchObject.period
         }).month;
         if(month == null){
         throw "month could not be found on search object period";
         }
         return {
         "validPeriods":month,
         "enabled":true
         }
         } 
         */
        overAllCheck = overAllCheck.replace(DIEZ, DOLAR);

        Document tempSearchObject = new Document();

        if (loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminRole())) {
            tempSearchObject.put(
                    formService.getMyForm().getLoginFkField(), filter.get(formService.getMyForm().getLoginFkField()));
            tempSearchObject.put(
                    PERIOD, filter.get(PERIOD));
            tempSearchObject.put(
                    TEMPLATE, filter.get(TEMPLATE));
        } else {
            tempSearchObject.put(
                    formService.getMyForm().getLoginFkField(), loginController.getLoggedUserDetail().getDbo().getObjectId());
            tempSearchObject.put(
                    PERIOD, filter.get(PERIOD));
            tempSearchObject.put(
                    TEMPLATE, filter.get(TEMPLATE));
        }

        Document commandResult = mongoDbUtil
                .runCommand(formService.getMyForm().getDb(), overAllCheck, tempSearchObject, null);

        Document constraintQuery = commandResult.get(RETVAL, Document.class);

        List<Document> constraintCursor = mongoDbUtil
                .find(constraintFormDef.getDb(), constraintFormDef.getTable(), constraintQuery, new Document(TRANSFER_ORDER, 1), null);

        boolean checkTestControl = false;//to accelarate followed loop
        List listOfTestControlNo = new ArrayList();

        if (appScopeSrvCtrl.getTestControlFormulaTransferOrders() != null //
                && !appScopeSrvCtrl.getTestControlFormulaTransferOrders().trim().isEmpty()) {
            String[] nolar = appScopeSrvCtrl.getTestControlFormulaTransferOrders().split(COMMA);

            for (String no : nolar) {
                listOfTestControlNo.add(Integer.parseInt(no));
                checkTestControl = true;
            }
        }

        for (Document next : constraintCursor) {

            MyConstraintFormula myConstraintFormula = new MyConstraintFormula(next);

            if ("BOS".equals(myConstraintFormula.getRelations())) {
                continue;
            }

            if (checkTestControl && !listOfTestControlNo.contains(((Number) myConstraintFormula.getTransferOrder()))) {
                continue;
            }

            all.addAll(new CheckConstraintTask(myConstraintFormula, filter).call());
        }
        /* */
        return all;
    }

    private class CheckConstraintTask implements Callable {

        private final MyConstraintFormula myConstraintFormula;
        private final Document filter;

        CheckConstraintTask(MyConstraintFormula myConstraintFormula, Document filter) {
            this.myConstraintFormula = myConstraintFormula;
            this.filter = filter;
        }

        @Override
        public List call() throws Exception {

            List<Map<String, Object>> all = new ArrayList<>();

            try {
                List<Map> resultListOfMap;

                resultListOfMap = runConstraintCrossCheck(myConstraintFormula, false, null, null, filter);

                int i = 0;
                for (Map map : resultListOfMap) {
                    myConstraintFormula.setControlResult(new MyControlResult(map));
                    Map row = new HashMap();
                    row.put(RESULT_TYPE, myConstraintFormula.getResultTypeCode());
                    row.put(EXPRESSION, myConstraintFormula.getControlResult().getExpression());
                    row.put(RESULT, myConstraintFormula.getControlResult().getControlResult());
                    row.put(TRANSFER_ORDER, myConstraintFormula.getTransferOrder());
                    row.put(DESCRIPTION, myConstraintFormula.getDescription());
                    row.put(NAME, myConstraintFormula.getName());
                    row.put(CHECK_INDEX, i);
                    row.put("constraintFormulaID", myConstraintFormula.getId());
                    all.add(row);
                    i++;
                }
            } catch (Exception e) {
                logger.error("error occured", e);
                throw new Exception(MessageFormat.format(MSG_FORMAT_CONTROL_FORMULA, myConstraintFormula.getTransferOrder(),
                        myConstraintFormula.getName(), myConstraintFormula.getEngineType()), e);
            }
            return all;
        }
    }

    public Map<String, List<MyConstraintFormula>> constraintCrossCheckVersionTwo(Document filter,
            Map<CellMultiDimensionKey, List<CustomOlapHashMap>> pivotData) throws Exception {

        Map<String, List<MyConstraintFormula>> returnMap = new HashMap();
        returnMap.put(SUCCESS_LIST, new ArrayList<>());
        returnMap.put(FAIL_LIST, new ArrayList<>());

        // Object constraint = MongodbUtil.getValue(selectedForm, CONSTRAINTS);
        // this should be retrieved from selectedNode structure
        Document costraintItems = formService.getMyForm().getConstraintItems();

        if (costraintItems == null) {
            return returnMap;
        }

        List<Document> constraintCursor = createConstraintCursor(costraintItems, filter);

        boolean checkTestControl = false;//to accelarate followed loop
        List listOfTestControlNo = new ArrayList();

        if (appScopeSrvCtrl.getTestControlFormulaTransferOrders() != null //
                && !appScopeSrvCtrl.getTestControlFormulaTransferOrders().trim().isEmpty()) {
            String[] nolar = appScopeSrvCtrl.getTestControlFormulaTransferOrders().split(COMMA);

            for (String no : nolar) {
                listOfTestControlNo.add(Integer.parseInt(no));
                checkTestControl = true;
            }
        }

        for (Document doc : constraintCursor) {
            MyConstraintFormula myConstraintFormula = new MyConstraintFormula(doc);

            try {

                if (checkTestControl
                        && !listOfTestControlNo.contains(myConstraintFormula.getTransferOrder())) {
                    continue;
                }

//                if (myConstraintFormula.getTransferOrder() != 80300) {
//                    continue;
//                }
                init(formService.getMyForm().getMyProject().getConfigTable());

                List<Map> resultListOfMap = runConstraintCrossCheck(myConstraintFormula, true, null, pivotData, filter);

                for (Map map : resultListOfMap) {

                    myConstraintFormula.setControlResult(new MyControlResult(map));
                    if (myConstraintFormula.getControlResult().isResult()) {
                        returnMap.get(SUCCESS_LIST).add(myConstraintFormula);
                    } else {
                        returnMap.get(FAIL_LIST).add(myConstraintFormula);
                    }
                }

            } catch (Exception ex) {
                throw new Exception(
                        MessageFormat.format("exception:{0}<br/>transferOrder:{1}<br/>name:{2}<br/>constraint:{3}<br/>",
                                ex.getMessage(),
                                myConstraintFormula.getTransferOrder(),
                                myConstraintFormula.getName(),
                                myConstraintFormula), ex);
            }
        }

        return returnMap;
    }

    public List<Document> createConstraintCursor(Document docConstraintItems, Document filter) {
        String constraintDB = docConstraintItems.get(FORM_DB, String.class);
        String constraintCollection = docConstraintItems.get(COLLECTION, String.class);
        Object constraintQuery = docConstraintItems.get(QUERY);

        if (constraintQuery instanceof String) {

            Document commandResult = mongoDbUtil
                    .runCommand(formService.getMyForm().getDb(), constraintQuery.toString(), filter, null);

            constraintQuery = commandResult.get(RETVAL);
        }

        constraintQuery = mongoDbUtil.replaceToDollar((Document) constraintQuery);

        return mongoDbUtil
                .find(constraintDB, constraintCollection, (Document) constraintQuery, Filters.eq(TRANSFER_ORDER, 1), null);
    }

    public Map createPivotCtrlData(Map filter) throws NullNotExpectedException {

        Map cellControl = new HashMap();

        if (formService.getMyForm().getControlCollection() != null) {
            List<Document> cursor;

            if (formService.getMyForm().getLoginFkField() == null) {
                throw new NullNotExpectedException(formService.getMyForm().printToConfigAnalyze(LOGIN_FK));
            }

            Document controlSearch = new Document()
                    .append(formService.getMyForm().getLoginFkField(), filter.get(formService.getMyForm().getLoginFkField()))
                    .append(PERIOD, filter.get(PERIOD))
                    .append(RESULT, HATA_VAR)
                    .append(RELATIONS, new Document(DOLAR_REGEX, formService.getMyForm().getName()));

            cursor = mongoDbUtil.find(formService.getMyForm().getDb(), formService.getMyForm().getControlCollection(), controlSearch);

            for (Document dBObject : cursor) {
                String resultType = dBObject.get(RESULT_TYPE, String.class);
                Document variables = (Document) dBObject.get(VARIABLES);
                for (String key : variables.keySet()) {
                    Document cell = (Document) variables.get(key);

                    Map map = new HashMap();
                    map.put(X_CODE, cell.get(X_CODE));
                    map.put(Y_CODE, cell.get(Y_CODE));
                    Map control = new HashMap();
                    control.put(COLOR, "000".equals(resultType) ? "red" : "yellow");
                    control.put(MONGO_ID, dBObject.get(MONGO_ID));
                    control.put(DESCRIPTION, dBObject.get(DESCRIPTION));
                    cellControl.put(map, control);
                }
            }

            controlSearch = new Document()
                    .append(formService.getMyForm().getLoginFkField(), filter.get(formService.getMyForm().getLoginFkField()))
                    .append(PERIOD, filter.get(PERIOD))
                    .append(RESULT, HATA_VAR)
                    .append(RELATIONS, formService.getMyForm().getForm());

            cursor = mongoDbUtil.find(formService.getMyForm().getDb(), formService.getMyForm().getControlCollection(), controlSearch);

            for (Document dBObject : cursor) {
                String resultType = dBObject.get(RESULT_TYPE, String.class);
                Document variables = (Document) dBObject.get(VARIABLES);
                for (String key : variables.keySet()) {
                    //check the state when value is retrieved
                    //by mongo search with function
                    if (variables.get(key) instanceof Document) {
                        Document cell = (Document) variables.get(key);

                        Map map = new HashMap();
                        map.put(X_CODE, cell.get(X_CODE));
                        map.put(Y_CODE, cell.get(Y_CODE));
                        Map control = new HashMap();
                        control.put(COLOR, "000".equals(resultType) ? "red" : "yellow");
                        control.put(MONGO_ID, dBObject.get(MONGO_ID));
                        control.put(DESCRIPTION, dBObject.get(DESCRIPTION));
                        cellControl.put(map, control);
                    }

                }
            }
        }

        return cellControl;
    }

    public void checkRecordConverterValueType(Document mm, MyForm mf) throws FormConfigException {
        for (String fieldKey : mm.keySet()) {

            MyField myField = mf.getField(fieldKey);

            if (myField == null) {
                continue;
            }

            if (myField == null
                    && !MONGO_ID.equals(fieldKey)
                    && !OPERATOR_LDAP_UID.equals(fieldKey)
                    && !FORMS.equals(fieldKey)
                    && !UYS_EASY_FIND_KEY.equals(fieldKey)
                    && !ADMIN_METADATA.equals(fieldKey)) {

                boolean nok = true;

                if (nok) {
                    throw new FormConfigException(String.format("it looks like you have a record key that is absent on the related data structure : %s", fieldKey));
                }
            }

            if (mm.get(fieldKey) instanceof Number) {
                if (myField.getMyconverter() instanceof TelmanStringConverter || myField.getMyconverter() instanceof SelectOneObjectIdConverter) {
                    StringBuilder sb = new StringBuilder();
                    //FIXME messagebundle
                    sb.append("kayıtlı olan değer türü ile tanımlanmış olan dönüşütürücü uyumsuzluğu tespit edildi");
                    sb.append("<hr/>");
                    sb.append("<br/>");
                    sb.append(mf.printToConfigAnalyze(fieldKey));
                    sb.append("<br/>");
                    sb.append("<br/>");
                    sb.append("data:");
                    sb.append("<br/>");
                    sb.append(String.format("mfdb=db.getSisterDB('%s');", mf.getDb()));
                    sb.append(String.format("mfdb.%s.findOne({_id:ObjectId('%s')},{%s:1});", mf.getTable(), mm.get(MONGO_ID), fieldKey));
                    throw new FormConfigException(sb.toString());
                }

                if (Arrays.asList(ProjectConstants.JAVALANG_STRING, ProjectConstants.JAVAUTIL_DATE, ProjectConstants.JAVALANG_DATE).contains(myField.getValueType())) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("kayıtlı olan değer türü ile tanımlanmış olan veri türü uyumsuzluğu tespit edildi");
                    sb.append("<hr/>");
                    sb.append("<br/>");
                    sb.append(mf.printToConfigAnalyze(fieldKey));
                    sb.append("<br/>");
                    sb.append("<br/>");
                    sb.append("data:");
                    sb.append("<br/>");
                    sb.append(String.format("mfdb=db.getSisterDB('%s');", mf.getDb()));
                    sb.append(String.format("mfdb.%s.findOne({_id:ObjectId('%s')},{%s:1});", mf.getTable(), mm.get(MONGO_ID), fieldKey));
                    throw new FormConfigException(sb.toString());
                }
            }
        }
    }

}
