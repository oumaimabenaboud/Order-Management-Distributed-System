package org.sid.structureservice.web;

import lombok.AllArgsConstructor;
import org.sid.structureservice.entities.DroitAcces;
import org.sid.structureservice.entities.Structure;
import org.sid.structureservice.enums.structurestype;
import org.sid.structureservice.feign.BudgetRestClient;
import org.sid.structureservice.feign.ProfesseurRestClient;
import org.sid.structureservice.model.Professeur;
import org.sid.structureservice.repository.DroitAccesRepository;
import org.sid.structureservice.repository.StructureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.sid.structureservice.enums.structurestype.LabodeRecherche;

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
    private DroitAccesController droitAccesController;

    Logger logger = LoggerFactory.getLogger(StructureRestController.class);

    @Autowired
    public StructureRestController(ProfesseurRestClient professeurRestClient, BudgetRestClient budgetRestClient, DroitAccesController droitAccesController) {
        this.professeurRestClient = professeurRestClient;
        this.budgetRestClient = budgetRestClient;
        this.droitAccesController = droitAccesController;
    }

    @PostMapping
    public ResponseEntity<?> createStructure(@RequestBody Structure addedStructure) {
        // Fetch responsible professor from the professor service
        Professeur responsibleProfessor = professeurRestClient.getProfesseurById(addedStructure.getIdResponsable());

        // Vérifier que le responsable n'est pas membre de l'équipe
        if (addedStructure.getEquipeProfIds().contains(addedStructure.getIdResponsable())) {
            return ResponseEntity.badRequest().body("Le responsable ne peut pas être membre de l'équipe.");
        }

        // Vérifier le nombre de membres pour le type 'EquipedeRecherche'
        if (addedStructure.getType() == structurestype.EquipedeRecherche) {
            if (addedStructure.getEquipeProfIds().size() < 4) {
                return ResponseEntity.badRequest().body("Pour le type 'Equipe de Recherche', il faut au moins 4 membres.");
            }
        }

        // Vérifier s'il y a des membres dupliqués
        Set<Long> uniqueIds = new HashSet<>();
        for (Long profId : addedStructure.getEquipeProfIds()) {
            // Si l'identifiant est déjà présent, c'est un membre en double
            if (!uniqueIds.add(profId)) {
                return ResponseEntity.badRequest().body("Des membres en double ont été détectés.");
            }
        }

        // Create a list to store equipe_prof_names
        List<String> equipeProfNames = new ArrayList<>();

        // Fetch details of each professor in the equipe and populate equipeProfNames
        for (Long profId : addedStructure.getEquipeProfIds()) {
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
        newStructure.setEquipeProfIds(addedStructure.getEquipeProfIds());
        newStructure.setEquipeProfNames(equipeProfNames);


        structureRepository.save(newStructure);

        for (Long profId : addedStructure.getEquipeProfIds()) {
            //logger.error("Professor ID: " + profId +" Structure Id:" +newStructure.getId());
            DroitAcces da = new DroitAcces();
            da.setIdStructure(newStructure.getId());
            da.setIdProfessor(profId);
            da.setDroitAcces(false);
            droitAccesController.createOrUpdateDroitAccess(da);
        }


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
            if (structure.getEquipeProfIds() != null) {
                for (Long profId : structure.getEquipeProfIds()) {
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
            structure.setEquipeProfNames(equipeProfNames);
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
        populateEquipeProfesseurs(structures);// Populate equipeProfesseurs for each structure
        populateEquipeChild(structures);
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
    public List<String> getAllLaboratoiresNames() {
        List<Structure> structures = structureRepository.findByType(LabodeRecherche);
        List<String> laboratoiresNames = new ArrayList<>();

        for (Structure structure : structures) {
            laboratoiresNames.add(structure.getNom());
        }

        return laboratoiresNames;
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStructure(@PathVariable Long id, @RequestBody Structure updatedStructure) {
        // Fetch the existing structure from the database
        Structure existingStructure = structureRepository.getStructureById(id);
        List<String> laboratoiresNames = getAllLaboratoiresNames();

        // If the structure doesn't exist, return 404 Not Found
        if (existingStructure == null) {
            return ResponseEntity.notFound().build();
        }

        // Verify that the responsible professor is not a member of the team
        if (updatedStructure.getEquipeProfIds().contains(updatedStructure.getIdResponsable())) {
            return ResponseEntity.badRequest().body("Le responsable ne peut pas être membre de l'équipe.");
        }

        // Verify the number of members for the type 'EquipedeRecherche'
        if (updatedStructure.getType() == structurestype.EquipedeRecherche) {
            if (updatedStructure.getEquipeProfIds().size() < 4) {
                return ResponseEntity.badRequest().body("Pour le type 'Equipe de Recherche', il faut au moins 4 membres.");
            }
        }

        // Verify if there are any duplicate members
        Set<Long> uniqueIds = new HashSet<>();
        for (Long profId : updatedStructure.getEquipeProfIds()) {
            // If the ID is already present, it's a duplicate member
            if (!uniqueIds.add(profId)) {
                return ResponseEntity.badRequest().body("Des membres en double ont été détectés.");
            }
        }

        if (updatedStructure.getParentLabId() == null && updatedStructure.getParentLabNom()!= null && !updatedStructure.getParentLabNom().isEmpty() &&  !laboratoiresNames.contains(updatedStructure.getParentLabNom())) {
            return ResponseEntity.badRequest().body("Ce laboratoire parent n'existe pas");
        }
        // Check if child equipe is changing lab parents
        if (existingStructure.getParentLabId()!= updatedStructure.getParentLabId()) {

            System.out.println("Child Equip with ID : "+ existingStructure.getId() +" moving from one parent lab to another");
            // Fetch the old parent lab from the database
            Structure oldParentLab = structureRepository.getStructureById(existingStructure.getParentLabId());
            if (oldParentLab != null) {
                System.out.println("Old parent lab found with ID: " + oldParentLab.getId());
                System.out.println("Existing child equipe IDs for old parent lab before removal: " + oldParentLab.getChildEquipesIds());
                List<Long> childEquipesIds = oldParentLab.getChildEquipesIds();
                if (childEquipesIds != null) {
                    childEquipesIds.remove(existingStructure.getId());
                    oldParentLab.setChildEquipesIds(childEquipesIds);
                    System.out.println("Updated child equipe IDs for old parent lab: " + childEquipesIds);
                    structureRepository.save(oldParentLab);
                    System.out.println("Old parent lab updated successfully");
                }
            } else {
                System.out.println("Old parent lab not found");
            }
            if (updatedStructure.getParentLabId() != null) {
                Structure newParentLab = structureRepository.getStructureById(updatedStructure.getParentLabId());
                if (newParentLab != null) {
                    List<Long> childEquipesIds = newParentLab.getChildEquipesIds();
                    if (childEquipesIds == null) {
                        childEquipesIds = new ArrayList<>();
                    }
                    System.out.println("Existing child equipe IDs for new parent lab before adding: " + childEquipesIds);
                    childEquipesIds.add(existingStructure.getId());
                    System.out.println("Updated child equipe IDs for new parent lab: " + childEquipesIds); // Add this line for debugging
                    newParentLab.setChildEquipesIds(childEquipesIds);
                    structureRepository.save(newParentLab);
                }
            }
        }

        // Check if Child Equipes are getting updated
        if (existingStructure.getChildEquipesIds()!= updatedStructure.getChildEquipesIds()) {
            List<Long> oldChildEquipesIds = existingStructure.getChildEquipesIds();
            List<Long> newChildEquipesIds = updatedStructure.getChildEquipesIds();
            System.out.println("Old child equipe IDs: " + oldChildEquipesIds+ "for Lab with Id:"+existingStructure.getId());
            System.out.println("New child equipe IDs: " + newChildEquipesIds+ "for Lab with Id:"+existingStructure.getId());
            // Verify if there are any duplicate equips
            Set<Long> uniqueEquips = new HashSet<>();
            for (Long equipId : newChildEquipesIds) {
                if (!uniqueEquips.add(equipId)) {
                    return ResponseEntity.badRequest().body("Des équipes de recherche affiliées en double ont été détectés.");
                }
            }
            // Remove the parent lab ID from old equipes that are no longer child equipes
            for (Long equipeId : oldChildEquipesIds) {
                Structure removedEquipe = structureRepository.getStructureById(equipeId);
                if (removedEquipe != null) {
                    removedEquipe.setParentLabId(null);// Remove parent lab ID
                    removedEquipe.setParentLabNom(null);// Remove parent lab ID
                    structureRepository.save(removedEquipe); // Save the equipe to update the parent lab ID
                    System.out.println("Parent lab ID removed from old equipe with ID: " + equipeId);
                }
            }

            // Add the parent lab ID for new added equipe children
            for (Long equipeId : newChildEquipesIds) {
                Structure addedEquipe = structureRepository.getStructureById(equipeId);
                if (addedEquipe != null) {
                    //if this equipe already has a parent lab
                    if(addedEquipe.getParentLabId()!=null){
                        Structure oldLabParent = structureRepository.getStructureById(addedEquipe.getParentLabId());
                        System.out.println("For equipe with ID: " + equipeId + "it's oldLab parent's Id is: " + oldLabParent.getId() );
                        List<Long> oldChildEquips = oldLabParent.getChildEquipesIds();
                        System.out.println("it's oldLab parent's child equipes before removing the equipe is: " + oldChildEquips);
                        oldChildEquips.remove(equipeId);
                        System.out.println("it's oldLab parent's child equipes after removing the equipe is: " + oldChildEquips);
                        oldLabParent.setChildEquipesIds(oldChildEquips);
                        structureRepository.save(oldLabParent);
                        System.out.println("Equipe with ID: " + equipeId + "removed from Parent Lab with Id"+ oldLabParent);
                    }
                    addedEquipe.setParentLabId(existingStructure.getId());
                    addedEquipe.setParentLabNom(updatedStructure.getNom());// Set parent lab ID to the ID of the new parent lab
                    structureRepository.save(addedEquipe); // Save the equipe to update the parent lab ID
                    System.out.println("Parent lab ID added to equipe with ID: " + equipeId);
                }
            }
        }

        // Update the existing structure with the new values
        if (updatedStructure.getNom() != null  && !updatedStructure.getNom().isEmpty()) {
            existingStructure.setNom(updatedStructure.getNom());
        } else {
            return ResponseEntity.badRequest().body("Le champ 'nom' ne peut pas être vide.");
        }

        if (updatedStructure.getAcronyme() != null  && !updatedStructure.getAcronyme().isEmpty()) {
            existingStructure.setAcronyme(updatedStructure.getAcronyme());
        } else {
            return ResponseEntity.badRequest().body("Le champ 'acronyme' ne peut pas être vide.");
        }

        if (updatedStructure.getType() != null) {
            existingStructure.setType(updatedStructure.getType());
        } else {
            return ResponseEntity.badRequest().body("Le champ 'type' ne peut pas être vide.");
        }


        if (updatedStructure.getNomResponsable() != null  && !updatedStructure.getNomResponsable().isEmpty()) {
            existingStructure.setNomResponsable(updatedStructure.getNomResponsable());
        } else {
            return ResponseEntity.badRequest().body("Le champ 'Nom du Responsable' ne peut pas être vide.");
        }

        List<String> equipeProfNames = new ArrayList<>();

        // Fetch details of each professor in the equipe and populate equipeProfNames
        for (Long profId : updatedStructure.getEquipeProfIds()) {
            Professeur prof = professeurRestClient.getProfesseurById(profId);
            if (prof != null) {
                equipeProfNames.add(prof.getPrenom() + " " + prof.getNom());
            }
        }

        existingStructure.setIdResponsable(updatedStructure.getIdResponsable());
        existingStructure.setBudgetAnnuel(updatedStructure.getBudgetAnnuel());
        existingStructure.setEquipeProfIds(updatedStructure.getEquipeProfIds());
        existingStructure.setEquipeProfNames(equipeProfNames);
        existingStructure.setParentLabId(updatedStructure.getParentLabId());
        existingStructure.setParentLabNom(updatedStructure.getParentLabNom());
        existingStructure.setChildEquipesIds(updatedStructure.getChildEquipesIds());
        existingStructure.setChildEquipesNoms(updatedStructure.getChildEquipesNoms());
        // Save the updated structure to the database
        structureRepository.save(existingStructure);

        droitAccesController.deleteStructureById(existingStructure.getId());

        for (Long profId : existingStructure.getEquipeProfIds()) {
            logger.error("Professor ID: " + profId +" structure Id:" +existingStructure.getId());
            DroitAcces da = new DroitAcces();
            da.setIdStructure(existingStructure.getId());
            da.setIdProfessor(profId);
            da.setDroitAcces(false);
            droitAccesController.createOrUpdateDroitAccess(da);
        }


        return ResponseEntity.ok("Structure mise à jour avec succès !");
    }

    @GetMapping("/byResponsable/{professorId}")
    public List<Structure> getStructuresByResponsable(@PathVariable Long professorId) {
        List<Structure> structures = structureRepository.findByIdResponsable(professorId);
        populateEquipeProfesseurs(structures);// Populate equipeProfesseurs for each structure
        populateEquipeChild(structures);
        return structures;
    }

    @GetMapping("/byEquipeMember/{professorId}")
    public List<Structure> getStructuresByEquipeMember(@PathVariable Long professorId) {
        List<Structure> structures = structureRepository.findByEquipeProfIdsContains(professorId);
        populateEquipeProfesseurs(structures);// Populate equipeProfesseurs for each structure
        populateEquipeChild(structures);
        return structures;
    }

    @GetMapping("/search")
    public List<Structure> searchStructures(@RequestParam(required = false) String searchTerm) {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            List<Structure> structures = structureRepository.findByNomContainingIgnoreCase(searchTerm);
            populateEquipeProfesseurs(structures);// Populate equipeProfesseurs for each structure
            populateEquipeChild(structures);
            return structures;
        } else {
            List<Structure> structures = structureRepository.findAll();
            populateEquipeProfesseurs(structures);// Populate equipeProfesseurs for each structure
            populateEquipeChild(structures);
            return structures;
        }
    }

}
