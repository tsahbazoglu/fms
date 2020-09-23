package tr.org.tspb.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFSDBFile;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import static tr.org.tspb.constants.ProjectConstants.SIMPLE_DATE_FORMAT__1;
import static tr.org.tspb.constants.ProjectConstants.UPLOAD_DATE;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyFile implements FmsFile {

    private final String id;
    private final String mimeType;
    private final String hash;
    private final String hashFile;
    private final String name;
    private final String content;
    private final BasicDBObject metadata;
    private final String uploadDateAsString;
    private byte[] bytes;
    private final InputStream inputStream;

    public MyFile(GridFSDBFile gridFSDBFile) throws IOException {
        this.inputStream = gridFSDBFile.getInputStream();
        this.id = gridFSDBFile.getId().toString();
        this.mimeType = gridFSDBFile.getContentType();
        this.hash = gridFSDBFile.getMD5();
        this.hashFile = "UYS_SHA256";
        this.name = gridFSDBFile.getFilename();
        this.metadata = (BasicDBObject) gridFSDBFile.getMetaData();
        this.content = DigestUtils.sha256Hex(gridFSDBFile.getInputStream());
        this.uploadDateAsString = gridFSDBFile.get(UPLOAD_DATE) == null ? null : SIMPLE_DATE_FORMAT__1.format(gridFSDBFile.get(UPLOAD_DATE));
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
    public BasicDBObject getMetadata() {
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
