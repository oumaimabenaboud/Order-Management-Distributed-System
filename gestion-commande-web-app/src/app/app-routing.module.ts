import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ProfDashboardComponent} from "./prof-dashboard/prof-dashboard.component";
import { LoginComponent } from './login/login.component';
import {ProfAdminViewComponent} from "./prof-admin-view/prof-admin-view.component";
import {StructureAdminViewComponent} from "./structure-admin-view/structure-admin-view.component";
import {RubriqueAdminViewComponent} from "./rubrique-admin-view/rubrique-admin-view.component";
import {ChangePasswordComponent} from "./change-password/change-password.component";
import { WelcomeComponent } from './welcome/welcome.component';
import { authGuard } from './guards/auth.guard';
import { StructuredetailsComponent } from './structuredetails/structuredetails.component';
import { ProductDashComponent } from "./product-dash/product-dash.component";
import {AddcommandeComponent} from "./addcommande/addcommande.component";

const routes: Routes = [
  { path: '', redirectTo: 'welcome', pathMatch: 'full' },
  { path:'prof-dash', component: ProfDashboardComponent , canActivate: [authGuard]},
  { path:'login' , component: LoginComponent },
  { path:'welcome' , component: WelcomeComponent },
  { path:'prof-admin' , component: ProfAdminViewComponent , canActivate: [authGuard] },
  { path:'structure-admin' , component: StructureAdminViewComponent, canActivate: [authGuard] },
  { path:'rubrique-admin' , component: RubriqueAdminViewComponent, canActivate: [authGuard] },
  { path:'change-password' , component: ChangePasswordComponent },
  { path:"structuredetail/:structureId" , component: StructuredetailsComponent, canActivate: [authGuard] },
  { path:"product" , component: ProductDashComponent, canActivate: [authGuard] },
  { path:"structuredetail/:structureId/:budgetId/addcommande" , component: AddcommandeComponent, canActivate: [authGuard]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
