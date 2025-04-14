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
        user.setEmail("eclipsegames20@gmail.com");
    }

    @Test
    void testSendEmailWithSchedule() throws IOException, GeneralSecurityException, jakarta.mail.MessagingException {
        Schedule schedule = new Schedule();
        ArrayList<Schedule> generatedSchedules = schedule.generateSchedule(new String[]{"COMP 340"}, Main.getCourses("data_wolfe.json"), "Spring");
        assertFalse(generatedSchedules.isEmpty(), "There should be at least one valid schedule");
        schedule = generatedSchedules.get(0);
        user.updateSchedule(schedule);

        user.sendEmail();

        System.out.println("Email sent to eclipsegames20@gmail.com with COMP 340 schedule. Please check the inbox.");
    }

    @Test
    void testSendEmailWithBigSchedule() throws IOException, GeneralSecurityException, jakarta.mail.MessagingException {
        Schedule schedule = new Schedule();
        ArrayList<Schedule> generatedSchedules = schedule.generateSchedule(new String[]{"COMP 340", "COMP 350", "COMP 445", "COMP 314"}, Main.getCourses("data_wolfe.json"), "Spring");
        assertFalse(generatedSchedules.isEmpty(), "There should be at least one valid schedule");
        schedule = generatedSchedules.get(0);
        user.updateSchedule(schedule);

        user.sendEmail();

        System.out.println("Email sent to eclipsegames20@gmail.com with multiple courses (COMP 340, COMP 350, COMP 445, COMP 314) schedule. Please check the inbox.");
    }

    @Test
    void testSendEmailWithEmptySchedule() throws IOException, GeneralSecurityException, jakarta.mail.MessagingException {
        Schedule schedule = new Schedule();
        user.updateSchedule(schedule);

        user.sendEmail();

        System.out.println("Email sent to eclipsegames20@gmail.com with empty schedule. Please check the inbox.");
    }
}