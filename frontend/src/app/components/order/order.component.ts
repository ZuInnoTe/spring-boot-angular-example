import { Component, ViewChild } from "@angular/core";
import { PageEvent, MatPaginatorModule } from "@angular/material/paginator";
import { MatTableModule } from "@angular/material/table";
import { OrderService } from "../../services/orderservice/order.service";
import { Order } from "../../services/orderservice/order.model";

@Component({
  selector: "app-order",
  templateUrl: "./order.component.html",
  styleUrls: ["./order.component.scss"],
  imports: [MatPaginatorModule, MatTableModule],
})
export class OrderComponent {
  displayedColumns: string[] = [
    "id",
    "orderDateTime",
    "productId",
    "productName",
    "productPrice",
  ];
  dataSource: Order[] = [];

  length = 50;
  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions = [5, 10, 25];

  hidePageSize = false;
  showPageSizeOptions = true;
  showFirstLastButtons = true;
  disabled = false;

  pageEvent: PageEvent;

  constructor(private orderService: OrderService) {
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
    this.orderService.getAllProducts().subscribe((orderPage) => {
      this.dataSource = orderPage.content;
      this.pageSize = orderPage.size;
      this.length = orderPage.totalElements;
      this.pageIndex = orderPage.number;
    });
  }
}
