package edu.gcc;

import java.util.*;

public class Schedule {
    private ArrayList<Course> courses;

    public Schedule() {
        courses = new ArrayList<>();
    }

    public Schedule(ArrayList<Course> courses) {
        this.courses = courses;
    }

    /**
     * Constructs a schedule based on a series of searchQueries, all classes separated by whitespace
     * @param searchQueries
     */
    public Schedule(String[] searchQueries) {
        ArrayList<ArrayList<Course>> domains = new ArrayList<>();
        ArrayList<Course> foundCourses = Main.courses;

        Map<String, ArrayList<Course>> courseMap = new HashMap<>();
        for (String query: searchQueries) {
            String[] queries = query.split(" ");
            for (Course c: foundCourses) {
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
        for (String s: courseMap.keySet())
            domains.add(courseMap.get(s));

        // Call backtracking search, if domains not found, courses are null
        Schedule generatedSchedule;
        generatedSchedule = backtrack(new Schedule(), domains, 0);
        this.courses = generatedSchedule.getCourses();
    }

    /**
     * Backtracking search with MRV heuristic and forward checking
     * @param schedule schedule that is being generated
     * @param domains domain of each Course variable
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
        for (Course c: currentDomain) {
            if (c.getNumSeats() > 0 && schedule.addCourse(c)) {

                // Create copy of domains
                ArrayList<ArrayList<Course>> domainCopy = new ArrayList<>();
                for (int i = nextVarToAssign + 1; i < domains.size(); i++) {
                    domainCopy.add(new ArrayList<>(domains.get(i)));
                }

                // Forward Checking remove conflicts from domains
                for (ArrayList<Course> variable: domainCopy) {
                    for (Course course: variable) {
                        if (course.hasConflict(c))
                            variable.remove(c);
                    }
                }

                // Check if any domains are empty
                boolean valid = true;
                for (ArrayList<Course> variable: domainCopy) {
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
     * Adds a specified course to the schedule
     * @param course the course to be added
     * @return whether the course was added successfully false if it conflicts with the other courses in the schedule
     */
    public boolean addCourse(Course course) {
        courses.add(course);
        if (!this.getConflicts(course).isEmpty()) {
            courses.remove(course);
            return false;
        }
        return true;
    }

    /**
     * Removes the specified course from the schedule
     * @param course the course to be removed
     * @return whether the course was successfully removed, false if it did not exist in the schedule
     */
    public boolean removeCourse(Course course) {
        return courses.remove(course);
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
}
