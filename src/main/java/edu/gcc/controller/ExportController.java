package edu.gcc.controller;

import edu.gcc.AuthenticatedUserUtil;
import edu.gcc.Export;
import edu.gcc.Schedule;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;

@Controller
public class ExportController {

    @GetMapping("/api/v1/export/pdf")
    public ResponseEntity<FileSystemResource> exportPDF(){
        Schedule schedule = AuthenticatedUserUtil.getScheduleFromUser();
        if (schedule == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        Export.exportToPDF("schedule.pdf",false, schedule);
        File f = new File("schedule.pdf");

        if (!f.exists()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        FileSystemResource resource = new FileSystemResource(f);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename= "+ f.getName());
        headers.add(HttpHeaders.CONTENT_TYPE,"application/pdf");
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/api/v1/export/googleCalendar")
    public String exportGoogleCalendar(){
        Schedule schedule = AuthenticatedUserUtil.getScheduleFromUser();
        if (schedule == null) return "redirect:/error";
        Export.exportToCalendar(schedule,AuthenticatedUserUtil.getAuthenticatedUser());
        return "redirect:/#export";
    }

    @GetMapping("/api/v1/export/email")
    public String exportEmail(){
        Schedule schedule = AuthenticatedUserUtil.getScheduleFromUser();
        if (schedule == null) return "redirect:/error";
        //Export.exportToEmail(schedule,AuthenticatedUserUtil.getAuthenticatedUser());
        return "redirect:/#export";
    }
}
