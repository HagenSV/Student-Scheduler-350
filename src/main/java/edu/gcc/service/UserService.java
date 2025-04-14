package edu.gcc.service;


import edu.gcc.UserRepository;
import edu.gcc.dbUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username taken");
        }
        dbUser user = new dbUser(username, passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public dbUser getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
