package edu.gcc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static edu.gcc.Main.courses;
import static edu.gcc.Main.minFrom8;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class SearchTest {
    @BeforeEach
    void setUp() {
        courses = Main.getCourses("data_wolfe.json");
    }
    @Test
    void testSetDaysMeeting() {
        Search search = new Search("query");
        search.fillFilteredResult();
        // Act
        ArrayList<Course> result = search.setDaysMeeting(true);
        assertNotNull(result);
        for (Course c : result) {
            if (c.getMWForTR() != true) {
                fail("Course should not meet on TR");
            }
        }

        search = new Search("query");
        search.fillFilteredResult();
        // Act
        result = search.setDaysMeeting(false);
        assertNotNull(result);
        for (Course c : result) {
            if (c.getMWForTR() == true) {
                fail("Course should not meet on MWF");
            }
        }
    }

    @Test
    void testSetTime() {
        Search search = new Search("query");
        search.fillFilteredResult();
        // Act
        ArrayList<Course> result = search.setTime(60, -1);
        // Assert
        assertNotNull(result);
        boolean goodTime = false;
        for (Course c : result) {
            for (int i : c.getStartTime()) {
                if (i == 60) {
                    goodTime = true;
                    break;
                }
            }
            if (!goodTime) {
                fail("Course does not start at 9:00");
            }
        }

        search = new Search("query");
        search.fillFilteredResult();
        // Act
        result = search.setTime(60, 240);
        // Assert
        assertNotNull(result);
        goodTime = false;
        for (Course c : result) {
            for (int i : c.getStartTime()) {
                if (i >= 60 && i <= 240) {
                    goodTime = true;
                    break;
                }
            }
            if (!goodTime) {
                fail("Course does not take place the min and max times");
            }
        }
    }

    @Test
    void testSetDesiredProfessor() {
        Search search = new Search("query");
        search.fillFilteredResult();
        // Act
        ArrayList<Course> result = search.setDesiredProfesor("shultz");
        // Assert
        assertNotNull(result);
        boolean goodProfessor = false;
        for (Course c : result) {
            for (String s : c.getProfessor()) {
                if (s.equals("Shultz, Tricia Michele")) {
                    goodProfessor = true;
                    break;
                }
            }
            if (!goodProfessor) {
                fail("Course does not have professor shultz");
            }
        }
    }

    @Test
    void testSetDepartment() {
        // Arrange
        Search search = new Search("query");
        // Act
        ArrayList<Course> result = search.setDepartment("comp");
        // Assert
        assertNotNull(result);
        for (Course c : result) {
            if (!c.getDepartment().equals("COMP")) {
                fail("Course is not in the comp department");
            }
        }
    }

    @Test
    void testSearch() {
        Search search = new Search("shultz 12:00");
        // Act
        search.search();
        ArrayList<Course> result = search.getResult();
        // Assert
        assertNotNull(result);
        boolean goodProfessor = false;
        boolean goodTime = false;
        for (Course c : result) {
            for (String s : c.getProfessor()) {
                if (s.equals("Shultz, Tricia Michele")) {
                    goodProfessor = true;
                    break;
                }
            }
            for (int i : c.getStartTime()) {
                if (i == 240) {
                    goodTime = true;
                    break;
                }
            }
            if (!goodProfessor || !goodTime) {
                fail("Course does not have professor shultz at 12:00");
            }
        }

        search = new Search("hutchins comp");
        // Act
        search.search();
        result = search.getResult();
        // Assert
        assertNotNull(result);
        goodProfessor = false;
        for (Course c : result) {
            for (String s : c.getProfessor()) {
                if (s.equals("Hutchins, Jonathan O.")) {
                    goodProfessor = true;
                    break;
                }
            }
            if (!goodProfessor || !c.getDepartment().equals("COMP")) {
                fail("Course does not have professor hutchins that is in the comp department");
            }
        }

        search = new Search("comp 141 8:00 11:00");
        // Act
        search.search();
        result = search.getResult();
        // Assert
        assertNotNull(result);
        goodTime = false;
        for (Course c : result) {
            for (int i : c.getStartTime()) {
                if (i >= 0 && i <= 360 - c.getDuration()) {
                    goodTime = true;
                    break;
                }
            }
            if (!goodTime || !c.getName().equals("COMP PROGRAMMING I")) {
                fail("Course does not have professor hutchins that is in the comp department");
            }
        }
    }

    @Test
    void testGetResult() {
        // Arrange
        Search search = new Search("query");
        search.fillFilteredResult();
        // Act
        ArrayList<Course> result = search.getResult();
        // Assert
        assertNotNull(result);
        if (!courses.equals(result)) {
            fail("Did not get result correctly");
        }
        // Add more assertions based on expected behavior
    }

    @Test
    void testSearchByDaysMeeting() {
        // Arrange
        Search search = new Search("query");
        search.fillFilteredResult();
        // Act
        search.searchByDaysMeeting("mwf");
        ArrayList<Course> result = search.getResult();
        // Assert
        assertNotNull(result);
        for (Course c : result) {
            if (c.getMWForTR() != true) {
                fail("Course should not meet on TR");
            }
        }

        search = new Search("query");
        search.fillFilteredResult();
        // Act
        search.searchByDaysMeeting("tr");
        result = search.getResult();
        // Assert
        assertNotNull(result);
        for (Course c : result) {
            if (c.getMWForTR() == true) {
                fail("Course should not meet on MWF");
            }
        }
    }

    @Test
    void testSearchByDepartment() {
        // Arrange
        Search search = new Search("query");
        // Act
        search.searchByDepartment("comp");
        ArrayList<Course> result = search.getResult();
        // Assert
        assertNotNull(result);
        for (Course c : result) {
            if (!c.getDepartment().equals("COMP")) {
                fail("Course is not in the comp department");
            }
        }

        // Arrange
        search = new Search("query");
        // Act
        search.searchByDepartment("accounting");
        result = search.getResult();
        // Assert
        assertNotNull(result);
        for (Course c : result) {
            if (!c.getDepartment().equals("ACCT") && !c.getDepartment().equals("FNCE")) {
                fail("Course is not in the acct department");
            }
        }
    }

    @Test
    void testSearchByProfessor() {
        // Arrange
        Search search = new Search("query");
        search.fillFilteredResult();
        // Act
        search.searchByProfessor("shultz");
        ArrayList<Course> result = search.getResult();
        // Assert
        assertNotNull(result);
        for (Course c : result) {
            boolean goodProfessor = false;
            for (String s : c.getProfessor()) {
                if (s.equals("Shultz, Tricia Michele")) {
                    goodProfessor = true;
                    break;
                }
            }
            if (!goodProfessor) {
                fail("Course does not have professor shultz");
            }
        }
    }

    @Test
    void testSearchByCourseCode() {
        // Arrange
        Search search = new Search("query");
        search.fillFilteredResult();
        // Act
        search.searchByCourseCode("comp 141");
        ArrayList<Course> result = search.getResult();
        // Assert
        assertNotNull(result);
        for (Course c : result) {
            if (!c.getName().equals("COMP PROGRAMMING I")) {
                fail("Course is not COMP 141");
            }
        }

        // Arrange
        search = new Search("query");
        search.fillFilteredResult();
        // Act
        search.searchByCourseCode("comp programming i");
        result = search.getResult();
        // Assert
        assertNotNull(result);
        for (Course c : result) {
            if (!c.getName().equals("COMP PROGRAMMING I")) {
                fail("Course is not COMP 141");
            }
        }
    }

    @Test
    void testSearchByTime() {
        // Arrange
        Search search = new Search("query");
        search.fillFilteredResult();
        // Act
        search.searchByTime("9:00");
        ArrayList<Course> result = search.getResult();
        // Assert
        assertNotNull(result);
        boolean goodTime = false;
        for (Course c : result) {
            for (int i : c.getStartTime()) {
                if (i == 60) {
                    goodTime = true;
                    break;
                }
            }
            if (!goodTime) {
                fail("Course does not start at 9:00");
            }
        }

        search = new Search("query");
        search.fillFilteredResult();
        // Act
        search.searchByTime("9:00 12:00");
        result = search.getResult();
        // Assert
        assertNotNull(result);
        goodTime = false;
        for (Course c : result) {
            for (int i : c.getStartTime()) {
                if (i >= 60 && i <= 240 - c.getDuration()) {
                    goodTime = true;
                    break;
                }
            }
            if (!goodTime) {
                fail("Course does not take place between 9:00 and 12:00");
            }
        }
    }

}