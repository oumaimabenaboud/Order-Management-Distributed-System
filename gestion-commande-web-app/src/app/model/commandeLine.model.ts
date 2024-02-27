import {Commande} from "./commande.model";
import {Product} from "./product.model";
export interface CommandeLine {
  id: number;
  quantity: number;
  prixHT: number;
  prixTTC: number;
  commandeId: Commande;
  productId: number;
  productName: string;
  produitRubriqueId : number;
}
