import { Component, ViewChild, ChangeDetectorRef, OnInit } from "@angular/core";
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
export class OrderComponent implements OnInit {
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

  pageEvent?: PageEvent;

  constructor(
    private cd: ChangeDetectorRef,
    private orderService: OrderService,
  ) {}

  ngOnInit() {
    this.refreshOrders();
  }

  handlePageEvent(e: PageEvent) {
    this.pageEvent = e;
    this.length = e.length;
    this.pageSize = e.pageSize;
    this.pageIndex = e.pageIndex;
    this.refreshOrders();
  }

  setPageSizeOptions(setPageSizeOptionsInput: string) {
    if (setPageSizeOptionsInput) {
      this.pageSizeOptions = setPageSizeOptionsInput
        .split(",")
        .map((str) => +str);
    }
  }

  refreshOrders() {
    this.orderService.getAllProducts().subscribe((orderPage) => {
      this.dataSource =
        orderPage.content !== undefined ? orderPage.content : this.dataSource;
      this.pageSize =
        orderPage.size !== undefined ? orderPage.size : this.pageSize;
      this.length =
        orderPage.totalElements !== undefined
          ? orderPage.totalElements
          : this.length;
      this.pageIndex =
        orderPage.number !== undefined ? orderPage.number : this.pageIndex;
      // this is only needed because it is called from an NgInit context. It is not needed if you trigger an async call from a template: https://angular.dev/best-practices/skipping-subtrees
      this.cd.markForCheck();
    });
  }
}
