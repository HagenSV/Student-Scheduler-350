package edu.gcc;

import org.checkerframework.checker.units.qual.A;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SearchDatabase {

    private static SearchDatabase instance;
    public static final String URL = "jdbc:mysql://10.31.105.110:3306/my_database";
    public static final String password = "password";
    public static final String usernameDB = "user";

    private SearchDatabase(){
    }

    public static SearchDatabase getInstance(){
        if(instance == null) {
            instance = new SearchDatabase();
        }
        return instance;
    }




    public ArrayList<Course> getScheduleFromDB(String username, String semester){

        ArrayList<Course> toReturn = new ArrayList<>();
        String sql =
                "SELECT c.cid, c.name, c.section, c.starttime, c.mwfortr, c.duration, c.isopen, \n" +
                        "       c.daysmeet, c.department, c.coursecode, c.credits, c.numseats, \n" +
                        "       c.islab, c.semester, c.location, \n" +
                        "       GROUP_CONCAT(p.name SEPARATOR '/') AS professors \n" +
                        "FROM course c \n" +
                        "JOIN course_professor cp ON c.cid = cp.cid \n" +
                        "JOIN professor p ON cp.pid = p.pid \n" +
                        "WHERE c.cid IN ( \n" +
                        "    SELECT cid \n" +
                        "    FROM courses_in_schedule \n" +
                        "    WHERE username = ? AND semester = ? \n" +
                        ") \n" +
                        "GROUP BY c.cid;";

        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username.trim());
            ps.setString(2, semester.trim());

            ResultSet rs = ps.executeQuery();
            toReturn = getCoursesFromRS(rs);
        }catch (SQLException e){
            e.printStackTrace();
        }
        if(toReturn.isEmpty()){
            return new ArrayList<>();
        }
        return toReturn;
    }

    public ArrayList<Course> getCompletedCoursesFromDB(String username){
        String sql = "SELECT c.*, GROUP_CONCAT(p.name SEPARATOR '/') AS professors\n" +
                "FROM course c\n" +
                "JOIN course_professor cp ON c.cid = cp.cid\n" +
                "JOIN professor p on cp.pid = p.pid\n" +
                "WHERE c.cid IN (\n" +
                "\tSELECT cid\n" +
                "    FROM completed_courses\n" +
                "    WHERE username = ?\n" +
                ")\n" +
                "GROUP BY c.cid;";
        try(Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            ArrayList<Course> toReturn = new ArrayList<>();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username.trim());
            ResultSet rs = ps.executeQuery();
            toReturn = getCoursesFromRS(rs);
            if(toReturn.isEmpty())
                return new ArrayList<>();
            return toReturn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<Course> getCoursesFromRS(ResultSet rs) throws SQLException {

        ArrayList<Course> toReturn = new ArrayList<>();
            while (rs.next()) {
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
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
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
