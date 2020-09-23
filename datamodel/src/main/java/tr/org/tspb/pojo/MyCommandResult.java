package tr.org.tspb.pojo;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyCommandResult {

    private final boolean result;
    private final String msg;

    public MyCommandResult(boolean result, String msg) {
        this.result = result;
        this.msg = msg;
    }

    public boolean getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return new StringBuilder("result : ").append(String.valueOf(result)).append(", msg : ").append(msg).toString();
    }

}
