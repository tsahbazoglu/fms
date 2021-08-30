package tr.org.tspb.pojo;

import static tr.org.tspb.constants.ProjectConstants.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.Code;
import org.bson.types.ObjectId;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyConstraintFormula {

    private final ObjectId id;
    private final EngineType engineType;
    private final String name;
    private final String relationsPresentation;
    private final String controlFunction;
    private final String resultType;
    private final String relations;
    private final String description;

    private final int transferOrder;
    private final List checkList;
    private final List<MyConstraintVariable> variables;
    private MyControlResult controlResult;

    public MyConstraintFormula(Map contraintFormula) {
        this.id = (ObjectId) contraintFormula.get(MONGO_ID);
        String localEngine = contraintFormula.get(ENGINE).toString();

        if (localEngine == null) {
            throw new RuntimeException("Capraz Kontrol Tanımlama Formülü İçin Tanımlanamayan Engine<br/>");
        }

        switch (localEngine) {
            case "jeval":
                this.engineType = EngineType.JEVAL;
                break;
            case "javascript":
                this.engineType = EngineType.JAVA_SCRIPT;
                break;
            case "mongodbFunction":
                this.engineType = EngineType.MONGODB_FUNCTION;
                break;
            default:
                throw new RuntimeException("Capraz Kontrol Tanımlama Formülü İçin Tanımlanamayan Engine<br/>");
        }

        this.relationsPresentation = contraintFormula.get(RELATIONS_PRESENTATION).toString();
        this.name = contraintFormula.get(NAME).toString();
        this.resultType = contraintFormula.get(RESULT_TYPE).toString();
        this.transferOrder = ((Number) contraintFormula.get(TRANSFER_ORDER)).intValue();
        this.description = contraintFormula.get(DESCRIPTION).toString();

        Object localControlFunction = contraintFormula.get(CONTROL_FUNCTION);
        this.controlFunction = localControlFunction == null ? null : ((Code) localControlFunction).getCode();

        this.relations = contraintFormula.get(RELATIONS).toString();

        variables = new ArrayList<>();

        Document variablesDbo = (Document) contraintFormula.get(VARIABLES);
        if (variablesDbo != null) {
            for (String variableKey : variablesDbo.keySet()) {
                variables.add(new MyConstraintVariable(variablesDbo.get(variableKey), variableKey));
            }
        }

        Object check = contraintFormula.get("mySmallLisp");

        if (check instanceof String) {
            checkList = new ArrayList();
            checkList.add(check);
        } else if (check instanceof List) {
            checkList = (List) check;
        } else {
            checkList = new ArrayList();
        }
    }

    public EngineType getEngineType() {
        return engineType;
    }

    public MyControlResult getControlResult() {
        return controlResult;
    }

    public void setControlResult(MyControlResult controlResult) {
        this.controlResult = controlResult;
    }

    public String getName() {
        return name;
    }

    public int getTransferOrder() {
        return transferOrder;
    }

    public String getControlFunction() {
        return controlFunction;
    }

    public List<MyConstraintVariable> getVariables() {
        return variables;
    }

    public List getCheckList() {
        return Collections.unmodifiableList(checkList);
    }

    public ObjectId getId() {
        return id;
    }

    public String getResultType() {
        return resultType;
    }

    public String getRelations() {
        return relations;
    }

    public String getDescription() {
        return description;
    }

    public String getRelationsPresentation() {
        return relationsPresentation;
    }

    public enum EngineType {
        JEVAL,
        JAVA_SCRIPT,
        MONGODB_FUNCTION
    }

}
