package org.sid.structureservice;

import jakarta.transaction.Transactional;
import org.sid.structureservice.entities.Structure;
import org.sid.structureservice.enums.structurestype;
import org.sid.structureservice.feign.BudgetRestClient;
import org.sid.structureservice.feign.ProfesseurRestClient;
import org.sid.structureservice.model.Professeur;
import org.sid.structureservice.repository.StructureRepository;
import org.sid.structureservice.repository.ResponsableStructureRepository;
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

	@Autowired
	public StructureServiceApplication(ProfesseurRestClient professeurRestClient) {
		this.professeurRestClient = professeurRestClient;
	}

	public static void main(String[] args) {
		SpringApplication.run(StructureServiceApplication.class, args);
	}

	@Bean
	@Transactional
	CommandLineRunner start(StructureRepository structureRepository, ProfesseurRestClient professeurRestClient) {
		return args -> {
			// Fetch professor IDs from your service or database
			Long professorId1 = 1L;
			Long professorId2 = 2L;
			Long professorId3 = 3L;
			Long professorId4 = 4L;

			// Add example structures
			Structure structure1 = addStructure(structureRepository, "Labo MIAAD", "LabodeRecherche", 10000.0, professorId1, "My.Ali Bekri", List.of(professorId2, professorId3, professorId4), professeurRestClient,null);
			Structure structure2 = addStructure(structureRepository, "Equipe AI", "EquipedeRecherche", 15000.0, professorId2, "Ali Oubelkacem", List.of(professorId1, professorId3), professeurRestClient,1L);
			Structure structure3 = addStructure(structureRepository, "Projet FSM", "ProjetdeRecherche", 20000.0, professorId1, "My.Ali Bekri", List.of(professorId2, professorId4), professeurRestClient,null);

			// Fetch all structures
			List<Structure> allStructures = structureRepository.findAll();
			populateEquipeProfesseurs(allStructures, professeurRestClient);
		};
	}

	private Structure addStructure(StructureRepository structureRepository, String name, String type, double budget, Long responsibleId, String nomResponsable, List<Long> equipeProfIds, ProfesseurRestClient professeurRestClient, Long parentLabId) {
		// Fetch professor names using IDs
		List<String> equipeProfNames = new ArrayList<>();
		for (Long id : equipeProfIds) {
			Professeur professor = professeurRestClient.getProfesseurById(id);
			if (professor != null) {
				equipeProfNames.add(professor.getPrenom() + " " + professor.getNom());
			}
		}

		// Create the structure
		Structure structure = Structure.builder()
				.nom(name)
				.acronyme(name.substring(0, 3)) // Assuming acronym is the first three letters of the name
				.type(structurestype.valueOf(type))
				.budget(budget)
				.idResponsable(responsibleId)
				.nomResponsable(nomResponsable)
				.equipe_prof_ids(equipeProfIds) // Set the team member IDs
				.equipe_prof_names(equipeProfNames) // Set the team member names
				.parentLabId(parentLabId)
				.build();

		// Save the structure
		return structureRepository.save(structure);
	}

	private void populateEquipeProfesseurs(List<Structure> structures, ProfesseurRestClient professeurRestClient) {
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
}
