import {CommandeLine} from "./commandeLine.model";
import {Professeur} from "./professeur.model";


export interface Commande {
  id: number;
  billingDate: Date;
  commandeLines: CommandeLine[];
  profID: number;
  professeur: Professeur;
  prix_total_HT: number;
  prix_total_TTC: number;
  type: commandestype;
}
enum commandestype {
  encours,passed
}
