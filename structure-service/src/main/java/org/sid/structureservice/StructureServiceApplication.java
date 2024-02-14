package org.sid.structureservice;

import org.sid.structureservice.entities.Structure;
import org.sid.structureservice.enums.structurestype;
import org.sid.structureservice.feign.BudgetRestClient;
import org.sid.structureservice.feign.ProfesseurRestClient;
import org.sid.structureservice.repository.StructureRepository;
import org.sid.structureservice.repository.ResponsableStructureRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class StructureServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StructureServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner start(StructureRepository structureRepository, ResponsableStructureRepository responsableStructureRepository, ProfesseurRestClient professeurRestClient, BudgetRestClient budgetRestClient) {
		return args -> {
			// Fetch professor IDs from your service or database
			Long professorId1 = 1L;
			Long professorId2 = 2L;

			// Add example structures
			Structure structure1 = addStructure(structureRepository, "Labo MIAAD", "LabodeRecherche", 10000.0, professorId1);
			Structure structure2 = addStructure(structureRepository, "Equipe AI", "EquipedeRecherche", 15000.0, professorId2);
			Structure structure3 = addStructure(structureRepository, "Projet FSM", "ProjetdeRecherche", 20000.0, professorId1);

			// Additional structures can be added as needed
		};
	}

	private Structure addStructure(StructureRepository structureRepository, String name, String type, double budget, Long responsibleId) {
		// Create the structure
		Structure structure = Structure.builder()
				.nom(name)
				.acronyme(name.substring(0, 3)) // Assuming acronym is the first three letters of the name
				.type(structurestype.valueOf(type))
				.budget(budget)
				.responsibleId(responsibleId)
				.build();

		// Save the structure
		return structureRepository.save(structure);
	}
}