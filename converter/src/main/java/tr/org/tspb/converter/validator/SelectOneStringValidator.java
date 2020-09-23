package tr.org.tspb.converter.validator;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import tr.org.tspb.converter.base.EnumConverterParam;
import tr.org.tspb.converter.base.ConverterAttrs;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class SelectOneStringValidator implements Validator, ConverterAttrs {

    public static final String OTHER_VALUE = "SelectOneStringValidator__OTHER_VALUE";
    public static final String NULL_VALUE = "SelectOneStringValidator__NULL_VALUE";

    public static boolean isNullOrOther(String value) {
        return value == null ? true : OTHER_VALUE.equals(value) || NULL_VALUE.equals(value);
    }

    public static boolean isOther(String value) {
        return value == null ? false : OTHER_VALUE.equals(value);
    }

    @Override
    public Object getNullValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        String label = (String) component.getAttributes().get(LABEL);
        Boolean submitAllow = "true".equals((String) component.getAttributes().get(SUBMITALLOW));
        EnumConverterParam converterParam = (EnumConverterParam) component.getAttributes().get(CONVERTER_PARAM_NAME);

        String otherFieldId = (String) component.getAttributes().get(OTHERFIELDID);
        if (otherFieldId != null) {
            UIComponent uiComponent = (UIComponent) context.getViewRoot().findComponent(otherFieldId);
            if (uiComponent != null) {
                uiComponent.setRendered(OTHER_VALUE.equals(value));
            }
        }

        if (OTHER_VALUE.equals(value)) {
            return;
        }

        if (NULL_VALUE.equals(value)) {
            FacesMessage facesMessage = new FacesMessage(//
                    FacesMessage.SEVERITY_ERROR, //
                    MessageFormat.format("[{0}] alanı zorunludur.", label),//
                    "*");
            if (submitAllow) {
                context.addMessage(component.getClientId(context), facesMessage);
            } else {
                throw new ValidatorException(facesMessage);
            }
        } else if (converterParam != null) {
            switch (converterParam) {
                case TAX_NO:
                    String numberExpression = "\\d{10}";
                    Pattern numberPattern = Pattern.compile(numberExpression, Pattern.CASE_INSENSITIVE);
                    Matcher numberMatcher = numberPattern.matcher(String.valueOf(value));
                    if (!numberMatcher.matches()) {
                        FacesMessage facesMessageTCWrong = new FacesMessage(//
                                FacesMessage.SEVERITY_ERROR, //
                                MessageFormat.format("[{0}] alanı 10 haneli sayı olmalı.", label),//
                                "*");
                        if (submitAllow) {
                            context.addMessage(component.getClientId(context), facesMessageTCWrong);
                        } else {
                            throw new ValidatorException(facesMessageTCWrong);
                        }
                    }
            }
        }

    }
}
