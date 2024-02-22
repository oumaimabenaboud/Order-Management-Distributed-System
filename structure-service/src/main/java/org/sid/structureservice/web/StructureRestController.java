package org.sid.structureservice.web;

import lombok.AllArgsConstructor;
import org.sid.structureservice.entities.Structure;
import org.sid.structureservice.enums.structurestype;
import org.sid.structureservice.feign.BudgetRestClient;
import org.sid.structureservice.feign.ProfesseurRestClient;
import org.sid.structureservice.model.Professeur;
import org.sid.structureservice.repository.StructureRepository;
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
        newStructure.setBudgetAnnuel(addedStructure.getBudgetAnnuel());
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
        populateEquipeProfesseurs(structures);// Populate equipeProfesseurs for each structure
        populateEquipeChild(structures);
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
    private void populateEquipeChild(List<Structure> structures) {
        for (Structure structure : structures) {
            List<String> childEquipesNoms = new ArrayList<>();
            if (structure.getChildEquipesIds() != null) {
                for (Long equipeId : structure.getChildEquipesIds()) {
                    Structure fetchedStructure = structureRepository.getStructureById(equipeId);
                    if (fetchedStructure != null) {
                        String equipeName = fetchedStructure.getNom();
                        childEquipesNoms.add(equipeName);
                    } else {
                        System.out.println("Failed to fetch structure with ID: " + equipeId);
                    }
                }
            } else {
                System.out.println("Equipes affiliées IDs is null for Laboratoire with ID: " + structure.getId());
            }
            structure.setChildEquipesNoms(childEquipesNoms);
        }
    }
    @GetMapping("/byType/{type}")
    public List<Structure> getStructuresByType(@PathVariable structurestype type) {
        List<Structure> structures = structureRepository.findByType(type);
        populateEquipeProfesseurs(structures); // Populate equipeProfesseurs for each structure
        return structures;
    }

    @GetMapping("{id}")
    public Structure getStructureById(@PathVariable Long id) {
        // Fetch the structure by ID
        Structure structure = structureRepository.getStructureById(id);

        // Populate child equipe names for the single structure
        populateEquipeChild(List.of(structure));

        return structure;
    }


    @DeleteMapping("{id}")
    public void deleteStructure(@PathVariable String id){
        structureRepository.deleteById(Long.valueOf(id));
    }
    /*private List<Professeur> fetchProfessorsForTeam(Collection<Professeur> professors) {
        List<Professeur> updatedProfessors = new ArrayList<>();

        for (Professeur professor : professors) {
            // Fetch professor details using the professor's ID from each professor object
            Professeur fetchedProfessor = professeurRestClient.getProfesseurById(professor.getId());
            updatedProfessors.add(fetchedProfessor);
        }

        return updatedProfessors;
    }*/

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStructure(@PathVariable Long id, @RequestBody Structure updatedStructure) {
        // Fetch the existing structure from the database
        Structure existingStructure = structureRepository.getStructureById(id);

        // If the structure doesn't exist, return 404 Not Found
        if (existingStructure == null) {
            return ResponseEntity.notFound().build();
        }

        // Verify that the responsible professor is not a member of the team
        if (updatedStructure.getEquipe_prof_ids().contains(updatedStructure.getIdResponsable())) {
            return ResponseEntity.badRequest().body("Le responsable ne peut pas être membre de l'équipe.");
        }

        // Verify the number of members for the type 'EquipedeRecherche'
        if (updatedStructure.getType() == structurestype.EquipedeRecherche) {
            if (updatedStructure.getEquipe_prof_ids().size() < 4) {
                return ResponseEntity.badRequest().body("Pour le type 'Equipe de Recherche', il faut au moins 4 membres.");
            }
        }

        // Verify if there are any duplicate members
        Set<Long> uniqueIds = new HashSet<>();
        for (Long profId : updatedStructure.getEquipe_prof_ids()) {
            // If the ID is already present, it's a duplicate member
            if (!uniqueIds.add(profId)) {
                return ResponseEntity.badRequest().body("Des membres en double ont été détectés.");
            }
        }
        // Check if this structure is moving from one parent lab to another
        if (existingStructure.getParentLabId()!= updatedStructure.getParentLabId()) {
            System.out.println("Structure is moving from one parent lab to another");
            // Fetch the old parent lab from the database
            Structure oldParentLab = structureRepository.getStructureById(existingStructure.getParentLabId());
            if (oldParentLab != null) {
                System.out.println("Old parent lab found with ID: " + oldParentLab.getId());
                // Log the existing child equipe IDs of the old parent lab before removing the structure
                System.out.println("Existing child equipe IDs for old parent lab before removal: " + oldParentLab.getChildEquipesIds());

                // Remove this structure from the child equipe list of the old parent lab
                List<Long> childEquipesIds = oldParentLab.getChildEquipesIds();
                if (childEquipesIds != null) {
                    childEquipesIds.remove(existingStructure.getId());
                    oldParentLab.setChildEquipesIds(childEquipesIds);

                    // Log the updated child equipe IDs of the old parent lab after removing the structure
                    System.out.println("Updated child equipe IDs for old parent lab: " + childEquipesIds);

                    // Save the old parent lab with the updated child equipe list
                    structureRepository.save(oldParentLab);
                    System.out.println("Old parent lab updated successfully");
                }
            } else {
                System.out.println("Old parent lab not found");
            }
        }
        // Determine the equipes being removed and added
        List<Long> oldChildEquipesIds = existingStructure.getChildEquipesIds();
        List<Long> newChildEquipesIds = updatedStructure.getChildEquipesIds();
        System.out.println("Old child equipe IDs: " + oldChildEquipesIds);
        System.out.println("New child equipe IDs: " + newChildEquipesIds);

        // Determine the equipes being removed from the child equipe list
        List<Long> removedChildEquipes = oldChildEquipesIds.stream()
                .filter(oldId -> !newChildEquipesIds.contains(oldId))
                .collect(Collectors.toList());
        System.out.println("Removed child equipe IDs: " + removedChildEquipes);

        // Determine the equipes being added to the child equipe list
        List<Long> addedChildEquipes = newChildEquipesIds.stream()
                .filter(newId -> !oldChildEquipesIds.contains(newId))
                .collect(Collectors.toList());
        System.out.println("Added child equipe IDs: " + addedChildEquipes);

        // Remove the parent lab ID from equipes that are no longer child equipes
        for (Long equipeId : removedChildEquipes) {
            Structure removedEquipe = structureRepository.getStructureById(equipeId);
            if (removedEquipe != null) {
                removedEquipe.setParentLabId(null);// Remove parent lab ID
                removedEquipe.setParentLabNom(null);// Remove parent lab ID
                structureRepository.save(removedEquipe); // Save the equipe to update the parent lab ID
                System.out.println("Parent lab ID removed from equipe with ID: " + equipeId);
            }
        }

        // Add the parent lab ID for new added equipe children
        for (Long equipeId : addedChildEquipes) {
            Structure addedEquipe = structureRepository.getStructureById(equipeId);
            if (addedEquipe != null) {
                addedEquipe.setParentLabId(updatedStructure.getId());
                addedEquipe.setParentLabNom(updatedStructure.getNom());// Set parent lab ID to the ID of the new parent lab
                structureRepository.save(addedEquipe); // Save the equipe to update the parent lab ID
                System.out.println("Parent lab ID added to equipe with ID: " + equipeId);
            }
        }


        // Update the existing structure with the new values
        existingStructure.setNom(updatedStructure.getNom());
        existingStructure.setAcronyme(updatedStructure.getAcronyme());
        existingStructure.setType(updatedStructure.getType());
        existingStructure.setBudgetAnnuel(updatedStructure.getBudgetAnnuel());
        existingStructure.setIdResponsable(updatedStructure.getIdResponsable());
        existingStructure.setNomResponsable(updatedStructure.getNomResponsable());
        existingStructure.setEquipe_prof_ids(updatedStructure.getEquipe_prof_ids());
        existingStructure.setEquipe_prof_names(updatedStructure.getEquipe_prof_names());
        existingStructure.setParentLabId(updatedStructure.getParentLabId());
        existingStructure.setParentLabNom(updatedStructure.getParentLabNom());
        existingStructure.setChildEquipesIds(updatedStructure.getChildEquipesIds());
        existingStructure.setChildEquipesNoms(updatedStructure.getChildEquipesNoms());


        // Check if this structure is a child equipe and add it to the parent lab
        if (updatedStructure.getParentLabId() != null) {
            // Fetch the new parent lab from the database
            Structure newParentLab = structureRepository.getStructureById(updatedStructure.getParentLabId());
            if (newParentLab != null) {
                // Add this structure as a child equipe to the new parent lab
                List<Long> childEquipesIds = newParentLab.getChildEquipesIds();
                if (childEquipesIds == null) {
                    childEquipesIds = new ArrayList<>();
                }
                // Log the existing child equipe IDs before adding the new one
                System.out.println("Existing child equipe IDs for new parent lab before adding: " + childEquipesIds);
                childEquipesIds.add(existingStructure.getId());
                System.out.println("Updated child equipe IDs for new parent lab: " + childEquipesIds); // Add this line for debugging
                newParentLab.setChildEquipesIds(childEquipesIds);
                // Save the new parent lab with the updated child equipe list
                structureRepository.save(newParentLab);
            }
        }
        // Save the updated structure to the database
        structureRepository.save(existingStructure);

        return ResponseEntity.ok("Structure mise à jour avec succès !");
    }

}
