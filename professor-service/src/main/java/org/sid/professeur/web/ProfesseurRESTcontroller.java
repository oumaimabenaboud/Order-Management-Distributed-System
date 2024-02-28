package org.sid.professeur.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.sid.professeur.entities.professeur;
import org.sid.professeur.repositories.ProfesseurRepo;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@RestController
@RequestMapping("/professeurs")
public class ProfesseurRESTcontroller {
    private final ProfesseurRepo ProfesseurRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(LoginController.class);

    public ProfesseurRESTcontroller (ProfesseurRepo ProfesseurRepo){
        this.ProfesseurRepo=ProfesseurRepo;
    }
    @GetMapping
    public List<professeur> getAllProfesseurs(){
        return ProfesseurRepo.findAllByIsAdminFalse();
    }
    @GetMapping("{id}")
    public professeur getProfesseurById(@PathVariable Long id){
        return ProfesseurRepo.findById(id)
                .orElseThrow(()->new RuntimeException(String.format("Account %s not found",id)));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody professeur professeur) {
        // Set the default value for droit_daccee to false

        professeur.setFirst_cnx(true);

        if (!isValidEmail(professeur.getMail())) {
            return new ResponseEntity<>("L'email doit être sous la forme 'p.nom@umi.ac.ma' ou 'pre.nom@umi.ac.ma' pour les professeurs.", HttpStatus.BAD_REQUEST);
        }

        if (professeur.getNom() != null && !professeur.getNom().isEmpty() ) {
            professeur.setNom(professeur.getNom());
        }else {
            return ResponseEntity.badRequest().body("Le champ 'Nom' ne peut pas être vide.");
        }
        if (professeur.getPrenom() != null && !professeur.getPrenom().isEmpty()) {
            professeur.setPrenom(professeur.getPrenom());
        }else {
            return ResponseEntity.badRequest().body("Le champ 'Prénom' ne peut pas être vide.");
        }
        professeur.setMdp(passwordEncoder.encode((professeur.getNom() + "_" + professeur.getPrenom()).toLowerCase()));
        professeur savedProfesseur = ProfesseurRepo.save(professeur);
        return new ResponseEntity<>(savedProfesseur, HttpStatus.CREATED);
    }
    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z]+\\.[a-zA-Z]+(@umi.ac.ma)$";
        return email.matches(emailPattern);
    }

    private static final AtomicLong idCounter = new AtomicLong(1);
    private Long generateUniqueLongId() {
        // Increment the counter and return the new value
        return idCounter.getAndIncrement();
    }

    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody professeur updatedProfesseur) {
        // Retrieve the existing professor by ID
        professeur existingProf = ProfesseurRepo.findById(id).orElseThrow(() -> new RuntimeException("Professor not found"));

        // Update the properties if provided
        if (updatedProfesseur.getNom() != null && !updatedProfesseur.getNom().isEmpty() ) {
            existingProf.setNom(updatedProfesseur.getNom());
        }else {
            return ResponseEntity.badRequest().body("Le champ 'Nom' ne peut pas être vide.");
        }
        if (updatedProfesseur.getPrenom() != null && !updatedProfesseur.getPrenom().isEmpty()) {
            existingProf.setPrenom(updatedProfesseur.getPrenom());
        }else {
            return ResponseEntity.badRequest().body("Le champ 'Prenom' ne peut pas être vide.");
        }
        if (updatedProfesseur.getMail() != null && !updatedProfesseur.getMail().isEmpty()) {
            // Validate the new email
            if (!isValidEmail(updatedProfesseur.getMail())) {
                return new ResponseEntity<>("L'email doit être sous la forme 'p.nom@umi.ac.ma' ou 'pre.nom@umi.ac.ma' pour les professeurs.", HttpStatus.BAD_REQUEST);
            }
            existingProf.setMail(updatedProfesseur.getMail());
        }else {
            return ResponseEntity.badRequest().body("Le champ 'Mail' ne peut pas être vide.");
        }
        if (updatedProfesseur.getMdp() != null) {
            existingProf.setMdp(passwordEncoder.encode(updatedProfesseur.getMdp()));
        }

        // Save the updated professor
        professeur savedProfesseur = ProfesseurRepo.save(existingProf);
        return new ResponseEntity<>(savedProfesseur, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public void deleteAccount(@PathVariable String id){
        ProfesseurRepo.deleteById(Long.valueOf(id));
    }

    @GetMapping("/search")
    public List<professeur> searchProfesseurs(@RequestParam(required = false) String searchTerm) {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Search by both prenom and nom
            return ProfesseurRepo.findByPrenomContainingIgnoreCaseAndIsAdminFalseOrNomContainingIgnoreCaseAndIsAdminFalse(searchTerm, searchTerm);
        } else {
            // If no search parameter provided or if it's empty, return all professeurs
            return ProfesseurRepo.findAll();
        }
    }
    @PostMapping("/isSamePassword")
    public ResponseEntity<?> isSamePassword(@RequestBody Map<String, String> requestBody) {
        String formPassword = requestBody.get("formPassword");
        Long idprofesseur = Long.parseLong(requestBody.get("idprofesseur"));

        professeur prof = ProfesseurRepo.getReferenceById(idprofesseur);
        String encodedPasswordFromDB = prof.getMdp();

        boolean passwordsMatch = passwordEncoder.matches(formPassword, encodedPasswordFromDB);

        if (passwordsMatch) {
            logger.error("LESSGO");
            return ResponseEntity.ok("Passwords match");
        } else {
            return ResponseEntity.badRequest().body("Passwords not match");
        }
    }

}
