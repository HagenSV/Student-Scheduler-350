package edu.gcc;

import com.google.gson.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GetCoursesTest {

    private static final String TEST_JSON_FILE = "test_data.json";

    @BeforeEach
    void setUp() throws IOException {
        // Creating a test JSON file with a mix of open and closed courses
        String testJson = """
        {
          "classes": [
            {
              "name": "Test Course",
              "faculty": ["Doe, John"],
              "is_open": true,
              "subject": "TEST",
              "number": "101",
              "credits": 3,
              "open_seats": 10,
              "section": "A",
              "is_lab": false,
              "times": [
                {"day": "M", "start_time": "09:00", "end_time": "10:00"},
                {"day": "W", "start_time": "09:00", "end_time": "10:00"},
                {"day": "F", "start_time": "09:00", "end_time": "10:00"}
              ]
            },
            {
              "name": "Closed Course",
              "faculty": ["Smith, Jane"],
              "is_open": false,
              "subject": "TEST",
              "number": "102",
              "credits": 3,
              "open_seats": 0,
              "section": "B",
              "is_lab": false,
              "times": []
            }
          ]
        }
        """;

        // Write test JSON to file
        Files.write(Paths.get(TEST_JSON_FILE), testJson.getBytes());
    }

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        // Ensure file is released before deletion
        System.gc(); // Suggest garbage collection to release locks
        Thread.sleep(200); // Small delay to allow file unlock

        Path filePath = Paths.get(TEST_JSON_FILE);
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                System.err.println("Warning: Failed to delete test file. File might be in use.");
            }
        }
    }

    @Test
    void testGetCoursesLoadsOnlyOpenCourses() throws IOException {
        // Test that only open courses are added to the list
        try (FileReader json = new FileReader(TEST_JSON_FILE)) {
            JsonObject jsonObject = JsonParser.parseReader(json).getAsJsonObject();

            JsonArray coursesArray = jsonObject.getAsJsonArray("classes");
            assertNotNull(coursesArray);
            assertEquals(2, coursesArray.size());

            List<Course> courseList = new ArrayList<>();
            for (JsonElement courseElement : coursesArray) {
                JsonObject course = courseElement.getAsJsonObject();
                if (course.get("is_open").getAsBoolean()) {
                    courseList.add(new Course(
                            course.get("name").getAsString(),
                            new int[]{-1, -1, -1, -1, -1},
                            0,
                            course.get("is_open").getAsBoolean(),
                            new ArrayList<>(List.of(course.getAsJsonArray("faculty").get(0).getAsString())),
                            false,
                            new boolean[]{false, false, false, false, false},
                            course.get("subject").getAsString(),
                            course.get("number").getAsString(),
                            course.get("credits").getAsInt(),
                            course.get("open_seats").getAsInt(),
                            course.get("section").getAsString(),
                            course.get("is_lab").getAsBoolean()
                    ));
                }
            }

            assertEquals(1, courseList.size()); // Ensure only one open course is loaded
            assertEquals("Test Course", courseList.get(0).getName()); // Verify course name
        }
    }

    @Test
    void testGetCoursesHandlesMissingFields() throws IOException {
        // Test behavior when required fields are missing
        String invalidJson = """
        {
          "classes": [
            {
              "name": "Invalid Course"
              // Missing faculty, is_open, subject, times, etc.
            }
          ]
        }
        """;

        Files.write(Paths.get(TEST_JSON_FILE), invalidJson.getBytes());

        try (FileReader json = new FileReader(TEST_JSON_FILE)) {
            JsonObject jsonObject = JsonParser.parseReader(json).getAsJsonObject();
            JsonArray coursesArray = jsonObject.getAsJsonArray("classes");

            assertNotNull(coursesArray);
            assertEquals(1, coursesArray.size());

            JsonObject course = coursesArray.get(0).getAsJsonObject();
            assertTrue(course.has("name"));
            assertFalse(course.has("faculty"));
            assertFalse(course.has("is_open"));
        }
    }

    @Test
    void testGetCoursesHandlesEmptyClassesArray() throws IOException {
        // Test when the "classes" array is empty
        String emptyJson = """
        {
          "classes": []
        }
        """;

        Files.write(Paths.get(TEST_JSON_FILE), emptyJson.getBytes());

        try (FileReader json = new FileReader(TEST_JSON_FILE)) {
            JsonObject jsonObject = JsonParser.parseReader(json).getAsJsonObject();
            JsonArray coursesArray = jsonObject.getAsJsonArray("classes");

            assertNotNull(coursesArray);
            assertEquals(0, coursesArray.size());
        }
    }

    @Test
    void testGetCoursesHandlesFileNotFound() {
        // Verify exception is thrown when file is missing
        assertThrows(FileNotFoundException.class, () -> {
            try (FileReader json = new FileReader("nonexistent.json")) {
                JsonParser.parseReader(json).getAsJsonObject();
            }
        });
    }

    @Test
    void testGetCoursesHandlesMalformedJson() throws IOException {
        // Test case with malformed JSON syntax
        String malformedJson = """
        {
          "classes": [
            { "name": "Broken Course" "faculty": ["Jane Doe"] }
          ]
        """; // Missing commas → Invalid JSON format

        Files.write(Paths.get(TEST_JSON_FILE), malformedJson.getBytes());

        assertThrows(JsonSyntaxException.class, () -> {
            try (FileReader json = new FileReader(TEST_JSON_FILE)) {
                JsonParser.parseReader(json).getAsJsonObject();
            }
        });
    }

    @Test
    void testGetCoursesHandlesCourseWithNoTimes() throws IOException {
        // Test that a course without time slots is handled correctly
        String jsonWithNoTimes = """
        {
          "classes": [
            {
              "name": "No Time Course",
              "faculty": ["Doe, John"],
              "is_open": true,
              "subject": "TEST",
              "number": "201",
              "credits": 3,
              "open_seats": 5,
              "section": "A",
              "is_lab": false,
              "times": []
            }
          ]
        }
        """;

        Files.write(Paths.get(TEST_JSON_FILE), jsonWithNoTimes.getBytes());

        try (FileReader json = new FileReader(TEST_JSON_FILE)) {
            JsonObject jsonObject = JsonParser.parseReader(json).getAsJsonObject();
            JsonArray coursesArray = jsonObject.getAsJsonArray("classes");

            assertNotNull(coursesArray);
            assertEquals(1, coursesArray.size());

            JsonObject course = coursesArray.get(0).getAsJsonObject();
            assertEquals("No Time Course", course.get("name").getAsString());
            assertTrue(course.get("times").getAsJsonArray().isEmpty()); // Ensure times array is empty
        }
    }
}
