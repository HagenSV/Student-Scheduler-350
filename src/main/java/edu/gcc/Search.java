package edu.gcc;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Search {
    private String query;
    private boolean daysMeeting; //true = MWF, false = TH
    private int minTime;
    private int maxTime;
    private String desiredProfesor;
    private String department;
    private ArrayList<Course> initialResult;
    private ArrayList<Course> filteredResult;

    private ArrayList<String> listDep;
    private ArrayList<String> listProf;

    public Search(String query){
        this.query = query;
        listDep = new ArrayList<>();
        listProf = new ArrayList<>();
        setDepartments();
        setProfessors();
        this.initialResult = Main.courses;
        filteredResult = new ArrayList<>();
    }

    // If any of these methods are called it will call their corresponding Filter
    //true = MWF, false = TH
    public ArrayList<Course> setDaysMeeting(boolean daysMeeting){
        this.daysMeeting = daysMeeting;
        return filterByDaysMeeting();
    }
    public ArrayList<Course> setTime(int newMinTime, int newMaxTime){
        this.minTime = newMinTime;
        this.maxTime = newMaxTime;
        return filterByTime();
    }
    public ArrayList<Course> setDesiredProfesor(String desiredProfesor){
        this.desiredProfesor = desiredProfesor;
        return filterByDesiredProfessor();
    }
    public ArrayList<Course> setDepartment(String department){
        this.department = department;
        return filterByDepartment();
    }

    //These will change result
    public ArrayList<Course> filterByDaysMeeting(){
        for (Course c : filteredResult) {
            if ((c.getDaysMeet()[0] || c.getDaysMeet()[2] || c.getDaysMeet()[4]) && daysMeeting) {
                filteredResult.add(c);
            }
            else if ((c.getDaysMeet()[1] || c.getDaysMeet()[3]) && !daysMeeting) {
                filteredResult.add(c);
            }
        }
        return filteredResult;
    }
    public ArrayList<Course> filterByTime(){
        boolean withinTime = false;
        if (minTime != -1 && maxTime != -1) {
            for (Course c : filteredResult) {
                for (int t : c.getStartTime()) {
                    if (t >= minTime && t  + c.getDuration() <= maxTime) {
                        withinTime = true;
                        break;
                    }
                }
                if (withinTime) {
                    filteredResult.add(c);
                }
            }
        }
        else if (minTime != -1) {
            for (Course c : filteredResult) {
                for (int t : c.getStartTime()) {
                    if (t >= minTime) {
                        withinTime = true;
                        break;
                    }
                }
                if (withinTime) {
                    filteredResult.add(c);
                }
            }
        }
        return filteredResult;
    }
    public ArrayList<Course> filterByDesiredProfessor(){
        boolean rightProfessor = false;
        for (Course c : filteredResult) {
            for (String p : c.getProfessor())   {
                if (desiredProfesor.equals(p))  {
                    rightProfessor = true;
                    break;
                }
            }
            if (rightProfessor)   {
                filteredResult.add(c);
            }
        }
        return filteredResult;
    }
    public ArrayList<Course> filterByDepartment(){
        for (Course c : filteredResult) {
            if (c.getDepartment().equals(department))    {
                filteredResult.add(c);
            }
        }
        return filteredResult;
    }

    public ArrayList<Course> getResult(){
        return filteredResult;
    }

    public void search(String query)    {
        query = query.toLowerCase();

        searchByCourseCode(query);
        searchByDaysMeeting(query);
        searchByTime(query);
        searchByDepartment(query);
        searchByProfessor(query);

    }

    public void searchByDaysMeeting(String query)   {
        boolean MWF = false;
        boolean TR = false;

        if (query.contains("monday") || query.contains("wednesday") || query.contains("friday") || (query.charAt(0) == 'm' && query.charAt(1) == ' ') || (query.charAt(query.length() - 1) == 'm' && query.charAt(query.length() - 2) == ' ') || query.contains(" w ") || (query.charAt(0) == 'w' && query.charAt(1) == ' ') || (query.charAt(query.length() - 1) == 'w' && query.charAt(query.length() - 2) == ' ') || query.contains("f") || (query.charAt(0) == 'f' && query.charAt(1) == ' ') || (query.charAt(query.length() - 1) == 'f' && query.charAt(query.length() - 2) == ' ') || query.contains("mwf")) {
            //need to add more edge case conditions
            MWF = true;

        }
        if (query.contains("tuesday") || query.contains("thursday") || (query.charAt(0) == 't' && query.charAt(1) == ' ') || (query.charAt(query.length() - 1) == 't' && query.charAt(query.length() - 2) == ' ') || query.contains(" tr ") || (query.charAt(0) == 't' && query.charAt(1) == 'r' && query.charAt(2) == ' ') || (query.charAt(query.length() - 1) == 'r' && query.charAt(query.length() - 2) == 't' && query.charAt(query.length() - 3) == ' ')) {
            //need to add more edge case conditions
            TR = true;

        }
        if (MWF && !TR)  {
            setDaysMeeting(true);
        }
        else if (TR && !MWF) {
            setDaysMeeting(false);
        }
    }

    /*
    public void searchByTime(String query)   {
        int firstNum = 0;
        int secondNum = 0;
        int thirdNum = 0;
        int fourthNum = 0;
        int firstCombined = 0;
        int secondCombined = 0;
        int finalTime = 0;

        int referenceTime = 8 * 60;
        boolean validTime = false;
        boolean firstTimeFound = false;

        if (query.contains(":"))    {
            query = query.replace(Character.toString('-'), "");
            Scanner word = new Scanner(query);
            word.useDelimiter("");
            while (word.hasNext())   {
                if (firstTimeFound == false) {
                    String temp = word.next();
                    char num = temp.charAt(0);
                    if (Character.isDigit(num)) {
                        int temp2 = num - '0';
                        num = temp.charAt(1);
                        if (Character.isDigit(num)) {
                            firstNum = temp2;
                            secondNum = num - '0';
                            num = temp.charAt(2);
                            if (num == ':') {
                                firstTimeFound = true;
                                num = temp.charAt(4);
                                if (Character.isDigit(num)) {
                                    thirdNum = num - '0';
                                    num = temp.charAt(5);
                                    if (Character.isDigit(num)) {
                                        fourthNum = num - '0';
                                        validTime = true;
                                    }
                                }
                            }
                        } else if (num == ':') {
                            firstTimeFound = true;
                            firstNum = 0;
                            secondNum = temp2;
                            num = temp.charAt(4);
                            if (Character.isDigit(num)) {
                                thirdNum = num - '0';
                                num = temp.charAt(5);
                                if (Character.isDigit(num)) {
                                    fourthNum = num - '0';
                                    validTime = true;
                                }
                            }
                        }
                    }
                }
                else {

                }
            }
        }
        if (validTime)  {

            firstCombined = firstCombined * 10 + firstNum + secondNum;
            if (firstCombined > 0 && firstCombined < 8)   {
                firstCombined = firstCombined + 12;
            }
            secondCombined = thirdNum * 10 + fourthNum;
            finalTime = firstCombined * 60 + secondCombined - referenceTime;
            setTime(finalTime, -1);

        }
    }


     */

    public void searchByTime(String query)   {
        String firstTime = "";
        String secondTime = "";
        int firstFinal = 0;
        int secondFinal = 0;
        int referenceTime = 8 * 60;
        boolean firstValid = false;
        boolean secondValid = false;

        query = query.replace(Character.toString('-'), " ");
        query = query.replace("am", " ");
        query = query.replace("pm", " ");
        Scanner word = new Scanner(query);

        while (word.hasNext())   {
            String temp = word.next();
            if (!firstValid) {
                DateTimeFormatter timeFormatter1 = DateTimeFormatter.ofPattern("HH:mm");
                try {
                    LocalTime.parse(temp, timeFormatter1);
                    firstValid = true;
                } catch (DateTimeParseException e) {
                    firstValid = false;
                }
                if (!firstValid) {
                    DateTimeFormatter timeFormatter2 = DateTimeFormatter.ofPattern("H:mm");
                    try {
                        LocalTime.parse(temp, timeFormatter2);
                        firstValid = true;
                    } catch (DateTimeParseException e) {
                        firstValid = false;
                    }
                }
            }
            else {
                DateTimeFormatter timeFormatter1 = DateTimeFormatter.ofPattern("HH:mm");
                try {
                    LocalTime.parse(temp, timeFormatter1);
                    secondValid = true;
                } catch (DateTimeParseException e) {
                    secondValid = false;
                }
                if (!secondValid) {
                    DateTimeFormatter timeFormatter2 = DateTimeFormatter.ofPattern("H:mm");
                    try {
                        LocalTime.parse(temp, timeFormatter2);
                        secondValid = true;
                        break;
                    } catch (DateTimeParseException e) {
                        secondValid = false;
                    }
                }
            }

        }

        if (firstValid) {
            firstTime = firstTime.replace(Character.toString(':'), "");
            Scanner firstTimeScanner = new Scanner(firstTime);
            firstTimeScanner.useDelimiter("");
            int firstNum = 0;
            int secondNum = 0;
            int thirdNum = 0;
            int fourthNum = 0;
            if (firstTime.length() == 4)    {
                String stringNum = word.next();
                char charNum = stringNum.charAt(0);
                firstNum = charNum - '0';
                stringNum = word.next();
                charNum = stringNum.charAt(1);
                secondNum = charNum - '0';
                stringNum = word.next();
                charNum = stringNum.charAt(2);
                thirdNum = charNum - '0';
                stringNum = word.next();
                charNum = stringNum.charAt(3);
                fourthNum = charNum - '0';
            }
            else    {
                String stringNum = word.next();
                char charNum = stringNum.charAt(0);
                firstNum = charNum - '0';
                stringNum = word.next();
                charNum = stringNum.charAt(1);
                secondNum = charNum - '0';
                stringNum = word.next();
                charNum = stringNum.charAt(2);
                thirdNum = charNum - '0';
            }
            if (firstNum > 0 && secondNum < 8)  {
                firstNum = firstNum + 12;
            }
            firstFinal = firstNum * 10 * 60 + secondNum * 60 + thirdNum * 10 + fourthNum - referenceTime;
        }

        if (secondValid) {
            secondTime = secondTime.replace(Character.toString(':'), "");
            Scanner secondTimeScanner = new Scanner(secondTime);
            secondTimeScanner.useDelimiter("");
            int firstNum = 0;
            int secondNum = 0;
            int thirdNum = 0;
            int fourthNum = 0;
            if (secondTime.length() == 4)    {
                String stringNum = word.next();
                char charNum = stringNum.charAt(0);
                firstNum = charNum - '0';
                stringNum = word.next();
                charNum = stringNum.charAt(1);
                secondNum = charNum - '0';
                stringNum = word.next();
                charNum = stringNum.charAt(2);
                thirdNum = charNum - '0';
                stringNum = word.next();
                charNum = stringNum.charAt(3);
                fourthNum = charNum - '0';
            }
            else    {
                String stringNum = word.next();
                char charNum = stringNum.charAt(0);
                firstNum = charNum - '0';
                stringNum = word.next();
                charNum = stringNum.charAt(1);
                secondNum = charNum - '0';
                stringNum = word.next();
                charNum = stringNum.charAt(2);
                thirdNum = charNum - '0';
            }
            if (firstNum > 0 && secondNum < 8)  {
                firstNum = firstNum + 12;
            }
            secondFinal = firstNum * 10 * 60 + secondNum * 60 + thirdNum * 10 + fourthNum - referenceTime;
        }
        if (firstFinal <= secondFinal && firstValid)   {
            setTime(firstFinal, secondFinal);
        }
        else if (secondFinal <= firstFinal && secondValid) {
            setTime(secondFinal, firstFinal);
        }
        else if (firstValid && !secondValid)    {
            setTime(firstFinal, -1);
        }
        else {
            setTime(-1, -1);
        }

    }

    public void searchByCourseCode(String query)   {
        for (Course c : initialResult) {
            if (query.contains(c.getCourseCode().toLowerCase()))    {
                filteredResult.add(c);
            }
        }
    }

    public void searchByDepartment(String query)    {
        for (String s : listDep)    {
            if (query.contains(s.toLowerCase()))    {
                setDepartment(s);
            }
        }
        //will add search options with keywords when can get a list of the departments seen

    }

    public void searchByProfessor(String query) {
        for (String s : listProf)   {
            if (query.contains(s.toLowerCase()))    {
                setDesiredProfesor(s);
            }
        }
        //might add more options depending on what the professor name looks like
    }











    public void setDepartments()    {
        for (Course c : initialResult)  {
            String dep = c.getDepartment();
            if (!listDep.contains(dep)) {
                listDep.add(dep);
            }
        }
    }

    public void setProfessors() {
        for (Course c : initialResult)  {
            for (String p : c.getProfessor())   {
                if (!listProf.contains(p))  {
                    listProf.add(p);
                }
            }
        }
    }


    //public ArrayList<String> getListDep()   {
    //    return listDep;
    //}
    //Search s = new Search("testing");
    //ArrayList<String> temp = s.getListDep();
    //    for (String ss : temp)   {
    //    System.out.println(ss);
    //}

}
