package tr.org.tspb.pojo;

import static tr.org.tspb.constants.ProjectConstants.EXPRESSION;
import static tr.org.tspb.constants.ProjectConstants.HATA_VAR;
import static tr.org.tspb.constants.ProjectConstants.RESULT;
import static tr.org.tspb.constants.ProjectConstants.TAMAM;
import java.util.Map;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyControlResult {

    private final String expression;
    private final String controlResult;
    private final boolean result;

    public MyControlResult(Map map) {

        this.result = Boolean.TRUE.equals(map.get(RESULT));
        this.expression = map.get(EXPRESSION).toString();

        if (result) {
            this.controlResult = TAMAM;
        } else {
            this.controlResult = HATA_VAR;
        }

    }

    public String getExpression() {
        return expression;
    }

    public String getControlResult() {
        return controlResult;
    }

    public boolean isResult() {
        return result;
    }

}
