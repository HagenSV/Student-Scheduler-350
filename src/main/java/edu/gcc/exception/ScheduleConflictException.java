package edu.gcc.exception;

import edu.gcc.ScheduleEvent;

public class ScheduleConflictException extends Exception {

    public ScheduleConflictException(ScheduleEvent event) {
        super("Unable to add " + event.getName() + " as it conflicts with other events on the schedule.");
    }
}
