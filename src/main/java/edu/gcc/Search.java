package edu.gcc;

import java.util.ArrayList;

public class Search {
    private String query;
    private boolean daysMeeting;
    private int minTime;
    private int maxTime;
    private String desiredProfesor;
    private String department;
    private ArrayList<Course> initialResult;
    private ArrayList<Course> filteredResult;

    public Search(String query){
    }

    // If any of these methods are called it will call their corresponding Filter
    public ArrayList<Course> setDaysMeeting(boolean daysMeeting){return null;}
    public ArrayList<Course> setTime(int newMinTime, int newMaxTime){return null;}
    public ArrayList<Course> setDesiredProfesor(String desiredProfesor){return null;}
    public ArrayList<Course> setDepartment(String department){return null;}

    //These will change result
    public ArrayList<Course> filterByDaysMeeting(){return null;}
    public ArrayList<Course> filterByTime(){return null;}
    public ArrayList<Course> filterByDesiredProfessor(){return null;}
    public ArrayList<Course> filterByDepartment(){return null;}

    public ArrayList<Course> getResult(){
        return filteredResult;
    }







}
