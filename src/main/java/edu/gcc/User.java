package edu.gcc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;

public class User {
    private String username;
    private String password;
    private List<String> majors;
    private List<String> minors;
    private int yearJoinedMajor;
    private int yearJoinedMinor;
    private Schedule schedule;
    private List<Course> completedCourses;
    private String email;

    public User(String name, String password, List<String> majors, List<String> minors, List<Course> completedCourses) {
        this.username = name;
        this.password = password;
        this.majors = majors;
        this.minors = minors;
        this.completedCourses = completedCourses;
        this.schedule = null;
    }

    /**
     * Adds a major to the list of User majors
     *
     * @param major       name of the major
     * @param joiningYear the year that the major was added to the User
     * @return whether adding the major was successful, false if already added
     */
    public boolean addMajor(String major, int joiningYear) {
        if (majors.contains(major))
            return false;
        majors.add(major);
        yearJoinedMajor = joiningYear;
        return true;
    }

    /**
     * Removes the specified major from the User
     *
     * @param major the major to remove
     * @return whether removing the major was successful, false it does not exist
     */
    public boolean removeMajor(String major) {
        return majors.remove(major);
    }

    /**
     * Adds a minor to the list of User minors
     *
     * @param minor       name of the minor
     * @param joiningYear the year that the major was added to the User
     * @return whether adding the minor was successful, false if already added
     */
    public boolean addMinor(String minor, int joiningYear) {
        if (minors.contains(minor))
            return false;
        minors.add(minor);
        yearJoinedMinor = joiningYear;
        return true;
    }

    /**
     * Removes the specified minor from the User
     *
     * @param minor the minor to remove
     * @return whether removal was successful, false if does not exist
     */
    public boolean removeMinor(String minor) {
        return minors.remove(minor);
    }

    /**
     * Updates the user schedule to the one provided
     *
     * @param schedule the updated schedule
     */
    public void updateSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void changeUserName(String newUsername) {
        this.username = newUsername;
    }

    public String getName() {
        return username;
    }

    public List<String> getMajors() {
        return majors;
    }

    public List<String> getMinors() {
        return minors;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean passwordAttempt(String password) {
        return this.password.equals(password);
    }

    /**
     * Saves the User's schedule to a text file that can be loaded later
     */
    public void saveSchedule() {
        File file = new File(username + ".txt");
        file.delete();
        try (PrintWriter writer = new PrintWriter(username + ".txt")) {
            for (Course c : schedule.getCourses()) {
                StringBuilder stringBuilder = new StringBuilder();

                // Add CID and Name
                stringBuilder.append(c.getCID()).append("_");
                stringBuilder.append(c.getName()).append("_");

                // Add StartTime array, times separated by ,
                int[] startTime = c.getStartTime();
                for (int i = 0; i < startTime.length; i++) {
                    if (i > 0)
                        stringBuilder.append(",");
                    stringBuilder.append(startTime[i]);
                }
                stringBuilder.append("_");

                // Add MWForTR and duration
                stringBuilder.append(c.getMWForTR()).append("_");
                stringBuilder.append(c.getDuration()).append("_");

                // Add professors, professors separated by ,
                for (int i = 0; i < c.getProfessor().size(); i++) {
                    if (i > 0)
                        stringBuilder.append(",");
                    stringBuilder.append(c.getProfessor().get(i));
                }
                stringBuilder.append("_");

                // Add isOpen
                stringBuilder.append(c.getIsOpen()).append("_");

                // Add daysMeet
                boolean[] daysMeet = c.getDaysMeet();
                for (boolean b : daysMeet) {
                    if (b)
                        stringBuilder.append("T");
                    else
                        stringBuilder.append("F");
                }
                stringBuilder.append("_");

                // Add department, courseCode, credits, numSeats, section and isLab
                stringBuilder.append(c.getDepartment()).append("_");
                stringBuilder.append(c.getCourseCode()).append("_");
                stringBuilder.append(c.getCredits()).append("_");
                stringBuilder.append(c.getNumSeats()).append("_");
                stringBuilder.append(c.getSection()).append("_");
                stringBuilder.append(c.getIsLab()).append("_");
                stringBuilder.append(c.getSemester()).append("_");
                stringBuilder.append(c.getLocation());

                // Finally write course into line
                writer.println(stringBuilder);
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Loads the schedule to the User saved in the schedule.txt file
     */
    public void loadSchedule() {
        File file = new File(username + ".txt");
        if (file.exists()) {
            try {
                ArrayList<Course> courses = new ArrayList<>();
                Scanner byLine = new Scanner(file);

                // Every line adds a new course to courses
                while (byLine.hasNextLine()) {
                    String line = byLine.nextLine();
                    Scanner byEntry = new Scanner(line);
                    byEntry.useDelimiter("_");

                    // Loads the CID and name
                    int cid = Integer.parseInt(byEntry.next());
                    String name = byEntry.next();

                    // Loads the startTime array
                    String timeString = byEntry.next();
                    Scanner parseTime = new Scanner(timeString);
                    parseTime.useDelimiter(",");
                    int[] startTime = new int[5];
                    for (int i = 0; i < 5; i++)
                        startTime[i] = Integer.parseInt(parseTime.next());

                    // Loads MWForTR and duration
                    boolean MWForTR = Boolean.parseBoolean(byEntry.next());
                    int duration = Integer.parseInt(byEntry.next());

                    // Loads the Professors
                    ArrayList<String> professors = new ArrayList<>();
                    String professorString = byEntry.next();
                    Scanner parseProfessors = new Scanner(professorString);
                    parseProfessors.useDelimiter(",");
                    while (parseProfessors.hasNext())
                        professors.add(parseProfessors.next());

                    // Loads isOpen
                    boolean isOpen = Boolean.parseBoolean(byEntry.next());

                    // Loads daysMeet
                    String daysMeetString = byEntry.next();
                    boolean[] daysMeet = new boolean[5];
                    for (int i = 0; i < 5; i++) {
                        if (daysMeetString.charAt(i) == 'T')
                            daysMeet[i] = true;
                        else
                            daysMeet[i] = false;
                    }

                    // Loads department, courseCode, credits, numSeats, section, and isLab
                    String department = byEntry.next();
                    String courseCode = byEntry.next();
                    int credits = Integer.parseInt(byEntry.next());
                    int numSeats = Integer.parseInt(byEntry.next());
                    String section = byEntry.next();
                    boolean isLab = Boolean.parseBoolean(byEntry.next());
                    String semester = byEntry.next();
                    String location = byEntry.next();

                    courses.add(new Course(cid, name, startTime, duration, isOpen, professors, MWForTR, daysMeet, department, courseCode, credits, numSeats, section, isLab, semester, location));
                }
                String semester;
                if (courses.isEmpty())
                    semester = "Fall";
                else
                    semester = courses.get(0).getSemester();
                schedule = new Schedule(username, semester, courses, new ArrayList<>());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Sends an email with the user's schedule to their email address from studentschedulerunemployedcs@gmail.com.
     *
     */
    public void sendEmail() {
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            System.err.println("Invalid or missing user email: " + email);
        }

        // Set up mail session properties
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            // Create MimeMessage
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress("studentschedulerunemployedcs@gmail.com"));
            mimeMessage.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(email));
            mimeMessage.setSubject("Your Course Schedule");

            // Build the email body with the schedule
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("Hello ").append(username).append(",\n\n");
            emailBody.append("Here are you current courses:\n\n");

            if (schedule.getCourses().isEmpty()) {
                emailBody.append("No courses scheduled.\n");
            } else {
                for (Course course : schedule.getCourses()) {
                    emailBody.append(course.getDepartment()).append(" ").append(course.getCourseCode());
                    emailBody.append(": ").append(course.getName()).append("\n");
                }
            }
            if (schedule.getNonAcademicEvents().isEmpty()) {
                emailBody.append("\nNo non-academic events scheduled.\n");
            } else {
                emailBody.append("\nNon-Academic Events:\n");
                for (ScheduleEvent event : schedule.getNonAcademicEvents()) {
                    emailBody.append(event.getName()).append("\n");
                }
            }
            emailBody.append("\nBest regards,\nStudent Scheduler Team");

            // Create multipart message
            Multipart multipart = new MimeMultipart();

            // Text part
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(emailBody.toString());
            multipart.addBodyPart(textPart);

            // PDF attachment part
            String pdfFileName = "EmailPDF.pdf";
            File pdfFile = new File(pdfFileName);
            Export.exportToPDF(pdfFileName, false, schedule);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(pdfFile);
            attachmentPart.setFileName("CourseSchedule.pdf");
            multipart.addBodyPart(attachmentPart);

            // Set multipart content
            mimeMessage.setContent(multipart);

            // Convert MimeMessage to Gmail API Message
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            mimeMessage.writeTo(buffer);
            byte[] rawMessageBytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
            Message message = new Message();
            message.setRaw(encodedEmail);

            // Send the email
            Gmail service = GmailService.getGmailService();
            service.users().messages().send("me", message).execute();
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
        System.out.println("Email sent to " + email);
    }
}