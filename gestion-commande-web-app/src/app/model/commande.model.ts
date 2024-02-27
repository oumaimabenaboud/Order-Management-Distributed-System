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
  EN_COURS = 'En cours',
  PASSE = 'Passée',
  ANNULÉE = 'Annulée',
  LIVRÉE = 'Livrée'
}

export namespace commandestype {
  export function toString(type: commandestype): string {
    switch (type) {
      case commandestype.EN_COURS:
        return "En cours";
      case commandestype.PASSE:
        return "Passée";
      case commandestype.ANNULÉE:
        return "Annulée";
      case commandestype.LIVRÉE:
        return "Livrée";
      default:
        throw new Error("Invalid commandes type");
    }
  }
}

