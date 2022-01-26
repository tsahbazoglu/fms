package tr.org.tspb.converter.base;

import tr.org.tspb.converter.props.MessageBundleLoader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Telman Şahbazoğlu
 */
@FacesConverter(value = "myNumberConverter")
public class NumberConverter implements Converter, ConverterAttrs {

    private int div = 1;
    private final Locale localeTR = new Locale("tr", "TR");

    // Locale locale = Locale.US;
    public int getDiv() {
        return div;
    }

    public int getDiv(String uysformat) {
        int returnValue = 1;
        if ("#.###.###".equals(uysformat)) {
            returnValue = 1;
        } else if ("#.###.###,#".equals(uysformat)) {
            returnValue = 10;
        } else if ("#.###.###,##".equals(uysformat)) {
            returnValue = 100;
        } else if ("#.###.###,###".equals(uysformat)) {
            returnValue = 1000;
        } else if ("#.###.###,####".equals(uysformat)) {
            returnValue = 10000;
        } else if ("#.###.###,#####".equals(uysformat)) {
            returnValue = 100000;
        }
        return returnValue;
    }

    public NumberConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {//view to model

        String label = (String) component.getAttributes().get("label");
        Number divider = (Number) component.getAttributes().get("divider");
        String instance = (String) component.getAttributes().get("converterInstance");

        if (divider != null) {
            div = divider.intValue();
        }

        Boolean required = Boolean.FALSE;

        if (component.getAttributes().get(TREQUIRED) instanceof Boolean) {
            required = Boolean.TRUE.equals(component.getAttributes().get(TREQUIRED));
        } else if (component.getAttributes().get(TREQUIRED) instanceof String) {
            required = "true".equals(component.getAttributes().get(TREQUIRED));
        }

        String componentId = component.getClientId(context);

        FacesMessage facesMessageRequired = new FacesMessage(//
                FacesMessage.SEVERITY_ERROR, //
                MessageFormat.format("[{0}] {1}", label == null ? "" : label.replaceAll("\\<.*?>", ""), MessageBundleLoader.getMessage("requiredMessage")),//
                "*");

        FacesMessage facesMessageConverter = new FacesMessage(//
                FacesMessage.SEVERITY_ERROR, //
                MessageFormat.format("[{0}] pozitif {1} olmak zorundadır.", //
                        label == null ? "" : label.replaceAll("\\<.*?>", ""), "integer".equals(instance) ? "tamsayı" : "sayı"),//
                "*");

        if (value == null || value.trim().length() == 0 || value.trim().isEmpty()) {
            if (required) {
                throw new ConverterException(facesMessageRequired);
            }
            //null durumunda veri set edilmiyor
            return 0;
        }

        Object max = component.getAttributes().get("maxValue");//kuruş degil,TL anlamındadır
        Double maxAllowed = null;

        if (max instanceof String) {
            maxAllowed = Double.valueOf((String) max);
        } else if (max instanceof Number) {
            maxAllowed = ((Number) max).doubleValue();
        }

        boolean shouldCheckNegative = "true".equals(component.getAttributes().get("shouldCheckNegative"));

        if (component.getAttributes().get("shouldCheckNegative") instanceof Boolean) {
            shouldCheckNegative = Boolean.TRUE.equals(component.getAttributes().get("shouldCheckNegative"));
        }

        try {

            //value = value.replaceAll("^[0-9.]+$", "");"[^\\d.]"
            //value = value.replaceAll("[^\\d.]", ""); 
            //value = value.replaceAll("[^\\d%.,]", ""); 
            //value = value.replaceAll("[^\\d.,]", "");
            value = value.replaceAll("[^\\d-.,]", "");

            if (value == null || value.trim().length() == 0 || value.trim().isEmpty()) {
                if (required) {
                    throw new ConverterException(facesMessageRequired);
                }
                return "";
            }

            Number minFractationDigits = (Number) component.getAttributes().get(MIN_FRACTATION_DIGITIS);

            NumberFormat numberFormat = getNumberFormat(component, minFractationDigits);

            //  Number number = numberFormat.parse(value);
            String converterFormat = (String) component.getAttributes().get("converterFormat");
            if ("'%' ##.##".equals(converterFormat)) {
                numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(localeTR);
                ((DecimalFormat) numberFormat).applyPattern("'%'###,###.##");
                numberFormat.setGroupingUsed(true);
                numberFormat.setMaximumFractionDigits(2);
                numberFormat.setMinimumFractionDigits(2);
                value = "%".concat(value);
            }

            Number number = numberFormat.parse(value);

            final double doubleValue = number.doubleValue();
            if (shouldCheckNegative && doubleValue < 0) {
//                context.addMessage(component.getClientId(context), facesMessageConverter);
//                return null;
                throw new ConverterException(facesMessageConverter);
            } else if (maxAllowed != null && doubleValue > maxAllowed) {
                facesMessageConverter = new FacesMessage(//
                        FacesMessage.SEVERITY_ERROR, //
                        MessageFormat.format("[{0}] {1} - {2} aralığında {3} olmak zorundadır.",//
                                label, 0D, maxAllowed, "integer".equals(instance) ? "tamsayı" : "sayı"),//
                        "*");
                throw new ConverterException(facesMessageConverter);
            } else {
                if ("integer".equals(instance)) {
                    return number.intValue() / div;
                }
                if ("##.##".equals(converterFormat)) {
                    return (double) Math.round(number.doubleValue() * div * 100) / 100;
                }
                return number.doubleValue() * div;
            }
        } catch (ParseException ex) {
//            context.addMessage(component.getClientId(context), facesMessageConverter);
//            return null;
            throw new ConverterException(facesMessageConverter);
        }

    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {//model to view

        String label = (String) component.getAttributes().get("label");
        Number divider = (Number) component.getAttributes().get("divider");
        String format = (String) component.getAttributes().get(UYSFORMAT);
        Number minFractationDigits = (Number) component.getAttributes().get(MIN_FRACTATION_DIGITIS);
        String converterFormat = (String) component.getAttributes().get("converterFormat");
        String instance = (String) component.getAttributes().get("converterInstance");

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

        if ("'%' ##.##".equals(converterFormat)) {
            numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(localeTR);
            ((DecimalFormat) numberFormat).applyPattern("'%'###,###.##");
            numberFormat.setGroupingUsed(true);
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);
        }

        if ("##.##".equals(converterFormat)) {
            numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(localeTR);
            ((DecimalFormat) numberFormat).applyPattern("###,###.##");
            numberFormat.setGroupingUsed(true);
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);
            numberFormat.setRoundingMode(RoundingMode.DOWN);
        }

        if (value instanceof Number) {
            //((Number)value)
            if ("integer".equals(instance)) {
                return numberFormat.format(((Number) value).intValue() / div);
            }
            return numberFormat.format(((Number) value).doubleValue() / div);
        } else if (value instanceof String && !((String) value).isEmpty()) {
            try {
                Number number = NumberFormat.getInstance().parse((String) value);
                return numberFormat.format(number.doubleValue() / div);
            } catch (ParseException ex) {
                //
            }
        }
        return null;
    }

    @Override
    public Object getNullValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            numberFormat.setMinimumFractionDigits(((Number) minFractationDigits).intValue());
        }
        if (maxFractationDigits instanceof Number) {
            numberFormat.setMaximumFractionDigits(((Number) maxFractationDigits).intValue());
        }

        return numberFormat;
    }
}
