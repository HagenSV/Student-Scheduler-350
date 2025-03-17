package edu.gcc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class CourseTest {

    private static Course testMWF; // Normal course that meets MWF
    private static Course testLab; // Lab Course
    private static Course testClosedCourse; // Closed Course
    private static Course testTr; // Normal course that meets TR
    private static Course testMWFR; // Course that meets MWFR and R is a different Time
    private static Course testT; // Course that meets on Tuesday
    private static Course testNoDays; // Course that doesn't have any daysMeet or startTime but is open
    private ArrayList<Course> testCourses;

    @BeforeEach
    void setUp() {
        testMWF = new Course(0, "Test MWF Course", new int[]{60, -1, 60, -1, 60}, 50, true, new ArrayList<>(Arrays.asList("Smith, John")), true, new boolean[]{true, false, true, false, true}, "CSCI", "101", 3, 10, "A", false);
        testLab = new Course(1, "Test Lab Course", new int[]{-1, 360, -1, 360, -1}, 120, true, new ArrayList<>(Arrays.asList("Johnson, Emily")), false, new boolean[]{false, true, false, true, false}, "PHYS", "110", 1, 5, "B", true);
        testClosedCourse = new Course(-1, "Test Closed Course", new int[]{0, 0, 0, -1, -1}, 60, false, new ArrayList<>(Arrays.asList("Lee, Robert")), true, new boolean[]{true, false, true, false, true}, "MATH", "201", 4, 0, "C", false);
        testTr = new Course(2, "Test TR Course", new int[]{-1, 180, -1, 180, -1}, 75, true, new ArrayList<>(Arrays.asList("Martinez, Anna")), false, new boolean[]{false, true, false, true, false}, "ART", "200", 3, 12, "D", false);
        testMWFR = new Course(3, "Test MWFR Course", new int[]{120, -1, 120, 360, 120}, 60, true, new ArrayList<>(Arrays.asList("Patel, Raj")), true, new boolean[]{true, false, true, true, true}, "CHEM", "220", 3, 8, "E", false);
        testT = new Course(4, "Test Tuesday Course", new int[]{-1, 300, -1, -1, -1}, 90, true, new ArrayList<>(Arrays.asList("Adams, Sarah", "Whaley, Daniel")), false, new boolean[]{false, true, false, false, false}, "BUS", "301", 3, 5, "F", false);
        testNoDays = new Course(5, "Test No Days Course", new int[]{-1, -1, -1, -1, -1}, 0, true, new ArrayList<>(Arrays.asList("Doe, Jane")), false, new boolean[]{false, false, false, false, false}, "LIB", "400", 2, 15, "G", false);
        testCourses = new ArrayList<>(Arrays.asList(testMWF, testLab, testTr, testMWFR, testT, testNoDays));
    }

    @Test
    public void testGetAllCourses() {
        ArrayList<Course> courses = Main.getCourses("JsonTest.json");
        for (int i = 0; i < courses.size(); i++) {
            assert courses.get(i).getCID() == testCourses.get(i).getCID();
            assert courses.get(i).getName().equals(testCourses.get(i).getName());
            assert Arrays.equals(courses.get(i).getStartTime(), testCourses.get(i).getStartTime());
            assert courses.get(i).getDuration() == testCourses.get(i).getDuration();
            assert courses.get(i).getIsOpen() == testCourses.get(i).getIsOpen();
            assert courses.get(i).getProfessor().equals(testCourses.get(i).getProfessor());
            assert courses.get(i).getMWForTR() == testCourses.get(i).getMWForTR();
            assert Arrays.equals(courses.get(i).getDaysMeet(), testCourses.get(i).getDaysMeet());
            assert courses.get(i).getDepartment().equals(testCourses.get(i).getDepartment());
            assert courses.get(i).getCourseCode().equals(testCourses.get(i).getCourseCode());
            assert courses.get(i).getCredits() == testCourses.get(i).getCredits();
            assert courses.get(i).getNumSeats() == testCourses.get(i).getNumSeats();
            assert courses.get(i).getSection().equals(testCourses.get(i).getSection());
            assert courses.get(i).getIsLab() == testCourses.get(i).getIsLab();
        }
    }

    @Test
    public void testClosedCourse(){
        ArrayList<Course> courses = Main.getCourses("JsonTest.json");
        assert !courses.contains(testClosedCourse);
    }

}