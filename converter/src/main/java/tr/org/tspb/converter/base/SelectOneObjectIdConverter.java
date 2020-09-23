package tr.org.tspb.converter.base;

import tr.org.tspb.converter.props.MessageBundleLoader;
import tr.org.tspb.converter.mb.MySessionStore;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.servlet.http.HttpSession;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class SelectOneObjectIdConverter implements Converter, ConverterAttrs {

    public static final ObjectId NULL_VALUE = new ObjectId();

    public static Map<Object, String> mapClientIdPerMongoKey = new HashMap<>();
    public static Map<Object, UIComponent> mapComponentPerMongoKey = new HashMap<>();

    Map<String, String> itemMap;

    public void setItemMap(Map<String, String> itemMap) {
        this.itemMap = itemMap;
    }

    public SelectOneObjectIdConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        String label = (String) component.getAttributes().get(LABEL);

        Boolean required = Boolean.TRUE.equals(component.getAttributes().get(TREQUIRED));

        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

        Object mapRequredControl = httpSession.getAttribute(HTTP_SESSION_ATTR_MAP_REQURED_CONTROL);
        if (mapRequredControl instanceof Map) {
            String fieldKey = (String) component.getAttributes().get(FIELD_KEY);
            Boolean required2 = (Boolean) ((Map) mapRequredControl).get(fieldKey);
            required = required2 == null ? required : required2;
        }

        /*
         * In case of submitting the form by other component with intermidiate
         * set to true the convertor/validate phase is jumped but the diabled
         * state component is still catch by the convertor. the following code
         * snippet is used as a workaround to the above mention problem (if this
         * is a problem :)
         */
        Object disabled = component.getAttributes().get("disabled");
        if (disabled == null) {
            ValueExpression ve = ((HtmlInputText) component).getValueExpression("disabled");
            if (ve != null) {
                disabled = (Boolean) ve.getValue(context.getELContext());
            }
        }
        if (disabled instanceof Boolean && (Boolean) disabled) {
            return NULL_VALUE;
        }

        ObjectId objectId = MySessionStore.map.get(value);

        if (NULL_VALUE.equals(objectId) && required) {
            FacesMessage facesMessageRequired = new FacesMessage(//
                    FacesMessage.SEVERITY_ERROR, //
                    MessageFormat.format("[{0}] {1}", label, MessageBundleLoader.getMessage("requiredMessage")),//
                    "*");
            //context.addMessage(null, facesMessageRequired);
            throw new ConverterException(facesMessageRequired);
        }

        return NULL_VALUE.equals(objectId) ? null : objectId;
    }

    //to View
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        String label = (String) component.getAttributes().get(LABEL);

        /**
         * Be aware about the unique "mongofField" attribute for each
         * participated component in concept of this pattern
         */
        Object mongoField = component.getAttributes().get("mongoField");

        boolean readonly = Boolean.TRUE.equals(component.getAttributes().get("readonly"));

        if (mongoField != null && !mapClientIdPerMongoKey.containsKey(mongoField)) {
            mapClientIdPerMongoKey.put(mongoField, component.getClientId(context));
        }
        if (mongoField != null && !mapComponentPerMongoKey.containsKey(mongoField)) {
            mapComponentPerMongoKey.put(mongoField, component);
        }

        //This function is called in a different phase 
        //Render phase. Object value is a object that have been put to the items. it is an object ıd
        //
        StringBuilder asStringValue = new StringBuilder();

        if (value instanceof ObjectId) {
            asStringValue.append(value.toString());
            if (!MySessionStore.map.containsKey(asStringValue)) {
                MySessionStore.map.put(asStringValue.toString(), (ObjectId) value);
            }
        } else if (value instanceof Document) {
            List<String> viewKeys = (List<String>) component.getAttributes().get("viewKey");
            if (viewKeys == null) {
                asStringValue.append(((Document) value).get("name").toString());
            } else {
                for (Iterator<String> iterator = viewKeys.iterator(); iterator.hasNext();) {
                    String next = iterator.next();
                    Object val = ((Document) value).get(next);
                    asStringValue.append(val == null ? "NONE" : val.toString());
                    if (iterator.hasNext()) {
                        asStringValue.append(" - ");
                    }
                }
            }
        } else if (value instanceof String) {
            asStringValue.append(value.toString());
        } else {
            asStringValue.append("");
        }

        if (readonly) {
            asStringValue.append(itemMap.get(asStringValue));
        }

        return asStringValue.toString();
    }

    @Override
    public Object getNullValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
