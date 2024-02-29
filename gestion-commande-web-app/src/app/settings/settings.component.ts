import { Component, OnInit } from '@angular/core';
import { Professeur } from "../model/professeur.model";
import { ProfesseurService } from "../services/professeur.service";
import { PlatformLocation } from '@angular/common';
import { Router } from '@angular/router';
import {StructuresService} from "../services/structures.service";
import {Structure, structurestype} from "../model/structure.model";
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginService } from '../services/login.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import e from 'express';


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
    newPassword: ['', [Validators.required, Validators.minLength(4)]],
    confirmPassword: ['', Validators.required],
  });


  constructor(
    private profService: ProfesseurService,
    private structureService: StructuresService,
    private platformLocation: PlatformLocation,
    private snackBar: MatSnackBar,
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
      const oldPassword = this.passwordUpdateForm.value.oldPassword;
      const newPassword = this.passwordUpdateForm.value.newPassword;
      const confirmPassword = this.passwordUpdateForm.value.confirmPassword;
      const idProfesseur = this.professeur.id;
      
      this.loginService.isSamePassword(oldPassword, this.professeur.id).subscribe(
        (response) => { 
          console.log(response); // Log the response
          
          if (response === 'Passwords match') {
            console.log('Passwords match');
            // Handle success, e.g., display a success message
            window.alert('Password updated successfully.');
          } else {
            console.log('Passwords do not match');
            // Handle failure, e.g., display an error message
            window.alert('Password update failed: ' + response);
          }
        },
        (error) => {
          //console.error("An error occurred while updating password:", error.status);
          if (error.status === 400) {
            window.alert("L'ancien mot de passe est incorrect. Veuillez réessayer.");
          } else if (error.status === 200) {
            if (newPassword === confirmPassword){
              this.loginService.updatePassword(idProfesseur, { mdp: newPassword }).subscribe(
                (response) => {
                  // console.log("Update success",response);
                  window.alert('Le mot de passe est mit à jour avec succès.');
                  window.location.reload();
                },
                (error) => {
                  // console.error('Error updating password:', error);
                  window.alert('Error dans la mise à jour du mot de passe. Essayez plus tard.');
                }
                );
            }else{
              window.alert("Les mots de passe ne correspondent pas");
            }

          } else {
            window.alert('Error dans la mise à jour du mot de passe. Essayez plus tard.');
          }
        }
      );
    } else {
      console.error('Professor information is not available.');
      window.alert('Unable to check password. Professor information is not available.');
    }
  }
  
  
  openErrorSnackBar(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000, // Adjust duration as needed
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['error-snackbar'] // Add custom styling if needed
    });
  }

}

