import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ProfesseurService} from "../services/professeur.service";
import {Professeur} from "../model/professeur.model";

@Component({
  selector: 'app-prof-admin-view',
  templateUrl: './prof-admin-view.component.html',
  styleUrls: ['./prof-admin-view.component.css']
})
export class ProfAdminViewComponent implements OnInit {
  profs : any;
  isFormOpen: boolean = false;
  constructor(private  profService:ProfesseurService, private formBuilder: FormBuilder) { }
  ngOnInit(): void {
    this.profService.getProfessors().subscribe(
      { next:(data)=>{
          this.profs = data;
        },
        error : (err)=>{}
      });
    this.initFormBuilder();
  }
  getProf(id: any) {
    this.profService.getProfessor(id);
  }
  deleteProf(id: any) {
    if (confirm("Are you sure you want to delete this professor?")) {
      this.profService.deleteProfessor(id).subscribe({
        next: () => {
          window.alert("Professor deleted successfully!");
          window.location.reload();
        },
        error: err => console.log(err)
      });
    }
  }



  openForm() {
    this.isFormOpen = true;
  }

  closeForm() {
    this.isFormOpen = false;
  }
public newProfForm! : FormGroup;

  saveNewProf() {
    let prof: Professeur = this.newProfForm.value;
    this.profService.addProfessor(prof).subscribe({
      next: (newProf) => {
        this.profs.push(newProf); 
        window.alert("Professor added successfully!"); 
        window.location.reload();
        this.closeForm(); // Optionally close the form
      }
    });
  }


private initFormBuilder() {
    this.newProfForm = this.formBuilder.group({
      prenom: this.formBuilder.control('', [Validators.required]),
      nom: this.formBuilder.control('', [Validators.required]),
      mail: this.formBuilder.control('', [Validators.required])
    });
  }
  status = false;
  addToggle() {
    this.status = !this.status;
  }
}
