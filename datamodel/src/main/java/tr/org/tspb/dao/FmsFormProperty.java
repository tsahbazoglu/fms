package tr.org.tspb.dao;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class FmsFormProperty {

    private String key;
    private Object value;
    private boolean renderValue = true;

    public FmsFormProperty(String key, Object value) {
        this.key = key + "\t\t";
        this.value = value;
    }

    public FmsFormProperty(String key, Object value, boolean renderValue) {
        this(key, value);
        this.renderValue = renderValue;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public boolean isRenderValue() {
        return renderValue;
    }

}
