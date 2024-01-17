import {Commande} from "./commande.model";
import {Product} from "./product.model";
export interface CommandeLine {
  id: number;
  quantity: number;
  total_prixHT_ligne: number;
  total_prixTTC_ligne: number;
  commande: Commande;
  product: Product;
  productName: string;
}
