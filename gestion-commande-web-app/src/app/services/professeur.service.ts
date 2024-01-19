import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Professeur} from "../model/professeur.model";

@Injectable({
  providedIn: 'root'
})
export class ProfesseurService {

  constructor(private http: HttpClient) { }

  public getProfessors():Observable<Array<Professeur>>{
    return this.http.get<Array<Professeur>>("http://localhost:1818/PROFESSOR-SERVICE/professeurs")
  }

  public getProfessor(id: any):Observable<Professeur>{
    return this.http.get<Professeur>("http://localhost:1818/PROFESSOR-SERVICE/professeurs/"+id)
  }
  public addProfessor(Professeur: any) {
    return this.http.post("http://localhost:1818/PROFESSOR-SERVICE/professeurs", Professeur)
  }
  public deleteProfessor(id: any) {
    return this.http.delete("http://localhost:1818/PROFESSOR-SERVICE/professeurs/"+id)
  }
  public updateProfessor(profId: any, Professeur: any) {
    return this.http.put("http://localhost:1818/PROFESSOR-SERVICE/professeurs/"+profId, Professeur)
  }
}
