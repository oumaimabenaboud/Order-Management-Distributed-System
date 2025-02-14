import {Component, OnInit} from '@angular/core';
import {StructuresService} from "../services/structures.service";
import {ActivatedRoute, Router} from "@angular/router";
import {BudgetService} from "../services/budget.service";
import {ProfesseurService} from "../services/professeur.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {PlatformLocation} from "@angular/common";
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Budget} from "../model/budget.model";
import {Professeur} from "../model/professeur.model";
import {ProductService} from "../services/product.service";
import { Location } from '@angular/common';
import {CommandesService} from "../services/commandes.service";

@Component({
  selector: 'app-addcommande',
  templateUrl: './addcommande.component.html',
  styleUrl: './addcommande.component.css'
})
export class AddcommandeComponent implements OnInit{
  status = true;
  structureId !:number;
  budgetId !:number;
  userId: number | null = null;
  professeurConnecté : Professeur | undefined;
  listrubriques: any[] = [];
  listproducts: any[] = [];
  structuredetail : any;
  currentBudget: any;
  listBudgetsStructure:any;
  newCommandForm!: FormGroup;
  commandLines!: FormArray;
  selectedProductRubrique: any;
  totalTTC: number = 0;


  constructor(
    private structureService: StructuresService,
    private router : Router,
    private route :ActivatedRoute,
    private budgetService: BudgetService,
    private profService: ProfesseurService,
    private productService: ProductService,
    private commandesService: CommandesService,
    private snackBar: MatSnackBar,
    private location: Location,
    private platformLocation: PlatformLocation,
    private formBuilder: FormBuilder) {
    this.structureId=route.snapshot.params['structureId']
    this.budgetId = route.snapshot.params['budgetId']

  }

  ngOnInit() {
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

  this.budgetService.getBudgetByStructureId(this.structureId).subscribe(
    (budgets: Budget[]) => { // Assuming Budget is the correct type
      this.listBudgetsStructure = budgets;
      console.log("all budgets", this.listBudgetsStructure);
      this.currentBudget = this.listBudgetsStructure.find((budget: Budget) => {
        // Convert selectedYear to number before comparison
        return budget.budgetYear === Number(new Date().getFullYear());
      });
    },
    (err) => {
      console.log(err);
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

    this.initnewCommandFormBuilder();
    this.calculateTotalTTC();

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
  calculateTotalTTC() {
    this.totalTTC = 0;
    const commandLines = this.newCommandForm.get('newCommandLines') as FormArray;
    commandLines.controls.forEach((commandLine: any) => {
      const quantity = commandLine.get('quantity')?.value;
      const prixTTC = commandLine.get('prixTTC')?.value;
      const productTotalTTC = quantity * prixTTC;
      this.totalTTC += productTotalTTC;
    });
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
      const ligneGroup = this.newCommandForm.get('newCommandLines').at(index) as FormGroup;
      ligneGroup.patchValue({
        produitRubriqueId: selectedProduct.rubriqueName
      });
    } else {
      console.log("Product not found");
    }
  }

  createCommandLine(): FormGroup {
    return this.formBuilder.group({
      productName: ['', Validators.required],
      produitRubriqueId: ['', Validators.required],
      prixHT: ['', Validators.required],
      prixTTC: ['', Validators.required],
      quantity: ['', Validators.required]
    });

    for (let i = 0; i < this.commandLines.length; i++) {
      this.newCommandForm.addControl(`commandLine${i}`, this.formBuilder.control(null));
    }
  }

  addNewCommandLine() {
    this.commandLines.push(this.createCommandLine());
  }

  removeCommandLine() {
    const commandLinesArray = this.newCommandForm.get('newCommandLines') as FormArray;
    commandLinesArray.removeAt(commandLinesArray.length - 1);
  }

  initnewCommandFormBuilder() {
    this.newCommandForm = this.formBuilder.group({
      newCommandLines: this.formBuilder.array([this.createCommandLine()])
    });

    this.commandLines = this.newCommandForm.get('newCommandLines') as FormArray;
    this.selectedProductRubrique = new Array(this.commandLines.length).fill('');
  }

  addCommande(){
    const formData = this.newCommandForm.value;
    // Initialize totalAlloue and totalRestant
    let totalHT = 0;
    let totalTTC = 0;

    const newCommande = {
      profId:this.professeurConnecté?.id,
      structureId: this.structureId,
      budgetId:this.budgetId,
      commandeDate: new Date(), // Convert to number if needed
      prixTotalHT: 0, // Initialize to 0
      prixTotalTTC: 0, // Initialize to 0
      commandeLines: [] // Initialize as an empty array
    };
    formData.newCommandLines.forEach((commandline: any) => {
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
      newCommande.commandeLines.push(commandeLine);
      newCommande.prixTotalHT=totalHT;
      newCommande.prixTotalTTC=totalTTC;
      });      // Log the new budget
      console.log(newCommande);
    this.commandesService.addCommande(newCommande).subscribe(
      () => {
        window.alert('Commande ajoutée avec succès !');
        window.location.reload();
      },
      error => {
        console.error("Une erreur s'est produite lors de l'ajout de la commande.", error);
        if (error.status === 200) {
          window.alert('Commande ajoutée avec succès !');
          window.location.reload();
        } else if (error.status === 400) {
          // Bad request, display error message from server
          window.alert(error.error);
        } else {
          // Other errors, display generic error message
          window.alert("Une erreur s'est produite lors de l'ajout de la commande. Veuillez réessayer plus tard.");
        }
      }
    );
}

  calculateTotal(field1: string, field2: string): number {
    const commandLines = this.newCommandForm.get('newCommandLines') as FormArray;
    let total = 0;
    commandLines.controls.forEach(commandLine => {
      total += commandLine.get(field1)?.value * commandLine.get(field2)?.value || 0;
    });
    return total;
  }


}
