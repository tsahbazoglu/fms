package tr.org.tspb.converter.base;

/**
 * 
 * @author Telman Şahbazoğlu
 */
public enum EnumConverterParam {

    TC("TC numarası controlu"),
    LITERAL("TC numarası controlu"),
    EMAIL("registration.xhtml"),
    ALPHABETIC("alphabetic"),
    NUMERIC("numeric"),
    PASSWORD("password"),
    ALPHANUMERIC("alphanumeric"),
    TAX_NO("taxNo"),
    JSF_ID_CHECK("key,jsfId");
    //
    private String description;

    private EnumConverterParam(String description) {
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public static EnumConverterParam getEnumConverterParam(String name) {
        switch (name) {
            case "TC":
                return EnumConverterParam.TC;
            default:
        }
        return null;
    }

}
