package edu.gcc;

import java.util.ArrayList;
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
        ArrayList<ArrayList<Integer>> courseDomains = new ArrayList<>();
        ArrayList<Course> foundCourses = Main.courses;

        // Generate a 2d arraylist of all the searched courses start times
        for (String courseCode: searchQueries) {
            // TODO get courses by course code
            foundCourses.add(null);
            // TODO add ArrayList of course start times to get course
            courseDomains.add(null);
        }

        // Call backtracking search, if domains not found, courses are null
        courseDomains = backtrack(courseDomains, 0);
        if (courseDomains == null) {
            this.courses = null;
            return;
        }

        // Domain found in backtracking search return schedule with the start times found
        for (int i = 0; i < foundCourses.size(); i++) {
            // Todo add courses by their code and start time to generatedSchedule
        }
        this.courses = generatedSchedule;
    }

    public ArrayList<ArrayList<Integer>> backtrack(ArrayList<ArrayList<Integer>> courseDomains, int nextVarToAssign) {
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
