import {Component, OnInit} from '@angular/core';
import {ProfesseurService} from "../services/professeur.service";
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Professeur} from "../model/professeur.model";
import {StructuresService} from "../services/structures.service";
import { Structure, structurestype } from "../model/structure.model";
import { Clipboard } from '@angular/cdk/clipboard';
import { MatSnackBar } from '@angular/material/snack-bar';


@Component({
  selector: 'app-structure-admin-view',
  templateUrl: './structure-admin-view.component.html',
  styleUrl: './structure-admin-view.component.css'
})
export class StructureAdminViewComponent implements OnInit{
  structures: any;
  isDetailsFormOpen: boolean = false;
  isNewStructureFormOpen: boolean = false;
  selectedStructure: any;
  searchTerm: string = '';
  structureTypes: string[] = Object.values(structurestype);
  listprof: any[] = [];
  dropdowns: number[][] = [[]];
  public newStructureForm! : FormGroup;
  detailsForm!: FormGroup;

  //NavBar
  status = false;

  addToggle() {
    this.status = !this.status;
  }
  constructor(
    private structureService: StructuresService,
    private profService: ProfesseurService,
    private formBuilder: FormBuilder,
    private clipboard: Clipboard,
    private snackBar: MatSnackBar // Inject the Clipboard service here
  ) {}

  ngOnInit(): void {

    this.structureService.getAllStructures().subscribe(
      { next:(data)=>{
          this.structures = data;
        },
        error : (err)=>console.error(err)
      });
    // Fetch professors and assign them to listprof
    this.profService.getProfessors().subscribe(
      (data) => {
        this.listprof = data;
      },
      (error) => console.error(error)
    );
    this.initDetailsFormBuilder();
    this.initnewStructureFormBuilder();
  }
  getStructureById(id: any) {
    this.structureService.getStructureById(id).subscribe({
      next: (structure) => {
        this.selectedStructure = structure;

        // Pre-fill the detailsForm with the selected professor's information
        this.detailsForm.patchValue({
          acronyme: structure.acronyme,
          nom: structure.nom,
          type:structure.type,
          responsable: structure.nomResponsable,
          budget: structure.budget,
          membres: structure.equipe_prof_names
        });

        this.openDetailsForm();
      },
      error: (err) => console.error(err)
    });
  }

  /*search() {
    // If both prenom and nom are empty, reset the table to show all professors
    if (!this.searchTerm) {
      this.structureService.getAllStructures();
      return;
    }
    this.structureService.searchProfessors(this.searchTerm).subscribe({
      next: (data) => {
        this.profs = data;
      },
      error: (err) => {
        console.error(err);
      }
    });
  }*/
  deleteStructure(id: any, event?: DragEvent): void {
    if (event) {
      // If the function is called from a drag event, prevent the default behavior
      event.preventDefault();
    }
    if (confirm("Êtes-vous sûr de vouloir supprimer cette structure ?")) {
      this.structureService.deleteStructure(id).subscribe({
        next: () => {
          window.alert("Structure supprimée avec succès !");
          window.location.reload();
        },
        error: err => console.log(err)
      });
    }
  }

  openDetailsForm() {
    this.isDetailsFormOpen = true;
    this.isNewStructureFormOpen = false;
  }

  closeDetailsForm() {
    this.isDetailsFormOpen = false;
  }

  openNewStructureForm() {
    this.isNewStructureFormOpen = true;
    this.isDetailsFormOpen = false;
  }

  closeNewStructureForm() {
    this.isNewStructureFormOpen = false;
  }






  private initnewStructureFormBuilder() {
    this.newStructureForm = this.formBuilder.group({
      acronyme: ['', [Validators.required]],
      nom: ['', [Validators.required]],
      idResponsable: ['', [Validators.required]],
      budget: [null, [Validators.required]],
      type: ['', [Validators.required]],
      equipe_prof_ids: [[]], // Initialize as an empty array
    });

    // Initialize form controls for each dropdown box dynamically
    for (let i = 0; i < this.dropdowns.length; i++) {
      this.newStructureForm.addControl(`equipe_prof_ids_${i}`, this.formBuilder.control(null));
    }
  }


  addDropdown() {
    this.dropdowns.push([]);
    const formControlName = `equipe_prof_ids_${this.dropdowns.length - 1}`;

    // Remove the previously added form control, if exists
    this.newStructureForm.removeControl(formControlName);

    // Add the new form control
    this.newStructureForm.addControl(formControlName, this.formBuilder.control(''));
  }
  removeDropdown() {
    if (this.dropdowns.length > 0) {
      this.dropdowns.pop(); // Remove the last dropdown from the array
  
      // Remove the corresponding form control from the FormGroup
      const formControlName = `equipe_prof_ids_${this.dropdowns.length}`;
      this.newStructureForm.removeControl(formControlName);
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
onDragStart(event: DragEvent, data: string): void {
  event.dataTransfer?.setData('text/plain', data);
}

allowDrop(event: DragEvent): void {
  event.preventDefault();
}



  // Method to save the new structure

  saveNewStructure() {
    const structure = this.newStructureForm.value;
    structure.type = this.mapStructureType(structure.type);

    // Extract selected professor IDs from form
    const selectedIds: number[] = [];
    for (let i = 0; i < this.dropdowns.length; i++) {
      const dropdownControlName = `equipe_prof_ids_${i}`;
      const selectedId = this.newStructureForm.get(dropdownControlName)?.value;
      if (selectedId) {
        selectedIds.push(selectedId);
      }
    }

    structure.equipe_prof_ids = selectedIds;

    // Remove individual equipe_prof_ids from the structure object
    for (let i = 0; i < this.dropdowns.length; i++) {
      delete structure[`equipe_prof_ids_${i}`];
    }

    // Call service to add the structure
    this.structureService.addStructure(structure).subscribe(
      () => {
        window.alert('Structure ajoutée avec succès !');
        window.location.reload();
        this.closeNewStructureForm();
      },
      error => {
        console.error("Une erreur s'est produite lors de l'ajout de la structure.", error);
        if (error.status === 200) {
          window.alert('Structure ajoutée avec succès !');
          window.location.reload();
          this.closeNewStructureForm();
        } else if (error.status === 400) {
          // Bad request, display error message from server
          window.alert(error.error);
        } else {
          // Other errors, display generic error message
          window.alert("Une erreur s'est produite lors de l'ajout de la structure. Veuillez réessayer plus tard.");
        }
      }
    );
  }


  // Map Angular enum value to Java enum value
  mapStructureType(structureType: string): string {
    switch (structureType) {
      case 'Laboratoire de Recherche':
        return 'LabodeRecherche';
      case 'Equipe de Recherche':
        return 'EquipedeRecherche';
      case 'Projet de Recherche':
        return 'ProjetdeRecherche';
      default:
        return '';
    }
  }

  /*private initDetailsFormBuilder() {
    this.detailsForm = this.formBuilder.group({
      acronyme: this.formBuilder.control('', [Validators.required]),
      nom: this.formBuilder.control('', [Validators.required]),
      responsable: this.formBuilder.control('', [Validators.required]),
      type:this.formBuilder.control('', [Validators.required]),
      budget: this.formBuilder.control('', [Validators.required]),
      membres: this.formBuilder.control('', [Validators.required])

    });
  }*/


  private initDetailsFormBuilder() {
    this.detailsForm = this.formBuilder.group({
      acronyme: ['', [Validators.required]],
      nom: ['', [Validators.required]],
      idResponsable: ['', [Validators.required]],
      budget: [null, [Validators.required]],
      type: ['', [Validators.required]],
      equipe_prof_ids: ['', [Validators.required]], // Initialize as an empty array
    });

  }
  isEditMode: boolean = false;

  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
  }

  /*saveProfessorChanges() {
    const updatedProf: Professeur = this.detailsForm.value;
    this.structureService.updateProfessor(this.selectedProf.id, updatedProf).subscribe({
      next: () => {
        window.alert("Professor updated successfully!");
        window.location.reload();
        this.isEditMode = false; // Disable edit mode after saving changes
      },
      error: err => {
        console.error('An error occurred while updating professor:', err);
        // Optionally, display an error message to the user
        window.alert("An error occurred while updating professor. Please try again later.");
      }
    });
  }*/

  



}
