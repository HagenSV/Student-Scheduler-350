package edu.gcc;

import java.sql.Array;
import java.time.Instant;
import java.util.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * This class is responsible for the search functionality of the program.
 * It takes a query string and searches through the courses and filters them based on user input.
 */
public class Search {
    private String query; // the search query
    private boolean daysMeeting; // days when the class is meeting - true = MWF, false = TH
    private int minTime; // minimum start time for the class
    private int maxTime; // maximum start time for the class
    private String desiredProfesor; // the desired professor for the class
    private String department; // the department of the class
    private ArrayList<Course> initialResult; // the original list of courses
    private ArrayList<Course> filteredResult; // the list of courses after filtering
    private ArrayList<String> listDep; // list of all departments
    private ArrayList<String> listProf; // list of all professors
    private boolean filterCompletedCourses; // whether to filter completed courses or not
    private String username;
    /**
     * Constructor for the Search class
     * @param query the search query
     */
    public Search(String query, String username, boolean filterCompletedCourses){
        this.query = query.toLowerCase();
        this.query = query.replaceAll("[\\p{Punct}&&[^:]]", "");
        this.initialResult = Main.courses;
        listDep = new ArrayList<>();
        listProf = new ArrayList<>();
        setDepartments();
        setProfessors();
        filteredResult = new ArrayList<>();
        this.filterCompletedCourses = filterCompletedCourses;
        this.username = username;
    }

    /**
     * Sets the days when the class is meeting
     * @param daysMeeting true if the class meets on MWF, false if it meets on TR
     * @return the filtered list of courses
     */
    public ArrayList<Course> setDaysMeeting(boolean daysMeeting){
        this.daysMeeting = daysMeeting;
        return filterByDaysMeeting();
    }

    /**
     * Sets the start time for the filter
     * @param newMinTime the minimum start time for filter
     * @param newMaxTime the maximum start time for filter
     * @return the filtered list of courses
     */
    public ArrayList<Course> setTime(int newMinTime, int newMaxTime){
        this.minTime = newMinTime;
        this.maxTime = newMaxTime;
        return filterByTime();
    }

    /**
     * Sets the desired professor for the filter
     * @param desiredProfesor the desired professor for the filter
     * @return the filtered list of courses
     */
    public ArrayList<Course> setDesiredProfesor(String desiredProfesor){
        this.desiredProfesor = desiredProfesor;
        return filterByDesiredProfessor();
    }

    /**
     * Sets the department for the filter
     * @param department the department for the filter
     * @return the filtered list of courses
     */
    public ArrayList<Course> setDepartment(String department){
        this.department = department;
        return filterByDepartment();
    }

    /**
     * Filters the list of courses based on the days when the class is meeting
     * @return the filtered list of courses
     */
    public ArrayList<Course> filterByDaysMeeting(){
        for (Course c : initialResult) {
            if (daysMeeting) {
                if (!c.getMWForTR()) {
                    filteredResult.remove(c);
                }
            }
            else {
                if (c.getMWForTR()) {
                        filteredResult.remove(c);
                    }
            }
        }
        return filteredResult;
    }

    /**
     * Filters the list of courses based on the minimum and maximum times
     * If only the minimum time is set, it will filter for that time only
     * If both are set it will filter for the range between the two times plus the duration of the course
     * If neither are set it will return the original list
     * @return the filtered list of courses
     */
    public ArrayList<Course> filterByTime(){
        boolean withinTime;
        if (minTime != -1 && maxTime != -1) {
            for (Course c : initialResult) {
                withinTime = true;
                for (int t : c.getStartTime()) {
                    if (!(t >= minTime && t  + c.getDuration() <= maxTime) && t != -1) {
                        withinTime = false;
                        break;
                    }
                }
                if (!withinTime) {
                    filteredResult.remove(c);
                }
            }
        }
        else if (minTime != -1) {
            for (Course c : initialResult) {
                withinTime = false;
                for (int t : c.getStartTime()) {
                    if (t == minTime) {
                        withinTime = true;
                        break;
                    }
                }
                if (!withinTime) {
                    filteredResult.remove(c);
                }
            }
        }
        return filteredResult;
    }

    /**
     * Filters the list of courses based on the desired professor
     * @return the filtered list of courses
     */
    public ArrayList<Course> filterByDesiredProfessor(){
        for (Course c : initialResult) {
            for (String p : c.getProfessor())   {
                Scanner namescan = new Scanner(p);
                namescan.useDelimiter(",");
                if (namescan.hasNext()) {
                    p = namescan.next().toLowerCase();
                }
                if (!(desiredProfesor.equals(p) && !p.equals("")))  {
                    filteredResult.remove(c);
                    break;
                }
            }
        }
        return filteredResult;
    }

    /**
     * Filters the list of courses based on the department
     * @return the filtered list of courses
     */
    public ArrayList<Course> filterByDepartment(){
        for (Course c : initialResult) {
            if (c.getDepartment().toLowerCase().equals(department.toLowerCase()) && !filteredResult.contains(c))    {
                filteredResult.add(c);
            }
        }
        return filteredResult;
    }

    /**
     * Returns the filtered list of courses
     * @return the filtered list of courses
     */
    public ArrayList<Course> getResult(){
        if(filterCompletedCourses)
            filterOutCompletedCourses();
        return filteredResult;

    }

    public void filterOutCompletedCourses(){
        ArrayList<Course> completedCourses = SearchDatabase.getInstance().getCompletedCoursesFromDB(username);
        for(Course completed : completedCourses){
            for(Course course : filteredResult){
                if(completed.getDepartment().equals(course.getDepartment()) && completed.getCourseCode().equals(course.getCourseCode())){
                    filteredResult.remove(course);
                    break;
                }
            }
        }
    }

    /**
     * Searches for courses based on the query
     */
    public void search()    {
        searchByDepartment(query);
        if (filteredResult.size() == 0) {
            fillFilteredResult();
        }
        searchByCourseCode(query);
        if (filteredResult.size() == 0) {
            fillFilteredResult();
        }
        searchByProfessor(query);
        if (filteredResult.size() == 0) {
            fillFilteredResult();
        }
        searchByDaysMeeting(query);
        if (filteredResult.size() == 0) {
            fillFilteredResult();
        }
        searchByTime(query);
        if (filteredResult.size() == 0) {
            fillFilteredResult();
        }
        if (filteredResult.size() == initialResult.size())  {
            filteredResult = new ArrayList<>();
        }
    }

    /**
     * Searches for courses based on the days when the query
     * @param query the search query
     */
    public void searchByDaysMeeting(String query)   {
        boolean MWF = false;
        boolean TR = false;
        String[] words = query.toLowerCase().split(" ");
        for (String s : words)  {
            if (s.equals("mon") || s.equals("wed") || s.equals("fri") || s.equals("monday") || s.equals("wednesday") || s.equals("friday") || s.equals("mwf"))  {
                MWF = true;
            }
        }
        for (String s : words)  {
            if (s.equals("tues") || s.equals("thurs") || s.equals("tuesday") || s.equals("thursday") || s.equals("tr"))  {
                TR = true;
            }
        }
        if (MWF && !TR)  {
            setDaysMeeting(true);
        }
        else if (TR && !MWF) {
            setDaysMeeting(false);
        }
    }

    /**
     * Filters courses by time based on the query
     * @param query the search query
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
                    firstTime = temp;
                    firstValid = true;
                } catch (DateTimeParseException e) {
                    firstValid = false;
                }
                if (!firstValid) {
                    DateTimeFormatter timeFormatter2 = DateTimeFormatter.ofPattern("H:mm");
                    try {
                        LocalTime.parse(temp, timeFormatter2);
                        firstTime = temp;
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
                    secondTime = temp;
                    secondValid = true;
                } catch (DateTimeParseException e) {
                    secondTime = temp;
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
                String stringNum = firstTimeScanner.next();
                char charNum = stringNum.charAt(0);
                firstNum = charNum - '0';
                stringNum = firstTimeScanner.next();
                charNum = stringNum.charAt(0);
                secondNum = charNum - '0';
                stringNum = firstTimeScanner.next();
                charNum = stringNum.charAt(0);
                thirdNum = charNum - '0';
                stringNum = firstTimeScanner.next();
                charNum = stringNum.charAt(0);
                fourthNum = charNum - '0';
            }
            else    {
                String stringNum = firstTimeScanner.next();
                char charNum = stringNum.charAt(0);
                secondNum = charNum - '0';
                stringNum = firstTimeScanner.next();
                charNum = stringNum.charAt(0);
                thirdNum = charNum - '0';
                stringNum = firstTimeScanner.next();
                charNum = stringNum.charAt(0);
                fourthNum = charNum - '0';
            }
            if (secondNum < 8 && firstTime.length() == 3)  {
                secondNum = secondNum + 12;
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
                String stringNum = secondTimeScanner.next();
                char charNum = stringNum.charAt(0);
                firstNum = charNum - '0';
                stringNum = secondTimeScanner.next();
                charNum = stringNum.charAt(0);
                secondNum = charNum - '0';
                stringNum = secondTimeScanner.next();
                charNum = stringNum.charAt(0);
                thirdNum = charNum - '0';
                stringNum = secondTimeScanner.next();
                charNum = stringNum.charAt(0);
                fourthNum = charNum - '0';
            }
            else    {
                String stringNum = secondTimeScanner.next();
                char charNum = stringNum.charAt(0);
                secondNum = charNum - '0';
                stringNum = secondTimeScanner.next();
                charNum = stringNum.charAt(0);
                thirdNum = charNum - '0';
                stringNum = secondTimeScanner.next();
                charNum = stringNum.charAt(0);
                fourthNum = charNum - '0';
            }
            if (secondNum < 8 && secondTime.length() == 3)  {
                secondNum = secondNum + 12;
            }
            secondFinal = firstNum * 10 * 60 + secondNum * 60 + thirdNum * 10 + fourthNum - referenceTime;
        }
        if (firstFinal <= secondFinal && firstValid && secondValid)   {
            setTime(firstFinal, secondFinal);
        }
        else if (secondFinal <= firstFinal && firstValid && secondValid) {
            setTime(secondFinal, firstFinal);
        }
        else if (firstValid && !secondValid)    {
            setTime(firstFinal, -1);
        }
        else {
            setTime(-1, -1);
        }

    }

    /**
     * Filters courses by course code based on the query
     * @param query the search query
     */
    public void searchByCourseCode(String query) {
        ArrayList<Course> temp = new ArrayList<>();
        String toUseQuery = query;
        for (Course c : initialResult) {
            String code = c.getDepartment() + " " + c.getCourseCode();
            if (query.toLowerCase().contains(code.toLowerCase()) || query.toLowerCase().contains(c.getName().toLowerCase()) && !temp.contains(c)) {
                temp.add(c);
                if (toUseQuery.contains(code.toLowerCase())) {
                    toUseQuery = toUseQuery.replace(code.toLowerCase(), "");
                }
                if (toUseQuery.contains(c.getName().toLowerCase())) {
                    toUseQuery = toUseQuery.replace(c.getName().toLowerCase(), "");
                }
            }
        }
            Map<Integer, ArrayList<Course>> keywordMap = new TreeMap<>();
            for (Course c : initialResult) {
                for (int i = toUseQuery.length(); i > 0; i--) {
                    for (int j = 0; j < i; j++) {
                        String s = toUseQuery.substring(j, i);
                        if (c.isKeyword(s) && s.length() > 3 && (j == 0 || toUseQuery.charAt(j - 1) == ' ')) {
                            if (!keywordMap.containsKey(s.length())) {
                                keywordMap.put(s.length(), new ArrayList<>());
                            }
                            keywordMap.get(s.length()).add(c);
                        }
                    }
                }
            }
            int largestKey = 0;
            for (int i : keywordMap.keySet()) {
                largestKey = i;
            }
        if (largestKey > 5 || initialResult.size() == filteredResult.size()) {
            for (int i = largestKey; i > 0; i--) {
                if (keywordMap.containsKey(i)) {
                    for (Course c : keywordMap.get(i)) {
                        if (!temp.contains(c)) {
                            temp.add(c);
                        }
                    }
                }
            }
        }

        if (temp.size() > 0) {
            filteredResult = temp;
        }
    }

    /**
     * Filters courses by department based on the query
     * @param query the search query
     */
    public void searchByDepartment(String query)    {
        for (String s : listDep)    {
            for (String d : query.split(" ")) {
                if (s.toLowerCase().equals(d.toLowerCase()))    {
                    setDepartment(s);
                }

            }
        }
    }

    /**
     * Filters courses by professor based on the query
     * @param query the search query
     */
    public void searchByProfessor(String query) {
        for (String s : listProf)   {
            for (String d : query.split(" ")) {
                if (s.toLowerCase().equals(d.toLowerCase()) && !s.equals(""))    {
                    setDesiredProfesor(s);
                }
            }
        }
    }

    /**
     * Makes a list of the all the departments
     */
    public void setDepartments()    {
        for (Course c : initialResult)  {
            String dep = c.getDepartment().toLowerCase();
            if (!listDep.contains(dep)) {
                listDep.add(dep);
            }
        }
    }

    /**
     * Makes a list of all the professors
     */
    public void setProfessors() {
        for (Course c : initialResult)  {
            for (String p : c.getProfessor())   {
                Scanner namescan = new Scanner(p);
                namescan.useDelimiter(",");
                if (namescan.hasNext()) {
                    p = namescan.next().toLowerCase();
                }
                if (!listProf.contains(p))  {
                    listProf.add(p);
                }
            }
        }
    }

    /**
     * Fills the filtered result with the initial result
     */
    public void fillFilteredResult() {
        filteredResult = new ArrayList<>(initialResult);
    }

}
