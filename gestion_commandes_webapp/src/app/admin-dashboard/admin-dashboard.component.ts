import {Component, OnInit} from '@angular/core';
import {Commande} from "../model/commande.model";
import {CommandesService} from "../services/commandes.service";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    NgClass
  ],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit{
  status = false;
  commandes: Commande[] = [];

  constructor(private commandeService: CommandesService) {}

  ngOnInit(): void {
    //this.getCommandes();
  }

  addToggle() {
    this.status = !this.status;
  }


}
