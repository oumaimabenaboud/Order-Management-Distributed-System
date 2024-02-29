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
      let new_router = '/';

    // Check if code is running in a browser environment
    if (!isPlatformBrowser(this.platformId)) {
      return false;
    }

    // Check if user is logged in
    const isLoggedIn = !!sessionStorage.getItem('id');
    const isAdmin = sessionStorage.getItem('isAdmin');

    if (!isLoggedIn) {
      return this.router.createUrlTree(['/login']);
    }

    // Define allowed routes based on user role
    let allowedRoutes: string[] = [];
    if (isAdmin === "true") {
      allowedRoutes = ['/prof-admin', '/structure-admin', '/rubrique-admin', '/product'];
    } else {
      allowedRoutes = ['/prof-dash', '/structuredetail', '/addcomande', '/settings', '/listeproduits'];
    }

    // Check if the requested route is allowed for the user's role
    if (allowedRoutes.some(route => state.url.startsWith(route))) {
      return true;
    } else if (isAdmin === "false") {
      return true; // Allow non-admin users to access '/structuredetail' and its children
    } else {
      // Redirect to default route based on user role
      const defaultRoute = isAdmin === "true" ? '/prof-admin' : '/prof-dash';
      return this.router.createUrlTree([defaultRoute]);
    }
  };
}

