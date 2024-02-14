import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProfesseurService } from '../services/professeur.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';

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
              private router: Router,
              private snackBar: MatSnackBar,
              private professorService: ProfesseurService,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const userId = params['userId'];
      console.log('User ID:', userId);
      // You can use the user ID as needed
    });
  }

  onPasswordChangeSubmit(): void {
    if (this.passwordChangeForm.valid) {
      const newPassword = this.passwordChangeForm.value.newPassword;
      const userId = this.route.snapshot.params['userId'];
      const updatedProfessor = { password: newPassword };
      this.professorService.updateProfessor(userId, updatedProfessor).subscribe(
        (response) => {
          console.log('Password updated successfully:', response);
        },
        (error) => {
          console.error('Error updating password:', error);
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
