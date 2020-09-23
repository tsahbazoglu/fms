package tr.org.tspb.converter.base;

import tr.org.tspb.converter.props.MessageBundleLoader;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class DoubleConverter implements Converter, ConverterAttrs {

    @Inject
    private Logger logger;

    private NumberFormat toView;

    public DoubleConverter() {
        toView = NumberFormat.getIntegerInstance(new Locale("tr", "TR"));
        toView.setGroupingUsed(true);
        toView.setMaximumFractionDigits(3);
        toView.setMinimumFractionDigits(3);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {//view to model

        String label = (String) component.getAttributes().get("label");
        Boolean required = "true".equals((String) component.getAttributes().get("trequired"));

        String componentId = component.getClientId(context);

        FacesMessage facesMessageRequired = new FacesMessage(//
                FacesMessage.SEVERITY_ERROR, //
                MessageFormat.format("[{0}] {1}", label, MessageBundleLoader.getMessage("requiredMessage")),//
                "*");

        FacesMessage facesMessageConverter = new FacesMessage(//
                FacesMessage.SEVERITY_ERROR, //
                MessageFormat.format("[{0}] pozitif tamsayı olmak zorunda", //
                        (String) component.getAttributes().get("label")),//
                "*");

        if (value == null || value.trim().length() == 0 || value.trim().isEmpty()) {
            if (required) {
                context.addMessage(componentId, facesMessageRequired);
            }
            return null;
        }

        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("tr"));

        try {
            Integer integer = numberFormat.parse(value).intValue();
            if (integer < 0) {
                context.addMessage(component.getClientId(context), facesMessageConverter);
                return null;
            }
            return integer;
        } catch (ParseException ex) {
            context.addMessage(component.getClientId(context), facesMessageConverter);
            return null;
        }

    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {//model to view

        if (value instanceof Number) {
            return toView.format(((Number) value).longValue());
        } else if (value instanceof String && !((String) value).isEmpty()) {
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            try {
                Number number = format.parse((String) value);
                return toView.format(number);
            } catch (ParseException ex) {
                logger.error("error occured", ex);
            }
        }

        return null;
    }

    @Override
    public Object getNullValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
