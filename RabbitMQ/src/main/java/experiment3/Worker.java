package experiment3;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

// Consumer
public class Worker {

    private final static String QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        // Connection and Channel implement java.io.Closeable (no need to explicitly close them)
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        // remember tasks if rabbitmq crashes
        boolean durable = true;
        // idempotent - it will only be created if it doesn't exist already.
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // dont give more than one message to a worker at a time
        // dispatch it to the next worker that is **not still busy**
        int prefetchCount = 1;
        channel.basicQos(prefetchCount); // accept only one unack-ed message at a time (see below)
        // buffer the messages that are pushed asynchronously
        DeliverCallback deliverCallback = (conumserTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            try {
                doWork(message);
            } finally {
                System.out.println(" [x] Done");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {});
    }

    private static void doWork(String task) {
        for (char c : task.toCharArray()) {
            if (c == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}