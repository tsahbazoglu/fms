package tr.org.tspb.converter.base;

import tr.org.tspb.converter.props.MessageBundleLoader;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import static tr.org.tspb.converter.base.ConverterAttrs.HTTP_SESSION_ATTR_MAP_REQURED_CONTROL;

/**
 *
 * @author Telman Şahbazoğlu
 */
@FacesConverter(value = MoneyConverter.CONVERTER_NAME)
public class MoneyConverter implements Converter, ConverterAttrs {

    @Inject
    private Logger logger;

    private boolean shouldCheckNegative = true;
    NumberFormat toView;
    FacesMessage facesMessageConverter;
    FacesMessage facesMessageRequired;
    FacesMessage facesMessageAttributeMissing;
    public final static String CONVERTER_NAME = "myMoneyConverter";

    /**
     * <pre>
     * DEFAULT CURRENCY : $7,929,904,213.57
     * TR CURRENCY : 7.929.904.213,57 YTL
     * US CURRENCY : $7,929,904,213.57
     * DEFAULT INSTANCE : 7,929,904,213.57
     * US INSTANCE : 7,929,904,213.57
     * TR INSTANCE : 7.929.904.213,57
     * 7.929.904.213,57
     * </pre>
     */
    /**
     * language= "tr",country="TR"
     *
     * @param language
     * @param country
     */
    public MoneyConverter() {
        Locale locale = new Locale("tr", "TR");
        toView = NumberFormat.getInstance(locale);//
        this.shouldCheckNegative = Boolean.FALSE;
        /**
         * <code>
         *
         * toView = NumberFormat.getCurrencyInstance(locale);//$7,321,805,073.95
         *
         * toView = NumberFormat.getCurrencyInstance(localeTR);
         * Currency currency;
         * //currency = Currency.getInstance(localeTR); currency =
         * Currency.getInstance("TRL");
         *
         * toView.setCurrency(currency); // w/o this code we get YTL instead of TL(this is a bug)
         *
         * // toView.setGroupingUsed(true);
         * // toView.setMaximumFractionDigits(2);
         * // toView.setMinimumFractionDigits(0);
         * </code>
         */
        //
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {//view to model
        Locale locale = new Locale("tr", "TR");

        String language = (String) component.getAttributes().get("language");
        String country = (String) component.getAttributes().get("country");
        if (language != null && country != null) {
            locale = new Locale(language, country);
        }
        NumberFormat toModel = NumberFormat.getInstance(locale);

        String label = (String) component.getAttributes().get("label");

        Boolean required = Boolean.FALSE;

        if (component.getAttributes().get(TREQUIRED) instanceof Boolean) {
            required = Boolean.TRUE.equals(component.getAttributes().get(TREQUIRED));
        } else if (component.getAttributes().get(TREQUIRED) instanceof String) {
            required = "true".equals(component.getAttributes().get(TREQUIRED));
        }

        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

        Object mapRequredControl = httpSession.getAttribute(HTTP_SESSION_ATTR_MAP_REQURED_CONTROL);
        if (mapRequredControl instanceof Map) {
            String fieldKey = (String) component.getAttributes().get(FIELD_KEY);
            Boolean required2 = (Boolean) ((Map) mapRequredControl).get(fieldKey);
            required = required2 == null ? required : required2;
        }

        Object disabled = component.getAttributes().get("disabled");
        if (disabled == null) {
            ValueExpression ve = component.getValueExpression("disabled");
            if (ve != null) {
                disabled = (Boolean) ve.getValue(context.getELContext());
            }
        }
        if (disabled instanceof Boolean && (Boolean) disabled) {
            //null durumunda veri set edilmiyor
            return "";
        }

        String max = (String) component.getAttributes().get("maxMoney");//kuruş degil,TL anlamındadır
        Double maxMoney = max == null ? null : Double.valueOf(max);

        facesMessageConverter = new FacesMessage(//
                FacesMessage.SEVERITY_ERROR, //
                MessageFormat.format("[{0}] pozitif tamsayı olmak zorunda",//
                        label),//
                "*");

        facesMessageRequired = new FacesMessage(//
                FacesMessage.SEVERITY_ERROR, //
                MessageFormat.format("[{0}] {1}", label, MessageBundleLoader.getMessage("requiredMessage")),//
                "*");

        if (value == null || value.length() == 0 || value.trim().isEmpty()) {
            if (required) {
                //context.addMessage(component.getClientId(context), facesMessageRequired);
//                return null;
                throw new ConverterException(facesMessageRequired);
            }
            //null durumunda veri set edilmiyor
            return "";
        }

        try {
            Number number = toModel.parse(value);
            final double doubleValue = number.doubleValue();
            if (shouldCheckNegative && doubleValue < 0) {
//                context.addMessage(component.getClientId(context), facesMessageConverter);
//                return null;
                throw new ConverterException(facesMessageConverter);
            } else if (maxMoney != null && doubleValue > maxMoney) {
                facesMessageConverter = new FacesMessage(//
                        FacesMessage.SEVERITY_ERROR, //
                        MessageFormat.format("[{0}] {1} - {2} aralığında pozitif tamsayı olmak zorunda",//
                                label, 0D, maxMoney),//
                        "*");
                throw new ConverterException(facesMessageConverter);
            } else {
                return (long) (doubleValue * 100);//veritabanına kuruş olarak gonderiyoruz
            }
        } catch (ParseException ex) {
            logger.error("error occured", ex);
//            context.addMessage(component.getClientId(context), facesMessageConverter);
//            return null;
            throw new ConverterException(facesMessageConverter);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {//model to view

        if (value instanceof Number) {

            double myValue = ((Number) value).doubleValue() / 100;

            if (Double.isInfinite(myValue) || Double.isNaN(myValue)) {
                return Double.toString(myValue);
            } else {
                return toView.format(Math.round(myValue));
            }
        } else if (value instanceof String) {

            if (((String) value).trim().isEmpty()) {
                // cause the below error on <ice:outputText/> but not on <h:outputText/>
                // SEVERE: java.lang.NullPointerException: WriteText method cannot write null text
                return null;
                // return "";
            }
            try {
                NumberFormat format = NumberFormat.getInstance();
                Number number = format.parse((String) value);
                 
                //FIXME IMPORTANT save aşamasında round edilmiş değer tekrar geri alınmalı
                return toView.format(Math.round(number.doubleValue() / 100));
            } catch (ParseException ex) {
                logger.error("error occured", ex);
            }
        } else {
            logger.info(value.toString());
        }
        // cause the below error on <ice:outputText/> but not on <h:outputText/>
        // SEVERE: java.lang.NullPointerException: WriteText method cannot write null text
        return null;
        // return "";
    }

    @Override
    public Object getNullValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
