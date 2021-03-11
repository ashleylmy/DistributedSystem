import java.sql.*;
import java.util.List;

import com.google.gson.Gson;
import io.swagger.client.model.PurchaseItems;
import model.PurchaseRecord;
import org.apache.commons.dbcp2.*;

public class PurchaseDao {
    //private static BasicDataSource dataSource;


    public void createPurchase(PurchaseRecord purchaseRecord) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "INSERT INTO PurchasesLog (storeId, custId, date, purchaseDetails) " +
                "VALUES (?,?,?,?)";
        try {
            conn = DBCPDataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setInt(1, purchaseRecord.getStoreId());
            preparedStatement.setInt(2, purchaseRecord.getCustId());
            preparedStatement.setString(3, purchaseRecord.getDate());
            preparedStatement.setString(4, purchaseRecord.getOrderDetails());
            // execute insert SQL statement
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

}