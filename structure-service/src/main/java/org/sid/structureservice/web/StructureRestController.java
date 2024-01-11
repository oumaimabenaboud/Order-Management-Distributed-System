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
    private StructureRepository structureRepository;
    private ResponsableStructureRepository responsableStructureRepository;
    @Autowired
    private ProfesseurRestClient professeurRestClient;
    @Autowired
    private BudgetRestClient budgetRestClient;

        @Autowired
        public StructureRestController(ProfesseurRestClient professeurRestClient, BudgetRestClient budgetRestClient) {
            this.professeurRestClient = professeurRestClient;
            this.budgetRestClient = budgetRestClient;
        }

        @PostMapping("/structures")
        public Structure createStructure(@RequestParam Structure addedStructure) {

            // Fetch responsible professor from the professor service
            Professeur responsibleProfessor = professeurRestClient.getProfesseurById(addedStructure.getRespoID());

            // Fetch other professors for the team
            Collection<Professeur> professors = fetchProfessorsForTeam(addedStructure.getProfesseurs());

            // Create a new Structure object
            Structure newStructure = new Structure();
            newStructure.setNom(addedStructure.getNom());
            newStructure.setAcronyme(addedStructure.getAcronyme());
            newStructure.setType(addedStructure.getType()); // Assuming structurestype is an enum

            // Assign responsible professor to the structure
            // Here, 'responsibleStructure' is assumed to be an instance of ResponsableStructure
            ResponsableStructure responsibleStructure = new ResponsableStructure();
            responsibleStructure.setProfesseur(responsibleProfessor);
            newStructure.setResponsableStructure(responsibleStructure);

            // Set other professors for the team
            newStructure.setProfesseurs(professors);

            // Save or perform necessary actions with the new structure
            structureRepository.save(newStructure);

            return newStructure;
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
