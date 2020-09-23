package tr.org.tspb.ejb.mdb;

import tr.org.tspb.shared.SharedValues;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import tr.org.tspb.mail.EmailData;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.MimeUtility;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MessageDriven(mappedName = MessageDriverBeanEmailQueueBean.JMS_EMAIL_QUEUE, activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MessageDriverBeanEmailQueueBean implements MessageListener {

    public static final String JMS_EMAIL_QUEUE = "jms/fmsEmailQueue";
    private final String CUSTOM_RESOURCE_MAIL_EXTERNAL = "mail/fmsExternal";
    private final String CUSTOM_RESOURCE_MAIL_INTERNAL = "mail/fmsInternal";
    private final String CUSTOM_RESOURCE_ENVIRONMENT = "fms/environment";

    // @Inject
    // @LoggerProducerQualifier(type = LogType.JAVA_UTIL_LOGGING)
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Resource(lookup = CUSTOM_RESOURCE_MAIL_EXTERNAL, type = javax.mail.Session.class)
    private Session sessionTspakbOutsideMail;

    @Resource(lookup = CUSTOM_RESOURCE_MAIL_INTERNAL, type = javax.mail.Session.class)
    private Session sessionTspakbInternalMail;

    @Resource(lookup = CUSTOM_RESOURCE_ENVIRONMENT)
    private String selectProperties;

    public MessageDriverBeanEmailQueueBean() {
    }

    @Override
    public void onMessage(Message message) {

        if (message instanceof ObjectMessage) {

            try {
                EmailData emailData = (EmailData) ((ObjectMessage) message).getObject();

                Session session = null;
                Transport transport = null;

                if (SharedValues.TEST.equals(selectProperties)) {
                    session = sessionTspakbInternalMail;
                } else if (SharedValues.LOCAL.equals(selectProperties)) {
                    session = sessionTspakbInternalMail;
                } else if (SharedValues.PRODUCT.equals(selectProperties)) {
                    session = sessionTspakbOutsideMail;
                }

                sendEmail(transport, session, emailData);

            } catch (JMSException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    private void sendEmail(Transport transport, Session session, EmailData emailData) {

        try {

            transport = session.getTransport("smtp");

            javax.mail.Message msg = new MimeMessage(session);

            msg.setFrom();
            msg.setRecipients(javax.mail.Message.RecipientType.TO, //
                    InternetAddress.parse(emailData.getRecipients(), false));

            if (emailData.getBccRecipients() != null) {
                msg.setRecipients(RecipientType.BCC, InternetAddress.parse(emailData.getBccRecipients(), false));
            }

            msg.addHeader("Content-Type", "text/html;charset=\"UTF-8\"");

            try {
                // msg.setSubject(new String(emailData.getSubject().getBytes(), "UTF-8"));
                msg.setSubject(MimeUtility.encodeText(emailData.getSubject(), "UTF-8", "Q"));
            } catch (UnsupportedEncodingException ex) {
                msg.setSubject(emailData.getSubject());
                logger.log(Level.SEVERE, null, ex);
            }

            msg.setSentDate(new Date());

            /**
             * **********************************************************
             */
            byte[] attachmentBytes = emailData.getAttachment();

            Multipart multipart = null;

            if (attachmentBytes != null) {
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(emailData.getMessage(), "text/html;charset=\"UTF-8\"");
                multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                DataSource source = new ByteArrayDataSource(attachmentBytes, "application/pdf");

                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName("ek.pdf");
                multipart.addBodyPart(messageBodyPart);
            }
            /**
             * **********************************************************
             */
            if (multipart == null) {
                msg.setContent(emailData.getMessage(), "text/html;charset=\"UTF-8\"");
            } else {
                msg.setContent(multipart);
            }

            transport.connect();

            //Log EmailData
            try {
                JAXBContext contextEmailData = JAXBContext.newInstance(EmailData.class);
                Marshaller marshallEmailData = contextEmailData.createMarshaller();
                marshallEmailData.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshallEmailData.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8");
                StringWriter sw = new StringWriter();
                marshallEmailData.marshal(emailData, sw);//FIXME . Provide Logger out !!!
                logger.info(MessageFormat.format("\nTrying to send an email via {0}...\n{1}",
                        session.getProperties().get("mail.host"), sw.toString()));
            } catch (JAXBException ex) {
                logger.log(Level.SEVERE, null, ex);
            }

            Transport.send(msg);

        } catch (MessagingException ex) {
            logger.log(Level.SEVERE, "Could not send an email to :".concat(emailData.getRecipients()));
            logger.log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (transport != null) {
                    transport.close();
                }
            } catch (MessagingException ex) {
                logger.log(Level.SEVERE, null, ex);
            }

        }
    }
}
