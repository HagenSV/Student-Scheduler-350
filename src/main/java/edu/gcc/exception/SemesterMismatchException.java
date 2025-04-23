package edu.gcc.exception;

import edu.gcc.Course;

public class SemesterMismatchException extends Exception {

    public SemesterMismatchException(Course course) {
        super(course.getName() + " is not available in the selected schedule's semester.");
    }
}
