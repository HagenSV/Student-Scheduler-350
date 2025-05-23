package edu.gcc;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.commons.codec.binary.Base64;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Export {
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String REDIRECT_URI = "http://localhost:8080/api/v1/export/oauth2callback";

    /**
     * Exports the schedule to Google Calendar by creating events for each course.
     */
    public static void exportToCalendar(Schedule schedule, String username) {
        try {
            // Build the HTTP transport and Calendar service
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            String APPLICATION_NAME = "Student Scheduler";
            Credential credential = getCredentials(httpTransport, username, null);
            Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Check if there are any courses to export
            if (schedule.getCourses().isEmpty() && schedule.getNonAcademicEvents().isEmpty()) {
                System.out.println("No courses or events in the schedule to export.");
                return;
            }

            // Iterate over courses and create events
            for (Course course : schedule.getCourses()) {
                createCalendarEvent(service, course, schedule);
            }

            for (ScheduleEvent scheduleEvent : schedule.getNonAcademicEvents()) {
                createCalendarEvent(service, scheduleEvent, schedule);
            }

            System.out.println("Schedule successfully exported to Google Calendar!");
        } catch (GeneralSecurityException e) {
            System.err.println("Security error while exporting to Google Calendar: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error while exporting to Google Calendar: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while exporting to Google Calendar: " + e.getMessage());
        }
    }

    /**
     * Authenticate with Google and retrieve credentials.
     *
     * @param httpTransport The HTTP transport to use for authentication.
     * @param username      The username for token storage.
     * @param authCode      The authorization code (null if initiating the flow).
     * @return The user's credentials, or null if an authorization URL is needed.
     * @throws IOException If there is an error reading the credentials file or during authorization.
     */
    public static Credential getCredentials(final NetHttpTransport httpTransport, String username, String authCode) throws IOException {
        String CREDENTIALS_FILE_PATH = "credentials.json";
        File credentialsFile = new File(CREDENTIALS_FILE_PATH);
        if (!credentialsFile.exists()) {
            throw new IOException("Credentials file not found at: " + credentialsFile.getAbsolutePath() + ". Ensure it is in the project root.");
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileReader(credentialsFile));

        String TOKENS_DIRECTORY_PATH = "tokens/" + username;
        List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/calendar.events");
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        File tokenDir = new File(TOKENS_DIRECTORY_PATH);
        File tokenFile = new File(tokenDir, "StoredCredential");

        // Load existing credentials
        Credential credential = flow.loadCredential("user");

        if (credential != null) {
            // Check if token is expired or invalid
            Long expiresInSeconds = credential.getExpiresInSeconds();
            if (expiresInSeconds == null || expiresInSeconds <= 0) {
                try {
                    System.out.println("Access token expired. Attempting to refresh...");
                    if (credential.refreshToken()) {
                        System.out.println("Token refreshed successfully.");
                        return credential;
                    } else {
                        System.out.println("Failed to refresh token. Tokens may be invalid or revoked.");
                    }
                } catch (Exception e) {
                    System.err.println("Error refreshing token: " + e.getMessage());
                }

                // Delete invalid tokens
                if (tokenFile.exists()) {
                    if (tokenFile.delete()) {
                        System.out.println("Deleted invalid token file: " + tokenFile.getAbsolutePath());
                    } else {
                        System.err.println("Failed to delete token file: " + tokenFile.getAbsolutePath());
                    }
                }
                credential = null; // Force re-authentication
            }
        }

        // If no valid credentials and no auth code, return null to indicate authorization is needed
        if (credential == null && authCode == null) {
            return null;
        }

        // If an auth code is provided, exchange it for credentials
        if (authCode != null) {
            try {
                // Explicitly set the redirect_uri in the token request
                credential = flow.createAndStoreCredential(
                        flow.newTokenRequest(authCode).setRedirectUri(REDIRECT_URI).execute(),
                        "user"
                );
                System.out.println("Authentication successful. New tokens stored.");
                return credential;
            } catch (IOException e) {
                System.err.println("Error exchanging authorization code: " + e.getMessage());
                throw e;
            }
        }

        return credential;
    }

    /**
     * Generates the OAuth2 authorization URL for the user to authenticate.
     *
     * @param httpTransport The HTTP transport to use for authentication.
     * @param username      The username for token storage.
     * @return The authorization URL.
     * @throws IOException If there is an error reading the credentials file.
     */
    public static String getAuthorizationUrl(final NetHttpTransport httpTransport, String username) throws IOException {
        String CREDENTIALS_FILE_PATH = "credentials.json";
        File credentialsFile = new File(CREDENTIALS_FILE_PATH);
        if (!credentialsFile.exists()) {
            throw new IOException("Credentials file not found at: " + credentialsFile.getAbsolutePath() + ". Ensure it is in the project root.");
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileReader(credentialsFile));

        List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/calendar.events");
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();

        return flow.newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI)
                .setState(username) // Pass username as state to identify the user
                .build();
    }

    /**
     * Creates recurring events for a course in Google Calendar.
     *
     * @param service The Google Calendar service instance.
     * @param scheduleEvent  The course to create events for.
     * @throws IOException If there is an error communicating with the Google Calendar API.
     */
    private static void createCalendarEvent(Calendar service, ScheduleEvent scheduleEvent, Schedule schedule) throws IOException {
        String[] daysOfWeek = {"MO", "TU", "WE", "TH", "FR"};
        boolean[] daysMeet = scheduleEvent.getDaysMeet();
        int[] startTimes = scheduleEvent.getStartTime();
        int duration = scheduleEvent.getDuration();

        // Define semester dates (adjust as needed)
        LocalDate startSemester;
        LocalDate endSemester;
        if (schedule.getSemester().equals("spring")) {
            startSemester = LocalDate.of(2025, 1, 13);
            endSemester = LocalDate.of(2025, 5, 1);
        } else {
            startSemester = LocalDate.of(2025, 8, 25);
            endSemester = LocalDate.of(2025, 12, 10);
        }

        for (int i = 0; i < daysMeet.length; i++) {
            if (daysMeet[i] && startTimes[i] != -1) {
                Event event;
                // Create the event
                if (scheduleEvent instanceof Course course) {
                    event = createEventCourse(course, i);
                } else {
                    event = createEventScheduleEvent(scheduleEvent);
                }

                // Calculate start and end times
                LocalTime startTime = LocalTime.of(8, 0).plusMinutes(startTimes[i]);
                LocalTime endTime = startTime.plusMinutes(duration);

                LocalDateTime startDateTime = LocalDateTime.of(startSemester, startTime);
                LocalDateTime endDateTime = LocalDateTime.of(startSemester, endTime);

                // Adjust to the first occurrence of the day of the week
                DayOfWeek targetDay = DayOfWeek.of(i + 1); // Monday = 1, ..., Friday = 5
                startDateTime = startDateTime.with(TemporalAdjusters.nextOrSame(targetDay));
                endDateTime = endDateTime.with(TemporalAdjusters.nextOrSame(targetDay));

                // Set event times
                DateTime startDate = new DateTime(startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                DateTime endDate = new DateTime(endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                event.setStart(new EventDateTime().setDateTime(startDate).setTimeZone(ZoneId.systemDefault().getId()));
                event.setEnd(new EventDateTime().setDateTime(endDate).setTimeZone(ZoneId.systemDefault().getId()));

                // Set recurrence rule (weekly until end of semester)
                String rrule = "RRULE:FREQ=WEEKLY;UNTIL=" + endSemester.format(DateTimeFormatter.BASIC_ISO_DATE) + ";BYDAY=" + daysOfWeek[i];
                event.setRecurrence(Collections.singletonList(rrule));

                // Insert the event into the calendar
                service.events().insert("primary", event).execute();
            }
        }
    }

    /**
     * Creates a calendar event for a course.
     *
     * @param course The course to create an event for.
     * @param day    The day of the week (0 = Monday, 1 = Tuesday, ..., 4 = Friday).
     * @return The created event.
     */
    private static Event createEventCourse(Course course, int day) {
        // Create the event
        Event event = new Event()
                .setSummary(course.getDepartment() + " " + course.getCourseCode() + " - " + course.getName())
                .setLocation(getCleanLocation(course, day))
                .setDescription("Professor: " + String.join(", ", course.getProfessor()));
        return event;
    }

    /**
     * Creates a calendar event for a schedule event.
     *
     * @param scheduleEvent The schedule event to create an event for.
     * @return The created event.
     */
    private static Event createEventScheduleEvent(ScheduleEvent scheduleEvent) {
        // Create the event
        Event event = new Event()
                .setSummary(scheduleEvent.getName())
                .setLocation(scheduleEvent.getLocation());
        return event;
    }

    /**
     * Exports the schedule to a PDF file in a Google Calendar-like time slot format.
     * Displays time slots from 8:00 AM to 8:00 PM with courses as blocks.
     * Automatically opens the PDF file after creation.
     */
    public static void exportToPDF(String fileName, boolean open, Schedule schedule) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set font for the title
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);

                // Center the title
                String title = "Course Schedule - " + schedule.getSemester() + " 2025";
                float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(title) / 1000 * 14;
                float pageWidth = page.getMediaBox().getWidth();
                float titleX = (pageWidth - titleWidth) / 2;

                contentStream.beginText();
                contentStream.newLineAtOffset(titleX, 750);
                contentStream.showText(title);
                contentStream.endText();

                // Grid dimensions
                float xStart = 10; // Starting x position
                float yStart = 700; // Starting y position
                float timeColumnWidth = 35; // Width for the time column
                float dayColumnWidth = 110; // Width for each day
                float rowHeight = 52; // How tall each row is
                int numHours = 13; // 13 hours from 8 AM to 9 PM

                // Set larger font for time and day labels
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);

                // Draw time labels (8:00 AM to 9:00 PM) closer to the grid
                for (int i = 0; i <= numHours; i++) {
                    float yPosition = yStart - (i * rowHeight);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(xStart, yPosition - 5);
                    String timeLabel = (i + 8) % 12 == 0 ? "12" : String.valueOf((i + 8) % 12);
                    timeLabel += (i + 8) < 12 || (i + 8) == 24 ? " AM" : " PM";
                    contentStream.showText(timeLabel);
                    contentStream.endText();
                }

                // Draw day headers (Mon to Fri) closer to the grid
                String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri"};
                for (int i = 0; i < days.length; i++) {
                    float xPosition = xStart + timeColumnWidth + (i * dayColumnWidth);
                    float textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(days[i]) / 1000 * 10;
                    float centeredX = xPosition + (dayColumnWidth - textWidth) / 2;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(centeredX, yStart + 5);
                    contentStream.showText(days[i]);
                    contentStream.endText();
                }

                // Draw the grid
                // Vertical lines (for days)
                contentStream.setLineWidth(1f);
                contentStream.setStrokingColor(0, 0, 0); // Black for vertical lines
                for (int i = 0; i <= 5; i++) {
                    float x = xStart + timeColumnWidth + (i * dayColumnWidth);
                    contentStream.moveTo(x, yStart);
                    contentStream.lineTo(x, yStart - (numHours * rowHeight));
                    contentStream.stroke();
                }

                // Horizontal lines (for hours) with faded style
                contentStream.setLineWidth(0.5f);
                contentStream.setStrokingColor(0.7f, 0.7f, 0.7f); // Light gray for faded effect
                for (int i = 0; i <= numHours; i++) {
                    float y = yStart - (i * rowHeight);
                    contentStream.moveTo(xStart + timeColumnWidth, y);
                    contentStream.lineTo(xStart + timeColumnWidth + (5 * dayColumnWidth), y); // Extended to match wider table
                    contentStream.stroke();
                }

                // Reset color for course blocks
                contentStream.setStrokingColor(0, 0, 0);

                // Set font for course blocks
                contentStream.setFont(PDType1Font.HELVETICA, 8);

                // Draw course blocks
                for (Course course : schedule.getCourses()) {
                    boolean[] daysMeet = course.getDaysMeet();
                    int[] startTimes = course.getStartTime();
                    int duration = course.getDuration();

                    for (int day = 0; day < daysMeet.length; day++) {
                        if (daysMeet[day] && startTimes[day] != -1) {
                            // Calculate the position and size of the course block
                            float x = xStart + timeColumnWidth + (day * dayColumnWidth);
                            float startHour = startTimes[day] / 60.0f; // Convert minutes to hours
                            float durationHours = duration / 60.0f; // Duration in hours
                            float y = yStart - (startHour * rowHeight);
                            float blockHeight = durationHours * rowHeight;

                            // Draw the course block (rectangle)
                            contentStream.setNonStrokingColor(0.9f, 0.9f, 0.9f);
                            contentStream.addRect(x + 2, y - blockHeight, dayColumnWidth - 4, blockHeight);
                            contentStream.fill();
                            contentStream.setNonStrokingColor(0, 0, 0); // Reset to black for text

                            // Add course details inside the block
                            contentStream.beginText();
                            contentStream.newLineAtOffset(x + 5, y - 10); // Starting position for the first line
                            String courseLabel = course.getDepartment() + " " + course.getCourseCode() + course.getSection();
                            contentStream.showText(truncateString(courseLabel, 20)); // Course code (e.g., "COMP 141B")
                            contentStream.newLineAtOffset(0, -10); // Move down for the second line
                            contentStream.showText(truncateString(course.getName(), 20)); // Course name (e.g., "COMP PROGRAM...")
                            contentStream.newLineAtOffset(0, -10); // Move down for the third line
                            contentStream.showText(truncateString(getCleanLocation(course, day), 25)); // Course location (e.g., "Room 101")
                            contentStream.endText();
                        }
                    }
                }

                // Draw nonacademic event blocks
                for (ScheduleEvent scheduleEvent : schedule.getNonAcademicEvents()) {
                    boolean[] daysMeet = scheduleEvent.getDaysMeet();
                    int[] startTimes = scheduleEvent.getStartTime();
                    int duration = scheduleEvent.getDuration();

                    for (int day = 0; day < daysMeet.length; day++) {
                        if (daysMeet[day] && startTimes[day] != -1) {
                            // Calculate the position and size of the course block
                            float x = xStart + timeColumnWidth + (day * dayColumnWidth);
                            float startHour = startTimes[day] / 60.0f; // Convert minutes to hours
                            float durationHours = duration / 60.0f; // Duration in hours
                            float y = yStart - (startHour * rowHeight);
                            float blockHeight = durationHours * rowHeight;

                            // Draw the course block (rectangle)
                            contentStream.setNonStrokingColor(0.9f, 0.9f, 0.9f);
                            contentStream.addRect(x + 2, y - blockHeight, dayColumnWidth - 4, blockHeight);
                            contentStream.fill();
                            contentStream.setNonStrokingColor(0, 0, 0); // Reset to black for text

                            // Add course details inside the block
                            contentStream.beginText();
                            contentStream.newLineAtOffset(x + 5, y - 10); // Starting position for the first line
                            contentStream.showText(truncateString(scheduleEvent.getName(), 20)); // Event name (e.g., "COMP PROGRAM...")
                            contentStream.newLineAtOffset(0, -10); // Move down for the second line
                            contentStream.showText(truncateString(scheduleEvent.getLocation(), 25)); // Event location (e.g., "Room 101")
                            contentStream.endText();
                        }
                    }
                }
            }

            // Save the document
            document.save(fileName);
            System.out.println("Schedule exported to " + fileName);

            // Automatically open the PDF file
            if (open) {
                File pdfFile = new File(fileName);
                if (pdfFile.exists()) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(pdfFile);
                        System.out.println("Opening " + fileName + " with default PDF viewer...");
                    } else {
                        System.err.println("Desktop API is not supported on this platform. Please open " + fileName + " manually.");
                    }
                } else {
                    System.err.println("PDF file was not found: " + fileName);
                }
            }

        } catch (IOException e) {
            System.err.println("Error exporting schedule to PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to truncate a string if it exceeds a specified length and remove newlines.
     */
    private static String truncateString(String text, int maxLength) {
        // Handle null or empty strings
        if (text == null) {
            return "";
        }
        // Remove newline characters and other control characters
        text = text.replaceAll("[\\n\\r\\t]", " ");
        // Truncate the string if it exceeds the max length
        if (text.length() > maxLength) {
            return text.substring(0, maxLength - 3) + "...";
        }
        return text;
    }

    /**
     * Gets the clean location for a course on a specific day, removing any " w/multimedia" suffix.
     * @param course The course to get the location for.
     * @param day The day index (0 = Monday, 1 = Tuesday, ..., 4 = Friday).
     * @return The cleaned location for the specified day, or the single location if the course meets in the same room every day.
     */
    private static String getCleanLocation(Course course, int day) {
        String location = course.getLocation();
        if (location == null) {
            return "Unknown";
        }

        // If the location contains a newline, it has multiple locations with times
        if (location.contains("\n")) {
            // Split the location string into parts
            String[] parts = location.split("\n");
            if (parts.length < 2) {
                // Fallback to the first part if parsing fails, and clean it
                return cleanLocationString(parts[0].trim());
            }

            // The first part is the default location (e.g., "SHAL 301")
            String defaultLocation = cleanLocationString(parts[0].trim());
            // The second part contains the exception (e.g., "T 9:30 AM-10:20 AM; SHAL 301")
            String exception = parts[1].trim();

            // Parse the exception (e.g., "T 9:30 AM-10:20 AM; SHAL 301 w/multimedia")
            String[] exceptionParts = exception.split(";");
            if (exceptionParts.length < 2) {
                return defaultLocation; // Fallback to default if parsing fails
            }

            // Get the day and time part (e.g., "T 9:30 AM-10:20 AM")
            String dayTime = exceptionParts[0].trim();
            // Get the exception location (e.g., "SHAL 301 w/multimedia")
            String exceptionLocation = cleanLocationString(exceptionParts[1].trim());

            // Map the day index to the day letter (0 = M, 1 = T, ..., 4 = F)
            String[] dayLetters = {"M", "T", "W", "R", "F"};
            String currentDayLetter = dayLetters[day];

            // Check if the exception applies to the current day
            if (dayTime.startsWith(currentDayLetter)) {
                return exceptionLocation; // Use the exception location for this day
            } else {
                return defaultLocation; // Use the default location for other days
            }
        }

        // If there's no newline, the location is the same every day
        return cleanLocationString(location.trim());
    }

    /**
     * Helper method to clean a location string by removing " w/multimedia" or similar suffixes.
     * @param location The location string to clean.
     * @return The cleaned location string.
     */
    private static String cleanLocationString(String location) {
        // Remove " w/multimedia" or similar suffixes (case-insensitive)
        return location.replaceAll("\\s+w/\\s*multimedia\\b", "").trim();
    }

    /**
     * Sends an email with the user's schedule to their email address from studentschedulerunemployedcs@gmail.com.
     */
    public static void sendEmail(String name, Schedule schedule, String email) {
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
            emailBody.append("Hello ").append(name).append(",\n\n");
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
