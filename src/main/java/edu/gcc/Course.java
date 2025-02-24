package edu.gcc;

import java.net.URL;
import java.util.ArrayList;

public class Course {
    private String name;

    private String description;
    private int[] startTime;
    private boolean MWForTR;
    private int[] duration;
    private ArrayList<String> professors;
    private boolean isOpen;
    private boolean[] daysMeet;
    private String department;
    private String courseCode;
    private int credits;
    private int numSeats;
    private URL rateMyProfessorLink;
    public Course(String name, int[] startTime, int[] duration, boolean isOpen, ArrayList<String> professors, boolean MWForTR, boolean[] daysMeet, String department, String courseCode, int credits, int numSeats) {
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
    }

    public URL getRateMyProfessorLink(){return null;}
    public void setRateMyProfessorLink(){}

    public int getCredits(){return credits;}
    public String getName() {
        return name;
    }
    public int getNumSeats(){return numSeats;}
    public String getDepartment(){return department;}
    public String getCourseCode(){return courseCode;}
    public boolean getIsOpen(){return isOpen;}
    public String getDescription() {
        return description;
    }
    public boolean[] getDaysMeet(){return daysMeet;}

    public int[] getStartTime() {
        return startTime;
    }
    public boolean getMWForTR(){return MWForTR;}

    public int[] getDuration() {
        return duration;
    }

    public ArrayList<String> getProfessor() {
        return professors;
    }
    public boolean hasConflict(Course course){
        return false;
    }

    @Override
    public String toString(){
        return null;
    }
}
