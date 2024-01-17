// app-routing.module.ts

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';

const routes: Routes = [
  { path: '', redirectTo: 'admin', pathMatch: 'full' },
  { path: 'admin', component: AdminDashboardComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
