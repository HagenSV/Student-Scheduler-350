package edu.gcc;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateDatabaseContents {

    public static final String URL = "jdbc:mysql://10.31.105.110:3306/my_database";
    public static final String usernameDB = "user";
    public static final String password = "password";
    public UpdateDatabaseContents(){
    }
    private static boolean readJsonIntoDatabase = true;
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            System.out.println("Connected to the database.");

            if(readJsonIntoDatabase) {
                JsonIntoDatabase();
            }


            connection.close();
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }

    public static boolean addUser(dbUser user){
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordUpload());
            if(user.getMajors() != null) {
                addMajor( user);
            }
            if(user.getMinors() != null){
                addMinor( user);
            }
            // Execute the insert statement
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println(user.getUsername() + " was inserted successfully!");
                createSchedules(user.getUsername());
                return true;
            }


        } catch (SQLException e) {
            System.err.println("Error inserting " + user.getUsername() + ": " + e.getMessage());
        }
        return false;
    }

    public static void addCompletedCourse(String username, String cid){
        String sql = "INSERT INTO completed_courses (cid, username) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, cid);
            preparedStatement.setString(2, username);

            // Execute the insert statement
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Course " + cid + " was inserted successfully into completed courses!");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting course " + cid + " into completed courses: " + e.getMessage());
        }
    }


    public static void createSchedules(String username){
        String[] semesters = {"Fall", "Winter_Online", "Spring", "Early_Summer", "Late_Summer "};
        int count = 0;
        for(String semester : semesters){
            String sql = "INSERT INTO schedule (username, semester) VALUES (?,?)";
            try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, semester.toLowerCase());

                int rowsInserted = preparedStatement.executeUpdate();
                if(rowsInserted > 0) {
                    count += rowsInserted;
                } else {
                    System.out.println(semester + " was not added to schedule.");
                }
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
            if(count == 5){
                System.out.println("Schedule successfully created.");
            }
        }
    }

    public static void addMinor( dbUser user){
        // TODO create a connection and then add the major to the database if needed, if not just create the user_major table
    }
    public static void addMajor(dbUser user){
        // TODO create a connection and then add the minor to the database if needed if not add to user_minor table
    }


    public static void JsonIntoDatabase(){
        wipeDatabase();
        ArrayList<Course> courses = getCourses("data_wolfe.json");
        Map<String, Integer> profMap = new HashMap<>();
        ArrayList<String> departments = new ArrayList<>();
        int count = 0;
        if (courses != null) {
            for (Course course : courses) {
                if(!departments.contains(course.getDepartment())) {
                    addDepartmentToDatabase(course.getDepartment());
                    departments.add(course.getDepartment());
                }

                addCourseToDatabase(course);

                addCourseDepartmentToDatabase(course.getCID(), course.getDepartment());

                for (String profName : course.getProfessor()) {
                    if (!profMap.containsKey(profName)) {
                        profMap.put(profName, count);
                        addProfToDatabase( profMap.get(profName), profName);
                        count++;
                    }
                    addCourseProfToDatabase(course.getCID(), profMap.get(profName));
                }
            }
        } else {
            System.out.println("No courses found.");
        }


    }
    public static void wipeDatabase(){
        String sql = "DELETE FROM course_professor";
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting course_prof: " + e.getMessage());
        }

        sql = "DELETE FROM course_department";
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting course_department: " + e.getMessage());
        }

        sql = "DELETE FROM professor";
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting professor: " + e.getMessage());
        }

        sql = "DELETE FROM course";
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
        }
        sql = "DELETE FROM department";
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting department: " + e.getMessage());
        }

        System.out.println("Entries successfully delted");
    }

    /**
     * Parses course data from a JSON file and populates the 'courses' list.
     * It reads the JSON file, extracts course information, and creates Course objects.
     * Only open courses are added to the 'courses' list.
     */
    public static ArrayList<Course> getCourses(String filename) {
        try {
            ArrayList<Course> returnArray = new ArrayList<>(); // List to store the Course objects.
            FileReader json = new FileReader(filename); // Opens the JSON file for reading.
            JsonObject jsonObject = JsonParser.parseReader(json).getAsJsonObject(); // Parses the JSON file into a JsonObject.

            JsonArray coursesArray = jsonObject.getAsJsonArray("classes"); // Extracts the "classes" array from the JSON.
            int CID = 0; // Variable to store the course ID.
            for (JsonElement courseElement : coursesArray) { // Iterates through each course element in the array.
                String name; // Variable to store the course name.
                int[] startingTimes = {-1, -1, -1, -1, -1}; // Array to store starting times for each day of the week (M-F).
                int duration = 0; // Variable to store the course duration.
                boolean isOpen; // Variable to store whether the course is open.
                ArrayList<String> professors = new ArrayList<>(); // List to store the professors teaching the course.
                boolean MWForTR = false; // Flag to indicate if MW times should be used for TR.
                boolean[] daysMeet = new boolean[5]; // Array to store which days the course meets (M-F).
                String department; // Variable to store the course department.
                String courseCode; // Variable to store the course code.
                int credits = 0; // Variable to store the course credits.
                int numSeats = 0; // Variable to store the number of open seats.
                String section; // Variable to store the course section.
                boolean isLab; // Variable to store whether the course is a lab.
                String semester; // Variable to store the semester.
                String location; // Variable to store the course location.

                JsonObject course = courseElement.getAsJsonObject(); // Converts the current course element to a JsonObject.

                JsonArray professorArray = course.get("faculty").getAsJsonArray(); // Extracts the "faculty" array from the course.

                for (JsonElement professorElement : professorArray) { // Iterates through each professor element.
                    professors.add(professorElement.getAsString()); // Adds the professor's name to the list.
                }

                JsonArray times = course.getAsJsonArray("times"); // Extracts the "times" array from the course.
                for (JsonElement dayElement : times) { // Iterates through each day element in the "times" array.
                    JsonObject dayObject = dayElement.getAsJsonObject(); // Converts the current day element to a JsonObject.
                    String day = dayObject.get("day").getAsString(); // Gets the day of the week.

                    // Determine which day the course meets and extract the start and end times.
                    if (day.equals("M")) {
                        MWForTR = true; // Set MWForTR flag to true.
                        daysMeet[0] = true; // Set Monday as a meeting day.
                        startingTimes[0] = minFrom8(dayObject.get("start_time").getAsString()); // Convert start time to minutes from 8 AM.
                        duration = minFrom8(dayObject.get("end_time").getAsString()) - startingTimes[0]; // Calculate duration.
                    } else if (day.equals("T")) {
                        daysMeet[1] = true; // Set Tuesday as a meeting day.
                        startingTimes[1] = minFrom8(dayObject.get("start_time").getAsString()); // Convert start time to minutes from 8 AM.
                        duration = minFrom8(dayObject.get("end_time").getAsString()) - startingTimes[1]; // Calculate duration.

                    } else if (day.equals("W")) {
                        MWForTR = true; // Set MWForTR flag to true.
                        daysMeet[2] = true; // Set Wednesday as a meeting day.
                        startingTimes[2] = minFrom8(dayObject.get("start_time").getAsString()); // Convert start time to minutes from 8 AM.
                        duration = minFrom8(dayObject.get("end_time").getAsString()) - startingTimes[2]; // Calculate duration.

                    } else if (day.equals("R")) {
                        daysMeet[3] = true; // Set Thursday as a meeting day.
                        startingTimes[3] = minFrom8(dayObject.get("start_time").getAsString()); // Convert start time to minutes from 8 AM.
                        duration = minFrom8(dayObject.get("end_time").getAsString()) - startingTimes[3]; // Calculate duration.

                    } else if (day.equals("F")) {
                        MWForTR = true; // Set MWForTR flag to true.
                        daysMeet[4] = true; // Set Friday as a meeting day.
                        startingTimes[4] = minFrom8(dayObject.get("start_time").getAsString()); // Convert start time to minutes from 8 AM.
                        duration = minFrom8(dayObject.get("end_time").getAsString()) - startingTimes[4]; // Calculate duration.
                    }
                }

                name = course.get("name").getAsString(); // Gets the course name.
                isOpen = course.get("is_open").getAsBoolean(); // Gets the open status.
                department = course.get("subject").getAsString(); // Gets the department.
                courseCode = course.get("number").getAsString(); // Gets the course code.
                credits = course.get("credits").getAsInt(); // Gets the credits.
                numSeats = course.get("open_seats").getAsInt(); // Gets the number of open seats.
                section = course.get("section").getAsString(); // Gets the section.
                isLab = course.get("is_lab").getAsBoolean(); // Gets the lab status.

                location = course.get("location").getAsString(); // Gets the location.
                semester = getSemester(course).toLowerCase(); // Gets the semester.


                if(isOpen){ // Check if the course is open.
                    returnArray.add(new Course(CID, name, startingTimes, duration, isOpen, professors, MWForTR, daysMeet, department, courseCode, credits, numSeats, section, isLab, semester, location)); // Create and add the Course object to the list.
                    CID++;
                }

            }

            return returnArray; // Return the list of Course objects.
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage()); // Print the exception message if the file is not found.
            return null;
        }
    }
    public static String getSemester(JsonObject course){
        String jsonResponse = course.get("semester").getAsString(); // Gets the semester from the course object.
        String[] jsonParts = jsonResponse.split("_"); // Splits the semester string into parts.
        String returnString = "";
        for (int i = 1; i < jsonParts.length; i++) {
            if(i + 1 == jsonParts.length){
                returnString = returnString + jsonParts[i];
            } else {
                returnString = returnString + jsonParts[i] + "_";
            }
        }
        return  returnString;
    }
    /**
     * Converts a time string (HH:MM) to minutes from 8:00 AM.
     *
     * @param time The time string in HH:MM format.
     * @return The number of minutes from 8:00 AM.
     */
    public static int minFrom8(String time) {
        String[] timeParts = time.split(":"); // Splits the time string into hours and minutes.
        int hours = Integer.parseInt(timeParts[0]); // Parses the hours.
        int min = Integer.parseInt(timeParts[1]); // Parses the minutes.

        return (hours - 8) * 60 + min; // Calculates and returns the total minutes from 8:00 AM.
    }

    public static void addCourseToDatabase(Course course) {
        String sql = "INSERT INTO course (CID, name, section, starttime, mwfortr, duration, isopen, daysmeet, department, coursecode, credits, numseats, isLab, semester, location) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, course.getCID());
            preparedStatement.setString(2, course.getName());
            preparedStatement.setString(3, course.getSection());
            preparedStatement.setString(4, startingTimesAsString(course));
            preparedStatement.setBoolean(5, course.getMWForTR());
            preparedStatement.setInt(6, course.getDuration());
            preparedStatement.setBoolean(7, course.getIsOpen());
            preparedStatement.setString(8, getDaysMeetAsString(course));
            preparedStatement.setString(9, course.getDepartment());
            preparedStatement.setString(10, course.getCourseCode());
            preparedStatement.setInt(11, course.getCredits());
            preparedStatement.setInt(12, course.getNumSeats());
            preparedStatement.setBoolean(13, course.getIsLab());
            preparedStatement.setString(14, course.getSemester());
            preparedStatement.setString(15, course.getLocation());

            // Execute the insert statement
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println(course.getName() + " was inserted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting " + course.getName() + ": " + e.getMessage());
        }
    }

    public static String getDaysMeetAsString(Course course){
        StringBuilder toReturn = new StringBuilder("-----");
        boolean[] daysMeet = course.getDaysMeet();
        for (int i = 0; i < course.getDaysMeet().length; i++) {
            switch (i) {
                case 0:
                    toReturn.setCharAt(i, daysMeet[i] ? 'M' : '-');
                    break;
                case 1:
                    toReturn.setCharAt(i, daysMeet[i] ? 'T' : '-');
                    break;
                case 2:
                    toReturn.setCharAt(i, daysMeet[i] ? 'W' : '-');
                    break;
                case 3:
                    toReturn.setCharAt(i, daysMeet[i] ? 'R' : '-');
                    break;
                case 4:
                    toReturn.setCharAt(i, daysMeet[i] ? 'F' : '-');
                    break;
            }
        }
        return toReturn.toString();
    }
    public static String startingTimesAsString(Course course) {
        StringBuilder startingTimes = new StringBuilder();
        for (int i = 0; i < course.getStartTime().length; i++) {
            if(course.getStartTime()[i] != -1) {
                startingTimes.append(course.getStartTime()[i]);
                if (i < course.getStartTime().length - 1) {
                    startingTimes.append(", ");
                }
            } else {
                startingTimes.append("---");
                if (i < course.getStartTime().length - 1) {
                    startingTimes.append(", ");
                }
            }
        }
        return startingTimes.toString();
    }

    public static void addProfToDatabase(int PID, String profName) {
        String sql = "INSERT INTO professor (PID, name) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, PID);
            preparedStatement.setString(2, profName);

            // Execute the insert statement
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println(profName + " was inserted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting " + profName + ": " + e.getMessage());
        }
    }

    public static void addCourseDepartmentToDatabase(int CID, String department) {
        String sql = "INSERT INTO course_department (CID, abbreviation) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, CID);
            preparedStatement.setString(2, department);

            // Execute the insert statement
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println(department + " was inserted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting " + department + ": " + e.getMessage());
        }
    }

    public static void addDepartmentToDatabase( String department) {
        String sql = "INSERT INTO department (abbreviation) VALUES (?)";
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, department);

            // Execute the insert statement
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println(department + " was inserted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting " + department + ": " + e.getMessage());
        }
    }

    public static void addCourseProfToDatabase( int CID, int PID) {
        String sql = "INSERT INTO course_professor (CID, PID) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, CID);
            preparedStatement.setInt(2, PID);

            // Execute the insert statement
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Course-Professor relationship was inserted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting Course-Professor relationship: " + e.getMessage());
        }
    }

    public void addCourseToSchedule(int cid, String username, String semester){
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            String sql = "INSERT INTO courses_in_schedule(cid, semester, username) VALUES(?, ?,?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, cid);
            preparedStatement.setString(2, semester.toLowerCase());
            preparedStatement.setString(3, username);

            int rowsInserted = preparedStatement.executeUpdate();
            if(rowsInserted > 0){
                System.out.println("Num Rows inserted" + rowsInserted);
                System.out.println("Successfully added course to the user's schedule in database");
            }

        } catch(SQLException e){
            System.out.println("Error adding to Course to user's schedule in DB" + e.getMessage());
        }
    }


    public boolean removeCourse(Course course, String username, String semester){
        try (Connection connection = DriverManager.getConnection(URL, usernameDB, password)) {
            String sql = "DELETE FROM courses_in_schedule WHERE cid = ? and username = ? and semester = ?";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, course.getCID());
            ps.setString(2, username);
            ps.setString(3, semester);

            int rowsDeleted = ps.executeUpdate();
            if(rowsDeleted > 0){
                return true;
            }
            return false;
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}

