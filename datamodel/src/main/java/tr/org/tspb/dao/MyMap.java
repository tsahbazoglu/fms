package tr.org.tspb.dao;

import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import java.util.HashMap;
import org.bson.Document;
import tr.org.tspb.constants.ProjectConstants;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyMap extends HashMap {

    public MyMap() {
        super();
    }

    @Override
    public Object get(Object key) {
        return super.get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        return super.put(key, value);
    }

    public boolean isNew() {
        return !this.containsKey(MONGO_ID);
    }

    public void initUnSet() {
        this.put(ProjectConstants.DOLAR_UNSET, new Document());
    }

    public void removeUnSetKey(String unsetKey) {
        ((Document) this.get(ProjectConstants.DOLAR_UNSET)).remove(unsetKey);
    }

    public void addUnSetKey(String unsetKey) {
        ((Document) this.get(ProjectConstants.DOLAR_UNSET)).put(unsetKey, true);
    }
}
