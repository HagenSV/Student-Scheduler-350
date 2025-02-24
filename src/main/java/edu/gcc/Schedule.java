package edu.gcc;

import java.util.ArrayList;

public class Schedule {
    private ArrayList<Course> courses;

    public Schedule() {
        courses = new ArrayList<>();
    }

    public Schedule(ArrayList<Course> courses) {
        this.courses = courses;
    }

    public Schedule(String searchQueries) {

    }

    public boolean addCourse(Course course) {
        courses.add(course);
        if (!this.getConflicts().isEmpty()) {
            courses.remove(course);
            return false;
        }
        return true;
    }

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
    public ArrayList<Course> getConflicts() {
        ArrayList<Course> conflicts = new ArrayList<>();
        for (Course c : courses) {
            for (Course other: courses)
                if (!c.getName().equals(other.getName()) && c.hasConflict(other)) {
                    conflicts.add(c);
                    break;
                }
        }
        return conflicts;
    }
}
