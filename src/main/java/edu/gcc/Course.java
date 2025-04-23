package edu.gcc;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class Course extends ScheduleEvent {
    // Private fields to store course information
    private String description;            // Course description (not set in constructor)
    private boolean MWForTR;              // True = Monday/Wednesday/Friday, False = Tuesday/Thursday
    private ArrayList<String> professors; // List of professors teaching the course
    private boolean isOpen;               // Indicates if course has available seats
    private String department;            // Academic department offering the course
    private String courseCode;            // Course identifier (e.g., "CS101")
    private int credits;                  // Number of credit hours
    private int numSeats;                 // Number of available seats
    private String section;               // Section identifier (e.g., "A", "B")
    private URL rateMyProfessorLink;      // URL for professor ratings (not implemented)
    private boolean isLab;
    // Indicates if course is a lab

    // Constructor to initialize a Course object with all necessary parameters
    public Course(int CID, String name, int[] startTime, int duration, boolean isOpen,
                  ArrayList<String> professors, boolean MWForTR, boolean[] daysMeet,
                  String department, String courseCode, int credits, int numSeats,
                  String section, boolean isLab, String semester, String location) {
        super(CID, name, startTime, duration, daysMeet, semester, location);
        this.MWForTR = MWForTR;
        this.professors = professors;
        this.isOpen = isOpen;
        this.department = department;
        this.courseCode = courseCode;
        this.credits = credits;
        this.numSeats = numSeats;
        this.section = section;
        this.isLab = isLab;
    }

    // Getter methods for accessing course properties
    public String getSection(){return section;}
    public URL getRateMyProfessorLink(){return null;}  // Stub method, not implemented
    public void setRateMyProfessorLink(){}            // Stub method, not implemented
    public int getCredits(){return credits;}
    public int getNumSeats(){return numSeats;}
    public String getDepartment(){return department;}
    public String getCourseCode(){return courseCode;}
    public boolean getIsOpen(){return isOpen;}
    public String getDescription() {return description;}
    public boolean getIsLab(){return isLab;}
    public boolean getMWForTR(){return MWForTR;}
    public ArrayList<String> getProfessor() {return professors;}

    // Check if the course name contains any of the given keywords
    public boolean hasKeyword(String keywordPhrase){
        String[] keywords = keywordPhrase.split(" ");
        String[] wordsInName = getName().split(" ");
        for(String name : wordsInName){
            for(String keyword : keywords){
                if(name.toLowerCase().contains(keyword.toLowerCase())){
                    return true;  // Keyword found in course name
                }
            }
        }
        return false;  // No keywords found
    }

    // Check if the course name contains a specific keyword
    public boolean isKeyword(String keyword){
        if(getName().toLowerCase().contains(keyword.toLowerCase())){
            return true;  // Keyword found in course name
        }
        return false;  // Keyword not found
    }

    // Convert course information to a formatted string
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        // Basic course info
        output.append("CID("+ getCID() +") " + department + " " + courseCode + " " +
                section + " - " + getName() + "\n\tnumOpenSeats: " + numSeats +
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
        for(int i = 0; i < 5; i++) {
            if(getDaysMeet()[i]) {
                output.append(getDay(i) + " " + convertTimeToString(getStartTime()[i]) + " ");
            }
        }
        output.append("\n\tDuration: " + getDuration() +
                "\n\tLocation: " + getLocation() + "\n\tSemester: " + getSemester() + "\n");

        return output.toString();
    }
}