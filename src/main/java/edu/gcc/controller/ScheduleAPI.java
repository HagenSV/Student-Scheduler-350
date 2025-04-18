package edu.gcc.controller;

import edu.gcc.AuthenticatedUserUtil;
import edu.gcc.Course;
import edu.gcc.Main;
import edu.gcc.Schedule;
import edu.gcc.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ScheduleAPI {

    private final UserService userService;

    public ScheduleAPI(UserService userService) {
        this.userService = userService;
    }

    public Schedule getScheduleFromUser(){
        String user = AuthenticatedUserUtil.getAuthenticatedUser();
        //TODO get schedule from db call
        return userService.getUserByUsername(user).getSchedule();
    }

    @GetMapping("/api/v1/schedule")
    public List<Course> getSchedule() {
        Schedule schedule = getScheduleFromUser();
        // This method will handle GET requests to the /api/v1/schedule endpoint
        // You can implement the logic to retrieve and return the schedule here
        return schedule.getCourses();
    }

    @PostMapping("/api/v1/schedule/add")
    public List<Course> addCourse(@RequestBody ScheduleQuery query) {
        // This method will handle adding a course to the schedule
        Course course = getCourse(query.id());
        Schedule schedule = getScheduleFromUser();
        //Course course = query.getCourse();
        if (course == null) return new ArrayList<>();
        List<Course> conflicts = schedule.getConflicts(course);
        schedule.addCourse(course);
        return conflicts;
    }

    @PostMapping("/api/v1/schedule/remove")
    public void removeCourse(@RequestBody ScheduleQuery query){
        // This method will handle removing a course from the schedule
        Course course = getCourse(query.id());
        Schedule schedule = getScheduleFromUser();
        //Course course = query.getCourse();
        if (course == null) return;
        schedule.removeCourse(course);
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
}
