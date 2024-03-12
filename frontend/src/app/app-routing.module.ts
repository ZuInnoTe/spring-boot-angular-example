import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { Module1Component } from "./components/module1/module1.component";
import { Module2Component } from "./components/module2/module2.component";

export const routes: Routes = [
  { path: "ui/module1", component: Module1Component },
  { path: "ui/module2", component: Module2Component },
  { path: "", redirectTo: "ui/module1", pathMatch: "full" },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
