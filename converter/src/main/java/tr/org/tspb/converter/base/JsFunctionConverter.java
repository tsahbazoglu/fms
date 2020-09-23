package tr.org.tspb.converter.base;

import tr.org.tspb.constants.ProjectConstants;
import static tr.org.tspb.constants.ProjectConstants.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.bson.Document;
import org.bson.types.Code;
import org.slf4j.Logger;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.tools.MongoDbUtilIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class JsFunctionConverter implements Converter {

    @Inject
    private Logger logger;

    @Inject
    @KeepOpenQualifier
    private MongoDbUtilIntr mongoDbUtil;

    private final Locale localeTR = new Locale("tr", "TR");
    private final NumberFormat toView;
    private final NumberFormat toCurrencyView;

    public JsFunctionConverter() {
        toView = NumberFormat.getNumberInstance(localeTR);
        toView.setGroupingUsed(true);
        toView.setMaximumFractionDigits(2);
        toView.setMinimumFractionDigits(2);

        toCurrencyView = NumberFormat.getCurrencyInstance(localeTR);
        Currency currency;
        //currency = Currency.getInstance(localeTR);
        currency = Currency.getInstance("TRL");
        toCurrencyView.setCurrency(currency);
        toCurrencyView.setGroupingUsed(true);
        toCurrencyView.setMaximumFractionDigits(2);
        toCurrencyView.setMinimumFractionDigits(2);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {//view to model

        Boolean myCalculateOnSave = (Boolean) component.getAttributes().get(ProjectConstants.CALCULATE_ON_SAVE);

        if (Boolean.TRUE.equals(myCalculateOnSave)) {
            Map crudObject = (Map) component.getAttributes().get("crudObject");
            String fieldKey = (String) component.getAttributes().get(ProjectConstants.FIELD_KEY);

            Object calculatedValue = calculateValue(crudObject, fieldKey, context);
            if (calculatedValue instanceof Number) {
                return ((Number) calculatedValue).doubleValue();
            }

            return null;
        }

        return "Should not hit here. This field have to marked as readonly.";

    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {//model to view

        Boolean myCalculateOnListView = (Boolean) component.getAttributes().get(ProjectConstants.CALCULATE_ON_LIST_VIEW);
        Boolean myCalculateOnCrudView = (Boolean) component.getAttributes().get(ProjectConstants.CALCULATE_ON_CRUD_VIEW);

        if (Boolean.TRUE.equals(myCalculateOnListView) || Boolean.TRUE.equals(myCalculateOnCrudView)) {
            Map crudObject = (Map) component.getAttributes().get("crudObject");
            String fieldKey = (String) component.getAttributes().get(ProjectConstants.FIELD_KEY);
            value = calculateValue(crudObject, fieldKey, context);
        }

        Number divider = (Number) component.getAttributes().get("divider");
        String format = (String) component.getAttributes().get("converterFormat");

        Number minFractationDigits = (Number) component.getAttributes().get(MIN_FRACTATION_DIGITIS);

        int div = 1;
        if (divider != null) {
            div = divider.intValue();
        }

        if ("#.###.###".equals(format)) {
            div = 1;
        } else if ("#.###.###,#".equals(format)) {
            div = 10;
            minFractationDigits = 1;
        } else if ("#.###.###,##".equals(format)) {
            div = 100;
            minFractationDigits = 2;
        } else if ("#.###.###,###".equals(format)) {
            div = 1000;
            minFractationDigits = 3;
        } else if ("#.###.###,####".equals(format)) {
            div = 10000;
            minFractationDigits = 4;
        } else if ("#.###.###,#####".equals(format)) {
            minFractationDigits = 5;
            div = 100000;
        }

        NumberFormat numberFormat = getNumberFormat(component, minFractationDigits);

        if ("##.##".equals(format)) {
            numberFormat = NumberFormat.getNumberInstance(localeTR);
            ((DecimalFormat) numberFormat).applyPattern("###,###.##");
            numberFormat.setGroupingUsed(true);
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);
        }

        if ("##.###".equals(format)) {
            numberFormat = NumberFormat.getNumberInstance(localeTR);
            ((DecimalFormat) numberFormat).applyPattern("###,###.###");
            numberFormat.setGroupingUsed(true);
            numberFormat.setMaximumFractionDigits(3);
            numberFormat.setMinimumFractionDigits(3);
        }

        if ("'%' ##.##".equals(format)) {
            numberFormat = NumberFormat.getNumberInstance(localeTR);
            ((DecimalFormat) numberFormat).applyPattern("'%' ###,###.##");
            numberFormat.setGroupingUsed(true);
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);
        }
        if ("'%' ##.#####".equals(format)) {
            numberFormat = NumberFormat.getNumberInstance(localeTR);
            ((DecimalFormat) numberFormat).applyPattern("'%' ###,###.#####");
            numberFormat.setGroupingUsed(true);
            numberFormat.setMaximumFractionDigits(5);
            numberFormat.setMinimumFractionDigits(5);
        }

        if (value instanceof Number) {
            double v = ((Number) value).doubleValue();
            if (Double.isNaN(v)) {
                return "~";
            }
            String returnValue = numberFormat.format(v / div);
            return returnValue;
        } else if (value instanceof String && !((String) value).isEmpty()) {
            try {
                Number number = NumberFormat.getInstance().parse((String) value);
                return numberFormat.format(number.doubleValue() / div);
            } catch (ParseException ex) {
                logger.error("error occured", ex);
            }
        }
        return null;

    }

    public NumberFormat getNumberFormat(UIComponent component, Number minFractationDigits) {

        String instance = (String) component.getAttributes().get("converterInstance");
        String language = (String) component.getAttributes().get("language");
        String country = (String) component.getAttributes().get("country");
        Object maxFractationDigits = component.getAttributes().get(MAX_FRACTATION_DIGITIS);

        Locale locale = new Locale("tr", "TR");
        // Locale locale = Locale.US;

        if (language != null && country != null) {
            locale = new Locale(language, country);
        }

        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        if (null != instance) {
            switch (instance) {
                case "integer":
                    numberFormat = NumberFormat.getInstance(locale);
                    break;
                case "number":
                    numberFormat = NumberFormat.getInstance(locale);
                    break;
                case "percent":
                    numberFormat = NumberFormat.getPercentInstance(locale);
                    break;
                case "currency":
                    numberFormat = NumberFormat.getCurrencyInstance(locale);
                    break;
                default:
                    numberFormat = NumberFormat.getInstance(locale);
                    break;
            }
        }

        if (minFractationDigits != null || maxFractationDigits != null) {
            numberFormat.setGroupingUsed(true);
        }
        if (minFractationDigits instanceof Number) {
            numberFormat.setMinimumFractionDigits(minFractationDigits.intValue());
        }
        if (maxFractationDigits instanceof Number) {
            numberFormat.setMaximumFractionDigits(((Number) maxFractationDigits).intValue());
        }

        return numberFormat;
    }

    public String calculateNDSum(String db, Map searchMap, Code code, Boolean isMoney) {
        //FIXME proivide dynamic searchObject here
        Document searchObject = new Document(searchMap);

        try {
            String codeString = code.toString();

            Document commandResult = mongoDbUtil.runCommand(db, codeString, searchObject, "personelCount");

            Object value = commandResult.get("retval");

            return value.toString();
        } catch (Exception ex) {
            logger.error("error occured", ex);
        }
        return null;
    }

    public String calculateNDSubSum(Map searchMap, Code code, Boolean isMoney, Object parameterField) {
        //FIXME proivide dynamic searchObject here
        Document searchObject = new Document(searchMap);

        try {
            String codeString = code.toString();

            Document commandResult = mongoDbUtil.runCommand(UYSDB, codeString, searchObject, parameterField);

            Object value = commandResult.get(RETVAL);

            if (value == null) {
                return "erro occured";
            } else if (value instanceof Number) {
                return toView.format(value);
            }

            return value.toString();
        } catch (Exception ex) {
            logger.error("error occured", ex);
        }
        return null;
    }

    private final ScriptEngineManager mgr = new ScriptEngineManager();
    private final ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");

    private Object calculateValue(Map crudObject, String fieldKey, FacesContext context) {

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
