package edu.gcc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {


    public static final String URL = "jdbc:mysql://10.31.105.110:3306/my_database";
    public static final String username = "user";
    public static final String password = "password";
    private static Connection connection;

    private static void connect() {
        // Initialize the connection here
        // For example, using JDBC:
        // connection = DriverManager.getConnection(url, user, password);
        try {
            connection = DriverManager.getConnection(URL, username, password);
        } catch (SQLException e){
            System.out.println("Failed to connect to database: " + e.getMessage());
        }
    }

    public static void close(){
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Failed to close the database connection: " + e.getMessage());
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
