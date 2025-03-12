package edu.gcc;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {

    private static final ArrayList<User> users = new ArrayList<>();
    private static Search search;
    private User[] user;
    protected static final ArrayList<Course> courses = new ArrayList<>();

    public static void main(String[] args) {
        getCourses();
        for(Course c : courses){
            System.out.println(c.toString());
        }
        // run();
    }

    public static void run() {
        String cmd = "";
        Scanner s = new Scanner(System.in);

        User currentUser = null;

        //Loop until account is found
        while (currentUser == null) {
            //Prompt user for name
            System.out.print("Enter your username: ");
            String username = s.nextLine();

            //Search for existing user with name
            for (User u : users) {
                if (username.equals(u.getName())) {
                    currentUser = u;
                    break;
                }
            }

            //If user was not found prompt user to create new account
            if (currentUser == null) {
                System.out.println("This account does not exist, would you like to create it?");
                String res = s.nextLine();
                if (res.equalsIgnoreCase("y") || res.equalsIgnoreCase("yes")){
                    currentUser = new User(username,"",null,null,null);
                    users.add(currentUser);
                }
            }
        }

        System.out.printf("Welcome to Student Scheduler %s!\n",currentUser.getName());

        //Main program loop
        while (!cmd.equals("exit")){
            System.out.println("Please enter a command or 'help' for options");
            System.out.print("> ");
            String[] input = s.nextLine().split(" ");
            cmd = input[0];
            switch (cmd) {
                case "exit":
                    break;
                case "help":
                    //Print list of commands
                    System.out.println("  add <id> - adds class to schedule");
                    System.out.println("  remove <id> - removes class from schedule");
                    System.out.println("  courses - display list of users classes");
                    System.out.println("  calendar - display schedule as calendar");
                    System.out.println("  search - search for classes");
                    System.out.println("  exit - exits the program");
                    break;
                case "list":
                    //Prints a list of the classes the user is in
                    Course[] enrolled = currentUser.getSchedule().getCourses();
                    for (Course c : enrolled){
                        //Needs to string defined
                        System.out.println(c);
                    }
                    break;
                case "calendar":
                    //Prints a calendar representation of the schedule
                    System.out.println("  Time   |   Mon   |   Tue   |   Wed   |   Thu   |   Fri   |");
                    for (int i = 0; i < 55; i++){
                        int currentTime = i*15;
                        System.out.print(formatTime(currentTime));
                        System.out.print(" |");
                        for (int j = 0; j < 5; j++){
                            boolean timeFilled = false;
                            for (Course c : currentUser.getSchedule().getCourses()){
                                int startTime = c.getStartTime()[j];
                                if (startTime >= currentTime && startTime+c.getDuration()[j] <= currentTime){
                                    //TODO get and display course dept and code
                                    //System.out.print(c.getDept());
                                    //System.out.print(c.getCode());
                                    //System.out.print(c.getSection());
                                    timeFilled = true;
                                }
                            }
                            if (!timeFilled) {
                                System.out.print("         |");
                            }
                        }
                        System.out.println();
                    }
                    break;
                case "search":
                    //TODO: implement this when search is working
                    System.out.println("Not implemented");
                    break;
                case "add":
                    //Adds class to schedule
                    if (input.length < 2){
                        System.out.println("Proper usage: add <course_id>");
                        break;
                    }
                    try {
                        int cid = Integer.parseInt(input[1]);
                        Course add = courses.get(cid);
                        currentUser.getSchedule().addCourse(add);
                        currentUser.saveSchedule();
                    } catch (NumberFormatException e){
                        System.out.printf("Error: %s is not a number\n",input[1]);
                    } catch (IndexOutOfBoundsException e){
                        System.out.println("Invalid course id "+input[1]);
                    }
                    break;

                case "remove":
                    //Removes class from schedule
                    if (input.length < 2){
                        System.out.println("Proper usage: remove <course_id>");
                        break;
                    }
                    try {
                        int cid = Integer.parseInt(input[1]);
                        Course add = courses.get(cid);
                        currentUser.getSchedule().removeCourse(add);
                        currentUser.saveSchedule();
                    } catch (NumberFormatException e){
                        System.out.printf("Error: %s is not a number\n",input[1]);
                    } catch (IndexOutOfBoundsException e){
                        System.out.println("Invalid course id "+input[1]);
                    }
                    break;
                default:
                    //Provide feedback on invalid command
                    System.out.println("Unknown Command: "+cmd);
            }
        }
        System.out.println("Thank you for using Student Scheduler");
    }

    private static String formatTime(int time){
        int hour = time/60+8;
        int minute = time%60;
        boolean morning = hour < 12;

        return String.format("%s%d:%s%d %s",
                (hour-1)% 12 + 1 < 10 ? " " : "",
                (hour-1) % 12 + 1,
                minute < 10 ? "0" : "",
                minute,
                morning ? "AM" : "PM"
        );
    }

    /**
     * Parses course data from a JSON file and populates the 'courses' list.
     * It reads the JSON file, extracts course information, and creates Course objects.
     * Only open courses are added to the 'courses' list.
     */
    public static void getCourses() {
        try {
            FileReader json = new FileReader("data_wolfe.json"); // Opens the JSON file for reading.
            JsonObject jsonObject = JsonParser.parseReader(json).getAsJsonObject(); // Parses the JSON file into a JsonObject.

            JsonArray coursesArray = jsonObject.getAsJsonArray("classes"); // Extracts the "classes" array from the JSON.

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

                if(isOpen){ // Check if the course is open.
                    courses.add(new Course(name, startingTimes, duration, isOpen, professors, MWForTR, daysMeet, department, courseCode, credits, numSeats, section, isLab)); // Create and add the Course object to the list.
                }

            }


        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage()); // Print the exception message if the file is not found.
        }
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


}


