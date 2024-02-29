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
    const id = sessionStorage.getItem('id');
    const isAdmin = sessionStorage.getItem('isAdmin');

    if (!isLoggedIn) {
      return this.router.createUrlTree(['/login']);
    }

    const allowedRoutesForAdmin = ['/prof-admin', '/structure-admin', '/rubrique-admin','/product'];
    const allowedRoutesForNonAdmin = ['/prof-dash', '/structuredetail','/product','/addcomande','/settings','/listeproduits'];

    // console.log(state.url," ",allowedRoutesForNonAdmin.some(route => state.url.startsWith(route))," ",state.url.startsWith('/structuredetail'))


     if (isAdmin == "false" && allowedRoutesForNonAdmin.includes(state.url) || state.url.startsWith('/structuredetail') || state.url.startsWith('/listeproduits')){
      return true;
    }else  if (isAdmin == "true" && allowedRoutesForAdmin.includes(state.url)){
      return true;
    } else {
      const defaultRoute = isAdmin === "true" ? '/prof-admin' : '/prof-dash';
      return this.router.createUrlTree([defaultRoute]);
    }
  };
}
