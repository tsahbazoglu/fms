package tr.org.tspb.pojo;

import static tr.org.tspb.constants.ProjectConstants.X_CODE;
import static tr.org.tspb.constants.ProjectConstants.Y_CODE;
import org.bson.Document;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyCalcCoordinate {

    private String xcode;
    private String ycode;

    public MyCalcCoordinate(MyConstraintVariable myConstraintVariable) {
        xcode = myConstraintVariable.getxCode().toString();
        ycode = myConstraintVariable.getyCode().toString();
    }

    public MyCalcCoordinate(Document dbo) {
        xcode = dbo.get(X_CODE).toString();
        ycode = dbo.get(Y_CODE).toString();
    }

    public String getXcode() {
        return xcode;
    }

    public String getYcode() {
        return ycode;
    }

}
