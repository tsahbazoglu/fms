package tr.org.tspb.pivot.simple.datamodel;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class ItemField {

    private final String code;
    private final String name;
    private final String style;

    public ItemField(String code, String name, String style) {
        this.code = code;
        this.name = name;
        this.style = style;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the style
     */
    public String getStyle() {
        return style;
    }

}
