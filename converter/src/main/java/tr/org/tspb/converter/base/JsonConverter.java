package tr.org.tspb.converter.base;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import org.bson.Document;

/**
 *
 * @author Telman Şahbazoğlu
 */
@FacesConverter(value = "tr.org.tspb.converter.jsf.util.converter.JsonConverter")
public class JsonConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        try {
            return (DBObject) JSON.parse(value);
        } catch (Exception e) {
            FacesMessage facesMessageRequired = new FacesMessage(//
                    FacesMessage.SEVERITY_ERROR, //
                    "Could not parse",//
                    "Could not parse");
            throw new ConverterException(facesMessageRequired);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return new Document().toString();
        }
        return value.toString();
    }
}
