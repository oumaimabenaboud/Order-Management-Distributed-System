import {Component, OnInit} from '@angular/core';
import {ProfesseurService} from "../services/professeur.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Clipboard} from "@angular/cdk/clipboard";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Professeur} from "../model/professeur.model";
import {ProductService} from "../services/product.service";
import {Product} from "../model/product.model";
import { PlatformLocation } from '@angular/common';
import { Router } from '@angular/router';
import e from 'express';

@Component({
  selector: 'app-product-dash',
  templateUrl: './product-dash.component.html',
  styleUrl: './product-dash.component.css'
})
export class ProductDashComponent implements OnInit{

  products : any;
  isDetailsFormOpen: boolean = false;
  isNewProductFormOpen: boolean = false;
  selectedProduct: any;
  searchTerm: string = '';
  userId: number | null = null;
  isAdmin: boolean = false;
  professeur : Professeur | undefined;

  status = true;
  public newProductForm! : FormGroup;
  detailsForm!: FormGroup;


  constructor(
    private productService: ProductService,
    private profService: ProfesseurService,
    private formBuilder: FormBuilder,
    private clipboard: Clipboard,
    private snackBar: MatSnackBar,
    private platformLocation: PlatformLocation,
    private router: Router
  ) { }

  ngOnInit(): void {
    if (this.isBrowser()) {
      const id = sessionStorage.getItem('id');
      this.userId = id ? parseInt(id, 10) : null;
      const isAdmin = sessionStorage.getItem('isAdmin');
      this.isAdmin = isAdmin === "true" ? true : false;
      // console.log(this.isAdmin);

      if (this.userId) {
        this.profService.getProfessor(this.userId).subscribe(
          (professor: Professeur) => {
            this.professeur = professor;
          },
          (error) => {
            console.error('Error fetching professor:', error);
          }
        );
      } else {
        console.error('User ID not found in sessionStorage');
      }
      this.productService.getAllProducts().subscribe(
        { next:(data)=>{
            this.products = data;
            console.log(data);
          },
          error : (err)=>console.error(err)
        });
      this.initDetailsFormBuilder();
      this.initNewProductFormBuilder();
  }
  }
  isBrowser(): boolean {
    return typeof window !== 'undefined' && this.platformLocation !== null;
  }
  addToggle() {
    this.status = !this.status;
  }

  logout() {
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }

  getProducts(id: any, event?: DragEvent):void {
    if (event) {
      event.preventDefault();
    }
    this.productService.getProductById(id).subscribe(
      { next:(product)=>{
          this.products = product;
          // console.log(product);
          this.detailsForm.patchValue({
            nom: product.nom,
            desc: product.Desc,
            rubrique: product.Rubrique
          })
          this.openDetailsForm();
        }, 
        error : (err)=>console.error(err)
      });
  }
  search() {
    // If both prenom and nom are empty, reset the table to show all professors
    if (!this.searchTerm) {
      this.productService.getAllProducts();
      return;
    }
    this.productService.searchProducts(this.searchTerm).subscribe({
      next: (data) => {
        this.products = data;
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  deleteProduct(id: any, event?: DragEvent): void {
    if (event) {
      // If the function is called from a drag event, prevent the default behavior
      event.preventDefault();
    }

    if (confirm("Êtes-vous sûr de vouloir supprimer ce produit ?")) {
      this.productService.deleteProduct(id).subscribe({
        next: () => {
          window.alert("Produit supprimé avec succès !");
          window.location.reload();
        },
        error: err => console.log(err)
      });
    }
  }

  onDragStart(event: DragEvent, data: string): void {
    event.dataTransfer?.setData('text/plain', data);
  }

  allowDrop(event: DragEvent): void {
    event.preventDefault();
  }

  openDetailsForm() {
    this.isDetailsFormOpen = true;
    this.isNewProductFormOpen = false;
  }

  closeDetailsForm() {
    this.isDetailsFormOpen = false;
  }

  openNewProductForm() {
    this.isNewProductFormOpen = true;
    this.isDetailsFormOpen = false;
  }

  closeNewProductForm() {
    this.isNewProductFormOpen = false;
  }

  private initNewProductFormBuilder() {
    this.newProductForm = this.formBuilder.group({
      nom: this.formBuilder.control('', [Validators.required])
    });
  }

  saveNewProduct() {
    let Product: Product = this.newProductForm.value;
    this.productService.createProduct(Product).subscribe({
      next: (newProduct) => {
        this.products.push(newProduct);
        window.alert("Produit ajouté avec succès !");
        window.location.reload();
        this.closeNewProductForm();
      },
      error: (error) => {
        console.error(error);
        window.alert("Une erreur s'est produite lors de l'ajout du produit. Veuillez réessayer plus tard.");
      }
    });
  }

  private initDetailsFormBuilder() {
    this.detailsForm = this.formBuilder.group({
      nom: this.formBuilder.control('', [Validators.required])
    });
  }

  isEditMode: boolean = false;

  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
  }

  saveProductChanges() {
    const updatedProduct: Product = this.detailsForm.value;
    this.productService.updateProduct(this.selectedProduct.id, updatedProduct).subscribe({
      next: () => {
        window.alert("Produit mis à jour avec succès !");
        window.location.reload();
        this.isEditMode = false;
      },
      error: (error) => {
        console.error("Produit mis à jour avec succès !", error);
        if (error.status === 200) {
          window.alert("Produit mis à jour avec succès !");
          window.location.reload();
        } else if (error.status === 400) {
          window.alert(error.error);
        } else {
          window.alert("Une erreur s'est produite lors de la mise à jour du produit. Veuillez réessayer plus tard.");
        }
      }
    });
  }
}
