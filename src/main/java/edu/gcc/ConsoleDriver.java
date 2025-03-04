package edu.gcc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleDriver {

    private static final Scanner s = new Scanner(System.in);
    private static User currentUser = null;

    //TODO: getUsers()
    public static List<User> getUsers(){
        return new ArrayList<>();
    }

    public static void addUser(User u){

    }

    public static Course getCourse(int courseId){
        return null;
    }

    //TODO: getCourse(id)

    public static void run() {
        String cmd = "";

        //Loop until account is found
        while (currentUser == null) {
            //Prompt user for name
            System.out.print("Enter your username: ");
            String username = s.nextLine();

            //Search for existing user with name
            for (User u : getUsers()) {
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
                    addUser(currentUser);
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
                case "help":
                    help(input);
                    break;
                case "list":
                    listCourses(input);
                    break;
                case "calendar":
                    printCalendar(input);
                    break;
                case "search":
                    search(input);
                    break;
                case "add":
                    addCourse(input);
                    break;
                case "remove":
                    removeCourse(input);
                    break;
                case "exit":
                    break;
                default:
                    //Provide feedback on invalid command
                    System.out.println("Unknown Command: "+cmd);
            }
        }
        System.out.println("Thank you for using Student Scheduler");
    }

    /**
     * Prints a list of commands
     * @param options
     */
    private static void help(String[] options){
        System.out.println("  add <id> - adds class to schedule");
        System.out.println("  remove <id> - removes class from schedule");
        System.out.println("  courses - display list of users classes");
        System.out.println("  calendar - display schedule as calendar");
        System.out.println("  search - search for classes");
        System.out.println("  exit - exits the program");
    }

    private static void search(String[] options){
        //TODO: implement this when search is working
        System.out.println("Not implemented");
    }

    /**
     * Prints a list of the users classes
     * @param options
     */
    private static void listCourses(String[] options){
        Course[] enrolled = currentUser.getSchedule().getCourses();
        for (Course c : enrolled){
            //Needs to string defined
            System.out.println(c);
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
                for (Course c : currentUser.getSchedule().getCourses()){
                    int startTime = c.getStartTime()[j];
                    if (startTime >= currentTime && startTime+c.getDuration() <= currentTime){
                        //TODO get and display course dept and code
                        System.out.print(c.getDepartment());
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

        try {
            Schedule schedule = currentUser.getSchedule();
            int cid = Integer.parseInt(options[1]);
            Course add = getCourse(cid);
            if (add == null){ return; }
            boolean added = schedule.addCourse(add);
            if (!added && replace){
                //List<Course> conflicts = schedule.getConflicts(add);
                //for (Course c : conflicts){
                //    schedule.removeCourse();
                //}
                //added = schedule.addCourse(add);
                //if (!added){
                //  System.out.println("An unknown error occurred");
                //}
            } else if (!added) {
                System.out.println("Failed to add course to schedule");
                System.out.println("Time conflict(s) with: <classes>");
                System.out.printf("Run 'add %s replace' to remove conflicts and add course",options[1]);
            }
            currentUser.saveSchedule();
        } catch (NumberFormatException e){
            System.out.printf("Error: %s is not a number\n",options[1]);
        }
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

            currentUser.getSchedule().removeCourse(remove);
            currentUser.saveSchedule();
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
