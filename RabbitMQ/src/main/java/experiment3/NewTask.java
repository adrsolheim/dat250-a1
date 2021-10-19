package experiment3;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;

// Publisher
public class NewTask {
    private final static String QUEUE_NAME = "task_queue";

    public static void main(String[] args) {
        String message = String.join(" ", args);
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        // Connection and Channel implement java.io.Closeable (no need to explicitly close them)
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel();
        ){
            // remember tasks if rabbitmq crashes
            boolean durable = true;
            // idempotent - it will only be created if it doesn't exist already.
            channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }
}