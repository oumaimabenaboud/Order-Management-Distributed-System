import { Component } from '@angular/core';

@Component({
  selector: 'app-listeproduits',
  templateUrl: './listeproduits.component.html',
  styleUrl: './listeproduits.component.css'
})
export class ListeproduitsComponent {

  status = true;
  isEditMode: boolean = false;


  addToggle() {
    this.status = !this.status;
  }
toggleEditMode() {
  this.isEditMode = !this.isEditMode;
}

}
