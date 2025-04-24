package edu.gcc;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static edu.gcc.Main.courses;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SearchTest {

    private static Course testMWF; // Normal course that meets MWF
    private static Course testLab; // Lab Course
    private static Course testClosedCourse; // Closed Course
    private static Course testTr; // Normal course that meets TR
    private static Course testMWFR; // Course that meets MWFR and R is a different Time
    private static Course testT; // Course that meets on Tuesday
    private static Course testNoDays; // Course that doesn't have any daysMeet or startTime but is open
    private static ArrayList<Course> answer;

    @BeforeEach
    void setUp() {
        testMWF = new Course(0, "Test MWF Course", new int[]{60, -1, 60, -1, 60}, 50, true, new ArrayList<>(Arrays.asList("Smith, John")), true, new boolean[]{true, false, true, false, true}, "CSCI", "101", 3, 10, "A", false, "Spring", "Room 101");
        testLab = new Course(1, "Test Lab Course", new int[]{-1, 360, -1, 360, -1}, 120, true, new ArrayList<>(Arrays.asList("Johnson, Emily")), false, new boolean[]{false, true, false, true, false}, "PHYS", "110", 1, 5, "B", true, "Spring", "Lab Room 1");
        testClosedCourse = new Course(-1, "Test Closed Course", new int[]{0, 0, 0, -1, -1}, 60, false, new ArrayList<>(Arrays.asList("Lee, Robert")), true, new boolean[]{true, false, true, false, true}, "MATH", "201", 4, 0, "C", false, "Spring", "Room 102");
        testTr = new Course(2, "Test TR Course", new int[]{-1, 180, -1, 180, -1}, 75, true, new ArrayList<>(Arrays.asList("Martinez, Anna")), false, new boolean[]{false, true, false, true, false}, "ART", "200", 3, 12, "D", false, "Spring", "Room 103");
        testMWFR = new Course(3, "Test MWFR Course", new int[]{120, -1, 120, 360, 120}, 60, true, new ArrayList<>(Arrays.asList("Patel, Raj")), true, new boolean[]{true, false, true, true, true}, "CHEM", "220", 3, 8, "E", false, "Spring", "Room 104");
        testT = new Course(4, "Test Tuesday Course", new int[]{-1, 300, -1, -1, -1}, 90, true, new ArrayList<>(Arrays.asList("Adams, Sarah", "Whaley, Daniel")), false, new boolean[]{false, true, false, false, false}, "BUS", "301", 3, 5, "F", false, "Spring", "Room 105");
        testNoDays = new Course(5, "Test No Days Course", new int[]{-1, -1, -1, -1, -1}, 0, true, new ArrayList<>(Arrays.asList("Doe, Jane")), false, new boolean[]{false, false, false, false, false}, "LIB", "400", 2, 15, "G", false, "Spring", "Room 106");
        courses = new ArrayList<>(Arrays.asList(testMWF, testLab, testClosedCourse, testTr, testMWFR, testT, testNoDays));
    }

    @Test
    void testSetAndFilterDaysMeeting() {
        Search search = new Search("query", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF, testClosedCourse, testMWFR));

        // Act
        ArrayList<Course> result = search.setDaysMeeting(true);
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("query", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testLab, testTr, testT, testNoDays));

        // Act
        result = search.setDaysMeeting(false);
        assertNotNull(result);
        assertEquals(answer, result);

    }

    @Test
    void testSetAndFilterTime() {
        Search search = new Search("query", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF, testNoDays));

        // Act
        ArrayList<Course> result = search.setTime(60, 240);
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("query", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF));

        // Act
        result = search.setTime(60, -1);
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("query", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF, testLab, testClosedCourse, testTr, testMWFR, testT, testNoDays));

        // Act
        result = search.setTime(-1, -1);
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("query", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testNoDays));

        // Act
        result = search.setTime(0, 0);
        assertNotNull(result);
        assertEquals(answer, result);
    }

    @Test
    void testSetAndFilterDesiredProfessor() {
        Search search = new Search("query", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF));

        // Act
        ArrayList<Course> result = search.setDesiredProfesor("smith");
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("query", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>();

        // Act
        result = search.setDesiredProfesor("");
        assertNotNull(result);
        assertEquals(answer, result);

    }

    @Test
    void testSetAndFilterDepartment() {
        Search search = new Search("query", "", false);
        //search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF));

        // Act
        ArrayList<Course> result = search.setDepartment("CSCI");
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("query", "", false);
        answer = new ArrayList<>();

        // Act
        result = search.setDepartment("");
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("query", "", false);
        answer = new ArrayList<>();
    }

    @Test
    void testSearch() {
        Search search = new Search("csci 101 smith 9:00 mwf", "" , false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF));

        // Act
        search.search();
        ArrayList<Course> result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("query", "", false);
        answer = new ArrayList<>();

        // Act
        result = search.setDepartment("");
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("query", "", false);
        answer = new ArrayList<>();
    }

    @Test
    void testGetResult() {
        Search search = new Search("query", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF, testLab, testClosedCourse, testTr, testMWFR, testT, testNoDays));

        // Act
        ArrayList<Course> result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);
    }

    @Test
    void testSearchByDaysMeeting() {
        Search search = new Search("query", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF, testClosedCourse, testMWFR));

        // Act
        search.searchByDaysMeeting("mwf");
        ArrayList<Course> result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("query", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testLab, testTr, testT, testNoDays));

        // Act
        search.searchByDaysMeeting("tr");
        result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF, testLab, testClosedCourse, testTr, testMWFR, testT, testNoDays));

        // Act
        search.searchByDaysMeeting("");
        result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);
    }

    @Test
    void testSearchByDepartment() {
        Search search = new Search("query", "", false);
        answer = new ArrayList<>(Arrays.asList(testMWF));

        // Act
        search.searchByDepartment("csci");
        ArrayList<Course> result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("", "", false);
        answer = new ArrayList<>();

        // Act
        search.searchByDepartment("");
        result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);

    }

    @Test
    void testSearchByProfessor() {
        Search search = new Search("query", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF));

        // Act
        search.searchByProfessor("smith");
        ArrayList<Course> result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF, testLab, testClosedCourse, testTr, testMWFR, testT, testNoDays));

        // Act
        search.searchByProfessor("");
        result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);
    }

    @Test
    void testSearchByCourseCode() {
        Search search = new Search("query", "", false);
        answer = new ArrayList<>(Arrays.asList(testMWF));

        // Act
        search.searchByCourseCode("csci 101");
        ArrayList<Course> result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("query", "", false);
        answer = new ArrayList<>(Arrays.asList(testClosedCourse));

        // Act
        search.searchByCourseCode("closed");
        result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("", "", false);
        answer = new ArrayList<>();

        // Act
        search.searchByCourseCode("");
        result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);
    }

    @Test
    void testSearchByTime() {
        Search search = new Search("query", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF));

        // Act
        search.searchByTime("9:00");
        ArrayList<Course> result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("query", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF, testNoDays));

        // Act
        search.searchByTime("9:00 12:00");
        result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);

        search = new Search("", "", false);
        search.fillFilteredResult();
        answer = new ArrayList<>(Arrays.asList(testMWF, testLab, testClosedCourse, testTr, testMWFR, testT, testNoDays));

        // Act
        search.searchByTime("");
        result = search.getResult();
        assertNotNull(result);
        assertEquals(answer, result);
    }

}