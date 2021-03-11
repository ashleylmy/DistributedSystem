package model;

import io.swagger.client.model.Purchase;
import io.swagger.client.model.PurchaseItems;

import java.util.List;
import java.util.UUID;


public class PurchaseRecord {
    private int storeId;
    private int custId;
    private String date;
    private List<PurchaseItems> orderDetails;

    public PurchaseRecord(int storeId, int userId, String date, List<PurchaseItems> orderDetails) {
        this.storeId = storeId;
        this.custId = userId;
        this.date = date;
        this.orderDetails=orderDetails;
    }

    public int getStoreId() {
        return storeId;
    }

    public int getCustId() {
        return custId;
    }

    public String getDate() {
        return date;
    }

    public String getOrderDetails() {
        return orderDetails.toString();
    }
}
