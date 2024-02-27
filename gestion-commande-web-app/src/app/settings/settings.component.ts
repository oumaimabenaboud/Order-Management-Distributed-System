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
  passwordChangeForm: FormGroup = this.fb.group({
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
  onPasswordChangeSubmit(): void {
    if (this.passwordChangeForm && this.passwordChangeForm.valid) {
      // Handle form submission logic here
      console.log('this.professeur.mdp:', this.professeur?.mdp);
      console.log('this.passwordChangeForm.value.oldPass:', this.passwordChangeForm.value.oldPassword);
      console.log(this.passwordChangeForm.value);
      this.loginService.isSamePassword(this.passwordChangeForm.value.oldPassword, this.professeur).subscribe(
        (response: any) => {
          console.log('response:', response);
          // if (response === 'true') {
          //   const newPassword = this.passwordChangeForm.value.newPassword;
          //   const updatedProfessor = { mdp: newPassword };
          //   this.loginService.updatePassword(this.userId as number, updatedProfessor).subscribe(
          //     (response: any) => {
          //       console.log('Password updated:', response);
          //       this.passwordChangeForm.reset();
          //     },
          //     (error) => {
          //       console.error('Error updating password:', error);
          //     }
          //   );
          // } else {
          //   console.error('Old password does not match');
          // }
        }, 
        (error) => {
          console.error('Error checking old password:', error);
        }
      );
    }
  }

}
