package tr.org.tspb.dp.prototype;

import org.bson.Document;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MongoRecord implements Prototype {

    private Document dbo;

    private MongoRecord(Document dbo) {
        this.dbo = dbo;
    }

    @Override
    public MongoRecord getClone() {
        return new MongoRecord(this.dbo);
    }

}
