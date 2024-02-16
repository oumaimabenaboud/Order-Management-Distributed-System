import {Component, OnInit} from '@angular/core';
import {ProfesseurService} from "../services/professeur.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Professeur} from "../model/professeur.model";
import {StructuresService} from "../services/structures.service";
import {Structure} from "../model/structure.model";

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

  //NavBar
  status = false;

  addToggle() {
    this.status = !this.status;
  }
  constructor(private  structureService:StructuresService, private proService:ProfesseurService, private formBuilder: FormBuilder) { }


  //Table of Profs
  ngOnInit(): void {
    this.structureService.getAllStructures().subscribe(
      { next:(data)=>{
          this.structures = data;
        },
        error : (err)=>console.error(err)
      });
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
          responsable: structure.nomResponsable
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


  deleteStructure(id: any) {
    if (confirm("Are you sure you want to delete this professor?")) {
      this.structureService.deleteStructure(id).subscribe({
        next: () => {
          window.alert("Structure deleted successfully!");
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



  public newStructureForm! : FormGroup;
  detailsForm!: FormGroup;


  private initnewStructureFormBuilder() {
    this.newStructureForm = this.formBuilder.group({
      prenom: this.formBuilder.control('', [Validators.required]),
      nom: this.formBuilder.control('', [Validators.required]),
      mail: this.formBuilder.control('', [Validators.required])
    });
  }

  saveNewStructure() {
    let structure: Structure = this.newStructureForm.value;
    this.structureService.addStructure(structure).subscribe({
      next: (newProf) => {
        this.structures.push(newProf);
        window.alert("Professor added successfully!");
        window.location.reload();
        this.closeNewStructureForm(); // Optionally close the form
      },
      error: (error) => {
        console.error("L'email doit être sous la forme 'p.nom@umi.ac.ma' ou 'pre.nom@umi.ac.ma'", error);
        window.alert("L'email doit être sous la forme 'p.nom@umi.ac.ma' ou 'pre.nom@umi.ac.ma' ");
      }
    });
  }

  private initDetailsFormBuilder() {
    this.detailsForm = this.formBuilder.group({
      acronyme: this.formBuilder.control('', [Validators.required]),
      nom: this.formBuilder.control('', [Validators.required]),
      responsable: this.formBuilder.control('', [Validators.required])
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
