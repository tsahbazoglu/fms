package tr.org.tspb.pojo.html;

import java.util.Map;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class HtmlCompany {

    private String code;
    private String title;

    public HtmlCompany(Map roleMap) {
        this.code = roleMap.get("kurulusKodu").toString();
        this.title = roleMap.get("unvan").toString();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
