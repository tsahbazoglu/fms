package tr.org.tspb.converter.base;

import tr.org.tspb.converter.props.MessageBundleLoader;
import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.servlet.http.HttpSession;
import tr.org.tspb.converter.validator.SelectOneStringValidator;

/**
 *
 * @author Telman Şahbazoğlu
 */
@FacesConverter(value = "tr.org.tspb.converter.base.TelmanStringConverter")
public class TelmanStringConverter implements Converter, ConverterAttrs {

    public static final Long BaseEntityConverter_OTHER_VALUE = -2L;

    public TelmanStringConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        Boolean required = Boolean.TRUE.equals(component.getAttributes().get(TREQUIRED));
        Boolean submitAllow = "true".equals((String) component.getAttributes().get(SUBMITALLOW));
        String label = (String) component.getAttributes().get(LABEL);
        String trequiredDependOnComponentId = (String) component.getAttributes().get(TREQUIREDDEPENDONID);
        EnumConverterParam converterParam = null;

        if (component.getAttributes().get(CONVERTER_PARAM_NAME) instanceof String) {
            converterParam = EnumConverterParam.getEnumConverterParam(component.getAttributes().get(CONVERTER_PARAM_NAME).toString());
        } else if (component.getAttributes().get(CONVERTER_PARAM_NAME) instanceof EnumConverterParam) {
            converterParam = (EnumConverterParam) component.getAttributes().get(CONVERTER_PARAM_NAME);
        }

        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

        Object mapRequredControl = httpSession.getAttribute(HTTP_SESSION_ATTR_MAP_REQURED_CONTROL);
        if (mapRequredControl instanceof Map) {
            String myFieldKey = (String) component.getAttributes().get(FIELD_KEY);
            Boolean required2 = (Boolean) ((Map) mapRequredControl).get(myFieldKey);
            required = required2 == null ? required : required2;
        }

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

        if (trequiredDependOnComponentId != null) {
            UIComponent uiComponent = (UIComponent) context.getViewRoot().findComponent(trequiredDependOnComponentId);
            if (uiComponent != null) {
                Object obj = uiComponent.getAttributes().get("value");
//                if (obj instanceof BaseEntity) {
//                    BaseEntity baseEntity = (BaseEntity) obj;
//                    required = required && BaseEntityConverter_OTHER_VALUE.equals(baseEntity.getId());
//                } else 
                if (obj instanceof String) {
                    required = required && SelectOneStringValidator.OTHER_VALUE.equals(obj);
                } else {
                    required = false;//
                }
            }
        }

        if (value == null || value.trim().isEmpty()) {
            if (required) {
                FacesMessage facesMessageRequired = new FacesMessage(//
                        FacesMessage.SEVERITY_ERROR, //
                        MessageFormat.format("[{0}] {1}", label, MessageBundleLoader.getMessage("requiredMessage")),//
                        "*");
                if (submitAllow) {
                    context.addMessage(component.getClientId(context), facesMessageRequired);
                } else {
                    throw new ConverterException(facesMessageRequired);
                }
            }
        } else if (converterParam != null) {
            FacesMessage facesMessage = null;
            switch (converterParam) {
                case TC:
                    String numberExpression = "\\d{11}";
                    Pattern numberPattern = Pattern.compile(numberExpression, Pattern.CASE_INSENSITIVE);
                    Matcher numberMatcher = numberPattern.matcher(value);
                    if (!numberMatcher.matches()) {
                        FacesMessage facesMessageTCWrong = new FacesMessage(//
                                FacesMessage.SEVERITY_ERROR, //
                                MessageFormat.format("[{0}] alanı 11 haneli sayı olmalı.", label),//
                                "*");
                        if (submitAllow) {
                            context.addMessage(component.getClientId(context), facesMessageTCWrong);
                        } else {
                            throw new ConverterException(facesMessageTCWrong);
                        }
                    } else {
                        int sum = 0;
                        for (int i = 0; i < value.length() - 1; i++) {
                            String c = String.valueOf(value.charAt(i));
                            sum += Integer.parseInt(c);
                        }

                        int check = sum % 10;
                        int last = Integer.parseInt(String.valueOf(value.charAt(10)));

                        if (check != last) {
                            FacesMessage facesMessageTCWrong = new FacesMessage(//
                                    FacesMessage.SEVERITY_ERROR, //
                                    MessageFormat.format("[{0}] alanı geçerli değil.", label),//
                                    "*");
                            if (submitAllow) {
                                context.addMessage(component.getClientId(context), facesMessageTCWrong);
                            } else {
                                throw new ConverterException(facesMessageTCWrong);
                            }
                        }
                    }
                    break;
                case TAX_NO:
                    String numberExpressionTaxNo = "\\d{10}";
                    Pattern numberPatternTaxNo = Pattern.compile(numberExpressionTaxNo, Pattern.CASE_INSENSITIVE);
                    Matcher numberMatcherTaxNo = numberPatternTaxNo.matcher(String.valueOf(value));
                    if (!numberMatcherTaxNo.matches()) {
                        facesMessage = new FacesMessage(//
                                FacesMessage.SEVERITY_ERROR, //
                                MessageFormat.format("[{0}] alanı 10 haneli sayı olmalı.", label),//
                                "*");
                        if (submitAllow) {
                            context.addMessage(component.getClientId(context), facesMessage);
                        } else {
                            throw new ConverterException(facesMessage);
                        }
                    }
                    break;
                case EMAIL:
                    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(value);
                    if (!matcher.matches()) {
                        FacesMessage facesMessageEmailWrong = new FacesMessage(//
                                FacesMessage.SEVERITY_ERROR, //
                                MessageFormat.format("[{0}] alanı yanlış biçimlendirilmiş", label),//
                                "*");
                        if (submitAllow) {
                            context.addMessage(component.getClientId(context), facesMessageEmailWrong);
                        } else {
                            throw new ConverterException(facesMessageEmailWrong);
                        }
                    }
                    break;
                case LITERAL:
                    String literalExpression = "[0-9]";
                    Pattern literalPattern = Pattern.compile(literalExpression, Pattern.CASE_INSENSITIVE);
                    Matcher literalMatcher = literalPattern.matcher(value);
                    if (literalMatcher.find()) {
                        FacesMessage facesMessageEmailWrong = new FacesMessage(//
                                FacesMessage.SEVERITY_ERROR, //
                                MessageFormat.format("[{0}] alanı sayı içermemeli", label),//
                                "*");
                        if (submitAllow) {
                            context.addMessage(component.getClientId(context), facesMessageEmailWrong);
                        } else {
                            throw new ConverterException(facesMessageEmailWrong);
                        }
                    }
                    break;
                case ALPHABETIC:
                    //   ^[a-zA-Z0-9ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝßàáâãäåæçèéêëìíîïñòóôõöøùúûüýÿ\.\,\-\/\']+[a-zA-Z0-9ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝßàáâãäåæçèéêëìíîïñòóôõöøùúûüýÿ\.\,\-\/\' ]+$

                    value = value.replaceAll("\\b\\s{2,}\\b", " ").trim();

                    String alphaExpression = "^[a-zöçşğüıA-Z\\sÖÇŞĞÜİ]+$";
                    Pattern alphaPattern = Pattern.compile(alphaExpression, Pattern.CASE_INSENSITIVE);
                    Matcher alphaMatcher = alphaPattern.matcher(value);
                    if (!alphaMatcher.matches()) {
                        FacesMessage facesMessageEmailWrong = new FacesMessage(//
                                FacesMessage.SEVERITY_ERROR, //
                                MessageFormat.format("[{0}] alanı geçerli harfler olmalı", label),//
                                "*");
                        if (submitAllow) {
                            context.addMessage(component.getClientId(context), facesMessageEmailWrong);
                        } else {
                            throw new ConverterException(facesMessageEmailWrong);
                        }
                    }
                    break;
                case ALPHANUMERIC:
                    value = value.replaceAll("\\b\\s{2,}\\b", " ").trim();

                    String alphaNumExpression = "^[0-9a-zöçşğüıA-Z\\sÖÇŞĞÜİ]+$";
                    Pattern alphaNumPattern = Pattern.compile(alphaNumExpression, Pattern.CASE_INSENSITIVE);
                    Matcher alphaNumMatcher = alphaNumPattern.matcher(value);
                    if (!alphaNumMatcher.matches()) {
                        FacesMessage facesMessageEmailWrong = new FacesMessage(//
                                FacesMessage.SEVERITY_ERROR, //
                                MessageFormat.format("[{0}] alanı alfanumerik olmalı", label),//
                                "*");
                        if (submitAllow) {
                            context.addMessage(component.getClientId(context), facesMessageEmailWrong);
                        } else {
                            throw new ConverterException(facesMessageEmailWrong);
                        }
                    }
                    break;
                case NUMERIC:
                    String numExpression = "^[0-9]+$";
                    Pattern numPattern = Pattern.compile(numExpression, Pattern.CASE_INSENSITIVE);
                    Matcher numMatcher = numPattern.matcher(value);
                    if (!numMatcher.matches()) {
                        FacesMessage facesMessageEmailWrong = new FacesMessage(//
                                FacesMessage.SEVERITY_ERROR, //
                                MessageFormat.format("[{0}] alanı sayılar olmalı", label),//
                                "*");
                        if (submitAllow) {
                            context.addMessage(component.getClientId(context), facesMessageEmailWrong);
                        } else {
                            throw new ConverterException(facesMessageEmailWrong);
                        }
                    }
                    break;
                case PASSWORD:
                    //String passwordExpression = ".*\\p{Digit}.*\\p{Digit}.*";
                    //String passwordExpression = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";

                    // if (!value.matches(passwordExpression) || value.matches(".*[\\s].*")) {
                    if (value.matches(".*[\\s].*") || value.length() < 8) {
//                        FacesMessage facesMessageEmailWrong = new FacesMessage(//
//                                FacesMessage.SEVERITY_ERROR, //
//                                MessageFormat//
//                                .format("[{0}]\talanı 6~20  boyutunda  boşluk olmayacak şekilde " +
//                                "\n\t\ten azından bir rakam, küçük ve büyük harf içermeli. " +
//                                "\n\t\tÖrnek : vefaM6", label),//
//                                "*");

                        FacesMessage facesMessageEmailWrong = new FacesMessage(//
                                FacesMessage.SEVERITY_ERROR, //
                                MessageFormat//
                                        .format("[{0}]\talanı 8 karakterden küçük olmamalı ve boşluk içermemeli.", label),//
                                "*");
                        if (submitAllow) {
                            context.addMessage(component.getClientId(context), facesMessageEmailWrong);
                        } else {
                            throw new ConverterException(facesMessageEmailWrong);
                        }
                    }
                    break;
                case JSF_ID_CHECK:
                    try {
                        validateId(value);
                    } catch (Exception ex) {
                        FacesMessage facesMessageEmailWrong = new FacesMessage(//
                                FacesMessage.SEVERITY_ERROR, //
                                MessageFormat//
                                        .format("[{0}]\talanı componentId olarak kullanılmaktadır. Girilen değer geçerli ID değil.", label),//
                                "*");
                        throw new ConverterException(facesMessageEmailWrong);
                    }
                    break;
                default:
                    ;
            }
        }

        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if ((!(value instanceof String)) || ((String) value).trim().isEmpty()) {
            return null;
        }

        return (String) value;
    }

    @Override
    public Object getNullValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void validateId(String id) {
        if (id == null) {
            return;
        }
        int n = id.length();
        if (n < 1) {
            throw new IllegalArgumentException("Empty id attribute is not allowed");
        }
        for (int i = 0; i < n; i++) {
            char c = id.charAt(i);
            if (i == 0) {
                if (!Character.isLetter(c) && (c != '_')) {
                    throw new IllegalArgumentException(id);
                }
            } else if (!Character.isLetter(c)
                    && !Character.isDigit(c)
                    && (c != '-') && (c != '_')) {
                throw new IllegalArgumentException(id);
            }
        }
    }
}
