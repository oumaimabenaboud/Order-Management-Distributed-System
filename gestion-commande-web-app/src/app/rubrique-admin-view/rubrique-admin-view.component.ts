import {Component, OnInit} from '@angular/core';
import {ProfesseurService} from "../services/professeur.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Clipboard} from "@angular/cdk/clipboard";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Professeur} from "../model/professeur.model";
import {RubriqueService} from "../services/rubrique.service";
import {Rubrique} from "../model/rubrique.model";
import { PlatformLocation } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-rubrique-admin-view',
  templateUrl: './rubrique-admin-view.component.html',
  styleUrl: './rubrique-admin-view.component.css'
})
export class RubriqueAdminViewComponent implements OnInit {
  rubriques: any;
  isDetailsFormOpen: boolean = false;
  isNewRubriqueFormOpen: boolean = false;
  selectedRubrique: any;
  searchTerm: string = '';
  userId: number | null = null;
  professeur : Professeur | undefined;

  //NavBar
  status = false;

  constructor(
    private rubriqueService: RubriqueService,
    private profService: ProfesseurService,
    private formBuilder: FormBuilder,
    private clipboard: Clipboard,
    private snackBar: MatSnackBar,
    private platformLocation: PlatformLocation,
    private router: Router
  ) { }




  ngOnInit(): void {
    if (this.isBrowser()) {
      const id = sessionStorage.getItem('id');
      this.userId = id ? parseInt(id, 10) : null;

      if (this.userId) {
        this.profService.getProfessor(this.userId).subscribe(
          (professor: Professeur) => {
            this.professeur = professor;
          },
          (error) => {
            console.error('Error fetching professor:', error);
          }
        );
      } else {
        console.error('User ID not found in sessionStorage');
      }
      this.rubriqueService.getAllRubriques().subscribe(
        { next:(data)=>{
            this.rubriques = data;
          },
          error : (err)=>console.error(err)
        });
      this.initDetailsFormBuilder();
      this.initNewRubriqueFormBuilder();
  }
  }
  copyToClipboard(email: string, event: MouseEvent): void {
    const targetElement = event.currentTarget as HTMLElement;
    this.clipboard.copy(email);
    this.snackBar.open('Email copié dans le presse-papiers', 'Close', {
      duration: 2000, // Duration in milliseconds (2 seconds)
      horizontalPosition: 'left',
      verticalPosition: 'top',
      panelClass: 'copy-snackbar',
      data: { trigger: targetElement }
    });
  }
  isBrowser(): boolean {
    return typeof window !== 'undefined' && this.platformLocation !== null;
  }
  addToggle() {
    this.status = !this.status;
  }

  logout() {
    sessionStorage.removeItem('id');
    this.router.navigate(['/login']);
  }

  getRubrique(id: any, event?: DragEvent): void  {
    if (event) {
      // If the function is called from a drag event, prevent the default behavior
      event.preventDefault();
    }

    this.rubriqueService.getRubriqueById(id).subscribe({
      next: (rubrique) => {
        this.selectedRubrique = rubrique;

        // Pre-fill the detailsForm with the selected professor's information
        this.detailsForm.patchValue({
          nom: rubrique.nom,
        });

        this.openDetailsForm();
      },
      error: (err) => console.error(err)
    });
  }

  search() {
    // If both prenom and nom are empty, reset the table to show all professors
    if (!this.searchTerm) {
      this.rubriqueService.getAllRubriques();
      return;
    }
    this.rubriqueService.searchRubriques(this.searchTerm).subscribe({
      next: (data) => {
        this.rubriques = data;
      },
      error: (err) => {
        console.error(err);
      }
    });
  }


  deleteRubrique(id: any, event?: DragEvent): void {
    if (event) {
      // If the function is called from a drag event, prevent the default behavior
      event.preventDefault();
    }

    if (confirm("Êtes-vous sûr de vouloir supprimer cette rubrique ?")) {
      this.rubriqueService.deleteRubrique(id).subscribe({
        next: () => {
          window.alert("Rubrique supprimée avec succès !");
          window.location.reload();
        },
        error: err => console.log(err)
      });
    }
  }


  onDragStart(event: DragEvent, data: string): void {
    event.dataTransfer?.setData('text/plain', data);
  }

  allowDrop(event: DragEvent): void {
    event.preventDefault();
  }


  openDetailsForm() {
    this.isDetailsFormOpen = true;
    this.isNewRubriqueFormOpen = false;
  }

  closeDetailsForm() {
    this.isDetailsFormOpen = false;
  }

  openNewRubriqueForm() {
    this.isNewRubriqueFormOpen = true;
    this.isDetailsFormOpen = false;
  }

  closeNewRubriqueForm() {
    this.isNewRubriqueFormOpen = false;
  }



  public newRubriqueForm! : FormGroup;
  detailsForm!: FormGroup;


  private initNewRubriqueFormBuilder() {
    this.newRubriqueForm = this.formBuilder.group({
      nom: this.formBuilder.control('', [Validators.required])
    });
  }

  saveNewRubrique() {
    let rubrique: Rubrique = this.newRubriqueForm.value;
    this.rubriqueService.addRubrique(rubrique).subscribe({
      next: (newRubrique) => {
        this.rubriques.push(newRubrique);
        window.alert("Rubrique ajoutée avec succès !");
        window.location.reload();
        this.closeNewRubriqueForm(); // Optionally close the form
      },
      error: (error) => {
        console.error("L'email doit être sous la forme 'p.nom@umi.ac.ma' ou 'pre.nom@umi.ac.ma'", error);
        window.alert("Une erreur s'est produite lors de l'ajout de la rubrique. Veuillez réessayer plus tard.");
      }
    });
  }

  private initDetailsFormBuilder() {
    this.detailsForm = this.formBuilder.group({
      nom: this.formBuilder.control('', [Validators.required])
    });
  }
  isEditMode: boolean = false;

  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
  }

  saveRubriqueChanges() {
    const updatedRubrique: Rubrique = this.detailsForm.value;
    this.rubriqueService.updateRubrique(this.selectedRubrique.id, updatedRubrique).subscribe({
      next: () => {
        window.alert("Rubrique mis à jour avec succès !");
        window.location.reload();
        this.isEditMode = false; // Disable edit mode after saving changes
      },
      error: err => {
        console.error("Une erreur s'est produite lors de la mise à jour de la rubrique:", err);
        // Optionally, display an error message to the user
        window.alert("Une erreur s'est produite lors de la mise à jour de la rubrique. Veuillez réessayer plus tard.");
      }
    });
  }

}
