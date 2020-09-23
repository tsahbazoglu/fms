package tr.org.tspb.dao;

import java.util.Map;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class FmsCodeName {

    private final String code;
    private final String name;

    public FmsCodeName(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public FmsCodeName(Map map) {
        this.code = map.get("code").toString();
        this.name = map.get("name").toString();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
