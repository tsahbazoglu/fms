package tr.org.tspb.converter.base;

import tr.org.tspb.converter.props.MessageBundleLoader;
import java.text.MessageFormat;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class SelectOneStringConverter implements Converter, ConverterAttrs {

    public static final String NULL_VALUE = "NULL_VALUE";

    public SelectOneStringConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        Object disabled = component.getAttributes().get("disabled");
        if (disabled == null) {
            ValueExpression ve = component.getValueExpression("disabled");
            if (ve != null) {
                disabled = (Boolean) ve.getValue(context.getELContext());
            }
        }
        if (disabled instanceof Boolean && (Boolean) disabled) {
            return null;
        }

        Boolean required = Boolean.FALSE;
        if (component.getAttributes().get(TREQUIRED) instanceof Boolean) {
            required = Boolean.TRUE.equals(component.getAttributes().get(TREQUIRED));
        } else if (component.getAttributes().get(TREQUIRED) instanceof String) {
            required = "true".equals(component.getAttributes().get(TREQUIRED));
        }

        String label = (String) component.getAttributes().get(LABEL);

        if (value == null || value.equals(NULL_VALUE) || value.trim().isEmpty()) {
            if (required) {
                FacesMessage facesMessageRequired = new FacesMessage(//
                        FacesMessage.SEVERITY_ERROR, //
                        MessageFormat.format("[{0}] {1}", label, MessageBundleLoader.getMessage("requiredMessage")),//
                        "*");
                throw new ConverterException(facesMessageRequired);
            }
        }

        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return (String) value;
    }

    @Override
    public Object getNullValue() {
        return NULL_VALUE;
    }
}
