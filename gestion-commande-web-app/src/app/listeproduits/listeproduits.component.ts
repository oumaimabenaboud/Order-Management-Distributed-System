import { Component, OnInit } from '@angular/core';
import { StructuresService } from '../services/structures.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { BudgetService } from '../services/budget.service';
import { ProfesseurService } from '../services/professeur.service';
import { CommandesService } from '../services/commandes.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PlatformLocation } from '@angular/common';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import { Professeur } from '../model/professeur.model';
import {ProductService} from "../services/product.service";
import {structurestype} from "../model/structure.model";
import {commandestype} from "../model/commande.model";

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
  listproducts: any[] = [];
  commandeLines: any[] = [];
  commandeForm!: FormGroup;
  selectedCommande:any;
  selectedProductRubrique: any;
  commandesTypes: string[] = Object.values(commandestype)
  .filter(value => typeof value === 'string') as string[];
  selectedCommandeType:any;

constructor(
    private structureService: StructuresService,
    private router: Router,
    private route: ActivatedRoute,
    private budgetService: BudgetService,
    private profService: ProfesseurService,
    private commandeService: CommandesService,
    private snackBar: MatSnackBar,
    private productService: ProductService,
    private platformLocation: PlatformLocation,
    private formBuilder: FormBuilder,
    private location: Location
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

   /* this.commandeService.getCommandeById(this.commandeId).subscribe(
      (commande) =>{
        console.log(commande);
        this.CommandeForm();
      },
      (error) => {
        console.error('Error fetching command:', error);
      }
    );*/
    this.commandeService.getCommandeById(this.commandeId).subscribe(
      (commande) =>{
        console.log(commande);
        this.CommandeForm();
        this.selectedCommandeType = commande.type;
        console.log(this.selectedCommandeType);
      },
      (error) => {
        console.error('Error fetching command:', error);
      }
    );

    this.budgetService.getAllRubriques().subscribe(
      (data)=>{
        this.listrubriques = data;
      },
      (error)=> console.error(error)
    );

    this.productService.getAllProducts().subscribe(
      (data)=>{
        this.listproducts = data;
      },
      (error)=> console.error(error)
    );
  }

  isBrowser(): boolean {
    return typeof window !== 'undefined' && this.platformLocation !== null;
  }
  logout() {
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }
  goBack(): void {
    this.location.back();
  }


  addToggle() {
    this.status = !this.status;
  }

  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
  }
  CommandeForm() {
    this.commandeForm = this.formBuilder.group({
      commandeType :['', [Validators.required]],
      commandeLines: this.formBuilder.array([]) // Initialize as an empty FormArray
    });
    this.selectedProductRubrique = new Array(this.commandeLines.length).fill('');
    this.commandeService.getCommandeById(this.commandeId).subscribe(
      (commande) => { // Assuming Budget is the correct type
        this.selectedCommande = commande;
        if (this.selectedCommande) {
          this.commandeForm.patchValue({
            commandeType: this.selectedCommande.type,
          });
          console.log(this.selectedCommande.type);
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
      rubriqueName: this.getRubriqueName(commandeLine ? commandeLine.produitRubriqueId : '')
    });
  }

  toggleEditModeAndReload() {
    if (this.isEditMode) {
      // Reload the page
      window.location.reload();
    } else {
      // Toggle edit mode without reloading
      this.toggleEditMode();
    }
  }

  getRubriqueName(produitRubriqueId: any): string {
    const rubrique = this.listrubriques.find(rubrique => rubrique.id === produitRubriqueId);
    return rubrique ? rubrique.nom : '';
  }
  updateRubriqueName(event: any, index: number) {
    const productName = event.target.value;
    const selectedProduct = this.listproducts.find(product => product.nom === productName);
    if (selectedProduct) {
      // Update the selectedProductRubrique array at the specified index
      this.selectedProductRubrique[index] = selectedProduct.rubriqueName;
      console.log(this.selectedProductRubrique);

      // Update the produitRubriqueId control for the specific form group
      // @ts-ignore
      const ligneGroup = this.commandeForm.get('commandeLines').at(index) as FormGroup;
      ligneGroup.patchValue({
        produitRubriqueId: selectedProduct.rubriqueName
      });
    } else {
      console.log("Product not found");
    }
  }
  onCommandeTypeChange(): void {
    //this.selectedCommandeType=this.selectedCommandeType
    console.log('Dropdown value changed:', this.selectedCommandeType);
  }
  Modifier() {
    const formData = this.commandeForm.value;
    let totalHT = 0;
    let totalTTC = 0;

    let existingCommande = this.commandeService.getCommandeById(this.commandeId);
    const updatedCommande = {
      prixTotalHT: 0, // Initialize to 0
      prixTotalTTC: 0, // Initialize to 0
      commandeLines: [], // Initialize as an empty array
      type :''
    };
    formData.commandeLines.forEach((commandline: any) => {
      const selectedProductName = commandline.productName;
      const selectedProduct = this.listproducts.find(product => product.nom === selectedProductName);
      if (!selectedProduct){
        window.alert("Veuillez choisir un produit parmi la liste des produits disponibles !");
      }
      const prixHT = commandline.prixHT;
      const prixTTC = commandline.prixTTC;
      const produitRubriqueId= selectedProduct.rubriqueId;
      const quantity = commandline.quantity;
      // Create a new rubrique allocation object
      const commandeLine = {
        quantity: commandline.quantity,
        prixHT: commandline.prixHT,
        prixTTC: commandline.prixTTC,
        productId: selectedProduct.id,
        productName: selectedProductName,
        produitRubriqueId : selectedProduct.rubriqueId,
      };

      // Increment totalAlloue and totalRestant
      totalHT += prixHT*quantity;
      totalTTC += prixTTC*quantity;
      // @ts-ignore
      updatedCommande.commandeLines.push(commandeLine);
      updatedCommande.prixTotalHT=totalHT;
      updatedCommande.prixTotalTTC=totalTTC;
    });
    updatedCommande.type = this.selectedCommandeType;
    console.log(updatedCommande);
    this.commandeService.updateCommande(this.commandeId, updatedCommande).subscribe(
      () => {
        window.alert('Commande mise à jour avec succès !');
        window.location.reload();
      },
      error => {
        console.error("Une erreur s'est produite lors de la mise à jour de la commande.", error);
        if (error.status === 200) {
          window.alert('Commande mise à jour avec succès !');
          window.location.reload();
        } else if (error.status === 400) {
          window.alert(error.error);
        } else {
          // Other errors, display generic error message
          window.alert("Une erreur s'est produite lors de la mise à jour de la commande. Veuillez réessayer plus tard.");
        }
      }
    );
  }
  calculateTotal(field1: string, field2: string): number {
    const commandLines = this.commandeForm.get('commandeLines') as FormArray;
    let total = 0;
    commandLines.controls.forEach(commandLine => {
      total += commandLine.get(field1)?.value * commandLine.get(field2)?.value || 0;
    });
    return total;
  }
}
