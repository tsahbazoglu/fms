package tr.org.tspb.converter.base;

import tr.org.tspb.converter.props.MessageBundleLoader;
import java.text.MessageFormat;
import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.servlet.http.HttpSession;
import tr.org.tspb.dao.MyBaseRecord;
import tr.org.tspb.dao.refs.PlainRecord;
import tr.org.tspb.dp.nullobj.PlainRecordData;

/**
 *
 * @author Telman Şahbazoğlu
 */
@FacesConverter("documentConverter")
public class DocumentConverter implements Converter, ConverterAttrs {

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
            return null;
        }

        if (value == null && required) {
            FacesMessage facesMessageRequired = new FacesMessage(//
                    FacesMessage.SEVERITY_ERROR, //
                    MessageFormat.format("[{0}] {1}", label, MessageBundleLoader.getMessage("requiredMessage")),//
                    "*");
            //context.addMessage(null, facesMessageRequired);
            throw new ConverterException(facesMessageRequired);
        }

        if (SelectOneObjectIdConverter.NULL_VALUE.toString().equals(value)) {
            return PlainRecordData.getNullPlainRecord();
        }

        return PlainRecordData.getPlainRecord(value);
    }

    //to View
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof MyBaseRecord) {
            return ((PlainRecord) value).getObjectIdStr();
        }
        return "";
    }

    @Override
    public Object getNullValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
