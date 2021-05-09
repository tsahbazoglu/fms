package tr.org.tspb.dao;

import org.bson.Document;

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

    public Document getMetadata();

    public String getUploadDateAsString();

}
