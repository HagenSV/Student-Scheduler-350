package edu.gcc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000") // Adjust the origin as needed
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf().disable()
            .formLogin(form -> form
                .loginPage("/login") // Specify the login page
                .defaultSuccessUrl("/",true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // Specify the logout URL
                .logoutSuccessUrl("/login") // Specify the logout success URL
                .permitAll()
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/api/v1/name", // Exclude the name API
                            "/api/v1/search", // Exclude the search API
                            "/static/**", // Exclude everything in the static directory
                            "/login",     // Exclude login endpoint
                            "/register",  // Exclude register endpoint
                            "/error",     // Exclude error endpoint
                            "/index.html" // Exclude index.html
                    ).permitAll() // Allow access to these routes
                    .anyRequest().authenticated() // Require authentication for all other requests
            )
            .httpBasic(); // Use basic authentication (or configure as needed)

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}