package edu.gcc;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import edu.gcc.exception.CourseFullException;
import edu.gcc.exception.ScheduleConflictException;
import edu.gcc.exception.SemesterMismatchException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.*;
import java.awt.Desktop;
import java.security.GeneralSecurityException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class Schedule {
    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private ArrayList<Course> courses;
    private ArrayList<ScheduleEvent> nonAcademicEvents;
    private String username;
    private String semester;

    public Schedule(String username, String semester) {
        this.username = username;
        this.semester = semester.toLowerCase();
        courses = getCoursesFromDB();
        nonAcademicEvents = new ArrayList<>();
    }

    public Schedule(String username, String semester, ArrayList<Course> courses, ArrayList<ScheduleEvent> nonAcademicEvents) {
        this.courses = courses;
        this.nonAcademicEvents = nonAcademicEvents;
        this.username = username;
        this.semester = semester;
    }

    /**
     * Gets the courses from the database
     *
     * @return arraylist of courses
     */
    public ArrayList<Course> getCoursesFromDB() {
        SearchDatabase sd = SearchDatabase.getInstance();
        return sd.getScheduleFromDB(username, semester);
    }

    /**
     * Adds a specified scheduleEvent to the schedule and updates the database and calls the logger function
     *
     * @param scheduleEvent the scheduleEvent to be added
     * @return whether the course was added successfully false if it conflicts with the other courses in the schedule
     */
    public boolean addCourse(ScheduleEvent scheduleEvent) throws CourseFullException, ScheduleConflictException, SemesterMismatchException {

        // Course or Event conflicts with the current schedule
        if (!this.getConflicts(scheduleEvent).isEmpty()) {
            throw new ScheduleConflictException(scheduleEvent);
        }

        // The ScheduleEvent is a Course object
        if (scheduleEvent instanceof Course course) {
            if (course.getNumSeats() < 1) {
                    throw new CourseFullException(course);
            }
            // Check if the course is in the same semester
            if (this.courses.contains(course)) {
                System.out.println("You are trying to add a course that is already in your schedule");
                throw new ScheduleConflictException(course);
            }
            // Check if the course is in the same semester
            if (!course.getSemester().equals(semester)) {
                throw new SemesterMismatchException(course);
            }
            this.courses.add(course);
            UpdateDatabaseContents addCourseToSchedule = new UpdateDatabaseContents();
            addCourseToSchedule.addCourseToSchedule(course.getCID(), username, semester);
        } else {
        // The ScheduleEvent is a ScheduleEvent object
            if (this.nonAcademicEvents.contains(scheduleEvent)) {
                System.out.println("You are trying to add an event that is already in your schedule");
                return false;
            }
            this.nonAcademicEvents.add(scheduleEvent);
        }
        // Log the addition of the course or event
        logger(true, scheduleEvent);
        return true;
    }

    /**
     * Adds a specified scheduleEvent to the schedule and calls the logger function
     *
     * @param scheduleEvent the scheduleEvent to be added
     * @return whether the course was added successfully false if it conflicts with the other courses in the schedule
     */
    public boolean addCourseNoDatabase(ScheduleEvent scheduleEvent) throws CourseFullException, ScheduleConflictException, SemesterMismatchException {

        // Course or Event conflicts with the current schedule
        if (!this.getConflicts(scheduleEvent).isEmpty()) {
            throw new ScheduleConflictException(scheduleEvent);
        }

        // The ScheduleEvent is a Course object
        if (scheduleEvent instanceof Course course) {
            if (course.getNumSeats() < 1) {
                throw new CourseFullException(course);
            }
            // Check if the course is in the same semester
            if (this.courses.contains(course)) {
                System.out.println("You are trying to add a course that is already in your schedule");
                throw new ScheduleConflictException(course);
            }
            // Check if the course is in the same semester
            if (!course.getSemester().equalsIgnoreCase(semester)) {
                throw new SemesterMismatchException(course);
            }
            this.courses.add(course);
        } else {
            // The ScheduleEvent is a ScheduleEvent object
            if (this.nonAcademicEvents.contains(scheduleEvent)) {
                System.out.println("You are trying to add an event that is already in your schedule");
                return false;
            }
            this.nonAcademicEvents.add(scheduleEvent);
        }
        // Log the addition of the course or event
        logger(true, scheduleEvent);
        return true;
    }

    /**
     * Removes the specified scheduleEvent from the schedule and updates the database and adds it to the logger
     *
     * @param scheduleEvent the scheduleEvent to be removed
     * @return whether the course was successfully removed, false if it did not exist in the schedule
     */
    public boolean removeCourse(ScheduleEvent scheduleEvent) {

        // ScheduleEvent is a Course object
        if (scheduleEvent instanceof Course course) {
            UpdateDatabaseContents udb = new UpdateDatabaseContents();
            if (udb.removeCourse(course, username, semester)) {
                courses.remove(course);
                logger(false, course);
                return true;
            }
            return false;
        } else {
            // ScheduleEvent is a ScheduleEvent object
            if (nonAcademicEvents.remove(scheduleEvent)) {
                // Need to change this section to support database change
                logger(false, scheduleEvent);
                return true;
            }
            return false;
        }
    }

    /**
     * Removes the specified scheduleEvent from the schedule and adds it to the logger
     *
     * @param scheduleEvent the scheduleEvent to be removed
     * @return whether the course was successfully removed, false if it did not exist in the schedule
     */
    public boolean removeCourseNoDatabase(ScheduleEvent scheduleEvent) {

        // ScheduleEvent is a Course object
        if (scheduleEvent instanceof Course course) {
            if (courses.remove(course)) {
                logger(false, course);
                return true;
            }
            return false;
        } else {
            // ScheduleEvent is a ScheduleEvent object
            if (nonAcademicEvents.remove(scheduleEvent)) {
                logger(false, scheduleEvent);
                return true;
            }
            return false;
        }
    }

    /**
     * Generates a schedule based on the search queries and found courses
     *
     * @param searchQueries array of search queries
     * @param foundCourses arraylist of courses that were found
     * @param semester semester to filter the courses by
     * @return arraylist of generated schedules
     */
    public ArrayList<Schedule> generateSchedule(String[] searchQueries, ArrayList<Course> foundCourses, String semester) {
        if (foundCourses == null)
            return null;

        ArrayList<Course> deepCopyCourses = new ArrayList<>(foundCourses);
        deepCopyCourses.removeIf(c -> !c.getSemester().equals(semester));
        ArrayList<Schedule> generatedSchedules = new ArrayList<>();
        ArrayList<ArrayList<Course>> domains = new ArrayList<>();

        Map<String, ArrayList<Course>> courseMap = new HashMap<>();
        for (String query : searchQueries) {
            String[] queries = query.split(" ");
            for (Course c : deepCopyCourses) {
                if (c.getDepartment().equals(queries[0])
                        && c.getCourseCode().equals(queries[1])) {
                    if (courseMap.containsKey(query))
                        courseMap.get(query).add(c);
                    else {
                        ArrayList<Course> newEntry = new ArrayList<>();
                        newEntry.add(c);
                        courseMap.put(query, newEntry);
                    }
                }
            }
        }

        // Print out courses that don't exist in the query
        for (String s: searchQueries) {
            boolean exists = false;
            for (String str : courseMap.keySet()) {
                if (s.equals(str)) {
                    exists = true;
                    break;
                }
            }
            if (!exists)
                System.err.println("Could not find course: " + s);
        }

        for (String s : courseMap.keySet())
            domains.add(courseMap.get(s));

        // Call backtracking search, if domains not found, courses are null
        ArrayList<Course> emptyCourses = new ArrayList<>();
        Schedule schedule = new Schedule(username, semester, emptyCourses, nonAcademicEvents);
        backtrack(generatedSchedules, schedule, domains);
        return generatedSchedules;
    }

    /**
     * Backtracking search with MRV heuristic and forward checking
     *
     * @param schedule schedule that is being generated
     * @param domains domain of each Course variable
     * */
    public void backtrack(ArrayList<Schedule> generatedSchedules, Schedule schedule, ArrayList<ArrayList<Course>> domains) {
        // Base case, all variables assigned
        if (domains.isEmpty()) {
            generatedSchedules.add(schedule);
            return;
        }

        // MRV Heuristic choose from ArrayList with the smallest size
        domains.sort(Comparator.comparing(ArrayList::size));
        ArrayList<Course> currentDomain = domains.get(0);
        for (Course c : currentDomain) {
            try {
                if (schedule.addCourseNoDatabase(c)) {
                    // Create copy of domains
                    ArrayList<ArrayList<Course>> domainCopy = new ArrayList<>();
                    for (int i = 1; i < domains.size(); i++) {
                        domainCopy.add(new ArrayList<>(domains.get(i)));
                    }

                    // Forward Checking remove conflicts from domains
                    for (ArrayList<Course> variable : domainCopy) {
                        for (Course course : variable) {
                            if (course.hasConflict(c))
                                variable.remove(c);
                        }
                    }

                    // Check if any domains are empty
                    boolean valid = true;
                    for (ArrayList<Course> variable : domainCopy) {
                        if (variable.isEmpty()) {
                            valid = false;
                            break;
                        }
                    }

                    // Domain isn't valid remove from schedule and go to next value
                    if (!valid) {
                        schedule.removeCourseNoDatabase(c);

                        // Domain is valid, call backtrack again with deepCopy of forward checked domains
                    } else {
                        backtrack(generatedSchedules, new Schedule(username, semester, new ArrayList<>(schedule.getCourses()), nonAcademicEvents), domainCopy);
                    }
                    schedule.removeCourseNoDatabase(c);
                }
            } catch (Exception ignored){}
        }
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }

    public ArrayList<ScheduleEvent> getNonAcademicEvents() {
        return nonAcademicEvents;
    }

    public void setNonAcademicEvents(ArrayList<ScheduleEvent> nonAcademicEvents) {
        this.nonAcademicEvents = nonAcademicEvents;
    }

    public String getUsername() {
        return username;
    }

    public String getSemester() {
        return semester;
    }

    /**
     * Finds conflicting classes and returns an arraylist of courses that conflict
     *
     * @return arraylist of courses that conflict
     */
    public ArrayList<ScheduleEvent> getConflicts(ScheduleEvent course) {
        ArrayList<ScheduleEvent> conflicts = new ArrayList<>();

        for (Course c : courses) {
            if (c.hasConflict(course)) {
                conflicts.add(c);
            }
        }

        for (ScheduleEvent c: nonAcademicEvents) {
            if (c.hasConflict(course)) {
                conflicts.add(c);
            }
        }
        return conflicts;
    }

    /**
     * Logs the addition or removal of a course to a log file
     *
     * @param type true if added, false if removed
     * @param event the course or event that was added or removed
     */
    public void logger(boolean type, ScheduleEvent event) {
        String action = type ? "Added" : "Removed";
        String log;
        if (event instanceof Course) {
            Course course = (Course) event;
            log = action + " " + course.getDepartment() + " " + course.getCourseCode() + " " + course.getSection() + "\n";
        } else {
            log = action + " Non-Academic Event: " + event.getName() + " (CID: " + event.getCID() + ")\n";
        }
        try {
            FileWriter writer = new FileWriter("log.txt", true);
            writer.write(log);
            writer.close();
        } catch (Exception e) {
            System.out.println("An error occurred while writing to the log file: " + e.getMessage());
        }
    }
}