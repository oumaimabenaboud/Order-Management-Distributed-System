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
  displayedStructures: any[] = [];
  isDetailsFormOpen: boolean = false;
  isNewStructureFormOpen: boolean = false;
  selectedStructure: any;
  searchTerm: string = '';
  structureTypes: string[] = Object.values(structurestype);
  listprof: any[] = [];
  listlabo:any[]=[];
  listequipe:any[]=[];
  dropdowns: number[][] = [[]];
  public newStructureForm! : FormGroup;
  detailsForm!: FormGroup;
  equipeProfNames: any[] = [];
  childEquipesNoms: any[] = [];// Assuming any type for the members, you can replace any with a specific type if available
  //NavBar
  status = false;
  selectedStructureType: string='';
  showFilter: boolean = false;

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
    this.getAllStructures();

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
    this.structureService.getStructuresByType('EquipedeRecherche').subscribe(
      (data)=>{
        this.listequipe = data;
      },
      (error)=> console.error(error)
    );
    this.initDetailsFormBuilder();
    this.initnewStructureFormBuilder();
  }
  getAllStructures() {
    this.structureService.getAllStructures().subscribe(
      (data) => {
        this.structures = data;
        this.structures.forEach((structure: Structure) => {
          // @ts-ignore
          structure['typeAsString'] = this.convertStructureTypes(structure);
        });
        this.updateDisplayedStructures();
      },
      (error) => console.error(error)
    );
  }
  getStructuresByType(type: string) {
    this.structureService.getStructuresByType(type).subscribe(
      (data) => {
        this.structures = data;
        this.structures.forEach((structure: Structure) => {
          // @ts-ignore
          structure['typeAsString'] = this.convertStructureTypes(structure);
        });
        this.updateDisplayedStructures();
      },
      (error) => console.error(error)
    );
  }
  toggleFilter() {
    this.showFilter = !this.showFilter;
  }
  onStructureTypeChange() {
    this.showFilter = false;
    if (this.selectedStructureType === 'all') {
      this.getAllStructures();
    } else {
      this.getStructuresByType(this.mapStructureType(this.selectedStructureType));
    }
  }
  updateDisplayedStructures() {
    this.displayedStructures = [...this.structures]; // Copy all structures to displayedStructures
  }
  search() {
    // If the search term is empty, reset the table to show all structures
    if (!this.searchTerm) {
      this.getAllStructures(); // Call getAllStructures() and subscribe to it
      return;
    }

    this.structureService.searchStructures(this.searchTerm).subscribe({
      next: (data) => {
        this.structures = data;
        this.structures.forEach((structure: Structure) => {
          // @ts-ignore
          structure['typeAsString'] = this.convertStructureTypes(structure);
        });
        this.updateDisplayedStructures(); // Update displayedStructures after receiving the search results
      },
      error: (error) => {
        console.error(error);
        // Handle error if necessary
      }
    });
  }
// Define a function to convert structure types to strings
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
        case 'Département':
          return 'Département';
        default:
          return 'Unknown';
      }
    }


    this.structureService.getStructureById(id).subscribe({
      next: (structure) => {
        this.selectedStructure = structure;
        this.equipeProfNames = [];
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
        structure.equipeProfNames.forEach(member => {
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
    this.equipeProfNames.push(this.formBuilder.control(member));
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
      equipeProfNames: this.formBuilder.array([]), // Initialize as a FormArray
      childEquipesNoms:this.formBuilder.array([]),
    });
  }

  isEditMode: boolean = false;
  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
  }
  addEquipeProf() {
    if (this.isEditMode) {
      // For professor equipe names
      this.equipeProfNames.push(this.formBuilder.control(''));
    }
  }
  addEquipeChildNom() {
    if (this.isEditMode) {
      // For child equipe names
      this.childEquipesNoms.push(this.formBuilder.control(''));
    }
  }

  removeEquipeProf() {
    if (this.isEditMode) {
      // For professor equipe names
      if (this.equipeProfNames.length > 1) {
        this.equipeProfNames.pop();
      }
    }
  }
  removeEquipeChildNom() {
    if (this.isEditMode) {
      // For child equipe names
      if (this.childEquipesNoms.length > 1) {
        this.childEquipesNoms.pop();
      }
    }
  }

  saveStructureChanges() {
    const updatedStructure: Structure = this.detailsForm.value;

    // Map structure type if needed
    updatedStructure.type = <structurestype>this.mapStructureType(updatedStructure.type);

    // Extract selected responsible person's ID
    const selectedResponsableName = this.detailsForm.get('nomResponsable')?.value;
    console.log('Selected Responsible Name:', selectedResponsableName);
    console.log('List of Professors:', this.listprof);// Log the selected responsible person's name
    const selectedResponsable = this.listprof.find(prof => prof.prenom + ' ' + prof.nom === selectedResponsableName);
    console.log('Selected Responsible Object:', selectedResponsable);
    console.log('Selected Responsible ID:', selectedResponsable.id);
    console.log('Selected Responsible ID:', selectedResponsable?.id);
    console.log('List of Professor Names:');
    this.listprof.forEach(prof => console.log(prof.prenom + ' ' + prof.nom));
    updatedStructure.idResponsable=selectedResponsable.id;




    // Extract selected member names and find corresponding IDs
    const selectedMemberNames: string[] = [];
    for (let i = 0; i < this.equipeProfNames.length; i++) {
      const control = this.equipeProfNames.at(i);
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
    updatedStructure.equipeProfNames = selectedMemberNames;
    updatedStructure.equipeProfIds = selectedMemberIds;

    // Extract selected labo's ID
    const selectedLaboName = this.detailsForm.get('parentLabNom')?.value;
    const selectedLabo = this.listlabo.find(labo => labo.nom === selectedLaboName);
    if (selectedLabo) {
      updatedStructure.parentLabId = selectedLabo.id;
    }


    const selectedChildEquipeNames: string[] = [];
    for (let i = 0; i < this.childEquipesNoms.length; i++) {
      const control = this.childEquipesNoms.at(i);
      selectedChildEquipeNames.push(control.value);
    }


    const selectedChildEquipeIds: number[] = [];
    selectedChildEquipeNames.forEach(equipeName => {
      const selectedEquipe = this.listequipe.find(equipe => equipe.nom === equipeName);
      if (selectedEquipe) {
        selectedChildEquipeIds.push(selectedEquipe.id);
      }
    });

    updatedStructure.childEquipesNoms = selectedChildEquipeNames;
    updatedStructure.childEquipesIds = selectedChildEquipeIds;
    console.log(updatedStructure)

    // Update the structure with the selected member names
    this.structureService.updateStructure(this.selectedStructure.id, updatedStructure).subscribe({
      next: () => {
        window.alert("Structure updated successfully!");
        window.location.reload();
        this.isEditMode = false; // Disable edit mode after saving changes
      },
      error : error => {
        console.error("Une erreur s'est produite lors de la mise à jour de la structure.", error);
      if (error.status === 200) {
        window.alert('Structure mise à jour avec succès !');
        window.location.reload();
        this.closeNewStructureForm();
      } else if (error.status === 400) {
        // Bad request, display error message from server
        window.alert(error.error);
      } else {
        // Other errors, display generic error message
        window.alert("Une erreur s'est produite lors de la modification de la structure. Veuillez réessayer plus tard.");
      }
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
      equipeProfIds: [[]], // Initialize as an empty array
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
    structure.equipeProfIds = selectedIds;

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
      case 'Département':
        return 'Département';
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


}
