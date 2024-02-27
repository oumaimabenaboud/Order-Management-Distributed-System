package org.sid.structureservice.web;

import com.fasterxml.jackson.databind.JsonSerializer;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.sid.structureservice.entities.DroitAcces;
import org.sid.structureservice.repository.DroitAccesRepository;
import org.sid.structureservice.model.Professeur;
import org.sid.structureservice.feign.ProfesseurRestClient;
import org.sid.structureservice.entities.Structure;
import org.sid.structureservice.repository.StructureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/droitAcces")
public class DroitAccesController {

    Logger logger = LoggerFactory.getLogger(DroitAccesController.class);


    @Autowired
    private DroitAccesRepository droitAccesRepository;
    @Autowired
    private StructureRepository structureRepository;
    @Autowired
    private ProfesseurRestClient professeurRestClient;

    @Autowired
    public DroitAccesController(ProfesseurRestClient professeurRestClient, StructureRepository structureRepository){
        this.professeurRestClient = professeurRestClient;
        this.structureRepository = structureRepository;

    }


    @GetMapping
    public List<DroitAcces> getAllDroitAcces(){
        return droitAccesRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DroitAcces> getDroitAccessById(@PathVariable Long id) {
        Optional<DroitAcces> droitAccessOptional = droitAccesRepository.findById(id);
        return droitAccessOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    // Get DroitAccess by Professor ID
    @GetMapping("/getAllDroitAccesByProfessorId/{IdProfessor}")
    public ResponseEntity<List<DroitAcces>> getDroitAccessByProfessorId(@PathVariable Long IdProfessor) {
        List<DroitAcces> droitAccessList = droitAccesRepository.findByIdProfessor(IdProfessor);
        if (!droitAccessList.isEmpty()) {
            return ResponseEntity.ok(droitAccessList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get DroitAccess by Structure ID
    @GetMapping("/getAllDroitAccesByStructureId/{Idstructure}")
    public ResponseEntity<List<DroitAcces>> getDroitAccessByStructureId(@PathVariable Long Idstructure) {
        List<DroitAcces> droitAccessList = droitAccesRepository.findByIdStructure(Idstructure);
        if (!droitAccessList.isEmpty()) {
            return ResponseEntity.ok(droitAccessList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get DroitAccess by Professor ID and Structure ID
    @GetMapping("/byProfessorIdAndStructureId/{IdProfessor}/{Idstructure}")
    public DroitAcces getDroitAccessByProfessorIdAndStructureId(@PathVariable Long IdProfessor, @PathVariable Long Idstructure) {
        DroitAcces droitAccessList = droitAccesRepository.findByIdProfessorAndIdStructure(IdProfessor, Idstructure);
        if (droitAccessList!= null) {
            return droitAccessList;
        } else {
            throw new RuntimeException("Droit d'acces non trouvé pour: " + IdProfessor);
        }
    }

    @PostMapping("/createDroitAccess")
    public ResponseEntity<?> createOrUpdateDroitAccess(@RequestBody DroitAcces addedDroitAccess) {
        logger.error("Structure added is : "+addedDroitAccess);
        // Check if professor ID and structure ID exist
        Optional<Professeur> professorOptional = Optional.ofNullable(professeurRestClient.getProfesseurById(addedDroitAccess.getIdProfessor()));
        Optional<Structure> structureOptional = structureRepository.findById(addedDroitAccess.getIdStructure());

        if (professorOptional.isPresent() && structureOptional.isPresent()) {
            // Check if a DroitAccess already exists for the given professor and structure
            DroitAcces existingDroitAccess = droitAccesRepository.findByIdProfessorAndIdStructure(addedDroitAccess.getIdProfessor(), addedDroitAccess.getIdStructure());

            if (existingDroitAccess == null) {
                // No existing DroitAccess, create a new one
                DroitAcces createdDroitAccess = droitAccesRepository.save(addedDroitAccess);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdDroitAccess);
            } else {
                // DroitAccess already exists, check if the droitAcces attribute is different
                DroitAcces existingAccess = existingDroitAccess;
                if (existingAccess.isDroitAcces() != addedDroitAccess.isDroitAcces()) {
                    // Update the existing DroitAccess with the new droitAcces value
                    existingAccess.setDroitAcces(addedDroitAccess.isDroitAcces());
                    DroitAcces updatedDroitAccess = droitAccesRepository.save(existingAccess);
                    return ResponseEntity.ok(updatedDroitAccess);
                } else {
                    // DroitAcces already exists with the same droitAcces value, return a bad request response
                    return ResponseEntity.badRequest().body("DroitAccess already exists with the same droitAcces value.");
                }
            }
        } else {
            // Either professor or structure does not exist, return a bad request response
            return ResponseEntity.badRequest().body("Le professeur ou la structure entrée n'existe pas.");
        }
    }

    @Transactional
    @PutMapping("/updateDroitAccess")
    public ResponseEntity<DroitAcces> updateDroitAccess(@RequestBody DroitAcces updatedDroitAccess, @RequestParam Long idProfessor, @RequestParam Long idStructure) {
        // Check if professor ID and structure ID exist
        Optional<Professeur> professorOptional = Optional.ofNullable(professeurRestClient.getProfesseurById(idProfessor));
        Optional<Structure> structureOptional = structureRepository.findById(idStructure);

        if (professorOptional.isPresent() && structureOptional.isPresent()) {
            Professeur professor = professorOptional.get();
            if (!professor.isAdmin()) {
                // Both professor and structure exist, proceed with updating DroitAccess
                DroitAcces droitAccessList = droitAccesRepository.findByIdProfessorAndIdStructure(idProfessor, idStructure);
                if (droitAccessList!=null) {
                    logger.error("I want to remove this struct Id: "+updatedDroitAccess.getIdStructure());
                    deleteStructureById(updatedDroitAccess.getIdStructure());
                    DroitAcces da = new DroitAcces();
                    da.setIdStructure(updatedDroitAccess.getIdStructure());
                    da.setIdProfessor(updatedDroitAccess.getIdProfessor());
                    da.setDroitAcces(updatedDroitAccess.isDroitAcces());

                    createOrUpdateDroitAccess(da);

                    /*droitAccessList.setIdProfessor(updatedDroitAccess.getIdProfessor());
                    droitAccessList.setIdStructure(updatedDroitAccess.getIdStructure());
                    droitAccessList.setDroitAcces(updatedDroitAccess.isDroitAcces());
                    DroitAcces updatedDroitAccessEntity = droitAccesRepository.save(droitAccessList);*/
                    return ResponseEntity.ok(da);
                } else {
                    // DroitAccess with the given IDs not found
                    return ResponseEntity.notFound().build();
                }
            } else {
                // Professor is an admin, return an error
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    @Transactional
    @DeleteMapping("/{structureId}")
    public ResponseEntity<?> deleteStructureById(@PathVariable Long structureId) {
        Optional<Structure> structureOptional = structureRepository.findById(structureId);
        if (structureOptional.isPresent()) {
            droitAccesRepository.deleteByIdStructure(structureId);
            logger.error("Removed :"+structureId);
            return ResponseEntity.ok("Structure supprimée avec succès !");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
