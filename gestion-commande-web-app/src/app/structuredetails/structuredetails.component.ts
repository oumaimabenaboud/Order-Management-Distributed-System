import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ProfesseurService} from "../services/professeur.service";
import {Professeur} from "../model/professeur.model";
import { Clipboard } from '@angular/cdk/clipboard';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-structuredetails',
  templateUrl: './structuredetails.component.html',
  styleUrl: './structuredetails.component.scss'
})
export class StructuredetailsComponent {

  
  profs: any;
  status = false;
  enabled: boolean = false; // Define the enabled property

  addToggle() {
    this.status = !this.status;
  }
  constructor(
    private profService: ProfesseurService,
    private formBuilder: FormBuilder

    
  ) { }

  //Table of Profs
  ngOnInit(): void {

    this.profService.getProfessors().subscribe(
      { next:(data)=>{
          this.profs = data;
        },
        error : (err)=>console.error(err)
      });
  }


  toggleAccess(prof: Professeur): void {
    const updatedAccess = !prof.droit_daccee; // Toggle the access
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
  }
  
}
