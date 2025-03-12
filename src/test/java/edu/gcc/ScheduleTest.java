package edu.gcc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        // Initialize a fresh Schedule object before each test
        schedule = new Schedule();

        // Create sample courses for testing
        ArrayList<String> professors1 = new ArrayList<>();
        professors1.add("Dr. Hutchins");
        boolean[] daysMeet1 = {true, false, true, false, true};
        course1 = new Course("Intro to Programming", new int[]{0}, 50, true,
                professors1, true, daysMeet1, "COMP", "141", 3, 30, "A", false);

        ArrayList<String> professors2 = new ArrayList<>();
        professors2.add("Dr. Johnson");
        boolean[] daysMeet2 = {false, true, false, true, false};
        course2 = new Course("Programming II", new int[]{60}, 50, true,
                professors2, false, daysMeet2, "COMP", "220", 3, 25, "B", false);
    }

    @Test
    void addCourse() {
        // Test adding a course successfully
        boolean result = schedule.addCourse(course1);
        assertTrue(result, "Course should be added successfully");
        assertEquals(1, schedule.getCourses().size(), "Schedule should contain 1 course");
        assertTrue(schedule.getCourses().contains(course1), "Schedule should contain course1");

        // Test adding a conflicting course (mock conflict)
        Course conflictingCourse = new Course("Conflict Course", new int[]{0}, 50, true,
                new ArrayList<>(), true, new boolean[]{true, false, true, false, true},
                "CS", "102", 3, 20, "C", false) {
            @Override
            public boolean hasConflict(Course course) {
                return true; // Force conflict
            }
        };
        result = schedule.addCourse(conflictingCourse);
        assertFalse(result, "Conflicting course should not be added");
        assertEquals(1, schedule.getCourses().size(), "Schedule should still contain only 1 course");
        assertFalse(schedule.getCourses().contains(conflictingCourse), "Conflicting course should not be in schedule");
    }

    @Test
    void removeCourse() {
        // Add a course first
        schedule.addCourse(course1);

        // Test removing an existing course
        boolean result = schedule.removeCourse(course1);
        assertTrue(result, "Course should be removed successfully");
        assertTrue(schedule.getCourses().isEmpty(), "Schedule should be empty after removal");

        // Test removing a non-existent course
        result = schedule.removeCourse(course2);
        assertFalse(result, "Removing non-existent course should return false");
        assertTrue(schedule.getCourses().isEmpty(), "Schedule should remain empty");
    }

    @Test
    void getCourses() {
        // Test empty schedule
        ArrayList<Course> courses = schedule.getCourses();
        assertNotNull(courses, "getCourses should never return null");
        assertTrue(courses.isEmpty(), "New schedule should have no courses");

        // Add courses and test
        schedule.addCourse(course1);
        schedule.addCourse(course2);
        courses = schedule.getCourses();
        assertEquals(2, courses.size(), "Schedule should contain 2 courses");
        assertTrue(courses.contains(course1), "Should contain course1");
        assertTrue(courses.contains(course2), "Should contain course2");
    }

    @Test
    void getConflicts() {
        // Test with no conflicts (default hasConflict returns false)
        schedule.addCourse(course1);
        schedule.addCourse(course2);
        ArrayList<Course> conflicts = schedule.getConflicts(course1);
        assertTrue(conflicts.isEmpty(), "No conflicts should be found with default hasConflict");

        // Test with a conflicting course
        Course conflictingCourse = new Course("Conflict Course", new int[]{0}, 50, true,
                new ArrayList<>(), true, new boolean[]{true, false, true, false, true},
                "CS", "102", 3, 20, "C", false) {
        };
        schedule.addCourse(conflictingCourse);
        conflicts = schedule.getConflicts(course1);
        assertEquals(1, conflicts.size(), "Should find 1 conflicting course");
        assertTrue(conflicts.contains(conflictingCourse), "Conflicts should include conflictingCourse");

        // Test with no courses
        Schedule emptySchedule = new Schedule();
        conflicts = emptySchedule.getConflicts(course1);
        assertTrue(conflicts.isEmpty(), "Empty schedule should have no conflicts");
    }
}