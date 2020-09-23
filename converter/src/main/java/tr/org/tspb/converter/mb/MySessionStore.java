package tr.org.tspb.converter.mb;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import org.bson.types.ObjectId;
import static tr.org.tspb.converter.base.ConverterAttrs.SPLITTER;

/**
 *
 * @author Telman Şahbazoğlu
 */
@Named
@SessionScoped
public class MySessionStore implements Serializable {

    public static final Map<String, ObjectId> map = new HashMap<String, ObjectId>();
    //  public static final Map<String, Document> mapBsonConverter = new HashMap<>();

    /**
     * Creates a new instance of MySessionStore
     */
    public MySessionStore() {
    }

    public static String createCacheKey(String projectKey, String upperNode, String formKey, String fieldKey, String code) {
        StringBuilder sb = new StringBuilder();
        sb.append(projectKey);
        sb.append(SPLITTER);
        sb.append(upperNode);
        sb.append(SPLITTER);
        sb.append(formKey);
        sb.append(SPLITTER);
        sb.append(fieldKey);
        sb.append(SPLITTER);
        sb.append(code);
        return sb.toString();
    }

    public static String createSessionKey(String projectKey, String upperNode, String formKey, String fieldKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(projectKey);
        sb.append(SPLITTER);
        sb.append(upperNode);
        sb.append(SPLITTER);
        sb.append(formKey);
        sb.append(SPLITTER);
        sb.append(fieldKey);
        return sb.toString();
    }
}
