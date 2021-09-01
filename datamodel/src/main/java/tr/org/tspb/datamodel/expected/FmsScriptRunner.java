package tr.org.tspb.datamodel.expected;

import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import tr.org.tspb.pojo.RoleMap;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface FmsScriptRunner {

    public Document uysdbCommonFindOneLdapUID(String ldapUID);

    public Document runCommand(String db, String code, Object... args);

    public List<Document> uysdbCommonFindSort(Document document, Document document0);

    public Document findOne(String db, String collection, Document document);

    public List<ObjectId> findObjectIds(String db, String collection, Document document, String projection);

    public long count(String db, String collection, Document document);

    public Document replaceToDolar(Document document);

    public boolean runActionAsDbTableFilterResult(Document actionDoc, RoleMap roleMap, Map filter);

    public List<Document> aggreagate(String db, String table, List<Document> listOfAggrDoc);

}
