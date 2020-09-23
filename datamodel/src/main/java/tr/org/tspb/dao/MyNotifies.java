package tr.org.tspb.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import org.bson.types.Code;
import tr.org.tspb.constants.ProjectConstants;
import static tr.org.tspb.constants.ProjectConstants.RETVAL;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyNotifies {

    public final static String MONGO_KEY = "notifies";

    private String notifyCode;
    private String logDB;
    private String logTable;
    private String type;//[sms,email]
    private String to;
    private String from;
    private String subject;
    private String content;
    private String status;
    private Boolean enable;
    private String enableCode;
    private String toCode;
    private String subjectCode;
    private String contentCode;
    private FmsScriptRunner scriptRunner;
    private Set<String> roles;

    private List<MyNotifies> list;

    public boolean isEmail() {
        return "email".equals(this.type);
    }

    public List<MyNotifies> getList() {
        return Collections.unmodifiableList(list);
    }

    public String getNotifyCode() {
        return notifyCode;
    }

    public String getLogDB() {
        return logDB;
    }

    public String getLogTable() {
        return logTable;
    }

    public String getType() {
        return type;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    public Boolean isEnable() {
        return Boolean.TRUE.equals(enable);
    }

    public void reEnable(MyMap crud) {
        if (enableCode != null) {
            Map tmp = new HashMap(crud);
            tmp.remove(ProjectConstants.INODE);
            this.enable = scriptRunner.runCommand("test", enableCode, tmp, roles)
                    .getBoolean(RETVAL);
        }
    }

    public void reTo(MyMap crud) {
        if (toCode != null) {
            Map tmp = new HashMap(crud);
            tmp.remove(ProjectConstants.INODE);
            this.to = scriptRunner.runCommand("test", toCode, tmp, roles)
                    .getString(RETVAL);
        }
    }

    public void reSubject(MyMap crud) {
        if (subjectCode != null) {
            Map tmp = new HashMap(crud);
            tmp.remove(ProjectConstants.INODE);
            this.subject = scriptRunner.runCommand("test", subjectCode, tmp, roles)
                    .getString(RETVAL);
        }
    }

    public void reContent(MyMap crud) {
        if (contentCode != null) {
            Map tmp = new HashMap(crud);
            tmp.remove(ProjectConstants.INODE);
            this.content = scriptRunner.runCommand("test", contentCode, tmp, roles)
                    .getString(RETVAL);
        }
    }

    public static class Builder {

        private final MyNotifies myNotifies;

        public Builder(List basicDBList, FmsScriptRunner scriptRunner, Set<String> roles) {

            this.myNotifies = new MyNotifies();
            this.myNotifies.list = new ArrayList<>();

            for (Object obj : basicDBList) {
                Document doc = (Document) obj;
                this.myNotifies.list.add(withDoc(doc, scriptRunner, roles));
            }
        }

        private MyNotifies withDoc(Document doc, FmsScriptRunner scriptRunner, Set<String> roles) {
            MyNotifies x = new MyNotifies();
            x.notifyCode = doc.getString("notifyCode");
            x.logDB = doc.getString("logDB");
            x.logTable = doc.getString("logTable");
            x.type = doc.getString("type");
            x.from = doc.getString("from");
            x.status = doc.getString("status");
            x.scriptRunner = scriptRunner;
            x.roles = roles;

            Object enable = doc.get("enable");
            if (enable instanceof Code) {
                x.enableCode = ((Code) enable).getCode();
                x.enable = scriptRunner.runCommand("test", x.enableCode, null, roles).getBoolean(RETVAL);
            } else {
                x.enable = Boolean.TRUE.equals(doc.get("enable"));
            }

            Object to = doc.get("to");
            if (to instanceof Code) {
                x.toCode = ((Code) to).getCode();
                x.to = scriptRunner.runCommand("test", x.toCode, null, roles).getString(RETVAL);
            } else {
                x.to = doc.getString("to");
            }

            Object subject = doc.get("subject");
            if (subject instanceof Code) {
                x.subjectCode = ((Code) subject).getCode();
                x.subject = scriptRunner.runCommand("test", x.subjectCode, null, roles).getString(RETVAL);
            } else {
                x.subject = doc.getString("subject");
            }

            Object content = doc.get("content");
            if (content instanceof Code) {
                x.contentCode = ((Code) content).getCode();
                x.content = scriptRunner.runCommand("test", x.contentCode, null, roles).getString(RETVAL);
            } else {
                x.content = doc.getString("content");
            }

            return x;
        }

        public MyNotifies build() {
            return this.myNotifies;
        }
    }

}
