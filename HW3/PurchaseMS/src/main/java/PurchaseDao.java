import java.sql.*;

import model.PurchaseItems;
import model.PurchaseRecord;

public class PurchaseDao {
    //private static BasicDataSource dataSource;

    public void createPurchase(PurchaseRecord purchaseRecord) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "INSERT INTO PurchasesLog (storeId, custId, date, itemID, itemAmount) " +
                "VALUES (?,?,?,?,?)";
        try {
            conn = DataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setInt(1, purchaseRecord.getStoreId());
            preparedStatement.setInt(2, purchaseRecord.getCustId());
            preparedStatement.setString(3, purchaseRecord.getDate());
            for(int i =0; i<5; i++){
                PurchaseItems item= purchaseRecord.getOrderDetails().get(i);
                preparedStatement.setString(4, item.getItemID());
                preparedStatement.setInt(5, 1);
                // execute insert SQL statement
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
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
                throw new RuntimeException(se);
            }
        }
    }

}