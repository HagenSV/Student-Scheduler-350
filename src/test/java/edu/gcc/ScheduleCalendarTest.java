package edu.gcc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleCalendarTest {

    private Schedule schedule;

    @BeforeEach
    void setUp() {
        // Populate Main.courses with data from the JSON file
        Main.courses = Main.getCourses("data_wolfe.json");
        if (Main.courses == null || Main.courses.isEmpty()) {
            fail("Failed to load courses from data_wolfe.json. Ensure the file exists and is valid.");
        }

        // Initialize schedule
        schedule = new Schedule();
    }

    @Test
    void testAddCoursesAndExportToGoogleCalendar() {
        ArrayList<Schedule> generatedSchedules = schedule.generateSchedule(new String[]{"COMP 340"}, Main.getCourses("data_wolfe.json"), "Spring");
        assertFalse(generatedSchedules.isEmpty(), "There should be at least one valid schedule");
        schedule = generatedSchedules.get(0);

        // Export to Google Calendar - success if no exceptions are thrown
        schedule.exportToCalendar();

        // If we reach here without exceptions, the test passes
        System.out.println("Courses added and exported to Google Calendar. Please check your Google Calendar manually.");
    }

    @Test
    void testAddCoursesAndExportToGoogleCalendarNonAcademic() {
        ArrayList<Schedule> generatedSchedules = schedule.generateSchedule(new String[]{"COMP 340"}, Main.getCourses("data_wolfe.json"), "Spring");
        assertFalse(generatedSchedules.isEmpty(), "There should be at least one valid schedule");
        schedule = generatedSchedules.get(0);
        // Add non-academic event (Monday 10:00-11:00 AM)
        boolean[] daysMeetEvent = {false, true, false, true, false}; // Monday
        int[] startTimesEvent = {-1, 360, -1, 360, -1}; // 10:00 AM
        ScheduleEvent event = new ScheduleEvent(100, "Club Meeting", startTimesEvent, 120,
                daysMeetEvent, "Spring", "Room 201");
        schedule.addCourse(event);

        // Export to Google Calendar - success if no exceptions are thrown
        schedule.exportToCalendar();

        // If we reach here without exceptions, the test passes
        System.out.println("Courses added and exported to Google Calendar. Please check your Google Calendar manually.");
    }
}