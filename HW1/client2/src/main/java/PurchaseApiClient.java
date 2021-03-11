import io.swagger.client.ApiClient;
import io.swagger.client.api.PurchaseApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class PurchaseApiClient {

    public static void main(String[] args) throws InterruptedException, IOException {

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
        ArrayList<ArrayList<ResponseResult>> responses = new ArrayList<>();

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
                    responses.add(storePurchase.hourlyPurchase(apiInstance, parser.getNumPurchases(),parser.getDate()));
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
        int phaseTwoStores = parser.getMaxStore()/ 2;
        for (; nextStoreID <= phaseTwoStores; nextStoreID++) {
            int storeID = nextStoreID;
            Runnable store = () -> {
                StorePurchaseRequest storePurchase = new StorePurchaseRequest(storeID, parser.getNumCustPerStore(), parser.getMaxNumItems());
                for (int j = 0; j < parser.getHOUR_OF_OPERATION(); j++) {
                    responses.add(storePurchase.hourlyPurchase(apiInstance, parser.getNumPurchases(),parser.getDate()));;
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
                    responses.add(storePurchase.hourlyPurchase(apiInstance, parser.getNumPurchases(),parser.getDate()));;
                }
                totalSuccessRequests.addAndGet(storePurchase.successRequests);
                totalFailedRequests.addAndGet(storePurchase.failedRequests);
                completed.countDown();
            };
            new Thread(store).start();
        }
        completed.await();
        long endTime = System.currentTimeMillis();

        // Write the running result to csv file.
        CSVWriter csvWriter = new CSVWriter();
        csvWriter.writeFile("", parser.getMaxStore() + ".cvs", responses);


        // Print out all statistics
        long wallTime = endTime - startTime;
        DataProcessor dataProcessor=new DataProcessor(responses, wallTime/1000, totalSuccessRequests.intValue());
        System.out.println("Total time: " + wallTime + " milliseconds");
        System.out.println("Successful request: " + totalSuccessRequests);
        System.out.println("Failed requests: " + totalFailedRequests);
        System.out.println("Throughput: " + dataProcessor.getThroughput() + " request/second");
        System.out.println("Mean response time: " + dataProcessor.getMean() + " ms");
        System.out.println("Median response time: " + dataProcessor.getMedian() + " ms");
        System.out.println("99th percentile: " + dataProcessor.getNintyninthPercentile() + " ms");
        System.out.println("Max response time: " + dataProcessor.getMaxResponse() + " ms");
    }
}
