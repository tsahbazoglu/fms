package tr.org.tspb.util.tools;

import java.util.List;
import org.bson.BsonJavaScript;
import org.bson.Document;
import tr.org.tspb.constants.ProjectConstants;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyExecDoc extends Document {

    public MyExecDoc(String code, List<Object> args) {
        put("$eval", code.replace(ProjectConstants.DIEZ, ProjectConstants.DOLAR));
        put("args", args);
        put("nolock", true);
    }

    public MyExecDoc(BsonJavaScript code, List<Object> args) {
        put("$eval", code);
        put("args", args);
        put("nolock", true);
    }

}
