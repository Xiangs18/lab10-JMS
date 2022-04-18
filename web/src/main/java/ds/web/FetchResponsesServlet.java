package ds.web;

import jakarta.annotation.Resource;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.jms.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "FetchResponsesServlet", urlPatterns = {"/DisplayResponses"})
public class FetchResponsesServlet extends HttpServlet {


    // Lookup the ConnectionFactory using resource injection and assign to cf
    @Resource(mappedName = "jms/myConnectionFactory")
    private ConnectionFactory cf;

    // Lookup the Queue using resource injection and assign to q
    @Resource(mappedName = "jms/myQueue3")
    private Queue q;

    private Connection con;
    private Session session;
    private MessageConsumer reader;

    public void init() {
        try {
            con = cf.createConnection();
            // Establish a Session on that Connection
            session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Be sure to start to connection
            con.start();
            // Create a MessageConsumer that will read from q
            reader = session.createConsumer(q);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            // We are going to do a simple response to the browser.  We are not using MVC in this example
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();

            // --------------------------------------------------
            // Then within the doGet() method, here is the code to receive from a queue
            // You need to add the code to handle the HTTP request and make the response.

            /*
             * You can now try to receive a message from q.  If you give a
             * time argument to receive, it will time out in that many milliseconds.
             * In this way you can receive until there are no more messages to be read
             * at this time from the q.
             */
            TextMessage tm = null;
            while ((tm = (TextMessage) reader.receive(1000)) != null) {
                // Do something with tm.getText() to add it to the HTTP response
                String tmt = tm.getText();
                out.println(tmt);
                out.flush();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
