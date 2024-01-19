import {Component, OnInit} from '@angular/core';
import {Commande} from "../model/commande.model";
import {CommandesService} from "../services/commandes.service";

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']  
})

export class AdminDashboardComponent implements OnInit {
  status = false;  // Declare status property
  commandes: Commande[] = [];

  constructor(private commandesService: CommandesService) { }

  ngOnInit(): void {
    this.getCommandes();
  }

  getCommandes() {
    this.commandesService.getCommandes().subscribe(
      (commandes: Commande[]) => {
        this.commandes = commandes;
      },
      error => {
        console.error('Error fetching commandes:', error);
      }
    );
  }

  addToggle() {
    this.status = !this.status;
  }
}

