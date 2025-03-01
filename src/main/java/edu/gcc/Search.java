package edu.gcc;

import java.util.ArrayList;

public class Search {
    private String query;
    private boolean daysMeeting[];
    private int minTime;
    private int maxTime;
    private String desiredProfesor;
    private String department;
    private ArrayList<Course> initialResult;
    private ArrayList<Course> filteredResult;

    public Search(String query){

    }

    // If any of these methods are called it will call their corresponding Filter
    public ArrayList<Course> setDaysMeeting(boolean[] daysMeeting){
        for (Course c : filteredResult) {
            for (int i = 0; i < daysMeeting.length; i++)    {
                if (daysMeeting[i] != c.getDaysMeet()[i])   {
                    filteredResult.remove(c);
                    break;
                }
            }
        }
        return filteredResult;
    }
    public ArrayList<Course> setTime(int newMinTime, int newMaxTime){
        boolean withinTime = false;
        for (Course c : filteredResult) {
            for (int t : c.getStartTime())  {
                if (t > newMinTime && t < newMinTime + c.getDuration()) {
                    withinTime = true;
                    break;
                }
            }
            if (!withinTime)    {
                filteredResult.remove(c);
            }
        }
        return filteredResult;
    }
    public ArrayList<Course> setDesiredProfesor(String desiredProfesor){
        boolean rightProfessor = false;
        for (Course c : filteredResult) {
            for (String p : c.getProfessor())   {
                if (desiredProfesor.equals(p))  {
                    rightProfessor = true;
                    break;
                }
            }
            if (!rightProfessor)   {
                filteredResult.remove(c);
            }
        }
        return filteredResult;
    }
    public ArrayList<Course> setDepartment(String department){
        for (Course c : filteredResult) {
            if (!c.getDepartment().equals(department))    {
                filteredResult.remove(c);
            }
        }
        return filteredResult;
    }

    //These will change result
    public ArrayList<Course> filterByDaysMeeting(){
        return setDaysMeeting(daysMeeting);
    }
    public ArrayList<Course> filterByTime(){
        return setTime(minTime, maxTime);
    }
    public ArrayList<Course> filterByDesiredProfessor(){
        return setDesiredProfesor(desiredProfesor);
    }
    public ArrayList<Course> filterByDepartment(){
        return setDepartment(department);
    }

    public ArrayList<Course> getResult(){
        return filteredResult;
    }







}
