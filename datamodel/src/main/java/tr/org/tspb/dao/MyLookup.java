package tr.org.tspb.dao;

import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyLookup {

    private final String db;
    private final String table;
    private final String fk;
    private final Bson fp;
    private final String as;

    public MyLookup(Document docLookup) {
        this.db = docLookup.getString("db");
        this.table = docLookup.getString("table");
        this.fk = docLookup.getString("fk");
        this.fp = docLookup.get("fp", Bson.class);
        this.as = docLookup.getString("as");
    }

    /**
     *
     * @return
     */
    public String getDb() {
        return db;
    }

    /**
     *
     * @return
     */
    public String getTable() {
        return table;
    }

    /**
     *
     * @return
     */
    public String getFk() {
        return fk;
    }

    /**
     *
     * @return
     */
    public Bson getFp() {
        return fp;
    }

    /**
     *
     * @return
     */
    public String getAs() {
        return as;
    }

}
