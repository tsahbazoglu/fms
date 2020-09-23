package tr.org.tspb.wf;

import org.bson.Document;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyTransition {

    private String trigger;
    private String state;
    private String endpoint;
    private String srcpoint;

    public MyTransition() {
    }

    public MyTransition(Document dbo) {
        this.trigger = dbo.get("trigger").toString();
        this.state = dbo.get("state").toString();
        this.endpoint = dbo.get("endPoint").toString();
        this.srcpoint = dbo.get("srcPoint").toString();
    }

    public String getSrcpoint() {
        return srcpoint;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getState() {
        return state;
    }

    public String getEndpoint() {
        return endpoint;
    }

}
