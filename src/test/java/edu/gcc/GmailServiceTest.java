package edu.gcc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.google.api.services.gmail.Gmail;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class GmailServiceTest {

    private User user;
    private static Gmail realGmailService;

    @BeforeAll
    static void authenticate() throws IOException, GeneralSecurityException {
        realGmailService = GmailService.getGmailService();
        System.out.println("Authentication completed. Tokens stored in gmail-tokens/");
    }

    @BeforeEach
    void setup() {
        ArrayList<String> majors = new ArrayList<>();
        majors.add("Computer Science");
        ArrayList<String> minors = new ArrayList<>();
        minors.add("Cybersecurity");
        minors.add("Artificial Intelligence");
        ArrayList<Course> completedCourses = new ArrayList<>();
        user = new User("Caleb Van Ee", "password", majors, minors, completedCourses);
    }

    @Test
    void testSendEmailWithSchedule() {
        ArrayList<Course> emptyCourses = new ArrayList<>();
        ArrayList<ScheduleEvent> emptyEvents = new ArrayList<>();
        Schedule schedule = new Schedule(user.getName(), "spring", emptyCourses, emptyEvents);
        ArrayList<Schedule> generatedSchedules = schedule.generateSchedule(new String[]{"COMP 340"}, Main.getCourses("data_wolfe.json"), "spring");
        assertFalse(generatedSchedules.isEmpty(), "There should be at least one valid schedule");
        schedule = generatedSchedules.get(0);
        user.updateSchedule(schedule);

        Export.sendEmail(user, "eclipsegames20@gmail.com");

        System.out.println("Email sent to eclipsegames20@gmail.com with COMP 340 schedule. Please check the inbox.");
    }

    @Test
    void testSendEmailWithBigSchedule() {
        ArrayList<Course> emptyCourses = new ArrayList<>();
        ArrayList<ScheduleEvent> emptyEvents = new ArrayList<>();
        Schedule schedule = new Schedule(user.getName(), "spring", emptyCourses, emptyEvents);
        ArrayList<Schedule> generatedSchedules = schedule.generateSchedule(new String[]{"COMP 340", "COMP 350", "COMP 445", "COMP 314"}, Main.getCourses("data_wolfe.json"), "spring");
        assertFalse(generatedSchedules.isEmpty(), "There should be at least one valid schedule");
        schedule = generatedSchedules.get(0);
        user.updateSchedule(schedule);

        user.sendEmail();

        System.out.println("Email sent to eclipsegames20@gmail.com with multiple courses (COMP 340, COMP 350, COMP 445, COMP 314) schedule. Please check the inbox.");
    }

    @Test
    void testSendEmailWithBigScheduleNonAcademicEvent() {
        ArrayList<Course> emptyCourses = new ArrayList<>();
        ArrayList<ScheduleEvent> emptyEvents = new ArrayList<>();
        Schedule schedule = new Schedule(user.getName(), "spring", emptyCourses, emptyEvents);
        ArrayList<Schedule> generatedSchedules = schedule.generateSchedule(new String[]{"COMP 340", "COMP 350", "COMP 445", "COMP 314"}, Main.getCourses("data_wolfe.json"), "spring");
        assertFalse(generatedSchedules.isEmpty(), "There should be at least one valid schedule");
        schedule = generatedSchedules.get(0);
        // Add non-academic event (Monday 10:00-11:00 AM)
        boolean[] daysMeetEvent = {false, true, false, true, false}; // Monday
        int[] startTimesEvent = {-1, 360, -1, 360, -1}; // 10:00 AM
        ScheduleEvent event = new ScheduleEvent(100, "Club Meeting", startTimesEvent, 120,
                daysMeetEvent, "Spring", "Room 201");
        try {
            schedule.addCourseNoDatabase(event);
        } catch (Exception ignored){}
        user.updateSchedule(schedule);

        Export.sendEmail(user, "eclipsegames20@gmail.com");

        System.out.println("Email sent to eclipsegames20@gmail.com with multiple courses (COMP 340, COMP 350, COMP 445, COMP 314) schedule. Please check the inbox.");
    }

    @Test
    void testSendEmailWithEmptySchedule() {
        ArrayList<Course> emptyCourses = new ArrayList<>();
        ArrayList<ScheduleEvent> emptyEvents = new ArrayList<>();
        Schedule schedule = new Schedule(user.getName(), "spring", emptyCourses, emptyEvents);
        user.updateSchedule(schedule);

        Export.sendEmail(user, "eclipsegames20@gmail.com");

        System.out.println("Email sent to eclipsegames20@gmail.com with empty schedule. Please check the inbox.");
    }
}