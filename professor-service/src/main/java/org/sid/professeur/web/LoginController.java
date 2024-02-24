package org.sid.professeur.web;


import org.sid.professeur.entities.professeur;
import org.sid.professeur.repositories.ProfesseurRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/login")
public class LoginController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private ProfesseurRepo ProfesseurRepo;


    public LoginController (ProfesseurRepo ProfesseurRepo){
        this.ProfesseurRepo=ProfesseurRepo;
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String login(@RequestParam String email, @RequestParam String password) {
            // Check if email and password fields are not empty
            if (email.isEmpty() || password.isEmpty()) {
                return "Email and password are required";
            }

            Optional<professeur> optionalProf = ProfesseurRepo.findByMail(email);
            if (optionalProf.isPresent()) {
                professeur prof = optionalProf.get();
                if (prof.isAdmin()) {
                    return "Admin login successful";
                } else if (!isValidEmailPattern(email)) {
                    return "Invalid email format";
                } else if (passwordEncoder.matches(password, prof.getMdp())) {
                    if (prof.isFirst_cnx()) {
                        // If it's the first connection, set first_cnx to false and return appropriate message
                        prof.setFirst_cnx(false);
                        ProfesseurRepo.save(prof);
                        return "User connected for the first time";
                    } else {
                        // If it's not the first connection, return regular login successful message
                        return "Login successful";
                    }
                } else {
                    return "Invalid credentials";
                }
            } else {
                logger.error("Professor not found");
                return "Professor not found";
            }

    }
    @GetMapping("/getUserByEmail")
    public Optional<professeur> getUserIdByEmail(@RequestParam String email) {
        return ProfesseurRepo.findByMail(email);
    }

    private boolean isValidEmailPattern(String email) {
        // Check if the email follows the specified pattern
        return email.matches("^[a-zA-Z]+\\.[a-zA-Z]+@umi.ac.ma$");
    }



}

