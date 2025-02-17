package edu.gcc;

public class Schedule {
    private Course[] courses;

    public Schedule() {

    }

    public Schedule(String searchQueries) {

    }

    public boolean addCourse(Course course) {
        return false;
    }

    public boolean removeCourse(Course course) {
        return false;
    }

    public void generateSchedule(String[] searchQueries) {    }

    public Course[] getCourses() {
        return courses;
    }

    public Course[] getConflicts() {
        return null;
    }
}
