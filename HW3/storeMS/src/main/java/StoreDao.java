import com.google.gson.Gson;
import model.PurchaseItems;
import model.PurchaseRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StoreDao {
    //private static BasicDataSource dataSource;
    private static Gson gson=new Gson();


    public static String queryStore(String storeID) {
        Connection conn = null;
        System.out.println("query for store");
        List<PurchaseItems> output = new ArrayList<>();
        String res="";
        Statement selectStmt = null;
        String query = "SELECT itemID, COUNT(*) AS NUM FROM PurchasesLog WHERE storeId = '" + storeID + "' GROUP BY itemID ORDER BY NUM DESC LIMIT 10;";
        try {
            conn = DataSource.getConnection();
            selectStmt = conn.createStatement();
            ResultSet rs = selectStmt.executeQuery(query);
            while (rs.next()) {
                PurchaseItems item= new PurchaseItems();
                item.setItemID(rs.getString("itemId"));
                item.setNumberOfItems(rs.getInt("NUM"));
                output.add(item);
            }
            res=gson.toJson(output);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return res;
    }
    public static String queryItem(String itemID) {
        Connection conn = null;
        System.out.println("query for item");
        String output = "";
        Statement selectStmt = null;
        String query = "SELECT storeId, COUNT(*) AS NUM FROM PurchasesLog WHERE itemID = '" + itemID + "' GROUP BY storeId ORDER BY NUM DESC LIMIT 5;";

        try {
            conn = DataSource.getConnection();
            selectStmt = conn.createStatement();
            ResultSet rs = selectStmt.executeQuery(query);
            while (rs.next()) {
                String store = rs.getString("storeId");
                String num = rs.getString("NUM");
                System.out.println("storeId: " + store + "numberOfItems:" + num);
                output += "{\"storeId\": " + store + "," + "\"numberOfItems\": " + num + "},";
            }
            output = output.substring(0, output.length() - 1);
            output = "{\"stores\":[ " + output + "]}";

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return output;
    }

}