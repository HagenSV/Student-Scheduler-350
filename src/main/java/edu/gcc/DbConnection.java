package edu.gcc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    public static final String URL = "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?user=postgres.chhgjsqthhxqsvutshqi&password=Comp350dics";
    private static Connection connection;

    private static void connect() {
        // Initialize the connection here
        // For example, using JDBC:
        // connection = DriverManager.getConnection(url, user, password);
        try {
            connection = DriverManager.getConnection(URL);
        } catch (SQLException e){
            System.out.println("Failed to connect to database: " + e.getMessage());
        }
    }

    /**
     * Get the connection to the database
     * @return single shared connection to the database. DO NOT CLOSE.
     */
    public static Connection getConnection() {
        if (connection == null){
            connect();
            for (int i = 0; i < 5; i++) {
                System.out.println("Database failed to connect, retrying...");
                if (connection != null) {
                    break;
                }
                try {
                    Thread.sleep(1000); // Wait for 1 second before retrying
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore the interrupted status
                }
            }
            if (connection == null) {
                System.out.println("Failed to establish a connection after multiple attempts.");
            }
        }
        return connection;
    }

}
