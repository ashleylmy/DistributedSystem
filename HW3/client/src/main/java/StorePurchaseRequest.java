import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.PurchaseApi;
import io.swagger.client.model.Purchase;
import io.swagger.client.model.PurchaseItems;
import org.apache.log4j.Logger;

import java.util.ArrayList;


public class StorePurchaseRequest {
    public int storeID;
    public int successRequests = 0;
    public int failedRequests = 0;
    public int totalRequest = 0;
    public int custPerStore;
    private int maxStoreItems;
    private int numItemsPerPurchase;
    private final Logger logger = Logger.getLogger(StorePurchaseRequest.class);

    public StorePurchaseRequest(int storeID, int custPerStore, int maxItems, int numItemsPerPurchase) {
        this.storeID = storeID;
        this.custPerStore = custPerStore;
        this.maxStoreItems = maxItems;
        this.numItemsPerPurchase=numItemsPerPurchase;
    }

    public ArrayList<ResponseResult> hourlyPurchase(PurchaseApi apiInstance, int numPurchases, String date) {
        ArrayList<ResponseResult> responses = new ArrayList<>();
        for (int i = 0; i < numPurchases; i++) {
            Purchase body = generatePurchaseBody(numItemsPerPurchase); // Purchase | items purchased
            Integer cusID = generateCustID();

            long startTime = System.currentTimeMillis();
            try {
                ApiResponse<Void> response = apiInstance.newPurchaseWithHttpInfo(body, storeID, cusID, date);
                long endTime = System.currentTimeMillis();
                responses.add(new ResponseResult(startTime, endTime - startTime, response.getStatusCode()));
                successRequests += 1;
               // updatePurchaseItems(body);
            } catch (ApiException e) {
                long endTime = System.currentTimeMillis();
                responses.add(new ResponseResult(startTime, endTime - startTime, e.getCode()));
                logger.trace(e);
                e.printStackTrace();
                failedRequests += 1;
            }
            totalRequest += 1;
        }
        return responses;
    }

    private void updatePurchaseItems(Purchase body) {
        if (body.getItems().size() != 0) {
            for (PurchaseItems item : body.getItems()) {
                item.setNumberOfItems(1);
            }
        }
    }

    /**
     * generate random Purchase Items
     */

    private Purchase generatePurchaseBody (int numItemsPerPurchase) {
        Purchase purchaseItems= new Purchase();
        for(int i=0; i<numItemsPerPurchase; i++) {
            PurchaseItems item=new PurchaseItems();
            Integer itemID= (int)(Math.random() * maxStoreItems);
            item.setItemID(String.valueOf(itemID));
            item.setNumberOfItems(1);
            purchaseItems.addItemsItem(item);
        }
        return  purchaseItems;
    }

    /**
     * generate random customer IDs
     * value between (storeIDx1000) and (storeIDx1000)+number of customers/store.
     * @return customerID
     */
    private Integer generateCustID(){
        return (int)(Math.random()*custPerStore)+storeID*1000;
    }
}