import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginService } from '../services/login.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { state } from '@angular/animations';
import { PlatformLocation } from '@angular/common';
//npm install @angular/material

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit{
  loginForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private loginService: LoginService,
    private router: Router,
    private snackBar: MatSnackBar,
    private platformLocation: PlatformLocation
  ) {
    this.loginForm = fb.group({
      email: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20)]],
    });

  }
  isBrowser(): boolean {
    return typeof window !== 'undefined' && this.platformLocation !== null;
  }

  ngOnInit(): void {
    if (this.isBrowser()) {
      // Check if user is logged in
      const isLoggedIn = !!sessionStorage.getItem('id');
      const id = sessionStorage.getItem('id');
      const isAdmin = sessionStorage.getItem('isAdmin');

      if (isLoggedIn) {
        const defaultRoute = isAdmin === "true" ? '/prof-admin' : '/prof-dash';
        this.router.navigate([defaultRoute]);
      }
    }
  }
  

  onSubmit(): void {
    let userId = null;
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;

      this.loginService.login(email, password).subscribe(
        (response) => {
          if (response) {
            if (response === 'Admin login successful') {
              console.log(response);
              this.loginService.getUserIdByEmail(email).subscribe(
                (professor: any) => {
                  console.log('Professor:', professor);
                  const userId = professor && professor.id;
                  const userIsAdmin = professor && professor.admin;
                  if (userId != null && userIsAdmin != null) {
                    sessionStorage.setItem('id', JSON.stringify(userId));
                    sessionStorage.setItem('isAdmin', JSON.stringify(userIsAdmin)); // Storing isAdmin attribute
                    this.router.navigate(['/prof-admin']);
                  } else {
                    console.error('User ID or isAdmin attribute not found for professor:', professor);
                    this.openErrorSnackBar("Identifiant utilisateur ou attribut isAdmin non trouvé");
                  }
                },
                (error) => {
                  console.error('Error fetching user ID:', error);
                  this.openErrorSnackBar("Erreur lors de la récupération de l'identifiant utilisateur");
                }                
              );
            } else if (response === 'Invalid email format' || response === 'Email and password are required') {
              this.openErrorSnackBar(response);
            } else if (response === 'User connected for the first time') {
              // console.log(response);
              this.loginService.getUserIdByEmail(email).subscribe(
                (professor: any) => {
                  console.log('Professor:', professor);
                  const userId = professor && professor.id;
                  const userIsAdmin = professor && professor.admin;
                  if (userId != null && userIsAdmin != null) {
                    sessionStorage.setItem('id', JSON.stringify(userId));
                    sessionStorage.setItem('isAdmin', JSON.stringify(userIsAdmin)); // Storing isAdmin attribute
                    this.router.navigate(['/change-password', { userId: userId }]);
                  } else {
                    console.error('User ID or isAdmin attribute not found for professor:', professor);
                    this.openErrorSnackBar("Identifiant utilisateur ou attribut isAdmin non trouvé");
                  }
                },
                (error) => {
                  console.error('Error fetching user ID:', error);
                  this.openErrorSnackBar("Erreur lors de la récupération de l'identifiant utilisateur");
                }  
              );
            } else if (response === 'Professor not found') {
              this.openErrorSnackBar("Professeur non trouvé");
            } else if (response === 'Login successful') {
              console.log(response);
              this.loginService.getUserIdByEmail(email).subscribe(
                (professor: any) => {
                  console.log('Professor:', professor);
                  const userId = professor && professor.id;
                  const userIsAdmin = professor && professor.admin;
                  if (userId != null && userIsAdmin != null) {
                    sessionStorage.setItem('id', JSON.stringify(userId));
                    sessionStorage.setItem('isAdmin', JSON.stringify(userIsAdmin)); // Storing isAdmin attribute
                    this.router.navigate(['/prof-dash']);
                  } else {
                    console.error('User ID or isAdmin attribute not found for professor:', professor);
                    this.openErrorSnackBar("Identifiant utilisateur ou attribut isAdmin non trouvé");
                  }
                },
                (error) => {
                  console.error('Error fetching user ID:', error);
                  this.openErrorSnackBar("Erreur lors de la récupération de l'identifiant utilisateur");
                }  
              );
            } else if (response === 'Invalid credentials') {
              this.openErrorSnackBar("Informations d'identification invalides");
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

          this.openErrorSnackBar("Une erreur inattendue s'est produite");
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
