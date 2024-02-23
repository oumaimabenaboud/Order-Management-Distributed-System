import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ProfDashboardComponent } from './prof-dashboard/prof-dashboard.component';
import { LoginComponent } from './login/login.component';
import { ProfAdminViewComponent } from './prof-admin-view/prof-admin-view.component';
import { StructureAdminViewComponent } from './structure-admin-view/structure-admin-view.component';
import { RubriqueAdminViewComponent } from './rubrique-admin-view/rubrique-admin-view.component';
import { ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule } from "@angular/common/http";
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ChangePasswordComponent } from './change-password/change-password.component'; // Import BrowserAnimationsModule
import { ClipboardModule } from '@angular/cdk/clipboard';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { Routes } from '@angular/router';
import { WelcomeComponent } from './welcome/welcome.component';
import { StructuredetailsComponent } from './structuredetails/structuredetails.component';

// const appRoutes: Routes = [
//   // { path: 'login', component: LoginComponent },
//   {
//     path: "login",
//     loadChildren:
//       "./modules/authentication/authentication.module#AuthenticationModule"
//   }, // {path: 'login', component: ReactiveLoginComponent},
//   {
//     path: "logout",
//     loadChildren:
//       "./modules/authentication/authentication.module#AuthenticationModule"
//   },
//   {
//     path: "home",
//     component: HomeComponent,
//     canActivate: [AuthGuard],
//     children: [
//       { path: "employeeList", component: EmployeeListComponent },
//       { path: "addEmployee", component: AddEmployeeComponent },
//       { path: "employee/:key", component: EmployeeComponent }
//     ]
//   },
//   { path: "**", component: PageNotFoundComponent }
// ];

@NgModule({
  declarations: [
    AppComponent,
    ProfDashboardComponent,
    LoginComponent,
    ProfAdminViewComponent,
    StructureAdminViewComponent,
    RubriqueAdminViewComponent,
    ChangePasswordComponent,
    WelcomeComponent,
    StructuredetailsComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule, // Add BrowserAnimationsModule here
    AppRoutingModule,
    ReactiveFormsModule,
    ClipboardModule,
    MatSnackBarModule,
    BrowserModule.withServerTransition({ appId: 'my-app' }),
    HttpClientModule,
    FormsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
