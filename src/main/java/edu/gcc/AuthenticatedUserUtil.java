package edu.gcc;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticatedUserUtil {

    public static String getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
        }
        return null;
    }

    public static Schedule getScheduleFromUser(){
        String semester = "fall";
        String user = AuthenticatedUserUtil.getAuthenticatedUser();
        if (user == null) {
            return null;
        }

        return new Schedule(user,semester);
    }
}
