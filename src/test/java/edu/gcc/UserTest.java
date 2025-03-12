package edu.gcc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Used GrokAI to help generate tests
 */
public class UserTest {

    private User user;
    private Schedule schedule;
    private Course sampleCourse;

    @BeforeEach
    public void setUp() {
        ArrayList<String> majors = new ArrayList<>();
        majors.add("Computer Science");
        ArrayList<String> minors = new ArrayList<>();
        minors.add("Mathematics");
        ArrayList<Course> completedCourses = new ArrayList<>();

        // Create a sample course for the schedule
        ArrayList<String> professors = new ArrayList<>();
        professors.add("Dr. Hutchins");
        boolean[] daysMeet = {true, false, true, false, true};
        sampleCourse = new Course("Intro to Programming", new int[]{2}, 50, true,
                professors, true, daysMeet, "COMP", "141", 3, 30, "A", false);

        schedule = new Schedule();
        schedule.addCourse(sampleCourse);

        user = new User("testUser", "password123", majors, minors, completedCourses);
    }

    @Test
    public void testConstructor() {
        assertEquals("testUser", user.getName());
        assertEquals("Computer Science", user.getMajors().get(0));
        assertEquals("Mathematics", user.getMinors().get(0));
        assertTrue(user.getSchedule() == null); // Initially null
    }

    @Test
    public void testAddMajorSuccess() {
        boolean result = user.addMajor("Physics", 2023);
        assertTrue(result);
        assertEquals(2, user.getMajors().size());
        assertTrue(user.getMajors().contains("Physics"));
    }

    @Test
    public void testAddMajorDuplicate() {
        boolean result = user.addMajor("Computer Science", 2023);
        assertFalse(result);
        assertEquals(1, user.getMajors().size());
    }

    @Test
    public void testRemoveMajorSuccess() {
        boolean result = user.removeMajor("Computer Science");
        assertTrue(result);
        assertTrue(user.getMajors().isEmpty());
    }

    @Test
    public void testRemoveMajorNotExist() {
        boolean result = user.removeMajor("Physics");
        assertFalse(result);
        assertEquals(1, user.getMajors().size());
    }

    @Test
    public void testAddMinorSuccess() {
        boolean result = user.addMinor("Physics", 2023);
        assertTrue(result);
        assertEquals(2, user.getMinors().size());
        assertTrue(user.getMinors().contains("Physics"));
    }

    @Test
    public void testAddMinorDuplicate() {
        boolean result = user.addMinor("Mathematics", 2023);
        assertFalse(result);
        assertEquals(1, user.getMinors().size());
    }

    @Test
    public void testRemoveMinorSuccess() {
        boolean result = user.removeMinor("Mathematics");
        assertTrue(result);
        assertTrue(user.getMinors().isEmpty());
    }

    @Test
    public void testRemoveMinorNotExist() {
        boolean result = user.removeMinor("Physics");
        assertFalse(result);
        assertEquals(1, user.getMinors().size());
    }

    @Test
    public void testUpdateSchedule() {
        user.updateSchedule(schedule);
        assertEquals(schedule, user.getSchedule());
        assertEquals(1, user.getSchedule().getCourses().size());
    }

    @Test
    public void testChangePassword() {
        user.changePassword("newPassword");
        assertEquals(true, user.passwordAttempt("newPassword"));
    }

    @Test
    public void testChangeUserName() {
        user.changeUserName("newUser");
        assertEquals("newUser", user.getName());
    }

    @Test
    public void testSaveSchedule() throws IOException {
        user.updateSchedule(schedule);
        File scheduleFile = new File("schedule.txt");

        // Redirect file output to temp directory by mocking the file path
        try (FileWriter writer = new FileWriter(scheduleFile)) {
            user.saveSchedule(); // This writes to "schedule.txt" in current dir, but we'll test content
        }

        // Read the file and verify content
        String content = Files.readString(scheduleFile.toPath());
        assertTrue(content.contains("Intro to Programming"));
        assertTrue(content.contains("Dr. Hutchins"));
        assertTrue(content.contains("COMP_141"));
    }

    @Test
    public void testLoadSchedule() throws IOException {
        // Prepare a sample schedule.txt file in temp directory
        File scheduleFile = new File("schedule.txt");
        try (FileWriter writer = new FileWriter(scheduleFile)) {
            writer.write("Intro to Programming_1,2_true_50_Dr. Hutchins_true_TFTFT_COMP_141_3_30_A_false");
        }

        // Load the schedule
        user.loadSchedule();

        // Verify the loaded schedule
        Schedule loadedSchedule = user.getSchedule();
        assertNotNull(loadedSchedule);
        assertEquals(1, loadedSchedule.getCourses().size());

        Course loadedCourse = loadedSchedule.getCourses().get(0);
        assertEquals("Intro to Programming", loadedCourse.getName());
        assertEquals("Dr. Hutchins", loadedCourse.getProfessor().get(0));
        assertEquals("COMP", loadedCourse.getDepartment());
        assertEquals("141", loadedCourse.getCourseCode());
    }

    @Test
    public void testLoadScheduleNoFile() {
        // Ensure no file exists
        File scheduleFile = new File("schedule.txt");
        if (scheduleFile.exists()) scheduleFile.delete();

        user.loadSchedule();
        assertNull(user.getSchedule()); // Should remain null if no file exists
    }
}