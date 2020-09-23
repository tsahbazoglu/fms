package tr.org.tspb.converter.base;

import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.bson.Document;
import tr.org.tspb.dao.MyField;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class BsonConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
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
                    return myField.getCacheBsonConverter().getOrDefault(valueKey, "alan hafızasında code testip edilemedi".concat(valueKey));
                } else {
                    System.out.println("exception");
                }
            }
        }
        return "";
    }

}
