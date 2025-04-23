package edu.gcc.exception;

import edu.gcc.Course;

public class ScheduleConflictException extends Exception {

    public ScheduleConflictException(Course course) {
        super("Unable to add" + course.getName() + " as it conflicts with other events on the schedule.");
    }
}
