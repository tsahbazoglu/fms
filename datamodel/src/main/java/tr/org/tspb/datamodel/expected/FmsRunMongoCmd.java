package tr.org.tspb.datamodel.expected;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import tr.org.tspb.dao.MyMap;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface FmsRunMongoCmd {

    public void dynamicButtonExecute(String db, String code, MyMap crudObject);

    public Boolean calcActionValue(String db, String code, Map<String, Serializable> searchObject, Set keySet);

}
