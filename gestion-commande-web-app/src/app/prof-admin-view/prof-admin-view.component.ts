import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ProfesseurService} from "../services/professeur.service";
import {Professeur} from "../model/professeur.model";
import { Clipboard } from '@angular/cdk/clipboard';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PlatformLocation } from '@angular/common';
import { Router } from '@angular/router';


@Component({
  selector: 'app-prof-admin-view',
  templateUrl: './prof-admin-view.component.html',
  styleUrls: ['./prof-admin-view.component.css']
})
export class ProfAdminViewComponent implements OnInit {

  profs: any;
  isDetailsFormOpen: boolean = false;
  isNewProfFormOpen: boolean = false;
  selectedProf: any;
  searchTerm: string = '';
  enabled: boolean = false; // Define the enabled property
  userId: number | null = null;
  professeur : Professeur | undefined;



  //NavBar
  status = true;

  constructor(
    private profService: ProfesseurService,
    private formBuilder: FormBuilder,
    private clipboard: Clipboard,
    private snackBar: MatSnackBar,
    private platformLocation: PlatformLocation,
    private router: Router

  ) { }



  //Table of Profs
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
      this.profService.getProfessors().subscribe(
        { next:(data)=>{
            this.profs = data;
          },
          error : (err)=>console.error(err)
        });
      this.initDetailsFormBuilder();
      this.initNewProfFormBuilder();
  }
}
    isBrowser(): boolean {
      return typeof window !== 'undefined' && this.platformLocation !== null;
    }
    addToggle() {
      this.status = !this.status;
    }

    logout() {
      sessionStorage.clear();
      this.router.navigate(['/login']);
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

loadProfessors(): void {
  this.profService.getProfessors().subscribe(
    (data) => {
      this.profs = data;
    },
    (error) => {
      console.error('Erreur de chargement des professeurs:', error);
    }
  );
}


  getProf(id: any, event?: DragEvent): void  {
    if (event) {
      // If the function is called from a drag event, prevent the default behavior
      event.preventDefault();
    }
    this.profService.getProfessor(id).subscribe({
      next: (prof) => {
        this.selectedProf = prof;

        // Pre-fill the detailsForm with the selected professor's information
        this.detailsForm.patchValue({
          prenom: prof.prenom,
          nom: prof.nom,
          mail: prof.mail
        });

        this.openDetailsForm();
      },
      error: (err) => console.error(err)
    });
  }

  search() {
    // If both prenom and nom are empty, reset the table to show all professors
    if (!this.searchTerm) {
      this.profService.getProfessors();
      return;
    }
    this.profService.searchProfessors(this.searchTerm).subscribe({
      next: (data) => {
        this.profs = data;
      },
      error: (err) => {
        console.error(err);
      }
    });
  }


  // deleteProf(id: any) {
  //   if (confirm("Are you sure you want to delete this professor?")) {
  //     this.profService.deleteProfessor(id).subscribe({
  //       next: () => {
  //         window.alert("Professor deleted successfully!");
  //         window.location.reload();
  //       },
  //       error: err => console.log(err)
  //     });
  //   }
  // }
  deleteProf(id: any, event?: DragEvent): void {
    if (event) {
      // If the function is called from a drag event, prevent the default behavior
      event.preventDefault();
    }

    if (confirm("Êtes-vous sûr de vouloir supprimer ce professeur ?")) {
      this.profService.deleteProfessor(id).subscribe({
        next: () => {
          window.alert("Professeur supprimé avec succès !");
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
    this.isNewProfFormOpen = false;
  }

  closeDetailsForm() {
    this.isDetailsFormOpen = false;
  }

  openNewProfForm() {
    this.isNewProfFormOpen = true;
    this.isDetailsFormOpen = false;
  }

  closeNewProfForm() {
    this.isNewProfFormOpen = false;
  }



public newProfForm! : FormGroup;
  detailsForm!: FormGroup;


  private initNewProfFormBuilder() {
    this.newProfForm = this.formBuilder.group({
      prenom: this.formBuilder.control('', [Validators.required]),
      nom: this.formBuilder.control('', [Validators.required]),
      mail: this.formBuilder.control('', [Validators.required])
    });
  }

  saveNewProf() {
    let prof: Professeur = this.newProfForm.value;
    this.profService.addProfessor(prof).subscribe({
      next: (newProf) => {
        this.profs.push(newProf);
        window.alert("Professeur ajouté avec succès !");
        window.location.reload();
        this.closeNewProfForm(); // Optionally close the form
      },
      error: (error) => {
        console.error( error);
        window.alert(error.error);
      }
    });
  }

  private initDetailsFormBuilder() {
    this.detailsForm = this.formBuilder.group({
      prenom: this.formBuilder.control('', [Validators.required]),
      nom: this.formBuilder.control('', [Validators.required]),
      mail: this.formBuilder.control('', [Validators.required])
    });
  }
  isEditMode: boolean = false;

  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
  }

  saveProfessorChanges() {
    const updatedProf: Professeur = this.detailsForm.value;
    this.profService.updateProfessor(this.selectedProf.id, updatedProf).subscribe({
      next: () => {
        window.alert("Professeur mis à jour avec succès !");
        window.location.reload();
        this.isEditMode = false; // Disable edit mode after saving changes
      },
      error: err => {
        console.error("Une erreur s'est produite lors de la mise à jour du professeur:", err);
        // Optionally, display an error message to the user
        window.alert(err.error);
      }
    });
  }



}
