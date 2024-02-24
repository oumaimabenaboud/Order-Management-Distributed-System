import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ProfesseurService} from "../services/professeur.service";
import {Professeur} from "../model/professeur.model";
import { Clipboard } from '@angular/cdk/clipboard';
import { MatSnackBar } from '@angular/material/snack-bar';
import {StructuresService} from "../services/structures.service";
import {ActivatedRoute, Router} from '@angular/router';
import {PlatformLocation} from "@angular/common";
import {Structure} from "../model/structure.model";

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
  structureId !:number;
  userId: number | null = null;
  professeurConnecté : Professeur | undefined;
  professeurRespo : Professeur | undefined;
  loading: boolean = true;
  constructor(
    private structureService: StructuresService,
    private router : Router,
    private route :ActivatedRoute,
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


  isBrowser(): boolean {
    return typeof window !== 'undefined' && this.platformLocation !== null;
  }

  logout() {
    sessionStorage.removeItem('id');
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
