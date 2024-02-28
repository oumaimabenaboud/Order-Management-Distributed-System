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

      this.loginService.isSamePassword('bekri_ali', 2).subscribe(
        (response) => { 
          console.log(response.status);
        },
        (error) => {
          console.error("An error occurred while updating password:", error.status);
          if (error.status === 400) {
            window.alert('Bad request: ' + error.error);
          } else if (error.status === 200) {
            window.alert('Success with error: ' + error.error);
          } else {
            window.alert("An error occurred while updating password. Please try again later.");
          }
        }
      );
    }
  }
  onPasswordUpdateSubmit(): void {
    if (this.passwordUpdateForm && this.passwordUpdateForm.valid && this.professeur) {
      const oldPassword = this.passwordUpdateForm.value.oldPassword;
      
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
          console.error("An error occurred while updating password:", error.status);
          if (error.status === 400) {
            // Handle 400 (Bad Request) error, e.g., display an error message from the server
            window.alert('Bad request: ' + error.error);
          } else if (error.status === 200) {
            // Handle 200 (OK) response with error message, e.g., display an error message from the server
            window.alert('Success with error: ' + error.error);
          } else {
            // Handle other errors, e.g., display a generic error message
            window.alert("An error occurred while updating password. Please try again later.");
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

