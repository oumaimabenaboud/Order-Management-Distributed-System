import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class authGuard implements CanActivate {

  constructor(private router: Router, @Inject(PLATFORM_ID) private platformId: Object) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean | UrlTree {
    
    // Check if code is running in a browser environment
    if (!isPlatformBrowser(this.platformId)) {
      // Handle server-side rendering or other non-browser environments
      return false;
    }

    // Check if user is logged in
    const isLoggedIn = !!sessionStorage.getItem('id');
    const id = sessionStorage.getItem('id');
    if (state.url === '/admin' && id === '-1') {
      this.router.navigate(['/prof-admin']);
    }

    console.log('isLoggedIn:', isLoggedIn, 'id : ',id);
    if (isLoggedIn) {
      return true;
    } else {
      // Redirect to login page
      return this.router.createUrlTree(['/login']);
    }
  }
}
