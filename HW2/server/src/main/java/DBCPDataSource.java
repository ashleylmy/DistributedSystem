import org.apache.commons.dbcp2.*;

import java.sql.Connection;
import java.sql.SQLException;

public class DBCPDataSource {
        private static BasicDataSource dataSource;

        // NEVER store sensitive information below in plain text!
        private static final String HOST_NAME = System.getProperty("MySQL_IP_ADDRESS");
        private static final String PORT = System.getProperty("MySQL_PORT");
        private static final String DATABASE = "PurchaseRecords";
        private static final String USERNAME = System.getProperty("DB_USERNAME");
        private static final String PASSWORD = System.getProperty("DB_PASSWORD");

        static {
            // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
            dataSource = new BasicDataSource();
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
            dataSource.setUrl(url);
            dataSource.setUsername(USERNAME);
            dataSource.setPassword(PASSWORD);
            dataSource.setInitialSize(10);
            dataSource.setMaxTotal(75);
            dataSource.setMaxOpenPreparedStatements(100);
        }

        public static BasicDataSource getDataSource() {
            return dataSource;
        }
        public static Connection getConnection()throws SQLException {return dataSource.getConnection();}
    }
