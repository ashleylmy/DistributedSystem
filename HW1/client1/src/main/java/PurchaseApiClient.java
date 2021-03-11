import io.swagger.client.ApiClient;
import io.swagger.client.api.PurchaseApi;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class PurchaseApiClient {

    public static void main(String[] args) throws InterruptedException {

        /*
         * use the InputParamsParser class to parse commandline input or read property file
         * update value stored in UC class
         */
        InputParamsParser parser = new InputParamsParser();
        if(!parser.parseArgs(args)){return;}

        AtomicInteger totalSuccessRequests = new AtomicInteger();
        AtomicInteger totalFailedRequests = new AtomicInteger();


        /*
         * create a thread for each store.
         * each store thread will send numPurchases POST request per hour to the server
         */

        ApiClient shop = new ApiClient();
        shop.setBasePath(parser.getUrl());
        PurchaseApi apiInstance = new PurchaseApi(shop);

        /*
         * phase 1
         * Start the program with 3 countdown latches
         */
        CountDownLatch secondPhase = new CountDownLatch(1);
        CountDownLatch thirdPhase = new CountDownLatch(1);
        CountDownLatch completed = new CountDownLatch(parser.getMaxStore());

        long startTime = System.currentTimeMillis();
        int phaseOneStores = parser.getMaxStore() / 4;
        int nextStoreID = 1;
        for (; nextStoreID <= phaseOneStores; nextStoreID++) {
            int storeID = nextStoreID;
            Runnable store = () -> {
                StorePurchaseRequest storePurchase = new StorePurchaseRequest(storeID, parser.getNumCustPerStore(), parser.getMaxNumItems());
                for (int j = 0; j < parser.getHOUR_OF_OPERATION(); j++) {
                    storePurchase.hourlyPurchase(apiInstance, parser.getNumPurchases(),parser.getDate());
                    if (j == 3) {
                        secondPhase.countDown();
                    }
                    if (j == 5) {
                        thirdPhase.countDown();
                    }
                }
                totalSuccessRequests.addAndGet(storePurchase.successRequests);
                totalFailedRequests.addAndGet(storePurchase.failedRequests);
                completed.countDown();
            };

            new Thread(store).start();
        }
        //wait till any store operates three hours
        secondPhase.await();

        /*
         * phase 2
         */
        int phaseTwoStores = parser.getMaxStore() / 2;
        for (; nextStoreID <= phaseTwoStores; nextStoreID++) {
            int storeID = nextStoreID;
            Runnable store = () -> {
                StorePurchaseRequest storePurchase = new StorePurchaseRequest(storeID, parser.getNumCustPerStore(), parser.getMaxNumItems());
                for (int j = 0; j < parser.getHOUR_OF_OPERATION(); j++) {
                    storePurchase.hourlyPurchase(apiInstance, parser.getNumPurchases(),parser.getDate());
                    if (j == 5) {
                        thirdPhase.countDown();
                    }
                }
                totalSuccessRequests.addAndGet(storePurchase.successRequests);
                totalFailedRequests.addAndGet(storePurchase.failedRequests);
                completed.countDown();
            };
            new Thread(store).start();
        }
        //wait till any store operates 5 hours
        thirdPhase.await();

        /*
         * phase 3
         */
        for (; nextStoreID <= parser.getMaxStore(); nextStoreID++) {
            int storeID = nextStoreID;
            Runnable store = () -> {
                StorePurchaseRequest storePurchase = new StorePurchaseRequest(storeID, parser.getNumCustPerStore(), parser.getMaxNumItems());
                for (int j = 0; j < parser.getHOUR_OF_OPERATION(); j++) {
                    storePurchase.hourlyPurchase(apiInstance, parser.getNumPurchases(), parser.getDate());
                }
                totalSuccessRequests.addAndGet(storePurchase.successRequests);
                totalFailedRequests.addAndGet(storePurchase.failedRequests);
                completed.countDown();
            };
            new Thread(store).start();
        }
        completed.await();
        long endTime = System.currentTimeMillis();
        long wallTime = endTime - startTime;
        System.out.println("Total time: " + wallTime +" milliseconds");
        System.out.println("successful request: " + totalSuccessRequests);
        System.out.println("failed requests: " + totalFailedRequests);
        System.out.println("throughput: " + (totalFailedRequests.intValue() + totalSuccessRequests.intValue()) / (wallTime / 1000) + " request/second");
    }
}
