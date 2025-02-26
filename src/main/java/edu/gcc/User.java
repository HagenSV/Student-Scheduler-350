package edu.gcc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class  User {
    private String username;
    private String password;
    private ArrayList<String> majors;
    private ArrayList<String> minors;
    private int yearJoinedMajor;
    private int yearJoinedMinor;
    private Schedule schedule;
    private ArrayList<Course> completedCourses;

    public User(String name, String password, ArrayList<String> majors, ArrayList<String> minors, ArrayList<Course> completedCourses) {
        this.username = name;
        this.password = password;
        this.majors = majors;
        this.minors = minors;
        this.completedCourses = completedCourses;
    }

    /**
     * Adds a major to the list of User majors
     * @param major name of the major
     * @param joiningYear the year that the major was added to the User
     * @return whether adding the major was successful, false if already added
     */
    public boolean addMajor(String major, int joiningYear) {
        if (majors.contains(major))
            return false;
        majors.add(major);
        yearJoinedMajor = joiningYear;
        return true;
    }

    /**
     * Removes the specified major from the User
     * @param major the major to remove
     * @return whether removing the major was successful, false it does not exist
     */
    public boolean removeMajor(String major) {
        return majors.remove(major);
    }

    /**
     * Adds a minor to the list of User minors
     * @param minor name of the minor
     * @param joiningYear the year that the major was added to the User
     * @return whether adding the minor was successful, false if already added
     */
    public boolean addMinor(String minor, int joiningYear) {
        if (minors.contains(minor))
            return false;
        minors.add(minor);
        yearJoinedMinor = joiningYear;
        return true;
    }

    /**
     * Removes the specified minor from the User
     * @param minor the minor to remove
     * @return whether removal was successful, false if does not exist
     */
    public boolean removeMinor(String minor){
        return minors.remove(minor);
    }

    /**
     * Updates the user schedule if the candidate schedule has no conflicts
     * @param schedule the updated schedule
     * @return whether it was updated successfully, false if there is a conflict with the new schedule
     */
    public boolean updateSchedule(Schedule schedule) {
        if (!schedule.getConflicts().isEmpty())
            return false;
        this.schedule = schedule;
        return true;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    public void changeUserName(String newUsername) {
        this.username = newUsername;
    }
    public String getName() {
        return username;
    }

    public ArrayList<String> getMajors() {
        return majors;
    }

    public ArrayList<String> getMinors() {
        return minors;
    }

    public Schedule getSchedule(){
        return schedule;
    }

    /**
     *  Saves the User's schedule to a text file that can be loaded later
     */
    public void saveSchedule() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("schedule.txt"))) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Course c: schedule.getCourses()) {

                // Add Name
                stringBuilder.append(c.getName()).append(" ");

                // Add StartTime array, times separated by ,
                int[] startTime = c.getStartTime();
                stringBuilder.append(startTime.length).append(",");
                for (int i = 0; i < startTime.length; i++) {
                    if (i > 0)
                        stringBuilder.append(",");
                    stringBuilder.append(startTime[i]);
                }
                stringBuilder.append(" ");

                // Add MWForTR and duration
                stringBuilder.append(c.getMWForTR()).append(" ");
                stringBuilder.append(c.getDuration()).append(" ");

                // Add professors, professors separated by ,
                for (int i = 0; i < c.getProfessor().size(); i++) {
                    if (i > 0)
                        stringBuilder.append(",");
                    stringBuilder.append(c.getProfessor().get(i));
                }

                // Add isOpen
                stringBuilder.append(c.getIsOpen()).append(" ");

                // Add daysMeet
                boolean[] daysMeet = c.getDaysMeet();
                for (boolean b : daysMeet) {
                    if (b)
                        stringBuilder.append("T");
                    else
                        stringBuilder.append("F");
                }
                stringBuilder.append(" ");

                // Add department, courseCode, credits, numSeats, section and isLab
                stringBuilder.append(c.getDepartment()).append(" ");
                stringBuilder.append(c.getCourseCode()).append(" ");
                stringBuilder.append(c.getCredits()).append(" ");
                stringBuilder.append(c.getNumSeats()).append(" ");
                stringBuilder.append(c.getSection()).append(" ");
                stringBuilder.append(c.getIsLab()).append(" ");

                // Finally write course into line
                writer.println(stringBuilder);
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Loads the schedule to the User saved in the schedule.txt file
     */
    public void loadSchedule() {
        File file = new File("schedule.txt");
        if (file.exists()) {
            try {
                ArrayList<Course> courses = new ArrayList<>();
                Scanner byLine = new Scanner(file);

                // Every line adds a new course to courses
                while (byLine.hasNext()) {
                    String line = byLine.next();
                    Scanner byEntry = new Scanner(line);

                    // Loads the name and description
                    String name = byEntry.next();

                    // Loads the startTime array
                    String timeString = byEntry.next();
                    Scanner parseTime = new Scanner(timeString);
                    parseTime.useDelimiter(",");
                    int startTimeSize = Integer.parseInt(parseTime.next());
                    int[] startTime = new int[startTimeSize];
                    for (int i = 0; i < startTimeSize; i++)
                        startTime[i] = Integer.parseInt(parseTime.next());

                    // Loads MWForTR and duration
                    boolean MWForTR = Boolean.parseBoolean(byEntry.next());
                    int duration = Integer.parseInt(byEntry.next());

                    // Loads the Professors
                    ArrayList<String> professors = new ArrayList<>();
                    String professorString = byEntry.next();
                    Scanner parseProfessors = new Scanner(professorString);
                    parseProfessors.useDelimiter(",");
                    while (parseProfessors.hasNext())
                        professors.add(parseProfessors.next());

                    // Loads isOpen
                    boolean isOpen = Boolean.parseBoolean(byEntry.next());

                    // Loads daysMeet
                    String daysMeetString = byEntry.next();
                    boolean[] daysMeet = new boolean[7];
                    for (int i = 0; i < 7; i++) {
                        if (daysMeetString.charAt(i) == 'T')
                            daysMeet[i] = true;
                        else
                            daysMeet[i] = false;
                    }

                    // Loads department, courseCode, credits, numSeats, section, and isLab
                    String department = byEntry.next();
                    String courseCode = byEntry.next();
                    int credits = Integer.parseInt(byEntry.next());
                    int numSeats = Integer.parseInt(byEntry.next());
                    String section = byEntry.next();
                    boolean isLab = Boolean.parseBoolean(byEntry.next());

                    courses.add(new Course(name, startTime, duration, isOpen, professors, MWForTR, daysMeet, department, courseCode, credits, numSeats, section, isLab));
                }
                schedule = new Schedule(courses);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}