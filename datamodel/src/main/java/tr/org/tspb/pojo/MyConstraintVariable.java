package tr.org.tspb.pojo;

import static tr.org.tspb.constants.ProjectConstants.DIEZ;
import static tr.org.tspb.constants.ProjectConstants.DOLAR;
import static tr.org.tspb.constants.ProjectConstants.FORM;
import static tr.org.tspb.constants.ProjectConstants.PERIOD;
import static tr.org.tspb.constants.ProjectConstants.VALUE_KEY;
import static tr.org.tspb.constants.ProjectConstants.X_CODE;
import static tr.org.tspb.constants.ProjectConstants.Y_CODE;
import org.bson.Document;
import org.bson.types.Code;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyConstraintVariable {

    private final boolean xxx;
    private Code code;
    private String key;
    private String valueKey;
    private String form;
    private Document period;
    private Object xCode;
    private Object yCode;
    private boolean shouldbeRetrievedFromDB;

    public MyConstraintVariable(Object object, String key) {
        this.key = key;
        this.xxx = (object instanceof Code);
        if (xxx) {
            this.code = new Code(((Code) object).getCode().replace(DIEZ, DOLAR));
        } else if (object instanceof Document) {
            Document dbo = (Document) object;
            this.valueKey = (String) dbo.get(VALUE_KEY);
            this.period = (Document) dbo.get(PERIOD);
            this.form = (String) dbo.get(FORM);
            this.xCode = dbo.get(X_CODE);
            this.yCode = dbo.get(Y_CODE);
            this.shouldbeRetrievedFromDB = ((Document) object).containsKey("search");
        }
    }

    public boolean isCode() {
        return xxx;
    }

    public Code getCode() {
        return code;
    }

    public String getValueKey() {
        return valueKey;
    }

    public Document getPeriod() {
        return period;
    }

    public String getForm() {
        return form;
    }

    public Object getxCode() {
        return xCode;
    }

    public Object getyCode() {
        return yCode;
    }

    public boolean isShouldbeRetrievedFromDB() {
        return shouldbeRetrievedFromDB;
    }

    public String getKey() {
        return key;
    }

}
