package edu.gcc.controller;

import edu.gcc.AuthenticatedUserUtil;
import edu.gcc.Export;
import edu.gcc.Schedule;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Controller
public class ExportController {

    @GetMapping("/api/v1/export/pdf")
    public ResponseEntity<FileSystemResource> exportPDF() {
        Schedule schedule = AuthenticatedUserUtil.getScheduleFromUser();
        if (schedule == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        Export.exportToPDF("schedule.pdf", false, schedule);
        File f = new File("schedule.pdf");

        if (!f.exists()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        FileSystemResource resource = new FileSystemResource(f);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + f.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/api/v1/export/googleCalendar")
    public String exportGoogleCalendar() {
        Schedule schedule = AuthenticatedUserUtil.getScheduleFromUser();
        if (schedule == null) return "redirect:/error";

        String username = AuthenticatedUserUtil.getAuthenticatedUser();
        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            // Attempt to get credentials
            com.google.api.client.auth.oauth2.Credential credential = Export.getCredentials(httpTransport, username, null);
            if (credential == null) {
                // No valid credentials; redirect to OAuth2 authorization URL
                String authUrl = Export.getAuthorizationUrl(httpTransport, username);
                return "redirect:" + authUrl;
            }
            // Credentials exist; proceed with export
            Export.exportToCalendar(schedule, username);
            return "redirect:/#export";
        } catch (IOException | GeneralSecurityException e) {
            System.err.println("Error initiating Google Calendar export: " + e.getMessage());
            return "redirect:/error";
        }
    }

    @GetMapping("/api/v1/export/oauth2callback")
    public String handleOAuth2Callback(@RequestParam(value = "code", required = false) String authCode,
                                       @RequestParam(value = "state") String username) {
        Schedule schedule = AuthenticatedUserUtil.getScheduleFromUser();
        if (schedule == null || authCode == null) {
            return "redirect:/error";
        }

        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            // Exchange authorization code for credentials
            com.google.api.client.auth.oauth2.Credential credential = Export.getCredentials(httpTransport, username, authCode);
            if (credential == null) {
                return "redirect:/error";
            }
            // Proceed with export
            Export.exportToCalendar(schedule, username);
            return "redirect:/#export";
        } catch (IOException | GeneralSecurityException e) {
            System.err.println("Error handling OAuth2 callback: " + e.getMessage());
            return "redirect:/error";
        }
    }

    @GetMapping("/api/v1/export/email")
    public String exportEmail() {
        Schedule schedule = AuthenticatedUserUtil.getScheduleFromUser();
        if (schedule == null) return "redirect:/error";
        // Export.exportToEmail(schedule, AuthenticatedUserUtil.getAuthenticatedUser());
        return "redirect:/#export";
    }
}