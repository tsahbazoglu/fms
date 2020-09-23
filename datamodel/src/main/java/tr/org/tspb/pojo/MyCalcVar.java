package tr.org.tspb.pojo;

import static tr.org.tspb.constants.ProjectConstants.FORM;
import static tr.org.tspb.constants.ProjectConstants.PERIOD;
import static tr.org.tspb.constants.ProjectConstants.VALUE_KEY;
import static tr.org.tspb.constants.ProjectConstants.X_CODE;
import static tr.org.tspb.constants.ProjectConstants.Y_CODE;
import org.bson.Document;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyCalcVar {

    private String xCode;
    private String yCode;
    private String valueKey;
    private Document period;
    private final VarType varType;
    private String form;
    private String level;
    private Document cacheKey;
    private String cacheValue;
    private String op;

    public MyCalcVar(Object obj) {
        if (obj instanceof Document) {
            Document docVariable = (Document) obj;
            this.varType = VarType.CELL;
            this.xCode = (String) docVariable.get(X_CODE);
            this.yCode = (String) docVariable.get(Y_CODE);
            this.valueKey = (String) docVariable.get(VALUE_KEY);
            this.period = (Document) docVariable.get(PERIOD);
            this.form = (String) docVariable.get(FORM);
            this.level = (String) docVariable.get("level");
            this.cacheKey = (Document) docVariable.get("cacheKey");
            this.cacheValue = docVariable.getString("cacheValue");
        } else if (obj instanceof Number) {
            this.varType = VarType.OP;
            this.op = String.valueOf(((Number) obj).doubleValue());
        } else {
            this.varType = VarType.OP;
            this.op = (String) obj;
        }
    }

    public String getxCode() {
        return xCode;
    }

    public String getyCode() {
        return yCode;
    }

    public VarType getVarType() {
        return varType;
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

    public String getLevel() {
        return level;
    }

    public Document getCacheKey() {
        return cacheKey;
    }

    public String getCacheValue() {
        return cacheValue;
    }

    public String getOp() {
        return op;
    }

    public enum VarType {
        OP,
        CELL,
        CONST
    }

}
