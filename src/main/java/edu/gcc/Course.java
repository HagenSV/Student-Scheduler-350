package edu.gcc;

import java.net.URL;
import java.util.ArrayList;

public class Course {
    // Private fields to store course information
    private String name;                    // Course title
    private String description;            // Course description (not set in constructor)
    private int[] startTime;               // Array of start times (minutes since 8 AM) for each day
    private boolean MWForTR;              // True = Monday/Wednesday/Friday, False = Tuesday/Thursday
    private int duration;                 // Length of class in minutes
    private ArrayList<String> professors; // List of professors teaching the course
    private boolean isOpen;               // Indicates if course has available seats
    private boolean[] daysMeet;           // Array of booleans for days course meets (M-F)
    private String department;            // Academic department offering the course
    private String courseCode;            // Course identifier (e.g., "CS101")
    private int credits;                  // Number of credit hours
    private int numSeats;                 // Number of available seats
    private String section;               // Section identifier (e.g., "A", "B")
    private URL rateMyProfessorLink;      // URL for professor ratings (not implemented)
    private int CID;                      // Unique course identification number
    private boolean isLab;
    private String semester;
    private String location;               // Location of the course (e.g., "Room 101");
    // Indicates if course is a lab

    // Constructor to initialize a Course object with all necessary parameters
    public Course(int CID, String name, int[] startTime, int duration, boolean isOpen,
                  ArrayList<String> professors, boolean MWForTR, boolean[] daysMeet,
                  String department, String courseCode, int credits, int numSeats,
                  String section, boolean isLab, String semester, String location) {
        this.CID = CID;
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
        this.MWForTR = MWForTR;
        this.professors = professors;
        this.isOpen = isOpen;
        this.daysMeet = daysMeet;
        this.department = department;
        this.courseCode = courseCode;
        this.credits = credits;
        this.numSeats = numSeats;
        this.section = section;
        this.isLab = isLab;
        this.semester = semester;
        this.location = location;
    }

    // Getter methods for accessing course properties
    public String getSection(){return section;}
    public URL getRateMyProfessorLink(){return null;}  // Stub method, not implemented
    public void setRateMyProfessorLink(){}            // Stub method, not implemented
    public int getCID(){return CID;}
    public int getCredits(){return credits;}
    public String getName() {return name;}
    public int getNumSeats(){return numSeats;}
    public String getDepartment(){return department;}
    public String getCourseCode(){return courseCode;}
    public boolean getIsOpen(){return isOpen;}
    public String getDescription() {return description;}
    public boolean[] getDaysMeet(){return daysMeet;}
    public boolean getIsLab(){return isLab;}
    public int[] getStartTime() {return startTime;}
    public boolean getMWForTR(){return MWForTR;}
    public int getDuration() {return duration;}
    public ArrayList<String> getProfessor() {return professors;}
    public String getSemester(){return semester;}
    public String getLocation(){return location;}
    /**
     * Checks if this course conflicts with another course in terms of scheduling
     * Generated using Grok AI
     * @param other The other Course to check against
     * @return true if there is a scheduling conflict, false otherwise
     */
    public boolean hasConflict(Course other) {
        // Check if courses follow different day patterns (MWF vs TR)
        if (this.MWForTR != other.MWForTR) {
            return false;  // No conflict if on different schedules
        }

        // Check each day (0-4 represents Monday-Friday)
        for (int i = 0; i < 5; i++) {
            // Check if both courses meet on this day and have valid start times
            if (this.daysMeet[i] && other.daysMeet[i] &&
                    this.startTime[i] != -1 && other.startTime[i] != -1) {
                int thisStart = this.startTime[i];
                int otherStart = other.startTime[i];
                int thisEnd = thisStart + this.duration;
                int otherEnd = otherStart + other.duration;
                // Check if time periods overlap
                if (thisStart < otherEnd && otherStart < thisEnd) {
                    return true;  // Conflict found
                }
            }
        }
        return false;  // No conflicts found
    }

    // Override equals method to compare courses based on CID
    @Override
    public boolean equals(Object other){
        if(other instanceof Course){
            Course otherCourse = (Course) other;
            if(otherCourse.getCID() == this.CID){
                return true;  // Courses are equal if they have the same CID
            }
        }
        return false;
    }

    // Check if course name contains any of the given keywords
    public boolean hasKeyword(String keywordPhrase){
        String[] keywords = keywordPhrase.split(" ");
        String[] wordsInName = name.split(" ");
        for(String name : wordsInName){
            for(String keyword : keywords){
                if(name.toLowerCase().contains(keyword.toLowerCase())){
                    return true;  // Keyword found in course name
                }
            }
        }
        return false;  // No keywords found
    }

    // Check if course name contains a specific keyword
    public boolean isKeyword(String keyword){
        if(name.toLowerCase().contains(keyword.toLowerCase())){
            return true;  // Keyword found in course name
        }
        return false;  // Keyword not found
    }

    // Convert course information to a formatted string
    @Override
    public String toString(){
        StringBuilder output = new StringBuilder();
        // Basic course info
        output.append("CID("+ CID +") " + department + " " + courseCode + " " +
                section + " - " + name + "\n\tnumOpenSeats: " + numSeats +
                " isLab: " + isLab +" professor(s) \n\t"
        );

        // Append all professors
        for(String p : professors){
            output.append(p + " ");
        }

        // Additional details
        output.append("\n\tCredits: " + credits + " numOpenSeats: " + numSeats +
                " MWF: " + MWForTR + "\n\tDays: ");

        // Append meeting days and times
        for(int i = 0; i < daysMeet.length; i++){
            if(daysMeet[i]){
                output.append(getDay(i) + " " + convertTimeToString(startTime[i]) + " ");
            }
        }
        output.append("\n\tDuration: " + duration +
                "\n\tLocation: " + location + "\n\tSemester: " + semester + "\n");


        return output.toString();
    }

    // Convert day index to day abbreviation
    public String getDay(int i){
        if(i == 0) return "M";      // Monday
        else if(i == 1) return "T"; // Tuesday
        else if(i == 2) return "W"; // Wednesday
        else if(i == 3) return "R"; // Thursday
        else if(i == 4) return "F"; // Friday
        return "Invalid Day";       // Error case
    }

    // Convert minutes since 8 AM to time string (HH:MM format)
    public String convertTimeToString(int minAfter8){
        int hour = 8 + minAfter8 / 60;
        int min = minAfter8 % 60;
        String time = "";
        if(hour < 10) time += "0";  // Add leading zero if needed
        time += hour + ":";
        if(min < 10) time += "0";   // Add leading zero if needed
        time += min;
        return time;
    }
}