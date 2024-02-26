import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {Rubrique} from "../model/rubrique.model";
import {Professeur} from "../model/professeur.model";
import {Budget} from "../model/budget.model";
@Injectable({
  providedIn: 'root'
})
export class BudgetService {

  constructor(private http: HttpClient) { }

  //Rubrique
  public getAllRubriques():Observable<Array<Rubrique>>{
    return this.http.get<Array<Rubrique>>("http://localhost:1818/BUDGET-SERVICE/budget-management/rubriques")
  }

  public getRubriqueById(id: any):Observable<Rubrique>{
    return this.http.get<Rubrique>("http://localhost:1818/BUDGET-SERVICE/budget-management/rubriques/"+id)
  }
  public addRubrique(Rubrique: any) {
    return this.http.post("http://localhost:1818/BUDGET-SERVICE/budget-management/rubriques", Rubrique)
  }

  public deleteRubrique(id: any) {
    return this.http.delete("http://localhost:1818/BUDGET-SERVICE/budget-management/rubriques/"+id)
  }
  public updateRubrique(id: any, Rubrique: any) {
    return this.http.put("http://localhost:1818/BUDGET-SERVICE/budget-management/rubriques/"+ id, Rubrique);
  }


  public searchRubriques(searchTerm: string): Observable<Rubrique[]> {
    let params = new HttpParams();
    if (searchTerm) {
      params = params.set('searchTerm', searchTerm);
    }

    return this.http.get<Rubrique[]>("http://localhost:1818/BUDGET-SERVICE/budget-management/rubriques/search", { params })
  }

  //Budget

  public getBudgetByStructureId(id: any):Observable<Budget[]>{
    return this.http.get<Budget[]>("http://localhost:1818/BUDGET-SERVICE/budget-management/budget/byStructure/"+id)
  }
  public addBudget(Budget: any) {
    return this.http.post("http://localhost:1818/BUDGET-SERVICE/budget-management/budget", Budget)
  }

  public updateBudget(id: any, Budget: any) {
    return this.http.put("http://localhost:1818/BUDGET-SERVICE/budget-management/budget/"+id, Budget)
  }
}
