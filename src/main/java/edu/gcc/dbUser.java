package edu.gcc;

import jakarta.persistence.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class dbUser {
    // Database URL
    private static String url = "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?user=postgres.chhgjsqthhxqsvutshqi&password=Comp350dics";

    @Id
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection
    @CollectionTable(name = "user_major", joinColumns = @JoinColumn(name = "username"))
    @Column(name = "majorname")
    private List<String> majors;

    @ElementCollection
    @CollectionTable(name = "user_minor", joinColumns = @JoinColumn(name = "username"))
    @Column(name = "minor_name")
    private List<String> minors;

    //private int yearJoinedMajor;
    //private int yearJoinedMinor;

    @Transient
    private Schedule schedule;

    //    @ElementCollection
//    @CollectionTable(name = "completed_courses", joinColumns = @JoinColumn(name = "username"))
//    @Column(name = "course")
    @Transient
    private List<Course> completedCourses;

    public dbUser(){
        this("","");
    }

    public dbUser(String name, String password){
        this.username = name;
        this.password = password;
        this.majors = new ArrayList<>();
        this.minors = new ArrayList<>();
        this.completedCourses = new ArrayList<>();
        this.schedule = new Schedule();
    }

    /**
     * Creates the user but doesn't add it to the database
     * @param name
     * @param password
     * @param majors
     * @param minors
     * @param completedCourses
     */
    public dbUser(String name, String password, List<String> majors, List<String> minors, List<Course> completedCourses) {
        this.username = name;
        this.password = password;
        this.majors = majors;
        this.minors = minors;
        this.completedCourses = completedCourses;
        this.schedule = new Schedule();
    }

    public dbUser(String name, String password, List<String> majors, List<String> minors, List<Course> completedCourses, int addToDatabase) {
        this.username = name;
        this.password = password;
        this.majors = majors;
        this.minors = minors;
        this.completedCourses = completedCourses;
        this.schedule = new Schedule();
        if (addToDatabase == 1) {
            UpdateDatabaseContents.addUser(this);
        }
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
        //yearJoinedMajor = joiningYear;
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

    public void setMajors(List<String> majors){
        this.majors = majors;
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
        //yearJoinedMinor = joiningYear;
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

    public void setMinors(List<String> minors){
        this.minors = minors;
    }

    /**
     * Updates the user schedule to the one provided
     * @param schedule the updated schedule
     */
    public void updateSchedule(Schedule schedule) {
        this.schedule = schedule;
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

    public List<String> getMajors() {
        return majors;
    }

    public List<String> getMinors() {
        return minors;
    }

    public Schedule getSchedule(){
        return schedule;
    }

    public String getUsername(){return username;}

    // Creates hashed_password
    public String getPasswordUpload(){return BCrypt.hashpw(password, BCrypt.gensalt());}

    public String getPassword(){
        return password;
    }
    public boolean passwordAttempt(String password) {
        return this.password.equals(password);
    }

}
