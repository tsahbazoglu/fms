package tr.org.tspb.converter.base;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface ConverterAttrs {

    String TREQUIRED = "trequired";
    String TREQUIREDDEPENDONID = "trequiredDependOnId";
    String SUBMITALLOW = "submitAllow";
    String LABEL = "label";
    String CONVERTER_PARAM_NAME = "converterParam";
    String RESETIDS = "resetIds";
    String OTHERFIELDID = "otherFieldId";
    String UYSFORMAT = "uysformat";

    String PROJECT_KEY = "projectKey";
    String UPPER_NODE = "upperNode";
    String FORM_KEY = "formKey";
    String FIELD_KEY = "fieldKey";
    String CODE = "code";
    String MIN_FRACTATION_DIGITIS = "minFractationDigits";
    String MAX_FRACTATION_DIGITIS = "maxFractationDigits";

    String SPLITTER = "_|_";

    //
    String HTTP_SESSION_ATTR_MAP_REQURED_CONTROL = "mapRequiredControl";

    Object getNullValue();
}
