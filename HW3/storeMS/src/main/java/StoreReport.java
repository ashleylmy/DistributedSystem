import com.rabbitmq.client.*;

public class StoreReport {

    private static final String RPC_QUEUE_NAME = "rpc_queue";


    private static String query(String id) {
        //figure out which query func to call
        if (id.charAt(0) == 'S') {
            String storeID = id.substring(5);
            System.out.println(storeID);
            return StoreDao.queryStore(storeID);
        } else{
            String itemID = id.substring(4);
            System.out.println(itemID);
            return StoreDao.queryItem(itemID);
        }
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        //TODO: figure out how to setup rabbitmq on aws
        factory.setHost("18.206.212.169");
        factory.setPort(5672);
        factory.setUsername("user1");
        factory.setPassword("user1");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicQos(1);

            System.out.println(" [x] Awaiting RPC requests");

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();
                String response = "";
                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    response = query(message);
                    System.out.println(" [.] (" + message + ")");
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
            // Wait and be prepared to consume the message from RPC client.
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
