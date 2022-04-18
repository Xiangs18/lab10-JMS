import jakarta.annotation.Resource;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

/*
 * MyQueueListener is a Message Listener that will listen to jms/myQueue.  Whenever a message
 * is available in that Queue, the onMessage method will be called with the available message.
 */

// This creates the mapping of this MessageListener to the appropriate Queue
@MessageDriven(mappedName = "jms/myQueue", activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class MyQueueListener implements MessageListener {

    // Lookup the ConnectionFactory using resource injection and assign to cf
    @Resource(mappedName = "jms/myConnectionFactory")
    private ConnectionFactory cf;
    // lookup the Queue using resource injection and assign to q
    @Resource(mappedName = "jms/myQueue3")
    private Queue q;

    /*
     * When a message is available in jms/myQueue, onMessage is called.
     */
    public void onMessage(Message message) {
        try {
            // Since there can be different types of Messages, make sure this is the right type.
            if (message instanceof TextMessage) {
                TextMessage tm = (TextMessage) message;
                String tmt = tm.getText();
                System.out.println("MyQueueListener received: " + tmt);
                writeToListner2(tmt);
            } else {
                System.out.println("I don't handle messages of this type");
            }
        } catch (JMSException e) {
            System.out.println("JMS Exception thrown" + e);
        } catch (Throwable e) {
            System.out.println("Throwable thrown" + e);
        }
    }


    public void writeToListner2(String message) {
        // Get the value of the simpleTextMessage parameter in the GET request
        String val = message;

        try {
            // With the ConnectionFactory, establish a Connection, and then a Session on that Connection
            Connection con = cf.createConnection();
            Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            con.start(); // Always remember to start the connection

            /*
             * You send and receive messages to/from the queue via a session. We
             * want to send, making us a MessageProducer. Therefore, create a
             * MessageProducer for the session
             */
            MessageProducer writer = session.createProducer(q);

            /*
             * The message can be text, a byte stream, a Java object, or a
             * attribute/value Map We want to send a text message. BTW, a text
             * message can be a string, or it can be a JSON, XML, or SOAP object.
             */
            TextMessage msg = session.createTextMessage();
            msg.setText(val);

            // Send the message to the destination Queue
            writer.send(msg);

//            System.out.println("Sending message to QueueListener2");
            System.out.println("Sending message to FetchResponseServet");

            // Close the connection
            con.close();
        } catch (Exception e) {
            System.out.println("Servlet through exception " + e);
        }
    }
}

