import {Component, OnInit} from '@angular/core';
import {Commande} from "../model/commande.model";
import {CommandesService} from "../services/commandes.service";

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']  // Fix the typo here
})

export class AdminDashboardComponent {
  status = false;  // Declare status property


  addToggle() {
    this.status = !this.status;
  }
}

