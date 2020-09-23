package tr.org.tspb.common.pojo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.slf4j.Logger;
import static tr.org.tspb.constants.ProjectConstants.MEASURE;
import static tr.org.tspb.constants.ProjectConstants.X_CODE;
import static tr.org.tspb.constants.ProjectConstants.X_IKS;
import static tr.org.tspb.constants.ProjectConstants.Y_CODE;
import static tr.org.tspb.constants.ProjectConstants.Y_IGREK;
import tr.org.tspb.dao.MyField;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class CellMultiDimensionKey extends HashMap {

    private List<MyField> coordinates;
    private boolean cramer = true;

    @Inject
    private Logger logger;

    public CellMultiDimensionKey() {
        super();
    }

    public CellMultiDimensionKey(List<MyField> coordinates) {
        Map<String, String> codeMatch = new HashMap<>();

        codeMatch.put(X_IKS, X_CODE);
        codeMatch.put(Y_IGREK, Y_CODE);

        this.coordinates = coordinates;
        int i = 0;

        for (MyField coordinate : coordinates) {
            put(codeMatch.get(coordinate.getNdAxis()), coordinate.getCode());
            if (MEASURE.equals(coordinate.getNdType())) {
                i++;
            }
        }
        if (i > 1) {
            logger.warn("Should not be. Multi Measure Axes is not supported yet by Telman Sphere Engine. Take the Cramer Cross Check into account");
        }

    }

    /**
     * <pre>
     * different collections inside of possible dimension
     * values can have different function to be calculated
     * </pre>
     */
    /**
     * try to find out little bit about men :)
     */
    public boolean isCramer() {
        return cramer;
    }

    /**
     * <p style="font-weight:bold;text-align:left"> This function provides
     * coordinate values on Dimension Axes which point to a Measure<br/>In a
     * Cartesian Cube Case their amount is 3(X,Y,Z) and we use two (X and Y) of
     * this three values <br/><font color="red">FIXME Should support Z-ZET Axis
     * also</font> </p>
     *
     * @return
     */
    public List<MyField> getCoordinates() {
        return Collections.unmodifiableList(coordinates);
    }
}
