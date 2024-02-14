package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectDB {
	
	
	private static final String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DB_CONNECTION = "jdbc:oracle:thin:@52.211.127.247:1521:orcl";
    private static final String DB_USER = "nimc_poc";
    private static final String DB_PASSWORD = "nimcpoc123";
    private static String otp;
    private static String backupPath;



    public static Connection getDBConnection() {

        Connection dbConnection = null;

        try {

            Class.forName(DB_DRIVER);

        } catch (ClassNotFoundException e) {

            System.out.println(e.getMessage());

        }

        try {

            dbConnection = DriverManager.getConnection(
                    DB_CONNECTION, DB_USER, DB_PASSWORD);
            

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }

        return dbConnection;

    }


    public static String getOTP() throws SQLException {

        Connection dbConnection = null;
        Statement statement = null;

        String getOTPSql = "select * from onboarding_status  order by Last_Modified desc";

        try {
            dbConnection = getDBConnection();
            if (dbConnection != null) {
                System.out.println("Connected to db");
            } else {
                System.out.println("Not able to connect to db");
            }
            statement = dbConnection.createStatement();
            ResultSet rs = statement.executeQuery(getOTPSql);
            if (rs.next()) {
                otp = rs.getString("OTP");
            }
            return otp;

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            return null;

        } finally {

            if (statement != null) {
                statement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }

        }

    }
    
    
    public static String getPayloadBackup() throws SQLException {

        Connection dbConnection = null;
        Statement statement = null;

        String getPayloadBackupSql = "Select * from AGILITY_INTEGRATION_LOG_ ORDER BY LAST_MODIFIED";

        try {
            dbConnection = getDBConnection();
            if (dbConnection != null) {
                System.out.println("Connected to db");
            } else {
                System.out.println("Not able to connect to db");
            }
            statement = dbConnection.createStatement();
            ResultSet rs = statement.executeQuery(getPayloadBackupSql);
            if (rs.next()) {
            	backupPath = rs.getString("BACKUP_PATH");
            }
            return backupPath;

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            return null;

        } finally {

            if (statement != null) {
                statement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }

        }

    }
    
    


}
