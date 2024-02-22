import {Component, OnInit} from '@angular/core';
import {ProfesseurService} from "../services/professeur.service";
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
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
  listlabo:any[]=[];
  dropdowns: number[][] = [[]];
  public newStructureForm! : FormGroup;
  detailsForm!: FormGroup;
  equipe_prof_names: any[] = [];
  childEquipesNoms: any[] = [];// Assuming any type for the members, you can replace any with a specific type if available
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
    this.structureService.getAllStructures().subscribe({
      next: (data) => {
        this.structures = data;
        // Assuming this.structures is an array of structures
        this.structures.forEach((structure: Structure) => {
          let p: string; // Declare p outside the loop
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
          }
          // @ts-ignore
          structure['typeAsString'] = p; // Assign p to a dynamically created property in the structure object
        });
      },
      error: (err) => console.error(err)
    });

    // Fetch professors and assign them to listprof
    this.profService.getProfessors().subscribe(
      (data) => {
        this.listprof = data;
      },
      (error) => console.error(error)
    );
    this.structureService.getStructuresByType('LabodeRecherche').subscribe(
      (data)=>{
      this.listlabo = data;
    },
      (error)=> console.error(error)
    );
    this.initDetailsFormBuilder();
    this.initnewStructureFormBuilder();
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


  // GET STRUCTURE BY ID , PUT
  getStructureById(id: any) {

    function getStructureTypeAsString(type: structurestype): string {
      switch (type.toString()) {
        case 'LabodeRecherche':
          return 'Laboratoire de Recherche';
        case 'EquipedeRecherche':
          return 'Equipe de Recherche';
        case 'ProjetdeRecherche':
          return 'Projet de Recherche';
        default:
          return 'Unknown';
      }
    }


    this.structureService.getStructureById(id).subscribe({
      next: (structure) => {
        this.selectedStructure = structure;
        this.equipe_prof_names = [];
        this.childEquipesNoms = [];
        // Pre-fill the detailsForm with the selected structure's information
        this.detailsForm.patchValue({
          acronyme: structure.acronyme,
          nom: structure.nom,
          type: getStructureTypeAsString(structure.type),
          nomResponsable: structure.nomResponsable,
          parentLabId:structure.parentLabId,
          parentLabNom:structure.parentLabNom,
          budgetAnnuel: structure.budgetAnnuel,
        });


        // Add each member to the membres FormArray
        structure.equipe_prof_names.forEach(member => {
          this.addMembre(member);
        });

        structure.childEquipesNoms.forEach(equipe => {
          this.addEquipe(equipe);
        });

        this.openDetailsForm();
      },
      error: (err) => console.error(err)
    });
  }

  addMembre(member: string) {
    this.equipe_prof_names.push(this.formBuilder.control(member));
  }

  addEquipe(equipe: string) {
    this.childEquipesNoms.push(this.formBuilder.control(equipe));
  }

  openDetailsForm() {
    this.isDetailsFormOpen = true;
    this.isNewStructureFormOpen = false;
  }

  closeDetailsForm() {
    this.isDetailsFormOpen = false;

  }


  private initDetailsFormBuilder() {
    this.detailsForm = this.formBuilder.group({
      acronyme: ['', [Validators.required]],
      nom: ['', [Validators.required]],
      nomResponsable: ['', [Validators.required]],
      budgetAnnuel: [null, [Validators.required]],
      type: ['', [Validators.required]],
      parentLabNom:['', [Validators.required]],
      equipe_prof_names: this.formBuilder.array([]), // Initialize as a FormArray
      childEquipesNoms:this.formBuilder.array([]),
    });



  }

  isEditMode: boolean = false;
  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
  }

  saveStructureChanges() {
    const updatedStructure: Structure = this.detailsForm.value;

    // Map structure type if needed
    // updatedStructure.type = this.mapStructureType(updatedStructure.type);

    // Extract selected responsible person's ID
    const selectedResponsableName = this.detailsForm.get('nomResponsable')?.value;
    const selectedResponsable = this.listprof.find(prof => prof.prenom + ' ' + prof.nom === selectedResponsableName);
    if (selectedResponsable) {
      updatedStructure.idResponsable = selectedResponsable.id;
    }

    // Extract selected member names and find corresponding IDs
    const selectedMemberNames: string[] = [];
    for (let i = 0; i < this.equipe_prof_names.length; i++) {
      const control = this.equipe_prof_names.at(i);
      selectedMemberNames.push(control.value);
    }

    // Map member names to their corresponding IDs
    const selectedMemberIds: number[] = [];
    selectedMemberNames.forEach(memberName => {
      const selectedMember = this.listprof.find(prof => prof.prenom + ' ' + prof.nom === memberName);
      if (selectedMember) {
        selectedMemberIds.push(selectedMember.id);
      }
    });

    // Update the structure with the selected member names and IDs
    updatedStructure.equipe_prof_names = selectedMemberNames;
    updatedStructure.equipe_prof_ids = selectedMemberIds;

    // Extract selected responsible person's ID
    const selectedLaboName = this.detailsForm.get('parentLabNom')?.value;
    const selectedLabo = this.listlabo.find(labo => labo.nom === selectedLaboName);
    if (selectedLabo) {
      updatedStructure.parentLabId = selectedLabo.id;
    }
    // Update the structure with the selected member names
    this.structureService.updateStructure(this.selectedStructure.id, updatedStructure).subscribe({
      next: () => {
        window.alert("Structure updated successfully!");
        window.location.reload();
        this.isEditMode = false; // Disable edit mode after saving changes
      },
      error: err => {
        console.error('An error occurred while updating structure:', err);
        // Optionally, display an error message to the user
        window.alert("An error occurred while updating structure. Please try again later.");
      }
    });
  }




  // NEW STRUCTURE
  private initnewStructureFormBuilder() {
    this.newStructureForm = this.formBuilder.group({
      acronyme: ['', [Validators.required]],
      nom: ['', [Validators.required]],
      idResponsable: ['', [Validators.required]],
      budgetAnnuel: [null, [Validators.required]],
      type: ['', [Validators.required]],
      equipe_prof_ids: [[]], // Initialize as an empty array
    });

    // Initialize form controls for each dropdown box dynamically
    for (let i = 0; i < this.dropdowns.length; i++) {
      this.newStructureForm.addControl(`equipe_prof_ids_${i}`, this.formBuilder.control(null));
    }
  }
  openNewStructureForm() {
    this.isNewStructureFormOpen = true;
    this.isDetailsFormOpen = false;
  }

  closeNewStructureForm() {
    this.isNewStructureFormOpen = false;
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

  // Method to save the new structure

  saveNewStructure() {
    const structure = this.newStructureForm.value;
    structure.type = this.mapStructureType(structure.type);

    // Extract selected professor ID for the responsible person
    const selectedResponsableName = this.newStructureForm.get('idResponsable')?.value;
    const selectedResponsable = this.listprof.find(prof => prof.prenom + ' ' + prof.nom === selectedResponsableName);
    if (selectedResponsable) {
      structure.idResponsable = selectedResponsable.id;
    }

    // Extract selected professor IDs for team members
    const selectedIds: number[] = [];
    for (let i = 0; i < this.dropdowns.length; i++) {
      const dropdownControlName = `equipe_prof_ids_${i}`;
      const selectedName = this.newStructureForm.get(dropdownControlName)?.value;
      const selectedProfessor = this.listprof.find(prof => prof.prenom + ' ' + prof.nom === selectedName);
      if (selectedProfessor) {
        selectedIds.push(selectedProfessor.id);
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



  copyToClipboard(nomResponsable: string, event: MouseEvent): void {
    const targetElement = event.currentTarget as HTMLElement;
    this.clipboard.copy(nomResponsable);
    this.snackBar.open('Nom copié dans le presse-papier', 'Close', {
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



  //DELETE STRUCTURE
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

}
