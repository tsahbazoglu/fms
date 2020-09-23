package tr.org.tspb.pojo.ui;

import java.util.List;
import java.util.Map;
import tr.org.tspb.dao.MyField;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class TwoDimDlgUi {

    private String header;
    private int fileLimit;
    private String mongoUploadFileType;
    private String invalidFileMessage;
    private Map<String, MyField> componentMap;
    private List<MyField> componentList;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Map<String, MyField> getComponentMap() {
        return componentMap;
    }

    public void setComponentMap(Map<String, MyField> componentMap) {
        this.componentMap = componentMap;
    }

    public List<MyField> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<MyField> componentList) {
        this.componentList = componentList;
    }

    public int getFileLimit() {
        return fileLimit;
    }

    public void setFileLimit(int fileLimit) {
        this.fileLimit = fileLimit;
    }

    public String getMongoUploadFileType() {
        return mongoUploadFileType;
    }

    public void setMongoUploadFileType(String mongoUploadFileType) {
        this.mongoUploadFileType = mongoUploadFileType;
    }

    public String getInvalidFileMessage() {
        return invalidFileMessage;
    }

    public void setInvalidFileMessage(String invalidFileMessage) {
        this.invalidFileMessage = invalidFileMessage;
    }

}
