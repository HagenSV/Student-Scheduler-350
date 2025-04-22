package edu.gcc;

import org.checkerframework.checker.units.qual.A;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SearchDatabase {

    private static SearchDatabase instance;
    private Connection connection;
    //private static String url = "jdbc:mysql://10.31.103.200:3306/my_database";
    //private static String password = "password";
    //private static String username = "user";

    private static String url = "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?user=postgres.chhgjsqthhxqsvutshqi&password=Comp350dics";

    private SearchDatabase(){
        this.connection = DbConnection.getConnection();
    }

    public static SearchDatabase getInstance(){
        if(instance == null || instance.connection == null) {
            instance = new SearchDatabase();
        }
        return instance;
    }

    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public ArrayList<Course> getScheduleFromDB(String username, String semester){

        ArrayList<Course> toReturn = new ArrayList<>();
        String sql = "SELECT c.cid, c.name, c.section, c.starttime, c.mwfortr, c.duration, c.isopen, \n" +
                "       c.daysmeet, c.department, c.coursecode, c.credits, c.numseats, \n" +
                "       c.islab, c.semester, c.location, \n" +
                "       STRING_AGG(p.name, '/') AS professors\n" +
                "FROM course c\n" +
                "JOIN course_professor cp ON c.cid = cp.cid\n" +
                "JOIN professor p ON cp.pid = p.pid\n" +
                "WHERE c.cid IN (\n" +
                "    SELECT cid \n" +
                "    FROM courses_in_schedule\n" +
                "    WHERE username = ? AND semester = ?\n" +
                ")\n" +
                "GROUP BY c.cid;\n";
        try{
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username.trim());
            ps.setString(2, semester.trim());

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int cid = rs.getInt("cid");
                String name = rs.getString("name");
                String section = rs.getString("section");
                int startTimes[] = getStartTimes(rs.getString("starttime"));
                boolean MWForTR = rs.getBoolean("mwfortr");
                int duration = rs.getInt("duration");
                boolean isOpen = rs.getBoolean("isopen");
                boolean[] daysMeet = getDaysMeet(rs.getString("daysmeet"));
                String department = rs.getString("department");
                String courseCode = rs.getString("coursecode");
                int credits = rs.getInt("credits");
                int numseats = rs.getInt("numseats");
                boolean isLab = rs.getBoolean("islab");
                String courseSemester = rs.getString("semester");
                String location = rs.getString("location");
                ArrayList<String> professors = new ArrayList<>(Arrays.asList(rs.getString("professors").split("/")));
                Course course = new Course(cid, name, startTimes, duration, isOpen, professors, MWForTR, daysMeet, department, courseCode, credits, numseats, section, isLab, courseSemester, location);
                toReturn.add(course);

            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        if(toReturn.isEmpty()){
            return new ArrayList<>();
        }
        return toReturn;
    }

    public int[] getStartTimes(String startTimes){
        int[] toReturn = new int[5];
        String[] parts = startTimes.split(", ");

        for (int i = 0; i < parts.length; i++) {
            if(parts[i].contains("---")){
                toReturn[i] = -1;
            } else {
                toReturn[i] = Integer.parseInt(parts[i]);
            }
        }

        return toReturn;

    }


    public boolean[] getDaysMeet(String daysMeet){
        boolean[] toReturn = new boolean[5];
        String[] parts = daysMeet.split("");
        for(int i = 0; i < parts.length; i++){
            if(parts[i].equals("-")){
                toReturn[i] = false;
            } else {
                toReturn[i] = true;
            }
        }

        return toReturn;
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
