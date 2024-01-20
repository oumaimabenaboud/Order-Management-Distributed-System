package org.sid.professeur.web;


import org.sid.professeur.entities.professeur;
import org.sid.professeur.repositories.ProfesseurRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private ProfesseurRepo ProfesseurRepo;


    public LoginController (ProfesseurRepo ProfesseurRepo){
        this.ProfesseurRepo=ProfesseurRepo;
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping()
    public String login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        if (isAdmin(email, password)) {
            return "Admin login successful";
        }else{

            // Check if email follows the specified pattern
            if (!isValidEmailPattern(email)) {
                return "Invalid email format";
            }

            // Check if email and password fields are not empty
            if (email.isEmpty() || password.isEmpty()) {
                return "Email and password are required";
            }

            Optional<professeur> optionalProf = ProfesseurRepo.findByMail(email);

            if (optionalProf.isPresent()) {
                professeur prof = optionalProf.get();

                // Check if the provided password matches the stored hashed password
                if (passwordEncoder.matches(password, prof.getMdp())) {
                    return "Login successful";
                } else {
                    return "Invalid credentials";
                }
            } else {
                return "Professeur not found";
            }

        }
    }

    private boolean isValidEmailPattern(String email) {
        // Check if the email follows the specified pattern
        return email.matches("^[a-zA-Z]+\\.[a-zA-Z]+@umi.ac.ma$");
    }

    // Add a simple class to represent the login request
    static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    private static final Map<String, String> adminCredentials = new HashMap<>();
    static {
        adminCredentials.put("admin", "admin");
        //adminCredentials.put("admin2@example.com", "adminpassword");
        // Add more admins as needed
    }
    private boolean isAdmin(String email, String password) {
        // Check if the provided credentials match any admin in the map
        return adminCredentials.entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals(email) && entry.getValue().equals(password));
    }

}

