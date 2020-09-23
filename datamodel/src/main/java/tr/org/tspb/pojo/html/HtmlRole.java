package tr.org.tspb.pojo.html;

import java.util.Map;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class HtmlRole {

    private String role;
    private String group;
    private String title;
    private String desc;
    private String info;

    public HtmlRole(String roleKey, Map roleMap) {

        if (roleMap == null) {
            this.role = roleKey;
            this.group = roleKey;
            this.title = roleKey;
            this.desc = roleKey;
            this.info = roleKey;
        } else {
            this.role = roleMap.get("role").toString();
            this.group = roleMap.get("group").toString();
            this.title = roleMap.get("title").toString();
            this.desc = roleMap.get("desc").toString();
            this.info = roleMap.get("info").toString();
        }

    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
