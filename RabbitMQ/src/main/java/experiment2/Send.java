package experiment2;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.nio.charset.StandardCharsets;

// Publisher
public class Send {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        // Connection and Channel implement java.io.Closeable (no need to explicitly close them)
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel();
        ){

            // idempotent - it will only be created if it doesn't exist already.
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello world!";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");


        } catch (Exception e ) {
            e.printStackTrace();
        }
    }
}
