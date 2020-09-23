package tr.org.tspb.pojo;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class PostSaveResult {

    private boolean result;
    private String msg;
    private MessageGuiType messageGuiType;
    private ErrType errType;

    private static PostSaveResult instance = new PostSaveResult();
    private static PostSaveResult errInstance;

    private PostSaveResult() {
        this.msg = "Verileriniz Kaydedildi";
    }

    public PostSaveResult(boolean result, String msg, MessageGuiType messageGuiType, ErrType errType) {
        this.result = result;
        this.msg = msg;
        this.messageGuiType = messageGuiType;
        this.errType = errType;
    }

    public static PostSaveResult getNullSingleton() {
        return instance;
    }

    public static PostSaveResult getErrSingleton() {
        if (errInstance == null) {
            errInstance = new PostSaveResult();
            errInstance.result = true;
            errInstance.msg = "<ul>"
                    + "<li><font color='red'>Kaydetme İşlemi Gerçekleştirilemedi.</font></li>"
                    + "<li>\"Birlik Temsilcisi\" yalnız bir defa seçilebilmektedir. <br/>Daha önce seçim yaptınız.</li>"
                    + "</ul>";
        }

        return instance;
    }

    public enum MessageGuiType {
        facesMessage,
        popup
    }

    public enum ErrType {
        error,
        info,
        warn
    }

    public boolean isResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public MessageGuiType getMessageGuiType() {
        return messageGuiType;
    }

    public ErrType getErrType() {
        return errType;
    }

}
