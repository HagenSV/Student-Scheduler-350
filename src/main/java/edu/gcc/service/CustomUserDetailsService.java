package edu.gcc.service;

import edu.gcc.DBUser;
import edu.gcc.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //Find user in database
        DBUser dbUser = userRepository.findByEmail(email);
        if (dbUser == null){
            throw new UsernameNotFoundException("User not found");
        }
        return User.builder()
                .username(dbUser.getEmail())
                .password(dbUser.getPassword())
                .roles("USER")
                .build();
    }
}
