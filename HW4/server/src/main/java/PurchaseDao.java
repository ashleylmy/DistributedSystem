import java.util.UUID;
import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.swagger.client.model.PurchaseItems;
import model.PurchaseRecord;
import org.apache.commons.dbcp2.*;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;


public class PurchaseDao {
    private static final AmazonDynamoDB client;
    static DynamoDB dynamoDB;
    private static Table table;

    //private static final DynamoDBMapper mapper;

    static {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setMaxConnections(6000);
        clientConfiguration.setMaxErrorRetry(3);

        client = AmazonDynamoDBClientBuilder.standard().withClientConfiguration(clientConfiguration)
                .withRegion(Regions.US_EAST_1).withCredentials(new InstanceProfileCredentialsProvider(false)).
                        build();
        dynamoDB= new DynamoDB(client);
        table=dynamoDB.getTable("PurchaseRecords");
        //mapper = new DynamoDBMapper(client);
    }

    public static void insertIntoDynamo(PurchaseRecord purchase) {
        try {
            String uuid = UUID.randomUUID().toString();
            Item item = new Item()
                    .withPrimaryKey("Id", uuid)
                    .withNumber("storeId", purchase.getStoreId())
                    .withNumber("custId", purchase.getCustId())
                    .withString("date", purchase.getDate())
                    .withJSON("orderDetails", new Gson().toJson(purchase.getOrderDetails().toString()));
            PutItemOutcome putItemOutcome = table.putItem(item);
            //client.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    


}