import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Structure} from "../model/structure.model";

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
  public addStructure(Structure: any) {
    return this.http.post("http://localhost:1818/STRUCTURE-SERVICE/structures/", Structure)
  }

  public deleteStructure(id: any) {
    return this.http.delete("http://localhost:1818/STRUCTURE-SERVICE/structures/"+id)
  }
}