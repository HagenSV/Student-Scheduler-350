package edu.gcc;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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
        return true;
    }
    public boolean removeMajor(String major) {
        return majors.remove(major);
    }
    public boolean addMinor(String minor, int joiningYear) {
        if (minors.contains(minor))
            return false;
        minors.add(minor);
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

    public void saveSchedule() {
    }
    public void loadSchedule() {
    }
}
