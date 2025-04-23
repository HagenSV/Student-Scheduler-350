package edu.gcc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
        ArrayList<Course> emptyCourses = new ArrayList<>();
        ArrayList<ScheduleEvent> emptyEvents = new ArrayList<>();
        schedule = new Schedule("Caleb", "Spring", emptyCourses, emptyEvents);

        ArrayList<String> professors1 = new ArrayList<>();
        professors1.add("Dr. Hutchins");
        boolean[] daysMeet1 = {true, false, true, false, true}; // MWF
        int[] startTimes1 = {0, -1, 0, -1, 0}; // 8:00 AM on MWF, -1 on TR
        course1 = new Course(0, "Intro to Programming", startTimes1, 50, true,
                professors1, true, daysMeet1, "COMP", "141", 3, 30, "A", false, "Spring", "Room 101");

        ArrayList<String> professors2 = new ArrayList<>();
        professors2.add("Dr. Johnson");
        boolean[] daysMeet2 = {false, true, false, true, false}; // TR
        int[] startTimes2 = {-1, 60, -1, 60, -1}; // 9:00 AM on TR, -1 on MWF
        course2 = new Course(1, "Programming II", startTimes2, 50, true,
                professors2, false, daysMeet2, "COMP", "220", 3, 25, "B", false, "Spring", "Room 102");
    }

    @Test
    void addCourseNoDatabase() {
        schedule.getCourses().clear();
        schedule.getNonAcademicEvents().clear();

        boolean result = schedule.addCourseNoDatabase(course1);
        assertTrue(result, "Course should be added successfully");
        assertEquals(1, schedule.getCourses().size(), "Schedule should contain 1 course");
        assertTrue(schedule.getCourses().contains(course1), "Schedule should contain course1");

        Course conflictingCourse = new Course(2, "Conflict Course",
                new int[]{0, -1, 0, -1, 0}, 60, true,
                new ArrayList<>(), true, new boolean[]{true, false, true, false, true},
                "CS", "102", 3, 20, "C", false, "Spring 2025", "Room 103");

        result = schedule.addCourseNoDatabase(conflictingCourse);
        assertFalse(result, "Conflicting course should not be added");
        assertEquals(1, schedule.getCourses().size(), "Schedule should still contain only 1 course");
        assertTrue(schedule.getCourses().contains(course1), "Schedule should still contain course1");

        assertFalse(schedule.getCourses().contains(conflictingCourse), "Conflicting course should not be in schedule");
    }

    @Test
    void removeCourseNoDatabase() {
        schedule.addCourseNoDatabase(course1);
        boolean result = schedule.removeCourseNoDatabase(course1);
        assertTrue(result, "Course should be removed successfully");
        assertTrue(schedule.getCourses().isEmpty(), "Schedule should be empty after removal");

        result = schedule.removeCourseNoDatabase(course2);
        assertFalse(result, "Removing non-existent course should return false");
        assertTrue(schedule.getCourses().isEmpty(), "Schedule should remain empty");
    }

    @Test
    void getCourses() {
        ArrayList<Course> courses = schedule.getCourses();
        assertNotNull(courses, "getCourses should never return null");
        assertTrue(courses.isEmpty(), "New schedule should have no courses");

        schedule.addCourseNoDatabase(course1);
        schedule.addCourseNoDatabase(course2);
        courses = schedule.getCourses();
        assertEquals(2, courses.size(), "Schedule should contain 2 courses");
        assertTrue(courses.contains(course1), "Should contain course1");
        assertTrue(courses.contains(course2), "Should contain course2");
    }

    @Test
    void getConflicts() {
        schedule.addCourseNoDatabase(course1); // 8:00-8:50 AM, MWF
        ArrayList<ScheduleEvent> conflicts = schedule.getConflicts(course2);
        assertTrue(conflicts.isEmpty(), "No conflicts between MWF 8:00-8:50 and TR 9:00-9:50");
        schedule.addCourseNoDatabase(course2); // 9:00-9:50 AM, TR

        Course conflictingCourse = new Course(3, "Conflict Course",
                new int[]{0, -1, 0, -1, 0}, 50, true, // 8:00-8:50 AM on MWF
                new ArrayList<>(), true, new boolean[]{true, false, true, false, true},
                "CS", "102", 3, 20, "C", false, "Spring", "Room 103");
        conflicts = schedule.getConflicts(conflictingCourse);
        assertEquals(1, conflicts.size(), "Should find 1 conflict (8:00-8:50 AM MWF vs same)");
        assertFalse(schedule.addCourseNoDatabase(conflictingCourse), "Conflicting course should not be added");
        assertTrue(conflicts.contains(course1), "Conflicts should include course1 MWF 8:00-8:50");

        ArrayList<Course> emptyCourses = new ArrayList<>();
        ArrayList<ScheduleEvent> emptyEvents = new ArrayList<>();
        Schedule emptySchedule = new Schedule("Caleb", "Spring", emptyCourses, emptyEvents);
        conflicts = emptySchedule.getConflicts(course1);
        assertTrue(conflicts.isEmpty(), "Empty schedule should have no conflicts");
    }

    @Test
    void testLogger() throws IOException {
        // Add course and check log
        schedule.addCourseNoDatabase(course1);
        String logContent = readLogFile();
        assertTrue(logContent.contains("Added COMP 141"), "Log should contain 'Added COMP 141'");

        // Remove course and check log
        schedule.removeCourseNoDatabase(course1);
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
    void generateSchedule() {
        ArrayList<Schedule> generatedSchedules = schedule.generateSchedule(new String[]{"COMP 141", "COMP 220", "HUMA 200", "MATH 214"}, Main.getCourses("data_wolfe.json"), "Spring");
        assertFalse(generatedSchedules.isEmpty(), "There should be at least one valid schedule");
        ArrayList<Course> foundSchedule = generatedSchedules.get(0).getCourses();
        ArrayList<String> courseNames = new ArrayList<>();
        for (Course course : foundSchedule) {
            courseNames.add(course.getDepartment() + " " + course.getCourseCode());
        }

        // Debugging the Generated Schedules
        for (int i = 0; i < generatedSchedules.size(); i++) {
            System.out.println("\n\nSchedule " + (i + 1) + ":");
            for (Course c: generatedSchedules.get(i).getCourses()) {
                System.out.println(c);
            }
        }

        assertTrue(courseNames.contains("COMP 141"), "Schedule should contain COMP 141");
        assertTrue(courseNames.contains("COMP 220"), "Schedule should contain COMP 220");
        assertTrue(courseNames.contains("HUMA 200"), "Schedule should contain HUMA 200");
        assertTrue(courseNames.contains("MATH 214"), "Schedule should contain MATH 214");
        assertFalse(courseNames.contains("COMP 101"), "Schedule should not contain COMP 101");
    }

    @Test
    void generateImpossibleSchedule() {
        schedule.addCourseNoDatabase(course1);
        schedule.addCourseNoDatabase(course2);
        ArrayList<Schedule> generatedSchedules = schedule.generateSchedule(new String[]{"WRIT 481", "THEA 384"}, Main.getCourses("data_wolfe.json"), "Spring");
        assertEquals(0, generatedSchedules.size());
        assertFalse(schedule.getCourses().isEmpty(), "Schedule should not be empty after failed generation");
        assertEquals(2, schedule.getCourses().size(), "Schedule should still contain 2 courses after failed generation");
        assertTrue(schedule.getCourses().contains(course1), "Schedule should still contain course1");
        assertTrue(schedule.getCourses().contains(course2), "Schedule should still contain course2");
    }

    @Test
    void addNonAcademicEventAndCheckConflicts() throws IOException {
        schedule.getCourses().clear();
        schedule.getNonAcademicEvents().clear();

        // Add course1 (MWF 8:00-8:50 AM) to set up the schedule
        ArrayList<ScheduleEvent> conflicts = schedule.getConflicts(course1);
        assertTrue(conflicts.isEmpty(), "Course1 should have no conflicts initially");
        boolean result = schedule.addCourseNoDatabase(course1);
        assertTrue(result, "Course1 should be added successfully");
        assertEquals(1, schedule.getCourses().size(), "Schedule should contain 1 course");
        assertTrue(schedule.getCourses().contains(course1), "Schedule should contain course1");

        // Scenario 1: Add non-conflicting non-academic event (Monday 10:00-11:00 AM)
        boolean[] daysMeetEvent1 = {true, false, false, false, false}; // Monday
        int[] startTimesEvent1 = {120, -1, -1, -1, -1}; // 10:00 AM (120 minutes from 8:00 AM)
        ScheduleEvent event1 = new ScheduleEvent(100, "Club Meeting", startTimesEvent1, 60,
                daysMeetEvent1, "Spring", "Room 201");

        conflicts = schedule.getConflicts(event1);
        assertTrue(conflicts.isEmpty(), "Event1 should have no conflicts with course1");
        result = schedule.addCourseNoDatabase(event1);
        assertTrue(result, "Non-academic event should be added successfully");
        assertEquals(1, schedule.getNonAcademicEvents().size(), "Schedule should contain 1 non-academic event");
        assertTrue(schedule.getNonAcademicEvents().contains(event1), "Schedule should contain event1");

        // Check logger
        String logContent = readLogFile();
        assertTrue(logContent.contains("Added Non-Academic Event: Club Meeting (CID: 100)"),
                "Log should contain 'Added Non-Academic Event: Club Meeting (CID: 100)'");

        // Scenario 2: Attempt to add conflicting non-academic event (Monday 8:00-8:50 AM, overlaps with course1)
        boolean[] daysMeetEvent2 = {true, false, false, false, false}; // Monday
        int[] startTimesEvent2 = {0, -1, -1, -1, -1}; // 8:00 AM
        ScheduleEvent event2 = new ScheduleEvent(101, "Study Group", startTimesEvent2, 50,
                daysMeetEvent2, "Spring", "Room 202");

        conflicts = schedule.getConflicts(event2);
        assertEquals(1, conflicts.size(), "Event2 should have 1 conflict with course1");
        assertTrue(conflicts.contains(course1), "Conflicts should include course1");
        result = schedule.addCourseNoDatabase(event2);
        assertFalse(result, "Conflicting non-academic event should not be added");
        assertEquals(1, schedule.getNonAcademicEvents().size(), "Schedule should still contain 1 non-academic event");
        assertFalse(schedule.getNonAcademicEvents().contains(event2), "Schedule should not contain event2");

        // Scenario 3: Attempt to add non-academic event conflicting with event1 (Monday 10:00-11:00 AM)
        boolean[] daysMeetEvent3 = {true, false, false, false, false}; // Monday
        int[] startTimesEvent3 = {120, -1, -1, -1, -1}; // 10:00 AM
        ScheduleEvent event3 = new ScheduleEvent(102, "Workshop", startTimesEvent3, 60,
                daysMeetEvent3, "Spring", "Room 203");

        conflicts = schedule.getConflicts(event3);
        assertEquals(1, conflicts.size(), "Event3 should have 1 conflict with event1");
        assertTrue(conflicts.contains(event1), "Conflicts should include event1");
        result = schedule.addCourseNoDatabase(event3);
        assertFalse(result, "Non-academic event conflicting with event1 should not be added");
        assertEquals(1, schedule.getNonAcademicEvents().size(), "Schedule should still contain 1 non-academic event");
        assertFalse(schedule.getNonAcademicEvents().contains(event3), "Schedule should not contain event3");

        // Scenario 4: Remove non-academic event
        result = schedule.removeCourseNoDatabase(event1);
        assertTrue(result, "Non-academic event should be removed successfully");
        assertTrue(schedule.getNonAcademicEvents().isEmpty(), "Non-academic events should be empty after removal");

        // Check logger
        logContent = readLogFile();
        assertTrue(logContent.contains("Removed Non-Academic Event: Club Meeting (CID: 100)"),
                "Log should contain 'Removed Non-Academic Event: Club Meeting (CID: 100)'");
    }
}