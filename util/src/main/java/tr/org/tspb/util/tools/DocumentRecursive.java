package tr.org.tspb.util.tools;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.Document;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class DocumentRecursive implements Map, Serializable {

    private static final DateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private MongoDbUtilIntr mongoDbUtil;
    private Map manualDbRefs;
    private Document document;

    public DocumentRecursive(Document document, Map manualDbRefs, MongoDbUtilIntr mongoDbUtil) {
        this.document = document;
        this.manualDbRefs = manualDbRefs;
        this.mongoDbUtil = mongoDbUtil;
    }

    @Override
    public int size() {
        return document.size();
    }

    @Override
    public boolean isEmpty() {
        return document.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return document.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return document.containsValue(o);
    }

    @Override
    public Object put(Object k, Object v) {
        return document.put((String) k, v);
    }

    @Override
    public Object remove(Object o) {
        return document.remove((String) o);
    }

    @Override
    public void putAll(Map map) {
        document.putAll(map);
    }

    @Override
    public void clear() {
        document.clear();
    }

    @Override
    public Set keySet() {
        return document.keySet();
    }

    @Override
    public Collection values() {
        return document.values();
    }

    @Override
    public Set entrySet() {
        return document.entrySet();
    }

    @Override
    public Object get(Object o) {
        if (manualDbRefs != null && manualDbRefs.containsKey(o)) {

            Map dbRef = (Map) manualDbRefs.get(o);

            Document dbo = mongoDbUtil.cacheAndGet(dbRef);
            if (dbo == null) {
                return null;
            }

            List<String> viewKeys = (List<String>) dbRef.get("viewKeys");

            if (viewKeys == null) {
                return dbo;
            } else {
                StringBuilder sb = new StringBuilder();
                for (Iterator<String> iterator = viewKeys.iterator(); iterator.hasNext();) {
                    String next = iterator.next();
                    Object value = dbo.get(next);
                    if (value instanceof Date) {
                        sb.append(SIMPLE_DATE_FORMAT.format((Date) value));
                    } else {
                        sb.append(value == null ? "" : value.toString());
                    }
                    if (iterator.hasNext()) {
                        sb.append(" - ");
                    }
                }
                return sb.toString();
            }
        }

        Object value = document.get((String) o);

        //FIXME We are not sure that mapRowData has implemented all fields 
        //value = "";//when it is null then the releated converter (JsFunctionConverter) does not take a place
        /*
         if (value == null) {
         if ("trading_volume_client_ratio".equals(o)) {
         return "";
         } else if ("trading_volume_portfolio_ratio".equals(o)) {
         return "";
         } else if ("abroad_ratio".equals(o)) {
         return "";
         } else if ("internal_ratio".equals(o)) {
         return "";
         } else if ("sum_client_portfolio".equals(o)) {
         return "";
         }
         }
         */
        if (value == null) {
            return "";
        }

        return document.get((String) o);
    }
}
