import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginService } from '../services/login.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { state } from '@angular/animations';
import { PlatformLocation } from '@angular/common';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrl: './welcome.component.scss'
})
export class WelcomeComponent implements OnInit{
  
  constructor(
    private router: Router,
    private platformLocation: PlatformLocation
  ) {}

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

}
