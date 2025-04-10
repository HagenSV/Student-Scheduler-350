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
        schedule = new Schedule();
    }

    @Test
    void exportToPDF() {
        // Add a few courses to the schedule
        Course course1 = Main.courses.get(82);
        Course course2 = Main.courses.get(84);
        Course course3 = Main.courses.get(86);

        // Add courses and ensure they are added successfully
        assertTrue(schedule.addCourse(course1), "Failed to add first course to schedule");
        assertTrue(schedule.addCourse(course2), "Failed to add second course to schedule");
        assertTrue(schedule.addCourse(course3), "Failed to add second course to schedule");

        schedule.exportToPDF();
        // Check if the PDF file is created
        File pdfFile = new File("ScheduleSpring2025.pdf");
        assertTrue(pdfFile.exists(), "PDF file should be created");
    }

    @Test
    void exportToPDFGeneratedSchedule() {
        ArrayList<Schedule> generatedSchedules = schedule.generateSchedule(new String[]{"COMP 340", "COMP 314", "COMP 445", "COMP 350", "COMP 435"}, Main.getCourses("data_wolfe.json"), "Spring");
        assertFalse(generatedSchedules.isEmpty(), "There should be at least one valid schedule");
        schedule = generatedSchedules.get(0);
        ArrayList<String> courseNames = new ArrayList<>();
        for (Course course : schedule.getCourses()) {
            courseNames.add(course.getDepartment() + " " + course.getCourseCode());
        }

        // Export the generated schedule to PDF
        schedule.exportToPDF();
        // Check if the PDF file is created
        File pdfFile = new File("ScheduleSpring2025.pdf");
        assertTrue(pdfFile.exists(), "PDF file should be created");
    }
}