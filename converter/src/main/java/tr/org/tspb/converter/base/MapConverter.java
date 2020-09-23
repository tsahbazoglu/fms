package tr.org.tspb.converter.base;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MapConverter implements Converter {

    public MapConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            return JSON.parse(value);
        } catch (Exception ex) {
            throw new ConverterException("Could not convert");
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof DBObject) {
            return value.toString();
        }
        return null;
    }
}
