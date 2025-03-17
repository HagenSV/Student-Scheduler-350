package edu.gcc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Used GrokAI to help generate Tests
 */
class ScheduleTest {

    private Schedule schedule;
    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        schedule = new Schedule();

        ArrayList<String> professors1 = new ArrayList<>();
        professors1.add("Dr. Hutchins");
        boolean[] daysMeet1 = {true, false, true, false, true}; // MWF
        int[] startTimes1 = {0, -1, 0, -1, 0}; // 8:00 AM on MWF, -1 on TR
        course1 = new Course(0, "Intro to Programming", startTimes1, 50, true,
                professors1, true, daysMeet1, "COMP", "141", 3, 30, "A", false);

        ArrayList<String> professors2 = new ArrayList<>();
        professors2.add("Dr. Johnson");
        boolean[] daysMeet2 = {false, true, false, true, false}; // TR
        int[] startTimes2 = {-1, 60, -1, 60, -1}; // 9:00 AM on TR, -1 on MWF
        course2 = new Course(1, "Programming II", startTimes2, 50, true,
                professors2, false, daysMeet2, "COMP", "220", 3, 25, "B", false);
    }

    @Test
    void addCourse() {
        boolean result = schedule.addCourse(course1);
        assertTrue(result, "Course should be added successfully");
        assertEquals(1, schedule.getCourses().size(), "Schedule should contain 1 course");
        assertTrue(schedule.getCourses().contains(course1), "Schedule should contain course1");

        Course conflictingCourse = new Course(2, "Conflict Course",
                new int[]{0, -1, 0, -1, 0}, 50, true, // 8:00-8:50 AM on MWF
                new ArrayList<>(), true, new boolean[]{true, false, true, false, true},
                "CS", "102", 3, 20, "C", false);
        result = schedule.addCourse(conflictingCourse);
        assertFalse(result, "Conflicting course should not be added");
        assertEquals(1, schedule.getCourses().size(), "Schedule should still contain only 1 course");
        assertFalse(schedule.getCourses().contains(conflictingCourse), "Conflicting course should not be in schedule");
    }

    @Test
    void removeCourse() {
        schedule.addCourse(course1);
        boolean result = schedule.removeCourse(course1);
        assertTrue(result, "Course should be removed successfully");
        assertTrue(schedule.getCourses().isEmpty(), "Schedule should be empty after removal");

        result = schedule.removeCourse(course2);
        assertFalse(result, "Removing non-existent course should return false");
        assertTrue(schedule.getCourses().isEmpty(), "Schedule should remain empty");
    }

    @Test
    void getCourses() {
        ArrayList<Course> courses = schedule.getCourses();
        assertNotNull(courses, "getCourses should never return null");
        assertTrue(courses.isEmpty(), "New schedule should have no courses");

        schedule.addCourse(course1);
        schedule.addCourse(course2);
        courses = schedule.getCourses();
        assertEquals(2, courses.size(), "Schedule should contain 2 courses");
        assertTrue(courses.contains(course1), "Should contain course1");
        assertTrue(courses.contains(course2), "Should contain course2");
    }

    @Test
    void getConflicts() {
        schedule.addCourse(course1); // 8:00-8:50 AM, MWF
        schedule.addCourse(course2); // 9:00-9:50 AM, TR
        ArrayList<Course> conflicts = schedule.getConflicts(course1);
        assertTrue(conflicts.isEmpty(), "No conflicts between MWF 8:00-8:50 and TR 9:00-9:50");

        Course conflictingCourse = new Course(3, "Conflict Course",
                new int[]{0, -1, 0, -1, 0}, 50, true, // 8:00-8:50 AM on MWF
                new ArrayList<>(), true, new boolean[]{true, false, true, false, true},
                "CS", "102", 3, 20, "C", false);
        schedule.getCourses().add(conflictingCourse); // Force add
        conflicts = schedule.getConflicts(course1);
        assertEquals(1, conflicts.size(), "Should find 1 conflict (8:00-8:50 AM MWF vs same)");
        assertTrue(conflicts.contains(conflictingCourse), "Conflicts should include conflictingCourse");

        Schedule emptySchedule = new Schedule();
        conflicts = emptySchedule.getConflicts(course1);
        assertTrue(conflicts.isEmpty(), "Empty schedule should have no conflicts");
    }

    @Test
    void testLogger() throws IOException {
        // Add course and check log
        schedule.addCourse(course1);
        String logContent = readLogFile();
        assertTrue(logContent.contains("Added COMP 141"), "Log should contain 'Added COMP 141'");

        // Remove course and check log
        schedule.removeCourse(course1);
        logContent = readLogFile();
        assertTrue(logContent.contains("Removed COMP 141"), "Log should contain 'Removed COMP 141'");
    }

    private String readLogFile() throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("log.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    @Test
    void undo() {
        schedule.addCourse(course1);
        schedule.addCourse(course2);
        assertEquals(2, schedule.getCourses().size(), "Schedule should contain 2 courses before undo");

        boolean result = schedule.undo();
        assertTrue(result, "Undo should be successful");
        assertEquals(1, schedule.getCourses().size(), "Schedule should contain 1 course after undo");
        assertTrue(schedule.getCourses().contains(course1), "Schedule should still contain course1");
        assertFalse(schedule.getCourses().contains(course2), "Schedule should not contain course2 after undo");

        result = schedule.undo();
        assertTrue(result, "Undo should be successful");
        assertTrue(schedule.getCourses().isEmpty(), "Schedule should be empty after second undo");

        result = schedule.undo();
        assertFalse(result, "Undo on empty schedule should return false");
    }
}