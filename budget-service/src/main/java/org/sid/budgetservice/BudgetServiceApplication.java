package org.sid.budgetservice;

import org.sid.budgetservice.entities.Budget;
import org.sid.budgetservice.entities.Rubrique;
import org.sid.budgetservice.entities.RubriqueAllocation;
import org.sid.budgetservice.repositories.BudgetRepository;
import org.sid.budgetservice.repositories.RubriqueAllocationRepository;
import org.sid.budgetservice.repositories.RubriqueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class BudgetServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BudgetServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner init(BudgetRepository budgetRepository, RubriqueRepository rubriqueRepository, RubriqueAllocationRepository rubriqueAllocationRepository) {
		return args -> {
			// Create a new budget for the year 2024
			Budget budget2024 = Budget.builder()
					.structureId(1L)
					.budgetYear(2024)
					.totalAlloue(10000)
					.totalRestant(10000) // Assuming totalAlloue and totalRestant are initially equal
					.build();
			budgetRepository.save(budget2024);
			// Create a new budget for the year 2024
			Budget budget2023 = Budget.builder()
					.structureId(1L)
					.budgetYear(2023)
					.totalAlloue(5000)
					.totalRestant(5000) // Assuming totalAlloue and totalRestant are initially equal
					.build();
			budgetRepository.save(budget2023);

			// Add rubriques for different allocations
			List<Rubrique> rubriques = List.of(
					Rubrique.builder().nom("Achat de matières premières").build(),
					Rubrique.builder().nom("Frais de participation aux séminaires, congrés et colloques").build(),
					Rubrique.builder().nom("Achat de matériel informatique").build(),
					Rubrique.builder().nom("Frais de mission à l'étranger des participants").build(),
					Rubrique.builder().nom("Frais de transport du personnel et des étudiants au Maroc").build(),
					Rubrique.builder().nom("Indemnités de déplacement à l'intérieur du Royaume").build(),
					Rubrique.builder().nom("Achat de matériel scientifique").build()
			);
			rubriqueRepository.saveAll(rubriques);

			List<Double> allocationAmounts = List.of(1000.0, 500.0, 4000.0,600.0,500.0,400.0,3000.0); // Corrected syntax
			List<Double> allocationAmounts2023 = List.of(500.0, 500.0, 2000.0,600.0,250.0,400.0,750.0); // Corrected syntax

			// Save RubriqueAllocations
			List<RubriqueAllocation> rubriqueAllocations1 = new ArrayList<>();
			int i = 0; // Counter for allocationAmounts
			for (Rubrique rubrique : rubriques) {
				Double allocationAmount = allocationAmounts.get(i);
				RubriqueAllocation allocation = RubriqueAllocation.builder()
						.rubriqueId(rubrique.getId())
						.budgetId(budget2024.getId())
						.rubriqueName(rubrique.getNom())
						.montantAlloue(allocationAmount)
						.montantRestant(allocationAmount)
						.build();
				rubriqueAllocations1.add(allocation);
				i++; // Move to the next allocationAmount
			}
			// Save RubriqueAllocations
			List<RubriqueAllocation> rubriqueAllocations2 = new ArrayList<>();
			int b = 0; // Counter for allocationAmounts
			for (Rubrique rubrique : rubriques) {
				Double allocationAmount = allocationAmounts2023.get(b);
				RubriqueAllocation allocation = RubriqueAllocation.builder()
						.rubriqueId(rubrique.getId())
						.budgetId(budget2023.getId())
						.rubriqueName(rubrique.getNom())
						.montantAlloue(allocationAmount)
						.montantRestant(allocationAmount)
						.build();
				rubriqueAllocations2.add(allocation);
				b++; // Move to the next allocationAmount
			}
			rubriqueAllocationRepository.saveAll(rubriqueAllocations1);
			budget2024.setRubriqueAllocations(rubriqueAllocations1);
			budgetRepository.save(budget2024);

			rubriqueAllocationRepository.saveAll(rubriqueAllocations2);
			budget2023.setRubriqueAllocations(rubriqueAllocations2);
			budgetRepository.save(budget2023);
		};
	}



}


