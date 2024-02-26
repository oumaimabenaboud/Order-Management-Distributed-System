package org.sid.structureservice;

import jakarta.transaction.Transactional;
import org.sid.structureservice.entities.DroitAcces;
import org.sid.structureservice.entities.Structure;
import org.sid.structureservice.enums.structurestype;
import org.sid.structureservice.feign.ProfesseurRestClient;
import org.sid.structureservice.model.Professeur;
import org.sid.structureservice.repository.DroitAccesRepository;
import org.sid.structureservice.repository.StructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableFeignClients
public class StructureServiceApplication {
	private ProfesseurRestClient professeurRestClient;
	private StructureRepository structureRepository;;
	private DroitAccesRepository droitAccesRepository;

	@Autowired
	public StructureServiceApplication(ProfesseurRestClient professeurRestClient, StructureRepository structureRepository, DroitAccesRepository droitAccesRepository) {
		this.professeurRestClient = professeurRestClient;
		this.structureRepository = structureRepository;
		this.droitAccesRepository = droitAccesRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(StructureServiceApplication.class, args);
	}

	@Bean
	@Transactional
	CommandLineRunner start(StructureRepository structureRepository, ProfesseurRestClient professeurRestClient, DroitAccesRepository droitAccesRepository) {
		return args -> {
			// Fetch professor IDs from your service or database
			Long professorId1 = 2L;
			Long professorId2 = 3L;
			Long professorId3 = 4L;
			Long professorId4 = 5L;
			Long professorId5 = 6L;
			// Add example structures
			Structure structure1 = addStructure(structureRepository, "Laboratoire d'IA", "LabodeRecherche", 10000.0, professorId1, "My.Ali Bekri", List.of(professorId2, professorId3, professorId4), professeurRestClient,null,null,List.of(2L));
			Structure structure2 = addStructure(structureRepository, "Equipe Robotique", "EquipedeRecherche", 15000.0, professorId2, "Ali Oubelkacem", List.of(professorId1, professorId3,professorId4,professorId5), professeurRestClient,1L,"Laboratoire d'IA",null );
			Structure structure3 = addStructure(structureRepository, "Projet NLP", "ProjetdeRecherche", 20000.0, professorId1, "My.Ali Bekri", List.of(professorId2, professorId5), professeurRestClient,null,null,null);
			Structure structure4 = addStructure(structureRepository, "Laboratoire de BioInformatique", "LabodeRecherche", 10000.0, professorId4, "Mehdi Alaoui Ismaili", List.of(professorId1, professorId2, professorId3), professeurRestClient,null,null,null);
			Structure structure5 = addStructure(structureRepository, "Equipe Smart Agriculture", "EquipedeRecherche", 60000.0, professorId1, "My.Ali Bekri", List.of(professorId2, professorId3,professorId4,professorId5), professeurRestClient,null,null,null);

			// Fetch all structures
			List<Structure> allStructures = structureRepository.findAll();
			populateEquipeProfesseurs(allStructures, professeurRestClient);
			populateEquipeChild(allStructures,structureRepository);
			addExampleDroitAccess(droitAccesRepository);
		};
	}
	private void addExampleDroitAccess(DroitAccesRepository droitAccesRepository) {
		// Example DroitAccess instances
		DroitAcces droitAccess1 = DroitAcces.builder()
				.idProfessor(2L)
				.idStructure(1L)
				.droitAcces(true)
				.build();

		DroitAcces droitAccess2 = DroitAcces.builder()
				.idProfessor(3L)
				.idStructure(2L)
				.droitAcces(false)
				.build();

		// Save example DroitAccess instances
		droitAccesRepository.saveAll(List.of(droitAccess1, droitAccess2));
	}

	private Structure addStructure(StructureRepository structureRepository, String name, String type, double budget, Long responsibleId, String nomResponsable, List<Long> equipeProfIds, ProfesseurRestClient professeurRestClient, Long parentLabId ,String parentLabNom, List<Long> childEquipesIds) {
		// Fetch professor names using IDs
		List<String> equipeProfNames = new ArrayList<>();
		for (Long id : equipeProfIds) {
			Professeur professor = professeurRestClient.getProfesseurById(id);
			if (professor != null) {
				equipeProfNames.add(professor.getPrenom() + " " + professor.getNom());
			}
		}

		// Create the structure without child equipe names
		Structure structure = Structure.builder()
				.nom(name)
				.acronyme(name.substring(0, 3)) // Assuming acronym is the first three letters of the name
				.type(structurestype.valueOf(type))
				.budgetAnnuel(budget)
				.idResponsable(responsibleId)
				.nomResponsable(nomResponsable)
				.equipeProfIds(equipeProfIds) // Set the team member IDs
				.equipeProfNames(equipeProfNames) // Set the team member names
				.parentLabId(parentLabId)
				.parentLabNom(parentLabNom)
				.childEquipesIds(childEquipesIds)
				.build();

		// Save the parent structure first
		Structure savedStructure = structureRepository.save(structure);

		// Fetch and add child equipe names using IDs after saving the parent structure
		List<String> childEquipesNoms = new ArrayList<>();
		if (childEquipesIds != null) {
			for (Long equipeId : childEquipesIds) {
				Structure fetchedStructure = structureRepository.getStructureById(equipeId);
				if (fetchedStructure != null) {
					String equipeName = fetchedStructure.getNom();
					childEquipesNoms.add(equipeName);
				} else {
					System.out.println("Failed to fetch structure with ID: " + equipeId);
				}
			}
		}

		// Update the parent structure with child equipe names and save again
		savedStructure.setChildEquipesNoms(childEquipesNoms);
		return structureRepository.save(savedStructure);
	}


	private void populateEquipeProfesseurs(List<Structure> structures, ProfesseurRestClient professeurRestClient) {
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
	private void populateEquipeChild(List<Structure> structures, StructureRepository structureRepository) {
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
			System.out.println("Structure with ID " + structure.getId() + " has child equipes: " + childEquipesNoms);
		}
	}

}
