package tr.org.tspb.dao;

import com.mongodb.BasicDBObject;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface FmsFile {

    public String getId();

    public String getMimeType();

    public String getHash();

    public String getHashFile();

    public String getName();

    public String getContent();

    public BasicDBObject getMetadata();

    public String getUploadDateAsString();

}
