import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable, catchError} from "rxjs";
import {Structure} from "../model/structure.model";
import {droitAcces} from "../model/droitAcces";
import {Professeur} from "../model/professeur.model";
import {Rubrique} from "../model/rubrique.model";

@Injectable({
  providedIn: 'root'
})
export class StructuresService {

  constructor(private http: HttpClient) { }

  public getAllStructures():Observable<Array<Structure>>{
    return this.http.get<Array<Structure>>("http://localhost:1818/STRUCTURE-SERVICE/structures")
  }

  public getStructureById(id: any):Observable<Structure>{
    return this.http.get<Structure>("http://localhost:1818/STRUCTURE-SERVICE/structures/"+id)
  }
  getStructuresByType(type: string): Observable<Structure[]> {
    return this.http.get<Structure[]>("http://localhost:1818/STRUCTURE-SERVICE/structures/byType/"+type);
  }

  public addStructure(Structure: any) {
    return this.http.post("http://localhost:1818/STRUCTURE-SERVICE/structures", Structure)
  }

  public deleteStructure(id: any) {
    return this.http.delete("http://localhost:1818/STRUCTURE-SERVICE/structures/"+id)
  }
  public updateStructure(id: any, Structure: any) {
    return this.http.put("http://localhost:1818/STRUCTURE-SERVICE/structures/"+id, Structure)
  }
  public searchStructures(searchTerm: string): Observable<Structure[]> {
    let params = new HttpParams();
    if (searchTerm) {
      params = params.set('searchTerm', searchTerm);
    }

    return this.http.get<Structure[]>("http://localhost:1818/STRUCTURE-SERVICE/structures/search", { params })
  }

  public getStructuresByResponsable(professorId: number): Observable<Structure[]> {
    return this.http.get<Structure[]>("http://localhost:1818/STRUCTURE-SERVICE/structures/byResponsable/"+professorId);
  }

  public getStructuresByEquipeMember(professorId: number): Observable<Structure[]> {
    return this.http.get<Structure[]>("http://localhost:1818/STRUCTURE-SERVICE/structures/byEquipeMember/"+professorId);
  }

  public getAllDroitAcces():Observable<Array<droitAcces>>{
    return this.http.get<Array<droitAcces>>("http://localhost:1818/STRUCTURE-SERVICE/droitAcces")
  }

  public getDroitAccessById(id: any): Observable<droitAcces> {
    return this.http.get<droitAcces>("http://localhost:1818/STRUCTURE-SERVICE/droitAcces/" + id);
  }

  public getDroitAccessByProfessorId(idProfessor: any):Observable<droitAcces>{
    return this.http.get<droitAcces>("http://localhost:1818/STRUCTURE-SERVICE/droitAcces/getAllDroitAccesByProfessorId/"+idProfessor)
  }

  public getDroitAccessByStructureId(idStructure: any):Observable<droitAcces>{
    return this.http.get<droitAcces>("http://localhost:1818/STRUCTURE-SERVICE/droitAcces/getAllDroitAccesByStructureId/"+idStructure)
  }

  public getDroitAccessByProfessorIdAndStructureId(idProfessor: any, idStructure: any):Observable<droitAcces>{
    return this.http.get<droitAcces>("http://localhost:1818/STRUCTURE-SERVICE/droitAcces/byProfessorIdAndStructureId/"+idProfessor+"/"+idStructure)
  }

  public updateDroitAccess(updatedDroitAccess: droitAcces, idProfessor: any, idStructure: any): Observable<droitAcces>{
    const params = new HttpParams()
      .set('idProfessor', idProfessor)
      .set('idStructure', idStructure);
  
    return this.http.put<droitAcces>("http://localhost:1818/STRUCTURE-SERVICE/droitAcces/updateDroitAccess", updatedDroitAccess, { params });
  }

}

