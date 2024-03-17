import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { InventoryComponent } from "./components/inventory/inventory.component";
import { OrderComponent } from "./components/order/order.component";

export const routes: Routes = [
  { path: "ui/inventory", component: InventoryComponent },
  { path: "ui/order", component: OrderComponent },
  { path: "", redirectTo: "ui/inventory", pathMatch: "full" },
];
