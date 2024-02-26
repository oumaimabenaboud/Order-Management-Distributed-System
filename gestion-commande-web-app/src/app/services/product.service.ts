import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {Rubrique} from "../model/rubrique.model";
import {Product} from "../model/product.model";

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  constructor(private http: HttpClient) { }

  public getAllProducts(): Observable<Array<Product>> {
    return this.http.get<Array<Product>>("http://localhost:1818/PRODUCT-SERVICE/products");
  }

  public getProductById(id: any): Observable<Product> {
    return this.http.get<Product>("http://localhost:1818/PRODUCT-SERVICE/products/" + id);
  }

  public getAllRubriques(): Observable<Array<Rubrique>> {
    return this.http.get<Array<Rubrique>>("http://localhost:1818/PRODUCT-SERVICE/products/rubProd");
  }

  public searchProducts(searchTerm: string): Observable<Product> {
    let params = new HttpParams();
    if(searchTerm){
      params = params.set('searchTerm', searchTerm);
    }
    return this.http.get<Product>("http://localhost:1818/PRODUCT-SERVICE/products/search", { params });
  }

  public deleteProduct(id: any): Observable<any> {
    return this.http.delete("http://localhost:1818/PRODUCT-SERVICE/products/" + id);
  }

  public createProduct(product: any): Observable<any> {
    return this.http.post("http://localhost:1818/PRODUCT-SERVICE/products", product);
  }

  public updateProduct(id: any, product: any): Observable<any> {
    return this.http.put("http://localhost:1818/PRODUCT-SERVICE/products/" + id, product);
  }
}
