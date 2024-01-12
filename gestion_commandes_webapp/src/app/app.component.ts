import { Component, OnInit } from '@angular/core';
import { CommandesService } from './services/commandes.service';
import { Commande } from './model/commande.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  status = false;
  commandes: Commande[] = [];

  constructor(private commandeService: CommandesService) { }

  ngOnInit(): void {
    this.getCommandes();
  }

  addToggle() {
    this.status = !this.status;
  }

  getCommandes() {
    this.commandeService.getCommandes().subscribe(
      (commandes: Commande[]) => {
        this.commandes.push(...commandes);
      },
      error => {
        console.error('Error fetching commandes:', error);
      }
    );
  }
}
