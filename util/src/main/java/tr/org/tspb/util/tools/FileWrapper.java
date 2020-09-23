package tr.org.tspb.util.tools;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class FileWrapper {

    private Object byteArrayResource;
    private String name;
    public FileWrapper() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getByteArrayResource() {
        return byteArrayResource;
    }

    public void setByteArrayResource(Object byteArrayResource) {
        this.byteArrayResource = byteArrayResource;
    }

}
