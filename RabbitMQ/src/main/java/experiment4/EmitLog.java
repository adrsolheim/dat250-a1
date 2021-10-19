package experiment4;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;

public class EmitLog {
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try (Connection connection = connectionFactory.newConnection(); Channel channel = connection.createChannel();){
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            // non-durable, exclusive, autodelete queue with a generated name
            String queue_name = channel.queueDeclare().getQueue();
            // make exchange send messages to queue
            channel.queueBind(queue_name, EXCHANGE_NAME, "");

            String message = args.length < 1 ? "info: Hello World!" :
                    String.join(" ", args);
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }
}
