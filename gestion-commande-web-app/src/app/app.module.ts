import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { LoginComponent } from './login/login.component';
import { ProfAdminViewComponent } from './prof-admin-view/prof-admin-view.component';
import { StructureAdminViewComponent } from './structure-admin-view/structure-admin-view.component';
import { RubriqueAdminViewComponent } from './rubrique-admin-view/rubrique-admin-view.component';
import { ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule } from "@angular/common/http";
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ChangePasswordComponent } from './change-password/change-password.component'; // Import BrowserAnimationsModule

@NgModule({
  declarations: [
    AppComponent,
    AdminDashboardComponent,
    LoginComponent,
    ProfAdminViewComponent,
    StructureAdminViewComponent,
    RubriqueAdminViewComponent,
    ChangePasswordComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule, // Add BrowserAnimationsModule here
    AppRoutingModule,
    ReactiveFormsModule,
    BrowserModule.withServerTransition({ appId: 'my-app' }),
    HttpClientModule,
    FormsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
