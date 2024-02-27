import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
import { PlatformLocation } from '@angular/common';
import { LoginService } from '../services/login.service';
import { ProfesseurService } from '../services/professeur.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {
  passwordChangeForm: FormGroup = this.fb.group({
    newPassword: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20)]],
    confirmPassword: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20)]]
  }, {
    validator: this.passwordMatchValidator
  });

  constructor(private fb: FormBuilder,
              private snackBar: MatSnackBar,
              private route: ActivatedRoute,
              private router: Router,
              private platformLocation: PlatformLocation,
              private loginService: LoginService,
              private profService: ProfesseurService
              ) { }

    
  isBrowser(): boolean {
    return typeof window !== 'undefined' && this.platformLocation !== null;
  }
  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const userId = params['userId'];
      console.log('User ID:', userId);
    });
    let firstLogin :boolean;

    if (this.isBrowser()) {
      // Check if user is logged in
      const isLoggedIn = !!sessionStorage.getItem('id');
      this.profService.getProfessor(this.route.snapshot.params['userId']).subscribe(
        (professor) => {
          firstLogin = professor.first_cnx;
          if (isLoggedIn && !firstLogin) {
            this.router.navigate(['/login']);
          }
        },
        (error) => {
          console.error('Error fetching professor:', error);
        }
      );

      
    }
  }

  onPasswordChangeSubmit(): void {
    if (this.passwordChangeForm.valid) {
      const newPassword = this.passwordChangeForm.value.newPassword;
      const userId = this.route.snapshot.params['userId'];
      const updatedProfessor = { mdp: newPassword };

      console.log(newPassword, userId, updatedProfessor);

      // Call the updatePassword function from LoginService
      this.loginService.updatePassword(userId, updatedProfessor).subscribe(
        response => {
          // Handle successful response
          console.log('Password updated successfully:', response);
          this.snackBar.open('Password updated successfully', 'Close', {
            duration: 3000,
          });
          this.router.navigate(['/login']);
        },
        error => {
          // Handle error
          console.error('Error updating password:', error);
          this.snackBar.open('Error updating password. Please try again.', 'Close', {
            duration: 3000,
          });
        }
      );
    }
  }

  passwordMatchValidator(formGroup: FormGroup) {
    const newPasswordControl = formGroup.get('newPassword');
    const confirmPasswordControl = formGroup.get('confirmPassword');

    if (newPasswordControl && confirmPasswordControl && newPasswordControl.value !== confirmPasswordControl.value) {
      confirmPasswordControl.setErrors({ passwordMismatch: true });
    } else {
      confirmPasswordControl?.setErrors(null);
    }
  }
}
