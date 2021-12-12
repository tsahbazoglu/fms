package tr.org.tspb.converter.base;

import java.text.MessageFormat;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.bson.Document;
import static tr.org.tspb.converter.base.ConverterAttrs.LABEL;
import static tr.org.tspb.converter.base.ConverterAttrs.TREQUIRED;
import tr.org.tspb.converter.props.MessageBundleLoader;
import tr.org.tspb.dao.MyField;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class BsonConverter implements Converter {

    public static final String NULL_VALUE = "BSON_CONVERTER_NULL_VALUE";
    public static final String SELECT_ALL = "BSON_CONVERTER_ALL_VALUE";

    public Object getNullValue() {
        return NULL_VALUE;
    }

    public Object getSelectAllItem() {
        return SELECT_ALL;
    }

    @Override
    public Object getAsObject(FacesContext fc, UIComponent component, String value) {
        String label = (String) component.getAttributes().get(LABEL);
        Boolean required = Boolean.TRUE.equals(component.getAttributes().get(TREQUIRED));
        if (required && NULL_VALUE.equals(value)) {
            FacesMessage facesMessageRequired = new FacesMessage(//
                    FacesMessage.SEVERITY_ERROR, //
                    MessageFormat.format("[{0}] {1}", label, MessageBundleLoader.getMessage("requiredMessage")),//
                    "*");
            throw new ConverterException(facesMessageRequired);
        }
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        if (component.getAttributes().get("myField") instanceof MyField) {

            MyField myField = (MyField) component.getAttributes().get("myField");

            List<String> viewKey = myField.getViewKey();

            /*
            
            we use this converter for both of selectOneMenu and inputText 
            
            selectOneMenu requires "code" field as a string
            
            outputText(table view) requires "label" field as a string
            
             */
            if (value instanceof Document) {
                return ((Document) value).get(viewKey.get(0)).toString();
            } else if (value instanceof String) {

                String valueKey = value.toString();

                if (valueKey.trim().isEmpty()) {
                    return "";
                }

                if (component instanceof UISelectOne) {
                    return valueKey;
                } else if (component instanceof UIOutput) {
                    return myField.getCacheBsonConverter().getOrDefault(valueKey, "alan hafızasında code tespit edilemedi".concat(valueKey));
                } else {
                    System.out.println("exception");
                }
            }
        }
        return "";
    }

}
