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
			Budget universityBudget = Budget.builder().totalAmount(1000000.0).build();
			universityBudget = budgetRepository.save(universityBudget);

			// Add rubriques for different allocations
			Rubrique academicDepartments = Rubrique.builder().nom("Départements académiques").allocatedAmount(300000.0).budget_id(universityBudget.getId()).build();
			Rubrique research = Rubrique.builder().nom("Recherche").allocatedAmount(200000.0).budget_id(universityBudget.getId()).build();
			Rubrique infrastructure = Rubrique.builder().nom("Infrastructure").allocatedAmount(250000.0).budget_id(universityBudget.getId()).build();
			Rubrique events = Rubrique.builder().nom("Événements").allocatedAmount(250000.0).budget_id(universityBudget.getId()).build();

			rubriqueRepository.saveAll(List.of(academicDepartments, research, infrastructure, events));

			// Update total amount in the university budget
			universityBudget.setRubriques(List.of(academicDepartments, research, infrastructure, events));
			double totalAmount = universityBudget.getRubriques().stream().mapToDouble(Rubrique::getAllocatedAmount).sum();
			universityBudget.setTotalAmount(totalAmount);
			budgetRepository.save(universityBudget);
		};
	}
}

