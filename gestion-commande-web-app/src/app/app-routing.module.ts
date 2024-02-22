import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AdminDashboardComponent} from "./admin-dashboard/admin-dashboard.component";
import { LoginComponent } from './login/login.component';
import {ProfAdminViewComponent} from "./prof-admin-view/prof-admin-view.component";
import {StructureAdminViewComponent} from "./structure-admin-view/structure-admin-view.component";
import {RubriqueAdminViewComponent} from "./rubrique-admin-view/rubrique-admin-view.component";
import {ChangePasswordComponent} from "./change-password/change-password.component";
import { WelcomeComponent } from './welcome/welcome.component';
import { authGuard } from './guards/auth.guard';


const routes: Routes = [
  { path: '', redirectTo: 'welcome', pathMatch: 'full' },
  { path:'admin', component: AdminDashboardComponent , canActivate: [authGuard]},
  {path:'login' , component:LoginComponent},
  {path:'welcome' , component:WelcomeComponent},
  {path:'prof-admin' , component:ProfAdminViewComponent , canActivate: [authGuard]},
  {path:'structure-admin' , component:StructureAdminViewComponent},
  {path:'rubrique-admin' , component:RubriqueAdminViewComponent},
  {path:'change-password' , component:ChangePasswordComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
