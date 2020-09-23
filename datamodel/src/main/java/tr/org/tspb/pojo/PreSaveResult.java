package tr.org.tspb.pojo;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class PreSaveResult {

    private boolean result;
    private String msg;
    private MessageGuiType messageGuiType;
    private ErrType errType;

    private static PreSaveResult instance = new PreSaveResult();
    private static PreSaveResult errInstance;

    private PreSaveResult() {
    }

    public PreSaveResult(boolean result, String msg, MessageGuiType messageGuiType, ErrType errType) {
        this.result = result;
        this.msg = msg;
        this.messageGuiType = messageGuiType;
        this.errType = errType;
    }

    public static PreSaveResult getNullSingleton() {
        return instance;
    }

    public static PreSaveResult getErrSingleton() {
        if (errInstance == null) {
            errInstance = new PreSaveResult();
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
