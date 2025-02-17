package edu.gcc;

import java.util.ArrayList;

public class  User {
    private String username;
    private String password;
    private String[] majors;
    private String[] minors;
    private int yearJoinedMajor;
    private int yearJoinedMinor;
    private Schedule schedule;

    public User(String name, String password, String[] majors, String[] minors, ArrayList<Course> completedCourses) {

    }

    public boolean addMajor(String major, int joiningYear) {
        return false;
    }
    public boolean removeMajor(String major) {
        return false;
    }
    public boolean addMinor(String minor, int joiningYear){return false;}
    public boolean removeMinor(String minor){return false;}

    public boolean updateSchedule(Schedule schedule){return false;}

    public void changePassword(String newPassword){}
    public void changeUserName(String newUsername){}
    public String getName() {
        return username;
    }

    public String[] getMajors() {
        return majors;
    }

    public String[] getMinors() {
        return minors;
    }

    public void saveSchedule(){}
}
