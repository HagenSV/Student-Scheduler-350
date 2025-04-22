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

        // Create a sample course for the schedule (MWF at 8:02 AM)
        ArrayList<String> professors = new ArrayList<>();
        professors.add("Dr. Hutchins");
        boolean[] daysMeet = {true, false, true, false, true}; // MWF
        int[] startTimes = {2, -1, 2, -1, 2}; // 8:02 AM on MWF, -1 on TR
        sampleCourse = new Course(0, "Intro to Programming", startTimes, 50, true,
                professors, true, daysMeet, "COMP", "141", 3, 30, "A", false, "Spring", "Room 101");

        ArrayList<Course> emptyCourses = new ArrayList<>();
        ArrayList<ScheduleEvent> emptyEvents = new ArrayList<>();
        schedule = new Schedule("testUser", "Spring", emptyCourses, emptyEvents);
        schedule.addCourseNoDatabase(sampleCourse);

        user = new User("testUser", "password123", majors, minors, completedCourses);
    }

    @Test
    public void testConstructor() {
        assertEquals("testUser", user.getName());
        assertEquals("Computer Science", user.getMajors().get(0));
        assertEquals("Mathematics", user.getMinors().get(0));
        assertNotNull(user.getSchedule()); // Schedule is initialized
        assertTrue(user.getSchedule().getCourses().isEmpty()); // But should be empty
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
        System.out.println("Initial schedule size: " + schedule.getCourses().size());
        assertEquals(1, schedule.getCourses().size(), "Schedule should start with 1 course");

        System.out.println("User's initial schedule size: " + user.getSchedule().getCourses().size());

        user.updateSchedule(schedule);

        System.out.println("User's schedule size after update: " + user.getSchedule().getCourses().size());

        assertEquals(schedule, user.getSchedule(), "Schedule objects should be the same");
        assertEquals(1, user.getSchedule().getCourses().size(), "Schedule should have 1 course after update");
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
        File scheduleFile = new File(user.getName() + ".txt");

        try (FileWriter writer = new FileWriter(scheduleFile)) {
            user.saveSchedule();
        }

        String content = Files.readString(scheduleFile.toPath());
        assertTrue(content.contains("Intro to Programming"));
        assertTrue(content.contains("Dr. Hutchins"));
        assertTrue(content.contains("COMP_141"));
        assertTrue(content.contains("2,-1,2,-1,2")); // Check all 5 start times
        assertTrue(content.contains("TFTFT")); // Check daysMeet
    }

    @Test
    public void testLoadSchedule() throws IOException {
        File scheduleFile = new File(user.getName() + ".txt");
        try (FileWriter writer = new FileWriter(scheduleFile)) {
            writer.write("0_Intro to Programming_2,-1,2,-1,2_true_50_Dr. Hutchins_true_TFTFT_COMP_141_3_30_A_false");
        }

        user.loadSchedule();

        Schedule loadedSchedule = user.getSchedule();
        assertNotNull(loadedSchedule);
        assertEquals(1, loadedSchedule.getCourses().size());

        Course loadedCourse = loadedSchedule.getCourses().get(0);
        assertEquals("Intro to Programming", loadedCourse.getName());
        assertEquals("Dr. Hutchins", loadedCourse.getProfessor().get(0));
        assertEquals("COMP", loadedCourse.getDepartment());
        assertEquals("141", loadedCourse.getCourseCode());
        assertArrayEquals(new int[]{2, -1, 2, -1, 2}, loadedCourse.getStartTime(), "Start times should match");
        assertArrayEquals(new boolean[]{true, false, true, false, true}, loadedCourse.getDaysMeet(), "Days should match");
    }

    @Test
    public void testLoadScheduleNoFile() {
        File scheduleFile = new File(user.getName() + ".txt");
        if (scheduleFile.exists()) scheduleFile.delete();

        user.loadSchedule();
        assertNotNull(user.getSchedule()); // Schedule still exists
        assertTrue(user.getSchedule().getCourses().isEmpty()); // But should be empty
    }
}