package tr.org.tspb.mail;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Telman Şahbazoğlu
 */
@XmlRootElement
public class EmailData implements Serializable {

    private String subject;
    private String message;
    private String recipients;
    private String bccRecipients;
    private byte[] inputStream;

    public EmailData() {
        //no-arg constructor have to be exists for XmlRootElement
    }

    public EmailData(String subject, String message, String recipients, String bccRecipients) {
        this.subject = subject;
        this.message = message;
        this.recipients = recipients;
        this.bccRecipients = bccRecipients;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the recipients
     */
    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    /**
     * @return the bccRecipients
     */
    public String getBccRecipients() {
        return bccRecipients;
    }

    /**
     * @param bccRecipients the bccRecipients to set
     */
    public void setBccRecipients(String bccRecipients) {
        this.bccRecipients = bccRecipients;
    }

    public byte[] getAttachment() {
        return inputStream;
    }

    public void setAttachment(byte[] inputStream) {
        this.inputStream = inputStream;
    }
}
