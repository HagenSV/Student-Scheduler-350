package edu.gcc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleDriver {

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
        Scanner s = new Scanner(System.in);

        User currentUser = null;

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
                        Course add = getCourse(cid);
                        if (add != null) {
                            currentUser.getSchedule().addCourse(add);
                            currentUser.saveSchedule();
                        }
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
                        Course remove = getCourse(cid);
                        if (remove != null) {
                            currentUser.getSchedule().removeCourse(remove);
                            currentUser.saveSchedule();
                        }
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
}
