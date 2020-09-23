package tr.org.tspb.converter.base;

import com.mongodb.DBObject;
import tr.org.tspb.converter.props.MessageBundleLoader;
import tr.org.tspb.converter.mb.MySessionStore;
import java.text.MessageFormat;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import static tr.org.tspb.converter.base.ConverterAttrs.LABEL;
import static tr.org.tspb.converter.base.ConverterAttrs.TREQUIRED;
import org.bson.Document;
import tr.org.tspb.dao.MyField;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class SelectOneDBObjectConverter implements Converter, ConverterAttrs {

    public static final Document NULL_VALUE = new Document("code", "null").append("name", "null");

    @Override
    public Object getNullValue() {
        return NULL_VALUE;
    }

    @Override
    public Object getAsObject(FacesContext fc, UIComponent component, String value) {

        Boolean required = Boolean.FALSE;
        if (component.getAttributes().get(TREQUIRED) instanceof Boolean) {
            required = Boolean.TRUE.equals(component.getAttributes().get(TREQUIRED));
        } else if (component.getAttributes().get(TREQUIRED) instanceof String) {
            required = "true".equals(component.getAttributes().get(TREQUIRED));
        }

        MyField myField = (MyField) component.getAttributes().get("myField");

        Object obj = myField.getCacheBsonConverter().get(value);

        if ((obj == null || NULL_VALUE.equals(obj)) && required) {
            String label = (String) component.getAttributes().get(LABEL);
            FacesMessage facesMessageRequired = new FacesMessage(//
                    FacesMessage.SEVERITY_ERROR, //
                    MessageFormat.format("[{0}] {1}", label, MessageBundleLoader.getMessage("requiredMessage")),//
                    "*");
            throw new ConverterException(facesMessageRequired);
        }

        return obj;

    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        if (value instanceof DBObject) {

            MyField myField = (MyField) component.getAttributes().get("myField");

            String projectKey = (String) component.getAttributes().get(PROJECT_KEY);
            String upperNode = (String) component.getAttributes().get(UPPER_NODE);
            String formKey = (String) component.getAttributes().get(FORM_KEY);
            String fieldKey = (String) component.getAttributes().get(FIELD_KEY);
            String code = (String) ((Document) value).get(CODE);
            String lastKey = MySessionStore.createCacheKey(projectKey, upperNode, formKey, fieldKey, code);

            return lastKey;
        }
        return (String) value;

    }

}
