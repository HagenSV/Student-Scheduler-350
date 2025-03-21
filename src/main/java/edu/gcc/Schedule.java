package edu.gcc;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
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

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class Schedule {
    private static final String APPLICATION_NAME = "Student Scheduler";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/calendar.events");
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    private ArrayList<Course> courses;

    public Schedule() {
        courses = new ArrayList<>();
    }

    public Schedule(ArrayList<Course> courses) {
        this.courses = courses;
    }

    /**
     * Constructs a schedule based on a series of searchQueries, all classes separated by whitespace
     *
     * @param searchQueries
     */
    public Schedule(String[] searchQueries) {
        ArrayList<ArrayList<Course>> domains = new ArrayList<>();
        ArrayList<Course> foundCourses = Main.courses;

        Map<String, ArrayList<Course>> courseMap = new HashMap<>();
        for (String query : searchQueries) {
            String[] queries = query.split(" ");
            for (Course c : foundCourses) {
                if (c.getDepartment().equals(queries[0])
                        && c.getCourseCode().equals(queries[1])) {
                    if (courseMap.containsKey(query))
                        courseMap.get(query).add(c);
                    else {
                        ArrayList<Course> newEntry = new ArrayList<>();
                        newEntry.add(c);
                        courseMap.put(query, newEntry);
                    }
                }
            }
        }
        for (String s : courseMap.keySet())
            domains.add(courseMap.get(s));

        // Call backtracking search, if domains not found, courses are null
        Schedule generatedSchedule;
        generatedSchedule = backtrack(new Schedule(), domains, 0);
        this.courses = generatedSchedule.getCourses();
    }

    /**
     * Backtracking search with MRV heuristic and forward checking
     *
     * @param schedule        schedule that is being generated
     * @param domains         domain of each Course variable
     * @param nextVarToAssign the next variable to assign in the domain
     * @return completed schedule
     */
    public Schedule backtrack(Schedule schedule, ArrayList<ArrayList<Course>> domains, int nextVarToAssign) {
        // Base case, all variables assigned
        if (domains.size() == nextVarToAssign)
            return schedule;

        // MRV Heuristic choose from ArrayList with the smallest size
        domains.sort(Comparator.comparing(ArrayList::size));
        ArrayList<Course> currentDomain = domains.get(nextVarToAssign);
        for (Course c : currentDomain) {
            if (c.getNumSeats() > 0 && schedule.addCourse(c)) {

                // Create copy of domains
                ArrayList<ArrayList<Course>> domainCopy = new ArrayList<>();
                for (int i = nextVarToAssign + 1; i < domains.size(); i++) {
                    domainCopy.add(new ArrayList<>(domains.get(i)));
                }

                // Forward Checking remove conflicts from domains
                for (ArrayList<Course> variable : domainCopy) {
                    for (Course course : variable) {
                        if (course.hasConflict(c))
                            variable.remove(c);
                    }
                }

                // Check if any domains are empty
                boolean valid = true;
                for (ArrayList<Course> variable : domainCopy) {
                    if (variable.isEmpty()) {
                        valid = false;
                        break;
                    }
                }

                // Domain isn't valid remove from schedule and go to next value
                if (!valid) {
                    schedule.removeCourse(c);

                    // Domain is valid, call backtrack again with deepCopy of forward checked domains
                } else {
                    return backtrack(schedule, domainCopy, nextVarToAssign + 1);
                }
            }
        }
        return null;
    }

    /**
     * Adds a specified course to the schedule and calls the logger function
     *
     * @param course the course to be added
     * @return whether the course was added successfully false if it conflicts with the other courses in the schedule
     */
    public boolean addCourse(Course course) {
        if (course.getNumSeats() < 1)
            return false;
        courses.add(course);
        if (!this.getConflicts(course).isEmpty()) {
            courses.remove(course);
            return false;
        }
        // Log the addition of the course
        logger(true, course);
        return true;
    }

    /**
     * Removes the specified course from the schedule and adds it to the logger
     *
     * @param course the course to be removed
     * @return whether the course was successfully removed, false if it did not exist in the schedule
     */
    public boolean removeCourse(Course course) {
        if (courses.remove(course)) {
            logger(false, course);
            return true;
        } else {
            return false;
        }
    }

    public void generateSchedule(String[] searchQueries) {
        Schedule generatedSchedule = new Schedule(searchQueries);
        this.courses = generatedSchedule.getCourses();
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    /**
     * Finds conflicting classes and returns an arraylist of courses that conflict
     *
     * @return arraylist of courses that conflict
     */
    public ArrayList<Course> getConflicts(Course course) {
        ArrayList<Course> conflicts = new ArrayList<>();
        for (Course c : courses) {
            if (c != course && c.hasConflict(course)) {
                conflicts.add(c);
            }
        }
        return conflicts;
    }

    /**
     * Removes the last course added to the schedule
     *
     * @return whether the undo was successful
     */
    public boolean undo() {
        if (!courses.isEmpty()) {
            courses.remove(courses.size() - 1);
            return true;
        }
        return false;
    }

    /**
     * Logs to a file the course that was added or removed
     * to a saved textile named log.txt
     */
    public void logger(boolean type, Course course) {
        String action = type ? "Added" : "Removed";
        String log = action + " " + course.getDepartment() + " " + course.getCourseCode() + "\n";
        try {
            FileWriter writer = new FileWriter("log.txt", true);
            writer.write(log);
            writer.close();
        } catch (Exception e) {
            System.out.println("An error occurred while writing to the log file.");
            e.printStackTrace();
        }
    }

    /**
     * Exports the schedule to Google Calendar by creating events for each course.
     * Helped largely in part by Grok AI
     */
    public void exportToCalendar() {
        try {
            // Build the HTTP transport and Calendar service
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Check if there are any courses to export
            if (courses.isEmpty()) {
                System.out.println("No courses in the schedule to export.");
                return;
            }

            // Iterate over courses and create events
            for (Course course : courses) {
                createCourseEvents(service, course);
            }
            System.out.println("Schedule successfully exported to Google Calendar!");
        } catch (GeneralSecurityException e) {
            System.err.println("Security error while exporting to Google Calendar: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO error while exporting to Google Calendar: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error while exporting to Google Calendar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Authenticate with Google and retrieve credentials.
     *
     * @param httpTransport The HTTP transport to use for authentication.
     * @return The user's credentials.
     * @throws IOException If there is an error reading the credentials file or during authorization.
     */
    private static Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets from the project root using FileReader
        File credentialsFile = new File(CREDENTIALS_FILE_PATH);
        if (!credentialsFile.exists()) {
            throw new IOException("Credentials file not found at: " + credentialsFile.getAbsolutePath() + ". Ensure it is in the project root.");
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileReader(credentialsFile));

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Creates recurring events for a course in Google Calendar.
     *
     * @param service The Google Calendar service instance.
     * @param course  The course to create events for.
     * @throws IOException If there is an error communicating with the Google Calendar API.
     */
    private void createCourseEvents(Calendar service, Course course) throws IOException {
        String[] daysOfWeek = {"MO", "TU", "WE", "TH", "FR"};
        boolean[] daysMeet = course.getDaysMeet();
        int[] startTimes = course.getStartTime();
        int duration = course.getDuration();

        // Define semester dates (adjust as needed)
        LocalDate startSemester = LocalDate.of(2025, 1, 13);
        LocalDate endSemester = LocalDate.of(2025, 5, 1);

        for (int i = 0; i < daysMeet.length; i++) {
            if (daysMeet[i] && startTimes[i] != -1) {
                // Create the event
                Event event = new Event()
                        .setSummary(course.getDepartment() + " " + course.getCourseCode() + " - " + course.getName())
                        .setDescription("Professor: " + String.join(", ", course.getProfessor()));

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
}
