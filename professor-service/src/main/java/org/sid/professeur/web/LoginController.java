package org.sid.professeur.web;


import org.sid.professeur.entities.professeur;
import org.sid.professeur.repositories.ProfesseurRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sid.professeur.entities.professeur;

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
                return "L'adresse e-mail et le mot de passe sont nécessaires";
            }

            Optional<professeur> optionalProf = ProfesseurRepo.findByMail(email);
            if (optionalProf.isPresent()) {
                professeur prof = optionalProf.get();
                if (prof.isAdmin()) {
                    return "Connexion admin réussie";
                } else if (!isValidEmailPattern(email)) {
                    return "Format d'email non valide";
                } else if (passwordEncoder.matches(password, prof.getMdp())) {
                    if (prof.isFirst_cnx()) {
                        // If it's the first connection, set first_cnx to false and return appropriate message
                        prof.setFirst_cnx(false);
                        ProfesseurRepo.save(prof);
                        return "Utilisateur connecté pour la première fois";
                    } else {
                        // If it's not the first connection, return regular login successful message
                        return "Connexion réussie";
                    }
                } else {
                    return "Informations d'identification incorrectes";
                }
            } else {
                logger.error("Professeur introuvable");
                return "Professeur introuvable";
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

    @GetMapping("/matchPasswords/{formPassword}")
    public ResponseEntity<?> isSamePassword(@PathVariable String formPassword, @RequestParam professeur prof){
        if(passwordEncoder.matches(formPassword, prof.getMdp())){
            logger.error("MATCHY MATCHY");
        }else {
            logger.error("No matchy matchy");
        }
        return ResponseEntity.ok("");
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @RequestBody professeur updatedProfesseur) {
        // Retrieve the existing professor by ID
        professeur existingProf = ProfesseurRepo.findById(id).orElseThrow(() -> new RuntimeException("Professor not found"));
        logger.error(updatedProfesseur.getMdp());
        logger.error(String.valueOf(id));
        logger.error(String.valueOf(existingProf));
        logger.error(existingProf.getMdp());
        try {
            professeur existingProfesseur = ProfesseurRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException(String.format("Professeur with ID %d not found", id)));

            String newPassword = updatedProfesseur.getMdp();
            if (newPassword != null && !newPassword.isEmpty()) {
                // Encode the new password
                existingProfesseur.setMdp(passwordEncoder.encode(newPassword));
                existingProfesseur.setFirst_cnx(false);

                // Save the updated professeur
                professeur savedProfesseur = ProfesseurRepo.save(existingProfesseur);
                return ResponseEntity.ok(savedProfesseur);
            } else {
                return ResponseEntity.badRequest().body("Le champ 'Mot de passe' ne peut pas être vide.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur s'est produite lors de la mise à jour du mot de passe du professeur.");
        }


    }}

