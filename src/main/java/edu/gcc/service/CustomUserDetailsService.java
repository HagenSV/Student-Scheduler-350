package edu.gcc.service;

import edu.gcc.repository.UserRepository;
import edu.gcc.dbUser;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Find user in database
        dbUser dbUser = userRepository.findByUsername(username);
        if (dbUser == null){
            throw new UsernameNotFoundException("User not found");
        }
        return User.builder()
                .username(dbUser.getUsername())
                .password(dbUser.getPassword())
                .roles("USER")
                .build();
    }
}
