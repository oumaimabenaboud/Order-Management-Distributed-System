package org.sid.structureservice.web;

import lombok.AllArgsConstructor;
import org.sid.structureservice.entities.Structure;
import org.sid.structureservice.entities.ResponsableStructure;
import org.sid.structureservice.enums.structurestype;
import org.sid.structureservice.feign.BudgetRestClient;
import org.sid.structureservice.feign.ProfesseurRestClient;
import org.sid.structureservice.model.Professeur;
import org.sid.structureservice.repository.StructureRepository;
import org.sid.structureservice.repository.ResponsableStructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController@AllArgsConstructor
@RequestMapping("/structures")
public class StructureRestController {
    @Autowired
    private StructureRepository structureRepository;
    @Autowired
    private ProfesseurRestClient professeurRestClient;
    @Autowired
    private BudgetRestClient budgetRestClient;

        @Autowired
        public StructureRestController(ProfesseurRestClient professeurRestClient, BudgetRestClient budgetRestClient) {
            this.professeurRestClient = professeurRestClient;
            this.budgetRestClient = budgetRestClient;
        }

    @PostMapping
    public ResponseEntity<?> createStructure(@RequestBody Structure addedStructure) {
        // Fetch responsible professor from the professor service
        Professeur responsibleProfessor = professeurRestClient.getProfesseurById(addedStructure.getIdResponsable());

        // Vérifier que le responsable n'est pas membre de l'équipe
        if (addedStructure.getEquipe_prof_ids().contains(addedStructure.getIdResponsable())) {
            return ResponseEntity.badRequest().body("Le responsable ne peut pas être membre de l'équipe.");
        }

        // Vérifier le nombre de membres pour le type 'EquipedeRecherche'
        if (addedStructure.getType() == structurestype.EquipedeRecherche) {
            if (addedStructure.getEquipe_prof_ids().size() < 4) {
                return ResponseEntity.badRequest().body("Pour le type 'Equipe de Recherche', il faut au moins 4 membres.");
            }
        }

        // Vérifier s'il y a des membres dupliqués
        Set<Long> uniqueIds = new HashSet<>();
        for (Long profId : addedStructure.getEquipe_prof_ids()) {
            // Si l'identifiant est déjà présent, c'est un membre en double
            if (!uniqueIds.add(profId)) {
                return ResponseEntity.badRequest().body("Des membres en double ont été détectés.");
            }
        }

        // Create a list to store equipe_prof_names
        List<String> equipeProfNames = new ArrayList<>();

        // Fetch details of each professor in the equipe and populate equipeProfNames
        for (Long profId : addedStructure.getEquipe_prof_ids()) {
            Professeur prof = professeurRestClient.getProfesseurById(profId);
            if (prof != null) {
                equipeProfNames.add(prof.getPrenom() + " " + prof.getNom());
            }
        }

        // Create a new Structure object
        Structure newStructure = new Structure();
        newStructure.setNom(addedStructure.getNom());
        newStructure.setAcronyme(addedStructure.getAcronyme());
        newStructure.setType(addedStructure.getType());
        newStructure.setBudget(addedStructure.getBudget());
        newStructure.setIdResponsable(responsibleProfessor.getId());
        newStructure.setNomResponsable(responsibleProfessor.getPrenom() + ' ' + responsibleProfessor.getNom());
        newStructure.setEquipe_prof_ids(addedStructure.getEquipe_prof_ids());
        newStructure.setEquipe_prof_names(equipeProfNames);


        // Save or perform necessary actions with the new structure
        structureRepository.save(newStructure);


        return ResponseEntity.ok("Structure créée avec succès !");
    }



    @GetMapping
    public List<Structure> getAllStructures(){
        List<Structure> structures = structureRepository.findAll();
        populateEquipeProfesseurs(structures); // Populate equipeProfesseurs for each structure
        return structures;
    }

    private void populateEquipeProfesseurs(List<Structure> structures) {
        for (Structure structure : structures) {
            List<String> equipeProfNames = new ArrayList<>();
            if (structure.getEquipe_prof_ids() != null) {
                for (Long profId : structure.getEquipe_prof_ids()) {
                    Professeur fetchedProfesseur = professeurRestClient.getProfesseurById(profId);
                    if (fetchedProfesseur != null) {
                        String profName = fetchedProfesseur.getPrenom() + " " + fetchedProfesseur.getNom();
                        equipeProfNames.add(profName);
                    } else {
                        System.out.println("Failed to fetch professeur with ID: " + profId);
                    }
                }
            } else {
                System.out.println("Equipe prof IDs is null for structure with ID: " + structure.getId());
            }
            structure.setEquipe_prof_names(equipeProfNames);
        }
    }


    @GetMapping("{id}")
    public Structure getStructureById(@PathVariable Long id){
        return structureRepository.findById(id)
                .orElseThrow(()->new RuntimeException(String.format("Structure %s not found",id)));
    }

    @DeleteMapping("{id}")
    public void deleteStructure(@PathVariable String id){
        structureRepository.deleteById(Long.valueOf(id));
    }
    private List<Professeur> fetchProfessorsForTeam(Collection<Professeur> professors) {
        List<Professeur> updatedProfessors = new ArrayList<>();

        for (Professeur professor : professors) {
            // Fetch professor details using the professor's ID from each professor object
            Professeur fetchedProfessor = professeurRestClient.getProfesseurById(professor.getId());
            updatedProfessors.add(fetchedProfessor);
        }

        return updatedProfessors;
    }
}
