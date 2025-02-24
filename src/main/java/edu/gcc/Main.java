package edu.gcc;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;


public class Main {

    private User[] user;
    private Search search;
    private ArrayList<Course> courses = new ArrayList<>();

    public static void main(String[] args) {
        getCourses();
        // run();
    }

    public static void run() {
    }

    public static void getCourses() {
        try {
            FileReader json = new FileReader("data_wolfe.json");
            JsonObject jsonObject = JsonParser.parseReader(json).getAsJsonObject();

            JsonArray coursesArray = jsonObject.getAsJsonArray("classes");

            for (JsonElement courseElement : coursesArray) {
                String name;
                int[] startingTimes = {-1, -1, -1, -1, -1};
                int duration = 0;
                boolean isOpen;
                ArrayList<String> professors = new ArrayList<>();
                boolean MWForTR = false;
                boolean[] daysMeet = new boolean[5];
                String department;
                String courseCode;
                int credits = 0;
                int numSeats = 0;

                JsonObject course = courseElement.getAsJsonObject();

                JsonArray professorArray = course.get("faculty").getAsJsonArray();


                for (JsonElement professorElement : professorArray) {
                    professors.add(professorElement.getAsString());
                }

                JsonArray times = course.getAsJsonArray("times");
                for (JsonElement dayElement : times) {
                    JsonObject dayObject = dayElement.getAsJsonObject();
                    String day = dayObject.get("day").getAsString();
                    if (day.equals("M")) {
                        MWForTR = true;
                        daysMeet[0] = true;
                        startingTimes[0] = minFrom8(dayObject.get("start_time").getAsString());
                        duration = minFrom8(dayObject.get("end_time").getAsString()) - startingTimes[0];
                    } else if (day.equals("T")) {
                        daysMeet[1] = true;
                        startingTimes[1] = minFrom8(dayObject.get("start_time").getAsString());
                        duration = minFrom8(dayObject.get("end_time").getAsString()) - startingTimes[1];

                    } else if (day.equals("W")) {
                        MWForTR = true;
                        daysMeet[2] = true;
                        startingTimes[2] = minFrom8(dayObject.get("start_time").getAsString());
                        duration = minFrom8(dayObject.get("end_time").getAsString()) - startingTimes[2];

                    } else if (day.equals("R")) {
                        daysMeet[3] = true;
                        startingTimes[3] = minFrom8(dayObject.get("start_time").getAsString());
                        duration = minFrom8(dayObject.get("end_time").getAsString()) - startingTimes[3];

                    } else if (day.equals("F")) {
                        MWForTR = true;
                        daysMeet[4] = true;
                        startingTimes[4] = minFrom8(dayObject.get("start_time").getAsString());
                        duration = minFrom8(dayObject.get("end_time").getAsString()) - startingTimes[4];
                    }
                }

                name = course.get("name").getAsString();
                isOpen = course.get("is_open").getAsBoolean();
                department = course.get("subject").getAsString();
                courseCode = course.get("number").getAsString();
                credits = course.get("credits").getAsInt();
                numSeats = course.get("open_seats").getAsInt();

                // For Testing
                System.out.print(name + ", " + courseCode + " " + department + " is open: " + isOpen + " credits: " + credits + " numOpenSeats: " + numSeats + " professors: ");
                for(String p : professors){
                    System.out.print(p + " ");
                }
                System.out.print(" MWF: " + MWForTR + " DaysMeets: ");
                for (boolean d : daysMeet) {
                    System.out.print(d + " ");
                }

                System.out.print(" starting times: ");
                for (int t : startingTimes) {
                    System.out.print(t + ", ");
                }

                System.out.print("duration: " + duration);
                System.out.println("");


            }


        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static int minFrom8(String time) {
        String[] timeParts = time.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int min = Integer.parseInt(timeParts[1]);

        return (hours - 8) * 60 + min;
    }


}


