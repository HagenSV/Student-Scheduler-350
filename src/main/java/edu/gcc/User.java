package edu.gcc;

import java.net.URL;
import java.io.*;
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

    public boolean addMajor(String major, int joiningYear) {
        if (majors.contains(major))
            return false;
        majors.add(major);
        yearJoinedMajor = joiningYear;
        return true;
    }
    public boolean removeMajor(String major) {
        return majors.remove(major);
    }
    public boolean addMinor(String minor, int joiningYear) {
        if (minors.contains(minor))
            return false;
        minors.add(minor);
        yearJoinedMinor = joiningYear;
        return true;
    }

    public boolean removeMinor(String minor){
        return minors.remove(minor);
    }

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

                // Add MWForTR
                stringBuilder.append(c.getMWForTR()).append(" ");

                // Add duration, durations separated by ,
                int[] duration = c.getDuration();
                stringBuilder.append(duration.length).append(",");
                for (int i = 0; i < startTime.length; i++) {
                    if (i > 0)
                        stringBuilder.append(",");
                    stringBuilder.append(duration[i]);
                }
                stringBuilder.append(" ");

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

                // Add rate my professor link
                stringBuilder.append(c.getRateMyProfessorLink());

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
                    String description = byEntry.next();

                    // Loads the startTime array
                    String timeString = byEntry.next();
                    Scanner parseTime = new Scanner(timeString);
                    parseTime.useDelimiter(",");
                    int startTimeSize = Integer.parseInt(parseTime.next());
                    int[] startTime = new int[startTimeSize];
                    for (int i = 0; i < startTimeSize; i++)
                        startTime[i] = Integer.parseInt(parseTime.next());

                    // Loads MWForTR
                    boolean MWForTR = Boolean.parseBoolean(byEntry.next());

                    // Loads the duration
                    String durationString = byEntry.next();
                    Scanner parseDuration = new Scanner(durationString);
                    parseDuration.useDelimiter(",");
                    int durationSize = Integer.parseInt(parseDuration.next());
                    int[] duration = new int[durationSize];
                    for (int i = 0; i < durationSize; i++)
                        duration[i] = Integer.parseInt(parseDuration.next());

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
                    }

                    // Loads RateMyProfessor link
                    URL rateMyProfessorLink = new URL(byEntry.next());

                    courses.add(new Course(name, startTime, duration, isOpen, professors, MWForTR, daysMeet, rateMyProfessorLink));
                }
                schedule = new Schedule(courses);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}