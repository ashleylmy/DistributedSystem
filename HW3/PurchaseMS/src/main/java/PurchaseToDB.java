
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.*;
import model.PurchaseRecord;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PurchaseToDB {

    private final static String QUEUE_NAME="threadExQ1";


    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
        factory.setHost("18.206.212.169");
        factory.setPort(5672);
        factory.setUsername("user1");
        factory.setPassword("user1");
        final Connection connection = factory.newConnection();
        Runnable runnable = () -> {
            try {
                final Channel channel = connection.createChannel();
                channel.queueDeclare(QUEUE_NAME, true,false, false, null);
                // max one message per receiver
                channel.basicQos(1);
                System.out.println(" [*] Consumer keep alive ,waiting for messages, and then process them");
                Consumer consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope,
                                               com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) throws IOException {

                        String msg = new String(body, "UTF-8");
                        System.out.println("Callback thread ID = " + Thread.currentThread().getId() + " Received '" + msg + "'");
                        Gson gson = new Gson();
                        PurchaseRecord purchase = gson.fromJson(msg, PurchaseRecord.class);
                        writeToDatabase(purchase);
                    }
                };
                //process messages
                channel.basicConsume(QUEUE_NAME, true, consumer);
            } catch (IOException ex) {
                Logger.getLogger(PurchaseToDB.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        };
        // start threads and block to receive messages
        for (int i = 0; i < 300; i++) {
            Thread recv = new Thread(runnable);
            recv.start();
        }
    }

    public static void writeToDatabase(PurchaseRecord purchase) {
        PurchaseDao purchaseDao= new PurchaseDao();
        purchaseDao.createPurchase(purchase);
    }
}