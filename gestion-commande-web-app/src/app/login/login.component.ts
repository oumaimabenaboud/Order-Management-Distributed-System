import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginService } from '../services/login.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
//npm install @angular/material

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private loginService: LoginService,
    private router: Router,
    private snackBar: MatSnackBar // Inject MatSnackBar here
  ) {
    this.loginForm = fb.group({
      email: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20)]],
    });
    
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;
  
      this.loginService.login(email, password).subscribe(
        (response) => {
          if (response) {
            if (response === 'Admin login successful') {
              console.log(response);
              this.router.navigate(['/admin']);
            } else if (response === 'Invalid email format' || response === 'Email and password are required') {
              this.openErrorSnackBar(response);
            } else if (response === 'User connected for the first time') {
              // console.log(response);
              this.loginService.getUserIdByEmail(email).subscribe(
                (professor: any) => {
                  // console.log('Professor:', professor);
                  const userId = professor && professor.id;
                  if (userId) {
                    this.router.navigate(['/change-password', { userId: userId }]);
                  } else {
                    console.error('User ID not found for email:', email);
                    this.openErrorSnackBar('User ID not found for email');
                  }
                },
                (error) => {
                  console.error('Error fetching user ID:', error);
                  this.openErrorSnackBar('Error fetching user ID');
                }
              );              
            } else if (response === 'Professor not found') {
              this.openErrorSnackBar("Professeur not found");
            } else if (response === 'Login successful') {
              console.log(response);
              this.loginService.getUserIdByEmail(email).subscribe(
                (professor: any) => {
                  // console.log('Professor:', professor);
                  const userId = professor && professor.id;
                  if (userId) {
                    console.log('local storage:', userId);
                    sessionStorage.setItem('id', JSON.stringify(userId));
                    // sessionStorage.setItem('id',userId)
                    this.router.navigate(['/admin']);                  
                  } else {
                    console.error('User ID not found for email:', email);
                    this.openErrorSnackBar('User ID not found for email');
                  }
                },
                (error) => {
                  console.error('Error fetching user ID:', error);
                  this.openErrorSnackBar('Error fetching user ID');
                }
              ); 
            } else if (response === 'Invalid credentials') {
              this.openErrorSnackBar(response);
            }
          } else {
            console.log(response);
          }
        },
        (error) => {
          console.error('Login error:', error);
          
          if (error instanceof HttpErrorResponse) {
            try {
              console.log('Server error:', JSON.parse(error.error));
            } catch (e) {
              console.error('Error parsing server response:', e);
            }
          }
          
          this.openErrorSnackBar('An unexpected error occurred');
        }
      );
    }
  }
  

  openErrorSnackBar(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000, // Adjust duration as needed
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['error-snackbar'] // Add custom styling if needed
    });
}}