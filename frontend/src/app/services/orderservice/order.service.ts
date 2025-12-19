import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { isDevMode } from "@angular/core";
import { Observable } from "rxjs";
import { of } from "rxjs";
import { OrderPage } from "./order.model";
import { MOCKDATA_ORDER_PAGE } from "../../mockdata/order.mockdata";
import {
  SpringDataPageRequest,
  sddrToHttpParams,
} from "../general/paging.model";
import { LoggingService } from "../logging/logging.service";

@Injectable({
  providedIn: "root",
})
export class OrderService {
  private http = inject(HttpClient);
  private log = inject(LoggingService);

  private endpoint = "/order";

  constructor() {}

  getAllProducts(pageable?: SpringDataPageRequest): Observable<OrderPage> {
    if (!isDevMode()) {
      return this.http.get<OrderPage>(this.endpoint, {
        params: sddrToHttpParams(pageable),
      });
    } else {
      this.log.warn(
        "Using Mockdata. This should not happen in a production application",
      );
      return of(MOCKDATA_ORDER_PAGE);
    }
  }
}
