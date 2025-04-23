package edu.gcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringMain {
    public static void main(String[] args) {
        //Initialize the courses list in main just in case
        Main.courses = Main.getCourses("data_wolfe.json");
        SpringApplication.run(SpringMain.class,args);
    }
}
