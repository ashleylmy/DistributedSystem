import io.swagger.client.ApiException;
import io.swagger.client.api.PurchaseApi;
import io.swagger.client.model.Purchase;
import io.swagger.client.model.PurchaseItems;
import org.apache.log4j.Logger;


public class StorePurchaseRequest {
    public int storeID;
    public int successRequests=0;
    public int failedRequests=0;
    public int totalRequest=0;
    public int custPerStore;
    private int maxStoreItems;
    private final Logger logger=Logger.getLogger(StorePurchaseRequest.class);

    public StorePurchaseRequest(int storeID, int custPerStore, int maxItems) {
        this.storeID = storeID;
        this.custPerStore=custPerStore;
        this.maxStoreItems=maxItems;
    }

    public void hourlyPurchase(PurchaseApi apiInstance, int numPurchases, String date) {
        for(int i=0; i<numPurchases; i++){
            Purchase body = generatePurchaseBody(numPurchases); // Purchase | items purchased
            Integer cusID = generateCustID();
            try {
                apiInstance.newPurchase(body, storeID, cusID, date);
                successRequests+=1;
                updatePurchaseItems(body);
            } catch (ApiException e) {
                System.err.println("Exception when calling PurchaseApi#newPurchase");
                logger.trace(e);
                e.printStackTrace();
                failedRequests+=1;
            }
            totalRequest+=1;
        }
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

    private Purchase generatePurchaseBody (int numPurchases) {
        Purchase purchaseItems= new Purchase();
        for(int i=0; i<numPurchases; i++) {
            PurchaseItems item=new PurchaseItems();
            Integer itemID= (int)(Math.random() * maxStoreItems);
            item.setItemID(String.valueOf(itemID));
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

