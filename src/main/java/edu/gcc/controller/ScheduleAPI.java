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

    public Schedule getScheduleFromUser(){
        String semester = "fall";
        String user = AuthenticatedUserUtil.getAuthenticatedUser();
        if (user == null) {
            return null;
        }

        return new Schedule(user,semester);
    }

    @GetMapping("/api/v1/schedule")
    public List<Course> getSchedule() {
        Schedule schedule = getScheduleFromUser();
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
        Schedule schedule = getScheduleFromUser();
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
        Schedule schedule = getScheduleFromUser();
        //Course course = query.getCourse();
        if (course == null || schedule == null) return;
        schedule.removeCourse(course);
    }

    @GetMapping("/api/v1/schedule/export?type=google")
    public String exportCalendar(){
        // This method will handle exporting the schedule to a calendar
        Schedule schedule = getScheduleFromUser();
        if (schedule == null) return "redirect:/error";
        schedule.exportToCalendar();
        return "redirect:/success";
    }

    @GetMapping("/api/v1/schedule/export?format=pdf")
    public String exportPDF(){
        // This method will handle exporting the schedule to a PDF
        Schedule schedule = getScheduleFromUser();
        if (schedule == null) return "redirect:/error";
        schedule.exportToPDF();
        return "redirect:/success";
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
