import { Component } from '@angular/core';

@Component({
  selector: 'app-addcommande',
  templateUrl: './addcommande.component.html',
  styleUrl: './addcommande.component.css'
})
export class AddcommandeComponent {
  status = true;

  addToggle() {
    this.status = !this.status;
  }


}
