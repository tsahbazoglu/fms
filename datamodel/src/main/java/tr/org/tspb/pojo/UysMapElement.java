package tr.org.tspb.pojo;

import java.util.Date;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import static tr.org.tspb.constants.ProjectConstants.SIMPLE_DATE_FORMAT__1;

/**
 *
 * @author Telman Şahbazoğlu
 */
@XmlRootElement(name = "uysMapElement")
@XmlAccessorType(XmlAccessType.FIELD)
public class UysMapElement {

    private String name;
    private String value;

    public UysMapElement() {
    }

    public UysMapElement(Map map) {
        this.name = ((String) map.get("key"));

        Object obj = map.get("value");
        if (obj instanceof Date) {
            this.value = SIMPLE_DATE_FORMAT__1.format(map.get("value"));
        } else {
            this.value = ((String) map.get("value"));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVallue() {
        return value;
    }

    public void setVallue(String value) {
        this.value = value;
    }
}
