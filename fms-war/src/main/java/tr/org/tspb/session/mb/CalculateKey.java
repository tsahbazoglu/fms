package tr.org.tspb.session.mb;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import static tr.org.tspb.constants.ProjectConstants.COMMA;
import tr.org.tspb.dao.MyField;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class CalculateKey {

    private List<MyField> coordinates;

    public CalculateKey(List<MyField> coordinates) {
        this.coordinates = coordinates;
    }

    public List<MyField> getCoordinates() {
        return Collections.unmodifiableList(coordinates);
    }

    @Override
    public String toString() {
        String presentation = "";

        Iterator it = coordinates.iterator();
        while (it.hasNext()) {
            MyField coordinate = (MyField) it.next();
            presentation += coordinate.toString()//
                    + (it.hasNext() ? COMMA : "");

        }

        return presentation;
    }
}
