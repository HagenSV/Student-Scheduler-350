package edu.gcc;

import java.net.URL;
import java.util.ArrayList;

public class Course {
    private String name;

    private String description;
    private int[] startTime;
    private boolean MWForTR;
    private int duration;
    private ArrayList<String> professors;
    private boolean isOpen;
    private boolean[] daysMeet;
    private String department;
    private String courseCode;
    private int credits;
    private int numSeats;
    private String section;
    private URL rateMyProfessorLink;
    private boolean isLab;
    public Course(String name, int[] startTime, int duration, boolean isOpen, ArrayList<String> professors, boolean MWForTR, boolean[] daysMeet, String department, String courseCode, int credits, int numSeats, String section, boolean isLab) {
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
    }

    public String getSection(){return section;}
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
    public boolean getIsLab(){return isLab;}
    public int[] getStartTime() {
        return startTime;
    }
    public boolean getMWForTR(){return MWForTR;}

    public int getDuration() {
        return duration;
    }

    public ArrayList<String> getProfessor() {
        return professors;
    }

    /**
     * Checks if courses conflict with each other
     * Generated using Grok AI
     * @param other Course to check if it conflicts
     * @return boolean whether there is a conflict
     */
    public boolean hasConflict(Course other) {
        // Check if they follow the same day pattern (MWF or TR)
        if (this.MWForTR != other.MWForTR) {
            return false;
        }

        // Check each day (0-4)
        for (int i = 0; i < 5; i++) {
            // Both must meet on this day and have valid start times
            if (this.daysMeet[i] && other.daysMeet[i] &&
                    this.startTime[i] != -1 && other.startTime[i] != -1) {
                int thisStart = this.startTime[i];
                int otherStart = other.startTime[i];
                int thisEnd = thisStart + this.duration;
                int otherEnd = otherStart + other.duration;
                if (thisStart < otherEnd && otherStart < thisEnd) {
                    return true; // Conflict found on this day
                }
            }
        }
        return false; // No conflicts found
    }

    @Override
    public String toString(){
        StringBuilder output = new StringBuilder();
        output.append("(" + department + " " + courseCode + " " + section + ") " + name + "\n\tnumOpenSeats: " + numSeats + " isLab: " + isLab +" professor(s) ");
        for(String p : professors){
            output.append(p + " ");
        }
        output.append("\n\tCredits: "  + credits +  " numOpenSeats:  " + numSeats + " MWF: " + MWForTR + "\n\tDaysMeet: ");
        for(Boolean d : daysMeet){
            output.append(d + " ");
        }
        output.append("\n\tStartingTimes (minutes after 8:00): ");
        for(int i : startTime){
            output.append(i + " ");
        }
        output.append("\n\tDuration: " + duration + "\n");

        return output.toString();
    }
}
