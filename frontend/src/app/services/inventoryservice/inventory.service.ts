import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { isDevMode } from "@angular/core";
import { Observable } from "rxjs/Observable";
import { of } from "rxjs";
import { ProductPage } from "./inventory.model";
import { MOCKDATA_INVENTORY_PAGE } from "../../mockdata/inventory.mockdata";

@Injectable({
  providedIn: "root",
})
export class InventoryService {
  private endpoint = "/product";
  constructor(private http: HttpClient) {}

  getAllProducts(): Observable<ProductPage> {
    if (!isDevMode()) {
      return this.http.get<ProductPage>(this.endpoint);
    } else {
      return of(MOCKDATA_INVENTORY_PAGE);
    }
  }
}
