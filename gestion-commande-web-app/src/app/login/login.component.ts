import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginService } from '../services/login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private loginService: LoginService, private router: Router) {
    this.loginForm = fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20)]],
    });
  }

  onSubmit(): void {
    console.log('Form submitted:', this.loginForm.valid);
  
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;
      console.log('Email:', email);
      console.log('Password:', password);
  
      this.loginService.login(email, password).subscribe(
        (response) => {
          console.log('Login response:', response);
  
          if (response && response.success) {
            console.log('Login success:', response);
            this.router.navigate(['/admin']);
          } else {
            console.log('Login failed:', response.message);
          }
        },
        (error) => {
          console.error('Login error:', error);
        }
      );
    }
  }
    

  // Function to check if the user is an admin based on email and password
  private isAdmin(email: string, password: string): boolean {
    // Replace this logic with your actual admin check logic
    return email === 'admin' && password === 'admin';
  }
}
