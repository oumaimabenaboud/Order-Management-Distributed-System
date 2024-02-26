package org.sid.structureservice.web;

import lombok.AllArgsConstructor;
import org.sid.structureservice.entities.DroitAccess;
import org.sid.structureservice.repository.DroitAccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/droitAcces")
public class DroitAccessController {
    @Autowired
    private DroitAccessRepository droitAccessRepository;


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




}
