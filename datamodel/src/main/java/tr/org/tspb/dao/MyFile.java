package tr.org.tspb.dao;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import static tr.org.tspb.constants.ProjectConstants.SIMPLE_DATE_FORMAT__1;

/**
 *
 * @author Telman Şahbazoğlu
 *
 * https://stackoverflow.com/questions/36175190/gridfsdownloadstream-can-not-read-all-data
 *
 */
public class MyFile implements FmsFile {

    private final String id;
    private final String mimeType;
    private final String hash;
    private final String hashFile;
    private final String name;
    private String content;
    private final Document metadata;
    private final String uploadDateAsString;
    private byte[] bytes;
    private InputStream inputStream;

    public MyFile(GridFSBucket gridFSBucket, GridFSFile gridFSDBFile) throws IOException {
        this.id = gridFSDBFile.getId().asObjectId().getValue().toString();
        this.mimeType = gridFSDBFile.getContentType();
        this.hash = gridFSDBFile.getMD5();
        this.hashFile = "UYS_SHA256";
        this.name = gridFSDBFile.getFilename();
        this.metadata = gridFSDBFile.getMetadata();
        this.inputStream = gridFSBucket.openDownloadStream(new ObjectId(id));
        this.content = DigestUtils.sha256Hex(gridFSBucket.openDownloadStream(new ObjectId(id)));
        this.uploadDateAsString = gridFSDBFile.getUploadDate() == null ? null : SIMPLE_DATE_FORMAT__1.format(gridFSDBFile.getUploadDate());
    }

    public MyFile withBytes() throws IOException {
        this.bytes = IOUtils.toByteArray(this.inputStream);
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public String getHashFile() {
        return hashFile;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public Document getMetadata() {
        return metadata;
    }

    @Override
    public String getUploadDateAsString() {
        return uploadDateAsString;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

}
