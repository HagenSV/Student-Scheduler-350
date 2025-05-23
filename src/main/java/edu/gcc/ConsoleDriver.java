package edu.gcc;

import edu.gcc.exception.CourseFullException;
import edu.gcc.exception.ScheduleConflictException;
import edu.gcc.exception.SemesterMismatchException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleDriver {

    private static final Scanner s = new Scanner(System.in);
    private static dbUser currentUser = null;
    private static Search search = null;
    private static final int RESULTS_PER_PAGE = 5;
    private static User tempUser;
    private static String semester = "Fall";
    private static Schedule schedule;
    private static ArrayList<Course> completedCourses;

    // The following methods act as a private API for the ConsoleDriver class
    // They will be updated in the future to interact with the database once complete

    /**
     * Attempts to find a user in the database
     * @param username the username of the user
     * @return User object if username/password match found, null otherwise
     */
    private static dbUser getUser(String username){
        SearchDatabase db = SearchDatabase.getInstance();
        return db.getUser(username);

    }

    /**
     * Creates a new user and adds them to the database
     * @param username username of new user
     * @param password password of new user
     */
    private static void addUser(String username, String password){
        currentUser = new dbUser(username,password,null,null,null,1);
    }

    /**
     * Gets a course from the database
     * @param courseId the id of the course
     * @return Course object if found, null otherwise
     */
    private static Course getCourse(int courseId){
        if (courseId < -1 || courseId >= Main.courses.size()){
            System.out.printf("Error: %d is not a valid course id\n",courseId);
            return null;
        }
        return Main.courses.get(courseId);
    }

    //End private api

    public static void run() {
        String cmd = "";

        //Loop until account is found
        while (currentUser == null) {
            System.out.println("Would you like to Login, or Create Account? (type: login/create)");
            String input = s.nextLine();
            if(input.equals("create")){
                System.out.println("Enter your enter desired username?");
                String username = s.next();
                System.out.println("Enter your desired password?");
                String password = s.next();


                dbUser user = getUser(username);
                if(user != null){
                    System.out.println("This username already exists");
                } else {
                    addUser(username, password);
                }

            } else if (input.equals("login")) {
                System.out.print("Enter your username: ");
                String username = s.nextLine();
                dbUser user = getUser(username);
                if (user == null){
                    System.out.println("Invalid username");
                    continue;
                } else {
                    while(true) {
                        System.out.println("Enter your password: ");
                        String password = s.nextLine();
                        if (BCrypt.checkpw(password.trim(), user.getPassword())) {
                            System.out.println("Welcome back " + user.getName());
                            currentUser = user;
                            tempUser = new User(currentUser.getName(), currentUser.getPassword(), currentUser.getMajors(), currentUser.getMinors(), null);
                            //tempUser.loadSchedule();
                            schedule = new Schedule(username, semester);
                            SearchDatabase sb = SearchDatabase.getInstance();
                            completedCourses = sb.getCompletedCoursesFromDB(username);
                            break;
                        } else {
                            System.out.println("Invalid password");
                        }
                    }
                }
            } else {
                System.out.println("Invalid input");
            }
        }

        System.out.printf("Welcome to Student Scheduler %s!\n",currentUser.getName());

        //Main program loop
        while (!cmd.equals("exit")){
            System.out.println("Please enter a command or 'help' for options \n\t Current Schedule: " + semester);
            System.out.print("> ");
            String[] input = s.nextLine().split(" ");
            cmd = input[0];
            switch (cmd) {
                case "help":
                    help(input);
                    break;
                case "courses":
                    listCourses(input);
                    break;
                case "calendar":
                    printCalendar(input);
                    break;
                case "search":
                    search(input);
                    break;
                case "results":
                case "page":
                    results(input);
                    break;
                case "add":
                    addCourse(input);
                    break;
                case "remove":
                    removeCourse(input);
                    break;
                case "exit":
                    break;
                case "change semester":
                    changeSemester();
                    break;
                case "completed":
                    markCompleted(input);
                    break;
                case "see_completed":
                    seeCompletedCourses(input);
                    break;
                default:
                    //Provide feedback on invalid command
                    System.out.println("Unknown Command: "+cmd);
            }
        }
        System.out.println("Thank you for using Student Scheduler");
    }


    public static void seeCompletedCourses(String[] options){
        if (completedCourses.isEmpty()){
            System.out.println("You have no completed courses");
        }
        for (Course c : completedCourses){
            System.out.println(c.toString());
        }
    }
    public static void markCompleted(String[] options){
        if (options.length < 2){
            System.out.println("Proper usage: completed <course_id>");
            return;
        }

        int cid = -1;

        try{
            cid = Integer.parseInt(options[1]);
        } catch (NumberFormatException e){
            System.out.printf("Error: %s is not a number\n",options[1]);
            return;
        }

        UpdateDatabaseContents ub = new UpdateDatabaseContents();
        ub.addCompletedCourse(currentUser.getUsername(), Integer.toString(cid));
        completedCourses.add(getCourse(cid));


    }
    /**
     * Prints a list of commands
     * @param options
     */
    private static void help(String[] options){
        System.out.println("  add <id> - adds class to schedule if there are no schedule conflicts");
        System.out.println("  add <id> replace - adds class to schedule removing any courses with schedule conflicts");
        System.out.println("  remove <id> - removes class from schedule");
        System.out.println("  courses - display list of users classes");
        System.out.println("  calendar - display schedule as calendar");
        System.out.println("  search - search for classes");
        System.out.println("  results <page> - view page of search results");
        System.out.println("  change semester -  change the semester of the schedule that you are working on.");
        System.out.println("  exit - exits the program");
        System.out.println("  completed <id> - marks a course as completed");
        System.out.println("  see_completed - view completed courses");
    }

    private static void search(String[] options){
        StringBuilder query = new StringBuilder();
        for (int i = 1; i < options.length; i++){
            query.append(options[i]);
            query.append(" ");
        }

        search = new Search(query.toString(), currentUser.getUsername(), false);
        search.search();
        results(new String[]{"results","1"});
    }

    public static void results(String[] options){
        int page;
        if (options.length < 2){
            System.out.println("Proper Usage: results <page>");
            return;
        }
        if (search == null){
            System.out.println("Please search for courses first");
            return;
        }
        try {
            page = Integer.parseInt(options[1]);
        } catch (NumberFormatException e){
            System.out.printf("Error: %s is not a number\n",options[1]);
            return;
        }
        if (page <= 0){
            System.out.println("Page cannot be less than 0!");
            return;
        }
        List<Course> results = search.getResult();
        if ((page-1)*RESULTS_PER_PAGE >= results.size()){
            System.out.println("Page does not exist");
            return;
        }
        int startIdx = (page-1)*RESULTS_PER_PAGE;
        int endIdx = Math.min(page*RESULTS_PER_PAGE,results.size());
        for (int i = startIdx; i < endIdx; i++){
            System.out.println(results.get(i));
        }
        System.out.printf("Page %d of %d (%d results)\n",page,(results.size()+RESULTS_PER_PAGE-1)/RESULTS_PER_PAGE,results.size());
        System.out.println("Use command: 'results <page#>' to change page");
    }

    /**
     * Prints a list of the users classes
     * @param options
     */
    private static void listCourses(String[] options){
        List<Course> enrolled = schedule.getCourses();
        if (enrolled.isEmpty()){
            System.out.println("You are not enrolled in any courses");
        }
        for (Course c : enrolled){
            //Needs to string defined
            System.out.println(c);
        }
    }

    private static void changeSemester(){
        while(true) {
            System.out.println("Enter the semester you would like to work on, (Fall, Winter_Online, Spring, Early_Summer, Late_Summer");
            String input = s.next();
            if(input.equals("Fall") || input.equals("Winter_Online") || input.equals("Spring") || input.equals("Early_Summer") || input.equals("Late_Summer")){
                semester = input;
                schedule = new Schedule(currentUser.getUsername(), semester);
            }
        }
    }

    /**
     * Prints a calendar representation of the schedule
     * @param options
     */
    private static void printCalendar(String[] options){
        System.out.println("  Time   |   Mon   |   Tue   |   Wed   |   Thu   |   Fri   |");
        for (int i = 0; i < 55; i++){
            int currentTime = i*15;
            System.out.print(formatTime(currentTime));
            System.out.print(" |");
            for (int j = 0; j < 5; j++){
                boolean timeFilled = false;
                for (Course c : schedule.getCourses()){
                    int startTime = c.getStartTime()[j];
                    if (startTime == -1){ continue; }
                    if (startTime <= currentTime && startTime+c.getDuration() >= currentTime){
                        System.out.print(c.getDepartment());
                        if (c.getDepartment().length() < 4){
                            System.out.print(" ");
                        }
                        System.out.print(" ");
                        System.out.print(c.getCourseCode());
                        System.out.print(c.getSection());
                        System.out.print("|");
                        timeFilled = true;
                    }
                }
                if (!timeFilled) {
                    System.out.print("         |");
                }
            }
            System.out.println();
        }
    }

    /**
     * Adds a course to the user's schedule
     * @param options
     */
    private static void addCourse(String[] options){
        if (options.length < 2){
            System.out.println("Proper usage: add <course_id>");
            return;
        }

        boolean replace = options.length >= 3 && options[2].equalsIgnoreCase("replace");

        int cid = -1;

        try {
            cid = Integer.parseInt(options[1]);
        } catch (NumberFormatException e){
            System.out.printf("Error: %s is not a number\n",options[1]);
            return;
        }
        Course add = getCourse(cid);
        if (add == null){ return; }
        try {
            schedule.addCourse(add);
        } catch (CourseFullException | SemesterMismatchException e){
            System.out.println(e.getMessage());
        } catch (ScheduleConflictException e) {
            if (!replace){
                System.out.println("Failed to add course to schedule");
                System.out.println("Time conflict(s) with:");
                List<ScheduleEvent> conflicts = schedule.getConflicts(add);
                for (ScheduleEvent c : conflicts){
                    System.out.printf("  %s\n",c.getName());
                }
                System.out.printf("Run 'add %s replace' to remove conflicts and add course\n",options[1]);
                return;
            }
            List<ScheduleEvent> conflicts = schedule.getConflicts(add);
            for (ScheduleEvent c : conflicts) {
                schedule.removeCourse(c);
                System.out.printf("Successfully removed %s from schedule\n",
                        c.getName());
            }
            try {
                schedule.addCourse(add);
            } catch (Exception e1){
                System.out.println(e1.getMessage());
            }
        }
        System.out.printf("Successfully added %s %s%s to schedule\n",
                    add.getDepartment(),add.getCourseCode(),add.getSection());
    }

    /**
     * Removes a course from the user's schedule
     * @param options
     */
    private static void removeCourse(String[] options){
        if (options.length < 2){
            System.out.println("Proper usage: remove <course_id>");
            return;
        }
        try {
            int cid = Integer.parseInt(options[1]);
            Course remove = getCourse(cid);
            if (remove == null) { return; }
            boolean removed = schedule.removeCourse(remove);
            //tempUser.saveSchedule();
            if (removed){
                System.out.printf("Successfully removed %s %s%s from schedule\n",
                        remove.getDepartment(),remove.getCourseCode(),remove.getSection());
            }
        } catch (NumberFormatException e){
            System.out.printf("Error: %s is not a number\n",options[1]);
        }
    }

    /**
     *
     * @param time offset (in minutes) from 8 AM
     * @return formatted time string
     */
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
}
