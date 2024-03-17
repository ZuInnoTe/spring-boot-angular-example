import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { isDevMode } from "@angular/core";
import { Observable } from "rxjs/Observable";
import { of } from "rxjs";
import { OrderPage } from "./order.model";
import { MOCKDATA_ORDER_PAGE } from "../../mockdata/order.mockdata";

@Injectable({
  providedIn: "root",
})
export class OrderService {
  private endpoint = "/order";
  constructor(private http: HttpClient) {}

  getAllProducts(): Observable<OrderPage> {
    if (!isDevMode()) {
      return this.http.get<OrderPage>(this.endpoint);
    } else {
      return of(MOCKDATA_ORDER_PAGE);
    }
  }
}
