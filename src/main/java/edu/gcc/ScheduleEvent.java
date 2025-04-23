package edu.gcc;

public class ScheduleEvent {

    // Private fields to store event information
    private int CID;                      // Unique event identification number
    private String name;                    // Event title
    private int[] startTime;               // Array of start times (minutes since 8 AM) for each day
    private int duration;                 // Length of event in minutes
    private boolean[] daysMeet;           // Array of booleans for days event meets (M-F)
    private String semester;            // Semester in which the event occurs (e.g., "Fall 2023")
    private String location;               // Location of the event (e.g., "Room 101");

    public ScheduleEvent(int CID, String name, int[] startTime, int duration, boolean[] daysMeet, String semester, String location) {
        this.CID = CID;
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
        this.daysMeet = daysMeet;
        this.semester = semester;
        this.location = location;
    }

    public int getCID() {
        return CID;
    }

    public String getName() {
        return name;
    }

    public int[] getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public boolean[] getDaysMeet() {
        return daysMeet;
    }

    public String getSemester() {
        return semester;
    }

    public String getLocation() {
        return location;
    }

    /**
     * Checks if this ScheduleEvent is equal to another object
     * @param other The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object other){
        if (other instanceof ScheduleEvent){
            return ((ScheduleEvent) other).getCID() == this.CID;
        }
        return false;  // Not equal if not the same class or different CID
    }

    /**
     * Checks if this ScheduleEvent conflicts with another course in terms of scheduling
     * Generated using Grok AI
     * @param other The other ScheduleEvent to check against
     * @return true if there is a scheduling conflict, false otherwise
     */
    public boolean hasConflict(ScheduleEvent other) {
        // Check each day (0-4 represents Monday-Friday)
        for (int i = 0; i < 5; i++) {
            // Check if both courses meet on this day and have valid start times
            if (this.daysMeet[i] && other.daysMeet[i] &&
                    this.startTime[i] != -1 && other.startTime[i] != -1) {
                int thisStart = this.startTime[i];
                int otherStart = other.startTime[i];
                int thisEnd = thisStart + this.duration;
                int otherEnd = otherStart + other.duration;
                // Check if time periods overlap
                if (thisStart < otherEnd && otherStart < thisEnd) {
                    return true;  // Conflict found
                }
            }
        }
        return false;  // No conflicts found
    }

    /**
     * Converts the time in minutes since 8 AM to a string representation
     * @param minAfter8 The number of minutes after 8 AM
     * @return A string representing the time in HH:MM format
     */
    public String convertTimeToString(int minAfter8){
        int hour = 8 + minAfter8 / 60;
        int min = minAfter8 % 60;
        String time = "";
        if(hour < 10) time += "0";  // Add leading zero if needed
        time += hour + ":";
        if(min < 10) time += "0";   // Add leading zero if needed
        time += min;
        return time;
    }

    /**
     * Converts the day index to a string representation
     * @param i The index of the day (0-4)
     * @return A string representing the day of the week
     */
    public String getDay(int i){
        if(i == 0) return "M";      // Monday
        else if(i == 1) return "T"; // Tuesday
        else if(i == 2) return "W"; // Wednesday
        else if(i == 3) return "R"; // Thursday
        else if(i == 4) return "F"; // Friday
        return "Invalid Day";       // Error case
    }

    /**
     * Converts the Event to a String
     * @return A string representation of the Event
     */
    @Override
    public String toString(){
        StringBuilder output = new StringBuilder();
        // Basic course info
        output.append("CID("+ CID +") " + name + "\n\t"
        );

        // Append meeting days and times
        for(int i = 0; i < daysMeet.length; i++){
            if(daysMeet[i]){
                output.append(getDay(i) + " " + convertTimeToString(startTime[i]) + " ");
            }
        }
        output.append("\n\tDuration: " + duration +
                "\n\tLocation: " + location + "\n\tSemester: " + semester + "\n");

        return output.toString();
    }
}
