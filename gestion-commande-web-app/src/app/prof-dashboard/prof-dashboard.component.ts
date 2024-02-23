import { Component, OnInit } from '@angular/core';
import { Professeur } from "../model/professeur.model";
import { ProfesseurService } from "../services/professeur.service";
import { PlatformLocation } from '@angular/common';
import { Router } from '@angular/router';
import {StructuresService} from "../services/structures.service";
import {Structure, structurestype} from "../model/structure.model";
import {error} from "@angular/compiler-cli/src/transformers/util";


@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './prof-dashboard.component.html',
  styleUrls: ['./prof-dashboard.component.css']
})
export class ProfDashboardComponent implements OnInit {
  status = true;  // Declare status property
  userId: number | null = null;
  userName: string = '';
  professeur: Professeur | undefined;
  structuresrespo: any;
  structuresmembre: any;

  constructor(
    private profService: ProfesseurService,
    private structureService: StructuresService,
    private platformLocation: PlatformLocation,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.isBrowser()) {
      // Retrieve the id from sessionStorage
      const id = sessionStorage.getItem('id');
      this.userId = id ? parseInt(id, 10) : null;

      if (this.userId) {
        this.profService.getProfessor(this.userId).subscribe(
          (professor: Professeur) => {
            this.professeur = professor;
            this.userName = professor.nom + ' ' + professor.prenom;
          },
          (error) => {
            console.error('Error fetching professor:', error);
          }
        );

        this.structureService.getStructuresByResponsable(this.userId).subscribe(
          (data) => {
            this.structuresrespo = data;
            this.structuresrespo.forEach((structure: Structure) => {
              // @ts-ignore
              structure['typeAsString'] = this.convertStructureTypes(structure);
            });
          },
          (error) => { // Define error handler with one argument (error)
            console.error(error);
          }
        );

        this.structureService.getStructuresByEquipeMember(this.userId).subscribe(
          (data) => {
            this.structuresmembre = data;
            this.structuresmembre.forEach((structure: Structure) => {
              // @ts-ignore
              structure['typeAsString'] = this.convertStructureTypes(structure);
            });
          },
          (error) => { // Define error handler with one argument (error)
            console.error(error);
          }
        );
      } else {
        console.error('User ID not found in sessionStorage');
      }
    }
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

  getStructureById(s: any) {
    this.router.navigateByUrl("/structuredetail/"+s.id)

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
}
