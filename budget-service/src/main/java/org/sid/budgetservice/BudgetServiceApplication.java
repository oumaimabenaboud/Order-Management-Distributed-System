package org.sid.budgetservice;

import org.sid.budgetservice.entities.Budget;
import org.sid.budgetservice.entities.Rubrique;
import org.sid.budgetservice.repositories.BudgetRepository;
import org.sid.budgetservice.repositories.RubriqueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class BudgetServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BudgetServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner init(BudgetRepository budgetRepository, RubriqueRepository rubriqueRepository) {
		return args -> {
			// Create a sample budget
			Budget structureBudget = Budget.builder().structureId(1L).build();
			structureBudget = budgetRepository.save(structureBudget);

			// Add rubriques for different allocations
			Rubrique academicDepartments = Rubrique.builder().nom("Achat de matières premières").build();
			Rubrique research = Rubrique.builder().nom("Frais de participation aux séminaires, congrés et colloques").build();
			Rubrique infrastructure = Rubrique.builder().nom("Achat de fournitures informatiques").build();
			Rubrique eventsa = Rubrique.builder().nom("Frais de mission à l'étranger des participants").build();
			Rubrique eventsb = Rubrique.builder().nom("Frais de transport du personnel et des étudiants au Maroc").build();
			Rubrique eventsd = Rubrique.builder().nom("Indemnités de déplacement à l'intérieur du Royaume").build();
			Rubrique eventse = Rubrique.builder().nom("Achat de matériel scientifique").build();

			rubriqueRepository.saveAll(List.of(academicDepartments, research, infrastructure, eventsa, eventsb, eventsd, eventse));


			budgetRepository.save(structureBudget);
		};
	}
}

