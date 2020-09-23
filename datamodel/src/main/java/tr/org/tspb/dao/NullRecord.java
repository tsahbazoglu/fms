package tr.org.tspb.dao;

import org.bson.types.ObjectId;
import tr.org.tspb.constants.ProjectConstants;
import tr.org.tspb.dao.refs.PlainRecord;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class NullRecord extends PlainRecord {

    private ObjectId objectId;
    private String objectIdStr;
    private String name;

    public NullRecord() {
        this.objectId = null;
        this.objectIdStr = null;
        this.name = ProjectConstants.SELECT_PLEASE;
    }

    @Override
    public ObjectId getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    @Override
    public String getObjectIdStr() {
        return objectIdStr;
    }

    @Override
    public void setObjectIdStr(String objectIdStr) {
        this.objectIdStr = objectIdStr;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

}
