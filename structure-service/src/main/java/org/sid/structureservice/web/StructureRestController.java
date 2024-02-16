package org.sid.structureservice.web;

import lombok.AllArgsConstructor;
import org.sid.structureservice.entities.Structure;
import org.sid.structureservice.entities.ResponsableStructure;
import org.sid.structureservice.feign.BudgetRestClient;
import org.sid.structureservice.feign.ProfesseurRestClient;
import org.sid.structureservice.model.Professeur;
import org.sid.structureservice.repository.StructureRepository;
import org.sid.structureservice.repository.ResponsableStructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        public Structure createStructure(@RequestBody Structure addedStructure) {

            // Fetch responsible professor from the professor service
            Professeur responsibleProfessor = professeurRestClient.getProfesseurById(addedStructure.getIdResponsable());


            // Create a new Structure object
            Structure newStructure = new Structure();
            newStructure.setNom(addedStructure.getNom());
            newStructure.setAcronyme(addedStructure.getAcronyme());
            newStructure.setType(addedStructure.getType()); // Assuming structurestype is an enum
            newStructure.setIdResponsable(responsibleProfessor.getId());
            newStructure.setNomResponsable(responsibleProfessor.getPrenom()+' '+responsibleProfessor.getNom());

            // Save or perform necessary actions with the new structure
            structureRepository.save(newStructure);

            return newStructure;
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
