package edu.gcc;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
    private Map<String, ArrayList<String>> departmentKeywords;

    public Search(String query){
        this.query = query.toLowerCase();
        this.initialResult = Main.courses;
        listDep = new ArrayList<>();
        listProf = new ArrayList<>();
        departmentKeywords = new HashMap<>();
        addToDepartmentKeywords();
        setDepartments();
        setProfessors();
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
        for (Course c : initialResult) {
            if (daysMeeting) {
                if (!(c.getDaysMeet()[0] && c.getDaysMeet()[2] && c.getDaysMeet()[4])) {
                    filteredResult.remove(c);
                }
            }
            else {
                if (!(c.getDaysMeet()[1] && c.getDaysMeet()[3])) {
                        filteredResult.remove(c);
                    }
            }
        }
        return filteredResult;
    }

    public ArrayList<Course> filterByTime(){
        boolean notwithinTime = true;
        if (minTime != -1 && maxTime != -1) {
            for (Course c : initialResult) {
                notwithinTime = true;
                for (int t : c.getStartTime()) {
                    if (t >= minTime && t  + c.getDuration() <= maxTime) {
                        notwithinTime = false;
                        break;
                    }
                }
                if (notwithinTime) {
                    filteredResult.remove(c);
                }
            }
        }
        else if (minTime != -1) {
            for (Course c : initialResult) {
                notwithinTime = true;
                for (int t : c.getStartTime()) {
                    if (t == minTime) {
                        notwithinTime = false;
                        break;
                    }
                }
                if (notwithinTime) {
                    filteredResult.remove(c);
                }
            }
        }
        return filteredResult;
    }

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

    public ArrayList<Course> filterByDepartment(){
        for (Course c : initialResult) {
            if (c.getDepartment().toLowerCase().equals(department))    {
                filteredResult.add(c);
            }
        }
        return filteredResult;
    }

    public ArrayList<Course> getResult(){
        return filteredResult;
    }

    public void search()    {
        searchByDepartment(query);
        if (filteredResult.size() == 0) {
            filteredResult = new ArrayList<>(initialResult);
        }
        searchByProfessor(query);
        if (filteredResult.size() == 0) {
            filteredResult = new ArrayList<>(initialResult);
        }
        searchByCourseCode(query);
        if (filteredResult.size() == 0) {
            filteredResult = new ArrayList<>(initialResult);
        }
        searchByDaysMeeting(query);
        if (filteredResult.size() == 0) {
            filteredResult = new ArrayList<>(initialResult);
        }
        searchByTime(query);
        if (filteredResult.size() == 0) {
            filteredResult = new ArrayList<>(initialResult);
        }
        if (filteredResult.size() == initialResult.size())  {
            filteredResult = new ArrayList<>();
        }
    }

    public void searchByDaysMeeting(String query)   {
        boolean MWF = false;
        boolean TR = false;
        String[] words = query.split(" ");
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

    public void searchByCourseCode(String query)   {
        ArrayList<Course> temp = new ArrayList<>();
        for (Course c : filteredResult) {
            String code = c.getDepartment() + " " + c.getCourseCode();
            if (query.contains(code.toLowerCase()))    {
                temp.add(c);
            }
        }
        if (temp.size() > 0)    {
            filteredResult = temp;
        }
    }

    public void searchByDepartment(String query)    {
        for (String s : listDep)    {
            if (query.contains(s.toLowerCase()))    {
                setDepartment(s);
            }
        }
        for (String a : departmentKeywords.keySet())    {
            for (String ss : departmentKeywords.get(a))  {
                if (query.contains(ss)) {
                    setDepartment(a);
                }
            }
        }

    }

    public void searchByProfessor(String query) {
        for (String s : listProf)   {
            if (query.contains(s.toLowerCase()))    {
                setDesiredProfesor(s);
                break;
            }
        }
    }

    public void setDepartments()    {
        for (Course c : initialResult)  {
            String dep = c.getDepartment().toLowerCase();
            if (!listDep.contains(dep)) {
                listDep.add(dep);
            }
        }
    }

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

    public void addToDepartmentKeywords()   {
        departmentKeywords.put("ACCT", new ArrayList<>(Arrays.asList("accounting", "financial accounting", "managerial accounting", "bookkeeping")));
        departmentKeywords.put("ART", new ArrayList<>(Arrays.asList("art", "fine arts", "visual arts", "art history", "studio art", "painting", "sculpture")));
        departmentKeywords.put("ASTR", new ArrayList<>(Arrays.asList("astronomy", "space science", "astrophysics", "observational astronomy", "stellar physics")));
        departmentKeywords.put("BIOL", new ArrayList<>(Arrays.asList("biology", "life sciences", "cellular biology", "genetics", "ecology", "anatomy", "zoology")));
        departmentKeywords.put("CHEM", new ArrayList<>(Arrays.asList("chemistry", "organic chemistry", "inorganic chemistry", "biochemistry", "chemical reactions", "lab work")));
        departmentKeywords.put("CMIN", new ArrayList<>(Arrays.asList("christian ministry", "ministry", "pastoral care", "theology", "religious studies", "evangelism")));
        departmentKeywords.put("COMM", new ArrayList<>(Arrays.asList("communication", "mass communication", "public relations", "journalism", "media studies", "digital communication")));
        departmentKeywords.put("COMP", new ArrayList<>(Arrays.asList("computer science", "programming", "software development", "algorithms", "computer programming", "data structures")));
        departmentKeywords.put("DESI", new ArrayList<>(Arrays.asList("design", "graphic design", "visual design", "industrial design", "ux design", "fashion design")));
        departmentKeywords.put("ECON", new ArrayList<>(Arrays.asList("economics", "microeconomics", "macroeconomics", "economic theory", "market analysis", "econometrics")));
        departmentKeywords.put("EDUC", new ArrayList<>(Arrays.asList("education", "teaching", "pedagogy", "elementary education", "secondary education", "education psychology")));
        departmentKeywords.put("ELEE", new ArrayList<>(Arrays.asList("electrical engineering", "circuit design", "electronics", "electrical systems", "signals and systems", "power engineering")));
        departmentKeywords.put("ENGL", new ArrayList<>(Arrays.asList("english", "literature", "writing", "composition", "english language", "creative writing", "literary analysis")));
        departmentKeywords.put("ENGR", new ArrayList<>(Arrays.asList("engineering", "mechanical engineering", "electrical engineering", "civil engineering", "engineering design", "engineering math")));
        departmentKeywords.put("ENTR", new ArrayList<>(Arrays.asList("entrepreneurship", "startups", "business innovation", "small business", "venture capital", "business development")));
        departmentKeywords.put("EXER", new ArrayList<>(Arrays.asList("exercise science", "kinesiology", "physical fitness", "athletic training", "sports science", "health and fitness")));
        departmentKeywords.put("FNCE", new ArrayList<>(Arrays.asList("finance", "corporate finance", "investment", "financial planning", "banking", "accounting", "financial markets")));
        departmentKeywords.put("FREN", new ArrayList<>(Arrays.asList("french", "french language", "french literature", "french culture", "francophone studies", "french translation")));
        departmentKeywords.put("GREK", new ArrayList<>(Arrays.asList("greek", "ancient greek", "koine greek", "biblical greek", "classical greek", "greek language", "greek literature")));
        departmentKeywords.put("HEBR", new ArrayList<>(Arrays.asList("hebrew", "biblical hebrew", "modern hebrew", "hebrew language", "jewish studies")));
        departmentKeywords.put("HIST", new ArrayList<>(Arrays.asList("history", "world history", "american history", "european history", "history of civilizations", "historical research")));
        departmentKeywords.put("HUMA", new ArrayList<>(Arrays.asList("humanities", "history", "philosophy", "literature", "arts", "social sciences", "cultural studies")));
        departmentKeywords.put("INBS", new ArrayList<>(Arrays.asList("international business", "global business", "international trade", "business strategy", "international economics")));
        departmentKeywords.put("MARK", new ArrayList<>(Arrays.asList("marketing", "marketing strategy", "digital marketing", "brand management", "consumer behavior", "market research")));
        departmentKeywords.put("MATH", new ArrayList<>(Arrays.asList("mathematics", "algebra", "calculus", "statistics", "geometry", "linear algebra", "mathematical analysis")));
        departmentKeywords.put("MECE", new ArrayList<>(Arrays.asList("mechanical engineering", "thermodynamics", "fluid mechanics", "mechanical systems", "heat transfer", "engineering design")));
        departmentKeywords.put("MNGT", new ArrayList<>(Arrays.asList("management", "business management", "organizational behavior", "leadership", "project management", "strategic management")));
        departmentKeywords.put("MUSE", new ArrayList<>(Arrays.asList("music education", "music theory", "music pedagogy", "instrumental music", "vocal music", "music teaching")));
        departmentKeywords.put("MUSI", new ArrayList<>(Arrays.asList("music", "music theory", "music history", "performance", "classical music", "music composition", "music technology")));
        departmentKeywords.put("NURS", new ArrayList<>(Arrays.asList("nursing", "registered nursing", "nursing care", "nursing practice", "healthcare", "clinical nursing", "patient care")));
        departmentKeywords.put("PHIL", new ArrayList<>(Arrays.asList("philosophy", "ethics", "metaphysics", "logic", "epistemology", "philosophical theory", "critical thinking")));
        departmentKeywords.put("PHYS", new ArrayList<>(Arrays.asList("physics", "mechanics", "thermodynamics", "electromagnetism", "quantum physics", "optics", "physics lab")));
        departmentKeywords.put("POLS", new ArrayList<>(Arrays.asList("political science", "political theory", "american politics", "international relations", "government", "public policy")));
        departmentKeywords.put("PSYC", new ArrayList<>(Arrays.asList("psychology", "clinical psychology", "cognitive psychology", "behavioral psychology", "social psychology", "psychological research")));
        departmentKeywords.put("PUBH", new ArrayList<>(Arrays.asList("public health", "health policy", "epidemiology", "global health", "healthcare systems", "public health administration")));
        departmentKeywords.put("RELI", new ArrayList<>(Arrays.asList("religion", "theology", "religious studies", "biblical studies", "comparative religion", "christian theology")));
        departmentKeywords.put("ROBO", new ArrayList<>(Arrays.asList("robotics", "automation", "artificial intelligence", "robotics engineering", "mechatronics", "robotics design")));
        departmentKeywords.put("SCIC", new ArrayList<>(Arrays.asList("science", "interdisciplinary science", "environmental science", "natural science", "science research")));
        departmentKeywords.put("SEDU", new ArrayList<>(Arrays.asList("special education", "special needs", "learning disabilities", "educational support", "inclusive education")));
        departmentKeywords.put("SOCI", new ArrayList<>(Arrays.asList("sociology", "social theory", "social problems", "sociology of families", "criminology", "social research")));
        departmentKeywords.put("SOCW", new ArrayList<>(Arrays.asList("social work", "social services", "social welfare", "clinical social work", "community outreach", "social policy")));
        departmentKeywords.put("SPAN", new ArrayList<>(Arrays.asList("spanish", "spanish language", "spanish literature", "spanish culture", "hispanic studies", "latin american studies")));
        departmentKeywords.put("STAT", new ArrayList<>(Arrays.asList("statistics", "data analysis", "probability", "statistical methods", "quantitative research", "applied statistics")));
        departmentKeywords.put("THEA", new ArrayList<>(Arrays.asList("theatre", "acting", "dramatic arts", "theatre production", "playwriting", "stagecraft", "theatre history")));
        departmentKeywords.put("WRIT", new ArrayList<>(Arrays.asList("writing", "creative writing", "academic writing", "composition", "writing for publication", "writing skills")));
        departmentKeywords.put("DSCI", new ArrayList<>(Arrays.asList("data science", "data analytics", "machine learning", "big data", "data visualization", "statistical analysis")));
        departmentKeywords.put("PHYE", new ArrayList<>(Arrays.asList("physical education", "sports education", "fitness", "athletics", "physical activity", "pe teaching")));
        departmentKeywords.put("SSFT", new ArrayList<>(Arrays.asList("social science foundations", "intro to social sciences", "sociology", "psychology", "anthropology")));
        departmentKeywords.put("ZLOAD", new ArrayList<>(Arrays.asList("special course load", "administrative course designation", "faculty load", "special topic course")));
        departmentKeywords.put("GEOL", new ArrayList<>(Arrays.asList("geology", "earth science", "geological mapping", "mineralogy", "geophysics", "environmental geology")));
        departmentKeywords.put("GOBL", new ArrayList<>(Arrays.asList("global studies", "global business", "international relations", "global economy", "global politics")));
    }

}
