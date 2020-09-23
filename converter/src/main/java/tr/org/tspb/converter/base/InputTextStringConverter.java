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
public class InputTextStringConverter implements Converter, ConverterAttrs {

    public InputTextStringConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        String label = (String) component.getAttributes().get(LABEL);

        Object disabled = component.getAttributes().get("disabled");
        ValueExpression ve = component.getValueExpression("disabled");
        if (ve != null) {
            disabled = (Boolean) ve.getValue(context.getELContext());
        }

        if (disabled instanceof Boolean && (Boolean) disabled) {
            return null;
        }

        Boolean required = "true".equals((String) component.getAttributes().get(TREQUIRED));

        if (value == null || value.trim().isEmpty()) {
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

//        if ((!(value instanceof String)) || ((String) value).trim().isEmpty()) {
//            return null;
//        }
        return (String) value;
    }

    @Override
    public Object getNullValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
