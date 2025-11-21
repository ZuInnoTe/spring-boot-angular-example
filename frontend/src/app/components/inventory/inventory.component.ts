import { Component } from "@angular/core";
import { PageEvent, MatPaginatorModule } from "@angular/material/paginator";
import { MatTableModule } from "@angular/material/table";
import { InventoryService } from "../../services/inventoryservice/inventory.service";
import { Observable } from "rxjs";
import {
  Product,
  ProductPage,
} from "../../services/inventoryservice/inventory.model";
import { LoggingService } from "../../services/logging/logging.service";

@Component({
  selector: "app-inventory",
  templateUrl: "./inventory.component.html",
  styleUrls: ["./inventory.component.scss"],
  imports: [MatPaginatorModule, MatTableModule],
})
export class InventoryComponent {
  displayedColumns: string[] = ["id", "name", "price"];
  dataSource: Product[] = [];

  length: number = 50;
  pageSize: number = 10;
  pageIndex = 0;
  pageSizeOptions = [5, 10, 25];

  hidePageSize = false;
  showPageSizeOptions = true;
  showFirstLastButtons = true;
  disabled = false;

  pageEvent?: PageEvent;

  constructor(
    private inventoryService: InventoryService,
    private log: LoggingService,
  ) {
    this.refreshInventory();
  }

  handlePageEvent(e: PageEvent) {
    this.pageEvent = e;
    this.length = e.length;
    this.pageSize = e.pageSize;
    this.pageIndex = e.pageIndex;
    this.refreshInventory();
  }

  setPageSizeOptions(setPageSizeOptionsInput: string) {
    if (setPageSizeOptionsInput) {
      this.pageSizeOptions = setPageSizeOptionsInput
        .split(",")
        .map((str) => +str);
    }
  }

  refreshInventory() {
    this.log.info("Refreshing inventory");
    this.inventoryService.getAllProducts().subscribe((productPage) => {
      this.dataSource =
        productPage.content !== undefined
          ? productPage.content
          : this.dataSource;
      this.pageSize =
        productPage.size !== undefined ? productPage.size : this.pageSize;
      this.length =
        productPage.totalElements !== undefined
          ? productPage.totalElements
          : this.length;
      this.pageIndex =
        productPage.number !== undefined ? productPage.number : this.pageIndex;
    });
  }
}
