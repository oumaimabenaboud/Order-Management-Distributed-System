package org.sid.structureservice.web;

import com.netflix.discovery.converters.Auto;
import lombok.AllArgsConstructor;
import org.sid.structureservice.entities.DroitAccess;
import org.sid.structureservice.repository.DroitAccessRepository;
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
public class DroitAccessController {

    Logger logger = LoggerFactory.getLogger(DroitAccessController.class);


    @Autowired
    private DroitAccessRepository droitAccessRepository;
    @Autowired
    private StructureRepository structureRepository;
    @Autowired
    private ProfesseurRestClient professeurRestClient;

    @Autowired
    public DroitAccessController(ProfesseurRestClient professeurRestClient, StructureRepository structureRepository){
        this.professeurRestClient = professeurRestClient;
        this.structureRepository = structureRepository;

    }


    @GetMapping("/getAllDroitAcces")
    public List<DroitAccess> getAllDroitAcces(){
        return droitAccessRepository.findAll();
    }

    @GetMapping("/getAllDroitAccesById")
    public ResponseEntity<DroitAccess> getDroitAccessById(@RequestParam Long id) {
        Optional<DroitAccess> droitAccessOptional = droitAccessRepository.findById(id);
        return droitAccessOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    // Get DroitAccess by Professor ID
    @GetMapping("/getAllDroitAccesByProfessorId")
    public ResponseEntity<List<DroitAccess>> getDroitAccessByProfessorId(@RequestParam Long professorId) {
        List<DroitAccess> droitAccessList = droitAccessRepository.findByIdProfessor(professorId);
        if (!droitAccessList.isEmpty()) {
            return ResponseEntity.ok(droitAccessList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get DroitAccess by Structure ID
    @GetMapping("/getAllDroitAccesByStructureId")
    public ResponseEntity<List<DroitAccess>> getDroitAccessByStructureId(@RequestParam Long structureId) {
        List<DroitAccess> droitAccessList = droitAccessRepository.findByIdStructure(structureId);
        if (!droitAccessList.isEmpty()) {
            return ResponseEntity.ok(droitAccessList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get DroitAccess by Professor ID and Structure ID
    @GetMapping("/byProfessorIdAndStructureId")
    public ResponseEntity<List<DroitAccess>> getDroitAccessByProfessorIdAndStructureId(@RequestParam Long professorId, @RequestParam Long structureId) {
        List<DroitAccess> droitAccessList = droitAccessRepository.findByIdProfessorAndIdStructure(professorId, structureId);
        if (!droitAccessList.isEmpty()) {
            return ResponseEntity.ok(droitAccessList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/createDroitAccess")
    public ResponseEntity<?> createOrUpdateDroitAccess(@RequestBody DroitAccess addedDroitAccess) {
        // Check if professor ID and structure ID exist
        Optional<Professeur> professorOptional = Optional.ofNullable(professeurRestClient.getProfesseurById(addedDroitAccess.getIdProfessor()));
        Optional<Structure> structureOptional = structureRepository.findById(addedDroitAccess.getIdStructure());

        if (professorOptional.isPresent() && structureOptional.isPresent()) {
            // Check if a DroitAccess already exists for the given professor and structure
            List<DroitAccess> existingDroitAccess = droitAccessRepository.findByIdProfessorAndIdStructure(addedDroitAccess.getIdProfessor(), addedDroitAccess.getIdStructure());

            if (existingDroitAccess.isEmpty()) {
                // No existing DroitAccess, create a new one
                DroitAccess createdDroitAccess = droitAccessRepository.save(addedDroitAccess);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdDroitAccess);
            } else {
                // DroitAccess already exists, check if the droitAcces attribute is different
                DroitAccess existingAccess = existingDroitAccess.get(0);
                if (existingAccess.isDroitAcces() != addedDroitAccess.isDroitAcces()) {
                    // Update the existing DroitAccess with the new droitAcces value
                    existingAccess.setDroitAcces(addedDroitAccess.isDroitAcces());
                    DroitAccess updatedDroitAccess = droitAccessRepository.save(existingAccess);
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

    @PutMapping("/updateDroitAccess")
    public ResponseEntity<DroitAccess> updateDroitAccess(@RequestBody DroitAccess updatedDroitAccess, @RequestParam Long idProfessor, @RequestParam Long idStructure) {
        // Check if professor ID and structure ID exist
        Optional<Professeur> professorOptional = Optional.ofNullable(professeurRestClient.getProfesseurById(idProfessor));
        Optional<Structure> structureOptional = structureRepository.findById(idStructure);

        if (professorOptional.isPresent() && structureOptional.isPresent()) {
            Professeur professor = professorOptional.get();
            if (!professor.isAdmin()) {
                // Both professor and structure exist, proceed with updating DroitAccess
                List<DroitAccess> droitAccessList = droitAccessRepository.findByIdProfessorAndIdStructure(idProfessor, idStructure);
                if (!droitAccessList.isEmpty()) {
                    // Assuming there's only one DroitAccess for a given professor and structure
                    DroitAccess existingDroitAccess = droitAccessList.get(0);
                    // Update the existing DroitAccess entity
                    existingDroitAccess.setDroitAcces(updatedDroitAccess.isDroitAcces());
                    // Save the updated DroitAccess
                    DroitAccess updatedDroitAccessEntity = droitAccessRepository.save(existingDroitAccess);
                    return ResponseEntity.ok(updatedDroitAccessEntity);
                } else {
                    // DroitAccess with the given IDs not found
                    return ResponseEntity.notFound().build();
                }
            } else {
                // Professor is an admin, return an error
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } else {
            // Either professor or structure does not exist, return a bad request response
            return ResponseEntity.badRequest().build();
        }
    }


}
