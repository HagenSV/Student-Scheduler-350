package edu.gcc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        // Add a few courses to the schedule
        Course course1 = Main.courses.get(82);
        Course course2 = Main.courses.get(85);

        if (course1 == null || course2 == null) {
            fail("Required test courses (e.g., MATH 101 or PHYS 201) not found in data_wolfe.json. Adjust course codes to match your data.");
        }

        // Add courses and ensure they are added successfully
        assertTrue(schedule.addCourse(course1), "Failed to add first course to schedule");
        assertTrue(schedule.addCourse(course2), "Failed to add second course to schedule");

        // Export to Google Calendar - success if no exceptions are thrown
        schedule.exportToCalendar();

        // If we reach here without exceptions, the test passes
        System.out.println("Courses added and exported to Google Calendar. Please check your Google Calendar manually.");
    }
}