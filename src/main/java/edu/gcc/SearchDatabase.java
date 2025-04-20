package edu.gcc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class SearchDatabase {
    private Connection connection;
    private static String url = "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?user=postgres.chhgjsqthhxqsvutshqi&password=Comp350dics";

    public SearchDatabase(){
        try{
            Connection connection = DriverManager.getConnection(url);
            this.connection = connection;
        } catch(SQLException e){
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public dbUser getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username.trim());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()){
                // User does not exist
                return null;
            } else {
                String password = resultSet.getString("password");
                //TODO add majors, minors, completedCourses
                return new dbUser(username, password, null, null, null);
            }
        } catch(SQLException e){
            System.out.println("Error retrieving user: " + e.getMessage());
        }
        return null;
    }
}
