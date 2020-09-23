package tr.org.tspb.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Telman Şahbazoğlu
 */
@XmlRootElement(name = "listOfMapElement")
@XmlAccessorType(XmlAccessType.FIELD)
public class UysListOfMapElement {


    @XmlElement(name = "uysMapElement1")
    private List<UysMapElement> listOfMapElement1 = null;

    @XmlElement(name = "uysMapElement2")
    private List<UysMapElement> listOfMapElement2 = null;

    @XmlElement(name = "uysMapElement3")
    private List<UysMapElement> listOfMapElement3 = null;
    public UysListOfMapElement() {
    }
    public UysListOfMapElement(List<Map> x, List<Map> y, List<Map> z) {
        listOfMapElement1 = new ArrayList<>();
        for (Map map : x) {
            listOfMapElement1.add(new UysMapElement(map));
        }

        listOfMapElement2 = new ArrayList<>();
        for (Map map : y) {
            listOfMapElement2.add(new UysMapElement(map));
        }

        listOfMapElement3 = new ArrayList<>();
        for (Map map : z) {
            listOfMapElement3.add(new UysMapElement(map));
        }
    }

}
