import { Component, OnInit } from '@angular/core';
import { StructuresService } from '../services/structures.service';
import { ActivatedRoute, Router } from '@angular/router';
import { BudgetService } from '../services/budget.service';
import { ProfesseurService } from '../services/professeur.service';
import { CommandesService } from '../services/commandes.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PlatformLocation } from '@angular/common';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import { Professeur } from '../model/professeur.model';
import {Budget} from "../model/budget.model";
import {Commande} from "../model/commande.model";

@Component({
  selector: 'app-listeproduits',
  templateUrl: './listeproduits.component.html',
  styleUrls: ['./listeproduits.component.css']
})
export class ListeproduitsComponent implements OnInit {
  commandeId!: number;
  userId: number | null = null;
  professeurConnecté: Professeur | undefined;
  status = true;
  isEditMode: boolean = false;
  listrubriques: any[] = [];
  commandeLines: any[] = [];
  commandeForm!: FormGroup;
  selectedCommande:any;

  constructor(
    private structureService: StructuresService,
    private router: Router,
    private route: ActivatedRoute,
    private budgetService: BudgetService,
    private profService: ProfesseurService,
    private commandeService: CommandesService,
    private snackBar: MatSnackBar,
    private platformLocation: PlatformLocation,
    private formBuilder: FormBuilder
  ) {
    this.commandeId = route.snapshot.params['commandeId'];
  }

  ngOnInit(): void {
    if (this.isBrowser()) {
      const id = sessionStorage.getItem('id');
      this.userId = id ? parseInt(id, 10) : null;

      if (this.userId) {
        this.profService.getProfessor(this.userId).subscribe(
          (professeurConnecté: Professeur) => {
            this.professeurConnecté = professeurConnecté;
          },
          (error) => {
            console.error('Error fetching professor:', error);
          }
        );
      } else {
        console.error('User ID not found in sessionStorage');
      }
    }

    this.commandeService.getCommandeById(this.commandeId).subscribe(
      (commande) =>{
        this.CommandeForm();
        console.log(commande);
      }
    )

  }

  isBrowser(): boolean {
    return typeof window !== 'undefined' && this.platformLocation !== null;
  }
  logout() {
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }

  addToggle() {
    this.status = !this.status;
  }

  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
  }

  CommandeForm() {
    this.commandeForm = this.formBuilder.group({
      commandeLines: this.formBuilder.array([]) // Initialize as an empty FormArray
    });

    this.commandeService.getCommandeById(this.commandeId).subscribe(
      (commande) => { // Assuming Budget is the correct type
        this.selectedCommande = commande;
        if (this.selectedCommande) {
          this.selectedCommande.commandeLines.forEach((commandeLine: any) => {
            this.addCommandeLine(commandeLine);
          });
        }
      },
      (err) => {
        console.log(err);
      }
    );
  }
// Function to clear existing controls in the commandeLines FormArray
  clearCommandeLinesFormArray() {
    const commandeLinesFormArray = this.commandeForm.get('commandeLines') as FormArray;
    while (commandeLinesFormArray.length !== 0) {
      this.removeCommandeLine(0);
    }
  }

  addCommandeLine(commandeLine: any = null) {
    const commandeLineFormGroup = this.createCommandeLineFormGroup(commandeLine);
    (this.commandeForm.get('commandeLines') as FormArray).push(commandeLineFormGroup);
    this.commandeLines.push(commandeLineFormGroup);
  }

  removeCommandeLine(index: number) {
    (this.commandeForm.get('commandeLines') as FormArray).removeAt(index);
    this.commandeLines.splice(index, 1);
  }

  createCommandeLineFormGroup(commandeLine: any = null): FormGroup {
    return this.formBuilder.group({
      productName: [commandeLine ? commandeLine.productName : '', [Validators.required]],
      produitRubriqueId: [commandeLine ? commandeLine.produitRubriqueId : '', [Validators.required]],
      prixHT: [commandeLine ? commandeLine.prixHT : '', [Validators.required]],
      prixTTC: [commandeLine ? commandeLine.prixTTC : '', [Validators.required]],
      quantity: [commandeLine ? commandeLine.quantity : '', [Validators.required]],

    });
  }
}
