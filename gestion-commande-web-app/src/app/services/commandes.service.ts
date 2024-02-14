import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Commande} from "../model/commande.model";

@Injectable({
  providedIn: 'root'
})
export class CommandesService {

  constructor(private http: HttpClient) { }

  public getCommandes():Observable<Array<Commande>>{
    return this.http.get<Array<Commande>>("http://localhost:1818/COMMANDE-SERVICE/commandes")
  }

  public getCommande(id: any):Observable<Commande>{
    return this.http.get<Commande>("http://localhost:1818/COMMANDE-SERVICE/commandes/"+id)
  }
  addCommande(Commande: any) {
    return this.http.post("http://localhost:1818/COMMANDE-SERVICE/commandes", Commande)
  }
  deleteCommande(id: any) {
    return this.http.delete("http://localhost:1818/COMMANDE-SERVICE/commandes/"+id)
  }
  updateCommande(commandeId: any, Commande: any) {
    return this.http.put("http://localhost:1818/COMMANDE-SERVICE/commandes/"+commandeId, Commande)
  }

  

}
