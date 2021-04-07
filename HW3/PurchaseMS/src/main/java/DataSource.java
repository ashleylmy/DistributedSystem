import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {
    //private static BasicDataSource dataSource;
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource dataSource;

    // NEVER store sensitive information below in plain text!
//    private static final String HOST_NAME = System.getProperty("MySQL_IP_ADDRESS");
//    private static final String PORT = System.getProperty("MySQL_PORT");
//    private static final String DATABASE = "PurchaseRecords";
//    private static final String USERNAME = System.getProperty("DB_USERNAME");
//    private static final String PASSWORD = System.getProperty("DB_PASSWORD");
    private static final String HOST_NAME = "database-1.cmymicb9ecka.us-east-1.rds.amazonaws.com";
    private static final String PORT = "3306";
    private static final String DATABASE = "PurchaseRecords";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "5NqGwi2UT72twQf";

    static {
//        // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
//        dataSource = new BasicDataSource();
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
//        dataSource.setUrl(url);
//        dataSource.setUsername(USERNAME);
//        dataSource.setPassword(PASSWORD);
//        dataSource.setInitialSize(10);
//        dataSource.setMaxTotal(75);
//        dataSource.setMaxOpenPreparedStatements(100);
        String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
        config.setJdbcUrl( url );
        config.setUsername( USERNAME );
        config.setPassword( PASSWORD );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        config.setConnectionTimeout(20000);
        config.setMaximumPoolSize(60);
        config.setMinimumIdle(5);
        dataSource = new HikariDataSource( config );

    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
