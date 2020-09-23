package tr.org.tspb.dao.refs;

import org.bson.types.ObjectId;

/**
 *
 * @author Telman Şahbazoğlu
 */
public abstract class PlainRecord {

    public abstract ObjectId getObjectId();

    public abstract String getObjectIdStr();

    public abstract String getName();

    public abstract void setObjectId(ObjectId objectId);

    public abstract void setObjectIdStr(String objectIdStr);

    public abstract void setName(String name);

    public abstract boolean isEmpty();

}
