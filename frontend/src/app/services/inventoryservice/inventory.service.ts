import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { isDevMode } from "@angular/core";
import { Observable, of } from "rxjs";
import { ProductPage } from "./inventory.model";
import { MOCKDATA_INVENTORY_PAGE } from "../../mockdata/inventory.mockdata";
import {
  SpringDataPageRequest,
  sddrToHttpParams,
} from "../general/paging.model";
import { LoggingService } from "../logging/logging.service";

@Injectable({
  providedIn: "root",
})
export class InventoryService {
  private endpoint = "/product";
  constructor(
    private http: HttpClient,
    private log: LoggingService,
  ) {}

  getAllProducts(pageable?: SpringDataPageRequest): Observable<ProductPage> {
    if (!isDevMode()) {
      return this.http.get<ProductPage>(this.endpoint, {
        params: sddrToHttpParams(pageable),
      });
    } else {
      this.log.warn(
        "Using Mockdata. This should not happen in a production application",
      );
      return of(MOCKDATA_INVENTORY_PAGE);
    }
  }
}
