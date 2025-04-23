package edu.gcc.exception;

import edu.gcc.Course;

public class CourseFullException extends Exception {

    public CourseFullException(Course course) {
        super("Unable to add "+course.getName() +", course is full.");
    }
}
