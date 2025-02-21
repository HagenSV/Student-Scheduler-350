package edu.gcc;

import java.net.URL;

public class Course {
    private String name;
    private String description;
    private int[] startTime;
    private boolean MWForTR;
    private int[] duration;
    private String professor;
    private boolean hasSeats;
    private URL rateMyProfessorLink;
    public Course(String name, String description, int[] startTime, int[] duration, String professor) {

    }

    public URL getRateMyProfessorLink(){return null;}
    public void setRateMyProfessorLink(){}

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int[] getStartTime() {
        return startTime;
    }
    public boolean getMWForTR(){return MWForTR;}

    public int[] getDuration() {
        return duration;
    }

    public String getProfessor() {
        return professor;
    }
    public boolean hasConflict(Course course){
        return false;
    }
}
