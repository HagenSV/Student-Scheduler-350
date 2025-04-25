package edu.gcc.controller;

import edu.gcc.*;
import edu.gcc.exception.CourseFullException;
import edu.gcc.exception.ScheduleConflictException;
import edu.gcc.exception.SemesterMismatchException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ScheduleAPI {

    @GetMapping("/api/v1/schedule")
    public List<Course> getSchedule() {
        Schedule schedule = AuthenticatedUserUtil.getScheduleFromUser();
        if (schedule == null){
            return new ArrayList<>();
        }
        // This method will handle GET requests to the /api/v1/schedule endpoint
        // You can implement the logic to retrieve and return the schedule here
        return schedule.getCourses();
    }

    @PostMapping("/api/v1/schedule/add")
    public AddCourseResponse addCourse(@RequestBody ScheduleQuery query) {
        // This method will handle adding a course to the schedule
        Course course = getCourse(query.id());
        Schedule schedule = AuthenticatedUserUtil.getScheduleFromUser();
        if (course == null || schedule == null) return new AddCourseResponse("An unknown error occurred", false, new ArrayList<>());
        try {
            schedule.addCourse(course);
        } catch (CourseFullException | SemesterMismatchException e) {
            return new AddCourseResponse(e.getMessage(), false, new ArrayList<>());
        } catch (ScheduleConflictException e) {
            List<ScheduleEvent> conflicts = schedule.getConflicts(course);
            return new AddCourseResponse(e.getMessage(), false, conflicts);
        }
        return new AddCourseResponse("Course added successfully", true, new ArrayList<>());
    }

    @PostMapping("/api/v1/schedule/remove")
    public void removeCourse(@RequestBody ScheduleQuery query){
        // This method will handle removing a course from the schedule
        Course course = getCourse(query.id());
        Schedule schedule = AuthenticatedUserUtil.getScheduleFromUser();
        //Course course = query.getCourse();
        if (course == null || schedule == null) return;
        schedule.removeCourse(course);
    }

    @PostMapping("/api/v1/completed")
    public List<Course> getCompletedCourses(){
        // This method will handle getting the completed courses
        String user = AuthenticatedUserUtil.getAuthenticatedUser();
        if (user == null) return new ArrayList<>();
        return SearchDatabase.getInstance().getCompletedCoursesFromDB(user);
    }

    @PostMapping("/api/v1/completed/add")
    public void addCompletedCourse(@RequestBody ScheduleQuery body){
        // This method will handle adding a course to the completed courses
        Course course = getCourse(body.id());
        String user = AuthenticatedUserUtil.getAuthenticatedUser();
        if (course == null) return;
        UpdateDatabaseContents.addCompletedCourse(user, String.valueOf(body.id()));
    }

    @PostMapping("/api/v1/completed/remove")
    public void removeCompletedCourse(@RequestBody ScheduleQuery body){
        // This method will handle removing a course from the completed courses
        Course course = getCourse(body.id());
        String user = AuthenticatedUserUtil.getAuthenticatedUser();
        if (course == null) return;
        //UpdateDatabaseContents.removeCompletedCourse(user,String.valueOf(body.id()));
    }

    public Course getCourse(int id){
        System.out.println("Getting course with ID: " + id);
        try {
            return Main.courses.get(id);
        } catch (Exception e){
            return null;
        }
    }

    public record ScheduleQuery(int id){}

    public record AddCourseResponse(String message, boolean success, List<ScheduleEvent> conflicts) {}
}
