import { Component, OnInit } from '@angular/core';
import { Professeur } from "../model/professeur.model";
import { ProfesseurService } from "../services/professeur.service";
import { PlatformLocation } from '@angular/common';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  status = false;  // Declare status property
  userId: number | null = null;
  userName: string = '';
  professeur: Professeur | undefined;

  constructor(
    private profService: ProfesseurService,
    private platformLocation: PlatformLocation
  ) {}

  ngOnInit(): void {
    if (this.isBrowser()) {
      // Retrieve the id from localStorage
      const id = localStorage.getItem('id');
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
      } else {
        console.error('User ID not found in localStorage');
      }
    }
  }

  isBrowser(): boolean {
    return typeof window !== 'undefined' && this.platformLocation !== null;
  }
  addToggle() {
    this.status = !this.status;
  }
}
