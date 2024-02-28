import { Component, OnInit } from '@angular/core';
import { Professeur } from "../model/professeur.model";
import { ProfesseurService } from "../services/professeur.service";
import { PlatformLocation } from '@angular/common';
import { Router } from '@angular/router';
import {StructuresService} from "../services/structures.service";
import {Structure, structurestype} from "../model/structure.model";
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginService } from '../services/login.service';


@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent implements OnInit{
  status = true;  
  userId: number | null = null;
  userName: string = '';
  professeur: Professeur | undefined;
  passwordUpdateForm: FormGroup = this.fb.group({
    oldPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', Validators.required],
  });


  constructor(
    private profService: ProfesseurService,
    private structureService: StructuresService,
    private platformLocation: PlatformLocation,
    private router: Router,
    private fb: FormBuilder,
    private loginService: LoginService
  ) {}
  addToggle() {
    this.status = !this.status;
  }
  logout() {
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }
  isBrowser(): boolean {
    return typeof window !== 'undefined' && this.platformLocation !== null;
  }

  ngOnInit(): void {
    if (this.isBrowser()) {
      
      // Retrieve the id from sessionStorage
      const id = sessionStorage.getItem('id');
      this.userId = id ? parseInt(id, 10) : null;

      if (this.userId) {
        this.profService.getProfessor(this.userId).subscribe(
          (professor: Professeur) => {
            // console.log('Professor:', professor);
            this.professeur = professor;
            this.userName = professor.nom + ' ' + professor.prenom;
          },
          (error) => {
            console.error('Error fetching professor:', error);
          }
        );

      } else {
        console.error('User ID not found in sessionStorage');
      }
    }
  }
  onPasswordUpdateSubmit(): void {
    if (this.passwordUpdateForm && this.passwordUpdateForm.valid && this.professeur) {
      // Handle form submission logic here
      
      const oldPassword = this.passwordUpdateForm.value.oldPassword;
      const professorPassword = this.professeur.mdp || '';
      console.log('oldPassword:', oldPassword, 'professorPassword:', professorPassword);
  
      this.loginService.isSamePassword(oldPassword, [professorPassword]).subscribe(
        (response) => { 
          window.alert("Le mot de passe est correct" + response);
        },
        (error: any) => {
          console.error('Error:', error.error);
          // Handle the error accordingly, e.g., show an error message
          window.alert('Une erreur s\'est produite lors de la vérification du mot de passe.');
        }
      );
    } else {
      // Handle the case when this.professeur is undefined or null
      console.error('Professor information is not available.');
      window.alert('Impossible de vérifier le mot de passe. Les informations du professeur ne sont pas disponibles.');
    }
  }
  
}

