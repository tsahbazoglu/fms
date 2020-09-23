package tr.org.tspb.pojo;

import static tr.org.tspb.constants.ProjectConstants.CONVERTER;
import static tr.org.tspb.constants.ProjectConstants.DROOLS_MEASURE_KEY;
import static tr.org.tspb.constants.ProjectConstants.DROOLS_RULE_COORDINATE;
import static tr.org.tspb.constants.ProjectConstants.DROOLS_VERTICAL;
import static tr.org.tspb.constants.ProjectConstants.FORM;
import static tr.org.tspb.constants.ProjectConstants.JEVAL_RETURN_TYPE;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import static tr.org.tspb.constants.ProjectConstants.NAME;
import static tr.org.tspb.constants.ProjectConstants.PERIOD;
import static tr.org.tspb.constants.ProjectConstants.RELATIONS;
import static tr.org.tspb.constants.ProjectConstants.STYLE;
import static tr.org.tspb.constants.ProjectConstants.VALUE_KEY;
import static tr.org.tspb.constants.ProjectConstants.X_CODE;
import static tr.org.tspb.constants.ProjectConstants.Y_CODE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;
import org.bson.types.Code;
import org.bson.types.ObjectId;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyCalcDef {

    private final List<String> calculatedFormulaSupportedElementKeys = Arrays.asList(//
            "search", VALUE_KEY, X_CODE, Y_CODE, "X-IKS", "Y-IGREK", PERIOD, FORM,//
            "level", "cacheKey", "cacheValue");

    private final String name;
    private final String droolsMeasureKey;
    private final String relations;
    private final String style;
    private final String converter;
    private final boolean jevalReturnTypeBool;
    private final MyCalcCoordinate droolsRuleCoordinate;
    private final ObjectId id;
    private final CalcType calcType;
    private Code funcCode;
    private List<MyCalcVar> jevalList;

    public MyCalcDef(Document dbo) {
        id = (ObjectId) dbo.get(MONGO_ID);
        droolsMeasureKey = (String) dbo.get(DROOLS_MEASURE_KEY);
        droolsRuleCoordinate = new MyCalcCoordinate((Document) dbo.get(DROOLS_RULE_COORDINATE));

        Object droolsVertical = dbo.get(DROOLS_VERTICAL);

        if (droolsVertical instanceof Code) {
            calcType = CalcType.FUNCTION;
            funcCode = (Code) droolsVertical;
        } else if (droolsVertical instanceof List) {
            calcType = CalcType.LIST;
            jevalList = new ArrayList<>();
            for (Object obj : (List) droolsVertical) {
                jevalList.add(new MyCalcVar(obj));
            }
        } else {
            throw new RuntimeException("droolsVertical type is not supported");
        }
        if (dbo.get(NAME) != null) {
            name = dbo.get(NAME).toString();
        } else {
            name = "calc formula name had not been set";
        }

        jevalReturnTypeBool = "boolean".equals(dbo.get(JEVAL_RETURN_TYPE));
        relations = (String) dbo.get(RELATIONS);
        style = (String) dbo.get(STYLE);
        converter = (String) dbo.get(CONVERTER);
    }

    public String getDroolsMeasureKey() {
        return droolsMeasureKey;
    }

    public MyCalcCoordinate getDroolsRuleCoordinate() {
        return droolsRuleCoordinate;
    }

    public String getName() {
        return name;
    }

    public boolean isJevalReturnTypeBool() {
        return jevalReturnTypeBool;
    }

    public String getRelations() {
        return relations;
    }

    public String getStyle() {
        return style;
    }

    public String getConverter() {
        return converter;
    }

    public ObjectId getId() {
        return id;
    }

    public CalcType getCalcType() {
        return calcType;
    }

    public Code getFuncCode() {
        return funcCode;
    }

    public List<MyCalcVar> getJevalList() {
        return jevalList;
    }

    public enum CalcType {
        FUNCTION,
        LIST
    }

}
