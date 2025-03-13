package edu.gcc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Schedule {
    private ArrayList<Course> courses;

    public Schedule() {
        courses = new ArrayList<>();
    }

    public Schedule(ArrayList<Course> courses) {
        this.courses = courses;
    }

    /**
     * Constructs a schedule based on a series of searchQueries, all classes separated by whitespace
     * @param searchQueries
     */
    public Schedule(String[] searchQueries) {
        ArrayList<Course> generatedSchedule = new ArrayList<>();
        Map<String, ArrayList<Course>>  domains = new HashMap<>();
        ArrayList<Course> foundCourses = Main.courses;

        // Generate a 2d arraylist of all the searched courses start times
        for (String courseCode: searchQueries) {
            String[] queries = courseCode.split(" ");
            for (Course c: foundCourses) {
                if (c.getDepartment().equals(queries[0]) &&
                        c.getCourseCode().equals(queries[1]))
                            if (domains.get(courseCode) == null) {
                                ArrayList<Course> entry = new ArrayList<>();
                                entry.add(c);
                                domains.put(courseCode, entry);
                            } else
                                domains.get(courseCode).add(c);
            }
            // TODO get courses by course code
            foundCourses.add(null);
            // TODO add ArrayList of course start times to get course
        }

        // Call backtracking search, if domains not found, courses are null
        generatedSchedule = backtrack(domains, 0);
        this.courses = generatedSchedule;
    }

    public ArrayList<Course> backtrack(Map<String, ArrayList<Course>> domains, int nextVarToAssign) {
        return null;
    }

    /**
     * Adds a specified course to the schedule
     * @param course the course to be added
     * @return whether the course was added successfully false if it conflicts with the other courses in the schedule
     */
    public boolean addCourse(Course course) {
        courses.add(course);
        if (!this.getConflicts(course).isEmpty()) {
            courses.remove(course);
            return false;
        }
        return true;
    }

    /**
     * Removes the specified course from the schedule
     * @param course the course to be removed
     * @return whether the course was successfully removed, false if it did not exist in the schedule
     */
    public boolean removeCourse(Course course) {
        return courses.remove(course);
    }

    public void generateSchedule(String[] searchQueries) {
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    /**
     * Finds conflicting classes and returns an arraylist of courses that conflict
     *
     * @return arraylist of courses that conflict
     */
    public ArrayList<Course> getConflicts(Course course) {
        ArrayList<Course> conflicts = new ArrayList<>();
        for (Course c : courses) {
                if (c.hasConflict(course)) {
                    conflicts.add(c);
                }
        }
        return conflicts;
    }
}
