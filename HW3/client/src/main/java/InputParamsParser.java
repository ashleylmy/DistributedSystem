import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
for this assignment, user only need to pass max store argument in command line. Everything else is set to default value.
 */

public class InputParamsParser {
    private int HOUR_OF_OPERATION = 9;
    private int maxStore; //number of stores
    private int numCustPerStore = 1000; //number of customers per store
    private int maxNumItems = 100000; //maximum item IDs
    private int numPurchases = 300; //number of purchases per hour
    private int numItemsPerPurchase = 5; // number of items per purchase, range 1-20
    private String date = "20210101";
    private String url;

    public InputParamsParser() {
    }

    public boolean parseArgs(String[] args){
        if (args.length !=1) {
            System.out.println("Please only type in the max stores number");
            return false;
        }
        maxStore=Integer.parseInt(args[0]);
        parseFileInputs();
        return true;
    }

    public void parseFileInputs() {
        /*
         Read from property file and set the parameters
         */
        try (InputStream input = PurchaseApiClient.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                return;
            }
            prop.load(input);
            numCustPerStore = Integer.parseInt(prop.getProperty("numbCustomersPerStore"));
            maxNumItems = Integer.parseInt(prop.getProperty("numbItems"));
            numPurchases = Integer.parseInt(prop.getProperty("numPurchases"));
            numItemsPerPurchase = Integer.parseInt(prop.getProperty("numItemsPerPurchase"));
            date = prop.getProperty("date");//TODO validate the date inputsd
            url = prop.getProperty("serverIP");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getHOUR_OF_OPERATION() {
        return HOUR_OF_OPERATION;
    }

    public int getMaxStore() {
        return maxStore;
    }

    public int getNumCustPerStore() {
        return numCustPerStore;
    }

    public int getMaxNumItems() {
        return maxNumItems;
    }

    public int getNumPurchases() {
        return numPurchases;
    }

    public int getNumItemsPerPurchase() {
        return numItemsPerPurchase;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }
}
