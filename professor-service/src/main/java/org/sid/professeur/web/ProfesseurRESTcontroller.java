package org.sid.professeur.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sid.professeur.entities.professeur;
import org.sid.professeur.repositories.ProfesseurRepo;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@RestController
@RequestMapping("/professeurs")
public class    ProfesseurRESTcontroller {
    private final ProfesseurRepo ProfesseurRepo;


    public ProfesseurRESTcontroller (ProfesseurRepo ProfesseurRepo){
        this.ProfesseurRepo=ProfesseurRepo;
    }
    @GetMapping
    public List<professeur> professeur(){
        return ProfesseurRepo.findAll();
    }
    @GetMapping("{id}")
    public professeur professeur(@PathVariable Long id){
        return ProfesseurRepo.findById(id)
                .orElseThrow(()->new RuntimeException(String.format("Account %s not found",id)));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody professeur professeur) {
        // Set the default value for droit_daccee to false
        professeur.setDroit_daccee(false);

        if (!isValidEmail(professeur.getMail())) {
            return new ResponseEntity<>("L'email doit être sous la forme 'p.nom@umi.ac.ma' ou 'pre.nom@umi.ac.ma' pour les professeurs.", HttpStatus.BAD_REQUEST);
        }

        professeur.setMdp((professeur.getNom() + "_" + professeur.getPrenom()).toLowerCase());
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
        if (updatedProfesseur.getNom() != null) {
            existingProf.setNom(updatedProfesseur.getNom());
        }
        if (updatedProfesseur.getPrenom() != null) {
            existingProf.setPrenom(updatedProfesseur.getPrenom());
        }
        if (updatedProfesseur.getMail() != null) {
            // Validate the new email
            if (!isValidEmail(updatedProfesseur.getMail())) {
                return new ResponseEntity<>("L'email doit être sous la forme 'p.nom@umi.ac.ma' ou 'pre.nom@umi.ac.ma' pour les professeurs.", HttpStatus.BAD_REQUEST);
            }
            existingProf.setMail(updatedProfesseur.getMail());
        }
        if (updatedProfesseur.getMdp() != null) {
            existingProf.setMdp(updatedProfesseur.getMdp());
        }
        if (updatedProfesseur.isDroit_daccee()) {
            existingProf.setDroit_daccee(true);
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
            return ProfesseurRepo.findByPrenomContainingIgnoreCaseOrNomContainingIgnoreCase(searchTerm, searchTerm);
        } else {
            // If no search parameter provided or if it's empty, return all professeurs
            return ProfesseurRepo.findAll();
        }
    }

}
