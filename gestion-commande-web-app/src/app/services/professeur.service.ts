  import { Injectable } from '@angular/core';
  import {HttpClient, HttpParams} from "@angular/common/http";
  import {Observable} from "rxjs";
  import {Professeur} from "../model/professeur.model";

  @Injectable({
    providedIn: 'root'
  })
  export class ProfesseurService {
    private baseUrl = 'http://localhost:1818/PROFESSOR-SERVICE/professeurs';

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


    public updateProfessorAccess(id: number, access: boolean): Observable<Professeur> {
      // Create an object with the updated access
      const updatedProf: Partial<Professeur> = { droit_daccee: access };
      return this.http.put<Professeur>(`${this.baseUrl}/${id}`, updatedProf);
    }

    

    public searchProfessors(searchTerm: string): Observable<Professeur[]> {
      let params = new HttpParams();
      if (searchTerm) {
        params = params.set('searchTerm', searchTerm);
      }

      return this.http.get<Professeur[]>("http://localhost:1818/PROFESSOR-SERVICE/professeurs/search", { params })
    }
  }
