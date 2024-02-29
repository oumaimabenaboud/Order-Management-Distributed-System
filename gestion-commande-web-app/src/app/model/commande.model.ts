import {CommandeLine} from "./commandeLine.model";
import {Professeur} from "./professeur.model";


export interface Commande {
  id: number;
  commandeDate: Date;
  commandeLines: CommandeLine[];
  profId: number;
  structureId: number;
  budgetId: number;
  prixTotalHT: number;
  prixTotalTTC: number;
  type: commandestype;
}
export enum commandestype {
  EN_COURS,
  PASSEE,
  ANNULÉE ,
  LIVRÉE
}


