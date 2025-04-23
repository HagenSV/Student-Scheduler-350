package edu.gcc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SchedulePDFTest {
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        // Populate Main.courses with data from the JSON file
        Main.courses = Main.getCourses("data_wolfe.json");
        if (Main.courses == null || Main.courses.isEmpty()) {
            fail("Failed to load courses from data_wolfe.json. Ensure the file exists and is valid.");
        }

        // Initialize schedule
        ArrayList<Course> emptyCourses = new ArrayList<>();
        ArrayList<ScheduleEvent> emptyEvents = new ArrayList<>();
        schedule = new Schedule("Caleb", "Spring", emptyCourses, emptyEvents);
    }

    @Test
    void exportToPDFGeneratedSchedule() {
        ArrayList<Schedule> generatedSchedules = schedule.generateSchedule(new String[]{"COMP 340", "COMP 314", "COMP 445", "COMP 350", "COMP 435"}, Main.getCourses("data_wolfe.json"), "Spring");
        assertFalse(generatedSchedules.isEmpty(), "There should be at least one valid schedule");
        schedule = generatedSchedules.get(0);

        // Export the generated schedule to PDF
        schedule.exportToPDF("ScheduleTest.pdf", true);
        // Check if the PDF file is created
        File pdfFile = new File("ScheduleTest.pdf");
        assertTrue(pdfFile.exists(), "PDF file should be created");
    }

    @Test
    void exportToPDFGeneratedScheduleWithNonAcademicEvent() {
        // Add non-academic event (Monday 10:00-11:00 AM)
        boolean[] daysMeetEvent = {false, true, false, true, false}; // Monday
        int[] startTimesEvent = {-1, 360, -1, 360, -1}; // 10:00 AM
        ScheduleEvent event = new ScheduleEvent(100, "Club Meeting", startTimesEvent, 120,
                daysMeetEvent, "Spring", "Room 201");
        ArrayList<Schedule> generatedSchedules = schedule.generateSchedule(new String[]{"COMP 340", "COMP 314", "COMP 445", "COMP 350", "COMP 435"}, Main.getCourses("data_wolfe.json"), "Spring");
        assertFalse(generatedSchedules.isEmpty(), "There should be at least one valid schedule");
        schedule = generatedSchedules.get(0);
        try {
            schedule.addCourseNoDatabase(event);
        } catch (Exception ignored){}

        // Export the generated schedule to PDF
        schedule.exportToPDF("ScheduleTest.pdf", true);
        // Check if the PDF file is created
        File pdfFile = new File("ScheduleTest.pdf");
        assertTrue(pdfFile.exists(), "PDF file should be created");
    }
}