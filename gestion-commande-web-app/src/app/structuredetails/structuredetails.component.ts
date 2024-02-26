import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ProfesseurService} from "../services/professeur.service";
import {Professeur} from "../model/professeur.model";
import { Clipboard } from '@angular/cdk/clipboard';
import { MatSnackBar } from '@angular/material/snack-bar';
import {StructuresService} from "../services/structures.service";
import {ActivatedRoute, Router} from '@angular/router';
import {PlatformLocation} from "@angular/common";
import {Structure} from "../model/structure.model";
import {BudgetService} from "../services/budget.service";
import {Budget} from "../model/budget.model";
import {RubriqueAllocation} from "../model/rubriqueAllocation.model";

@Component({
  selector: 'app-structuredetails',
  templateUrl: './structuredetails.component.html',
  styleUrl: './structuredetails.component.scss'
})
export class StructuredetailsComponent implements OnInit{


  profs: any[] = [];
  status = true;
  enabled: boolean = false; // Define the enabled property
  structuredetail : any;
  listBudgetsStructure:any;
  structureId !:number;
  userId: number | null = null;
  professeurConnecté : Professeur | undefined;
  professeurRespo : Professeur | undefined;
  loading: boolean = true;
  listrubriques: any[] = [];
  rubriqueAllocations: any[] = [];
  rubriqueAllocationForm!: FormGroup;
  isEditMode: boolean = false;
  listYears: number[] =[];
  selectedYear: number | null = null;
  isNewCommandeFormOpen: boolean = false;
  public newCommandeForm! : FormGroup;
  isDetailsFormOpen: boolean = false;
  selectedBudget:any;

  constructor(
    private structureService: StructuresService,
    private router : Router,
    private route :ActivatedRoute,
    private budgetService: BudgetService,
    private profService: ProfesseurService,
    private snackBar: MatSnackBar,
    private platformLocation: PlatformLocation,
    private formBuilder: FormBuilder) {
    this.structureId=route.snapshot.params['structureId']

  }

  addToggle() {
    this.status = !this.status;
  }

  ngOnInit(): void {
    if (this.isBrowser()) {
      const id = sessionStorage.getItem('id');
      this.userId = id ? parseInt(id, 10) : null;

      if (this.userId) {
        this.profService.getProfessor(this.userId).subscribe(
          (professeurConnecté: Professeur) => {
            this.professeurConnecté = professeurConnecté;
          },
          (error) => {
            console.error('Error fetching professor:', error);
          }
        );
      } else {
        console.error('User ID not found in sessionStorage');
      }
    }

    this.budgetService.getAllRubriques().subscribe(
      (data)=>{
        this.listrubriques = data;
      },
      (error)=> console.error(error)
    );

    this.budgetService.getBudgetByStructureId(this.structureId).subscribe(
      (budget: any) => {
        this.listBudgetsStructure = budget;
        this.repartitionBudgetForm();
        this.listBudgetsStructure.forEach((budget: any) => {
          this.listYears.push(budget.budgetYear);
          this.listYears.sort((a, b) => a - b);
          if (this.listYears.length > 0) {
            this.selectedYear = this.listYears[this.listYears.length - 1];
          }
        });
        },
      (err) => {
        console.log(err);
      }
    );


    this.structureService.getStructureById(this.structureId).subscribe({
      next: (structuredetail) => {
        this.structuredetail = structuredetail;
        // @ts-ignore
        this.structuredetail['typeAsString'] = this.convertStructureTypes(this.structuredetail);
        this.profService.getProfessor(this.structuredetail.idResponsable).subscribe({
          next: professeurRespo => this.professeurRespo = professeurRespo,
          error: err => console.log(err)
        });
        this.loading = false; // Data received, loading is complete

        // Check if equipeProfIds is not null or undefined before iterating over it
        if (this.structuredetail.equipeProfIds) {
          this.structuredetail.equipeProfIds.forEach((id: number) => {
            this.profService.getProfessor(id).subscribe({
              next: (prof) => {
                this.profs.push(prof);
              },
              error: (err) => console.error(err)
            });
          });
        }
      },
      error: (err) => {
        console.log(err);
        this.loading = false;
      },
    });
  }

  onYearChange(year: number | null) {
    if (year !== null) {
      // Store the selected year in the selectedYear property
      this.selectedYear = year;
      console.log('Selected year:', year);
      this.clearRubriqueAllocationsFormArray();
      this.repartitionBudgetForm();
   } else {
      // Handle the case when the selected year is null
      console.error('Selected year is null.');
    }
  }

  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
  }

  isBrowser(): boolean {
    return typeof window !== 'undefined' && this.platformLocation !== null;
  }

  logout() {
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }
  convertStructureTypes(structure: Structure): string {
    let p: string;
    switch (structure.type.toString()) {
      case 'LabodeRecherche':
        p = 'Laboratoire de Recherche';
        break;
      case 'EquipedeRecherche':
        p = 'Equipe de Recherche';
        break;
      case 'ProjetdeRecherche':
        p = 'Projet de Recherche';
        break;
      case 'Département':
        p = 'Département';
        break;
      default:
        p = 'Unknown Type';
        break;
    }
    return p;
  }


  repartitionBudgetForm() {
    this.rubriqueAllocationForm = this.formBuilder.group({
      rubriqueAllocations: this.formBuilder.array([]) // Initialize as an empty FormArray
    });

    this.budgetService.getBudgetByStructureId(this.structureId).subscribe(
      (budgets: Budget[]) => { // Assuming Budget is the correct type
        this.listBudgetsStructure = budgets;
        console.log("all budgets", this.listBudgetsStructure);
        const selectedBudget = this.listBudgetsStructure.find((budget: Budget) => {
          // Convert selectedYear to number before comparison
          return budget.budgetYear === Number(this.selectedYear);
        });
        if (selectedBudget) {
          selectedBudget.rubriqueAllocations.forEach((rubriqueAllocation: any) => {
            this.addRubriqueAllocation(rubriqueAllocation);
          });
        }
      },
      (err) => {
        console.log(err);
      }
    );
  }
// Function to clear existing controls in the rubriqueAllocations FormArray
  clearRubriqueAllocationsFormArray() {
    const rubriqueAllocationsFormArray = this.rubriqueAllocationForm.get('rubriqueAllocations') as FormArray;
    while (rubriqueAllocationsFormArray.length !== 0) {
      this.removeRubriqueAllocation(0);
    }
  }

  addRubriqueAllocation(rubriqueAllocation: any = null) {
    const rubriqueAllocationFormGroup = this.createRubriqueAllocationFormGroup(rubriqueAllocation);
    (this.rubriqueAllocationForm.get('rubriqueAllocations') as FormArray).push(rubriqueAllocationFormGroup);
    this.rubriqueAllocations.push(rubriqueAllocationFormGroup);
  }

  removeRubriqueAllocation(index: number) {
    (this.rubriqueAllocationForm.get('rubriqueAllocations') as FormArray).removeAt(index);
    this.rubriqueAllocations.splice(index, 1);
  }

  createRubriqueAllocationFormGroup(rubriqueAllocation: any = null): FormGroup {
    return this.formBuilder.group({
      rubriqueName: [rubriqueAllocation ? rubriqueAllocation.rubriqueName : '', [Validators.required]],
      montantAlloue: [rubriqueAllocation ? rubriqueAllocation.montantAlloue : '', [Validators.required]],
      montantRestant: [rubriqueAllocation ? rubriqueAllocation.montantRestant : '', [Validators.required]],
    });
  }

  calculateTotal(fieldName: string): number {
    const allocations = this.rubriqueAllocationForm.controls['rubriqueAllocations'] as FormArray;
    let total = 0;
    allocations.controls.forEach(allocation => {
      total += allocation.get(fieldName)?.value || 0;
    });
    return total;
  }


  /*toggleAccess(prof: Professeur): void {
    const updatedAccess = !prof.droit_daccee; // Toggle the access
    console.log("updatedAccess", updatedAccess);
    this.profService.updateProfessorAccess(prof.id, updatedAccess).subscribe(
      () => {
        console.log("L'accès a été mis à jour avec succès");
        // Update the local object's access
        prof.droit_daccee = updatedAccess;
      },
      (error) => {
        console.error("Erreur de mise à jour de l'accès :", error);
        // If the backend call fails, update the local object's access anyway
        prof.droit_daccee = updatedAccess;
      }
    );
  }*/



  Enregistrer(): void {
    const formData = this.rubriqueAllocationForm.value;
    // Initialize totalAlloue and totalRestant
    let totalAlloue = 0;
    let totalRestant = 0;

    const newBudget = {
      structureId: this.structureId, // Assuming you have the structureId
      budgetYear: new Date().getFullYear(), // Convert to number if needed
      totalAlloue: 0, // Initialize to 0
      totalRestant: 0, // Initialize to 0
      rubriqueAllocations: [] // Initialize as an empty array
    };
    let invalidAllocation = false;
    let duplicate =false;
    const rubriqueNamesSet = new Set<string>();
    // Iterate over each rubrique allocation form data
    formData.rubriqueAllocations.forEach((allocation: any) => {
      const selectedRubriqueName = allocation.rubriqueName;
      const selectedRubrique = this.listrubriques.find(rubrique => rubrique.nom === selectedRubriqueName);
      const montantAlloue = allocation.montantAlloue;
      const montantRestant = allocation.montantRestant;

      // Check if rubrique and montantAlloue exist
      if (selectedRubrique && montantAlloue && montantAlloue >= 0 && montantAlloue <= this.structuredetail.budgetAnnuel) {
        if (rubriqueNamesSet.has(selectedRubriqueName)) {
          window.alert(`Duplication de la rubrique détectée: ${selectedRubriqueName}. Veuillez les corriger avant de poursuivre.`);
          duplicate=true;
          return; // Exit the forEach loop early
        }
        // Add the rubrique name to the set
        rubriqueNamesSet.add(selectedRubriqueName);


        // Create a new rubrique allocation object
        const rubriqueAllocation = {
          rubriqueId: selectedRubrique.id,
          rubriqueName: selectedRubriqueName,
          montantAlloue: montantAlloue,
          montantRestant: montantAlloue
        };

        // Increment totalAlloue and totalRestant
        totalAlloue += montantAlloue;
        totalRestant += montantAlloue;

        // Push the rubrique allocation to the new budget's allocations
        // @ts-ignore
        newBudget.rubriqueAllocations.push(rubriqueAllocation);
      } else {
        // Invalid rubrique allocation found
        invalidAllocation = true;
      }
    });

    if (invalidAllocation) {
      console.error('Une allocation de rubrique invalide a été détectée. Veuillez les corriger avant de poursuivre.');
      window.alert('Une allocation de rubrique invalide a été détectée. Veuillez les corriger avant de poursuivre.');
      return; // Exit the method without proceeding with the post operation
    }if(duplicate){
      console.error("Il semble qu'il y ait des rubriques en double. Veuillez les corriger avant de poursuivre.");
      return;
    }if(totalAlloue>this.structuredetail.budgetAnnuel ){
      window.alert('Vous avez dépassé le budget annuel de la structure.');
      return;
    }else{
    // Set the calculated totalAlloue and totalRestant to the new budget
    newBudget.totalAlloue = totalAlloue;
    newBudget.totalRestant = totalRestant;

    // Log the new budget
    console.log(newBudget);
    if (!this.listYears.includes(newBudget.budgetYear)){
      console.log('no budget post');
    this.budgetService.addBudget(newBudget).subscribe(
      () => {
        window.alert('Budget ajouté avec succès !');
        window.location.reload();
      },
      error => {
        console.error("Une erreur s'est produite lors de l'ajout du budget.", error);
        if (error.status === 200) {
          window.alert('Budget ajouté avec succès !');
          window.location.reload();
        } else if (error.status === 400) {
          // Bad request, display error message from server
          window.alert(error.error);
        } else {
          // Other errors, display generic error message
          window.alert("Une erreur s'est produite lors de l'ajout du budget. Veuillez réessayer plus tard.");
        }
      }
    );

    }else{
      console.log('there is budget put');
      const selectedBudget = this.listBudgetsStructure.find((budget: Budget) => {
        // Convert selectedYear to number before comparison
        return budget.budgetYear === newBudget.budgetYear;
      });
      this.budgetService.updateBudget(selectedBudget.id, newBudget).subscribe(
        () => {
          window.alert('Budget mis à jour avec succès !');
          window.location.reload();
        },
        error => {
          console.error("Une erreur s'est produite lors de la mise à jour du budget.", error);
          if (error.status === 200) {
            window.alert('Budget ajouté avec succès !');
          } else if (error.status === 400) {
            // Bad request, display error message from server
            window.alert(error.error);
          } else {
            // Other errors, display generic error message
            window.alert("Une erreur s'est produite lors de la mise à jour du budget. Veuillez réessayer plus tard.");
          }
        }
      );
    }
    }
  }

  /*Enregistrer() {
    const budget: Budget = this.rubriqueAllocationForm.value;
    let rubriquesAllocations: [RubriqueAllocation];

    if (this.selectedBudget === null) {
      rubriquesAllocations = this.rubriqueAllocationForm.value.rubriqueAllocations.map((allocation: any) => {
        const selectedRubriqueName = allocation.rubriqueName;
        const selectedRubrique = this.listrubriques.find(rubrique => rubrique.nom === selectedRubriqueName);
        const montantAlloue = allocation.montantAlloue;
        const montantRestant = allocation.montantRestant;

        if (selectedRubrique && montantAlloue) {
          return {
            id: 0, // Provide an id (you may need to generate it dynamically)
            budgetId: 0, // Provide a budgetId (you may need to get it from the selectedBudget or generate it dynamically)
            rubriqueName: selectedRubriqueName,
            rubriqueId: selectedRubrique.id,
            montantAlloue: montantAlloue,
            montantRestant: montantRestant
          };
        } else {
          return null; // or handle invalid data accordingly
        }
      }).filter((allocation: RubriqueAllocation | null) => allocation !== null);
    }

    budget.rubriqueAllocations = rubriquesAllocations;
    console.log(budget);
  }*/





  openNewCommandeForm() {
    this.isNewCommandeFormOpen = true;
    this.isDetailsFormOpen = false;
  }

  closeNewCommandeForm() {
    this.isNewCommandeFormOpen = false;
  }


}
