import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AdminDashboardComponent} from "./admin-dashboard/admin-dashboard.component";
import { LoginComponent } from './login/login.component';
import {ProfAdminViewComponent} from "./prof-admin-view/prof-admin-view.component";
import {StructureAdminViewComponent} from "./structure-admin-view/structure-admin-view.component";
import {RubriqueAdminViewComponent} from "./rubrique-admin-view/rubrique-admin-view.component";
import {ChangePasswordComponent} from "./change-password/change-password.component";



const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path:'admin', component: AdminDashboardComponent },
  {path:'login' , component:LoginComponent},
  {path:'prof-admin' , component:ProfAdminViewComponent},
  {path:'structure-admin' , component:StructureAdminViewComponent},
  {path:'rubrique-admin' , component:RubriqueAdminViewComponent},
  {path:'change-password' , component:ChangePasswordComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
