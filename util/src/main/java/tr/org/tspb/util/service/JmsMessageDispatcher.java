package tr.org.tspb.util.service;

import java.io.Serializable;
import java.text.MessageFormat;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import org.slf4j.Logger;
import tr.org.tspb.util.stereotype.MyController;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyController
public class JmsMessageDispatcher implements Serializable {

    @Inject
    private Logger logger;

    public JmsMessageDispatcher() {

    }

    public void sendRequestData(QueueConnectionFactory queueConnectionFactory,//
            Queue queue, //
            Serializable serializable) {

        QueueConnection queueConnection = null;
        try {
            queueConnection = queueConnectionFactory.createQueueConnection();
            logger.info(MessageFormat.format("JMS Queue Connection Client ID is : {0}", queueConnection.getClientID()));
            //queueConnection.setClientID("eys");
            //MQJMSRA_DC2001: Unsupported:setClientID():inACC=false:connectionId=2867153105572952320
            Session session = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(queue);
            //
            ObjectMessage objectMessage = session.createObjectMessage();
            objectMessage.setObject(serializable);
            messageProducer.send(objectMessage);
            messageProducer.close();
            //
        } catch (JMSException ex) {
            logger.error("error occured", ex);
        } finally {
            try {
                if (queueConnection != null) {
                    queueConnection.close();
                }
                logger.info(MessageFormat.format("JMS Queue Connection had been successfully closed.", ""));

            } catch (JMSException ex) {
                logger.error("error occured", ex);
            }
        }
    }
}
