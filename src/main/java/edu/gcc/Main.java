package edu.gcc;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final ArrayList<User> users = new ArrayList<>();
    private static Search search;
    private static ArrayList<Course> courses = new ArrayList<>();
    public static void main(String[] args) {
        run();
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
                        System.out.println(c);
                    }
                    break;
                case "calendar":
                    //Prints a calendar representation of the schedule
                    break;
                case "search":
                    //Search
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
}
