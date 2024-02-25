import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { Module1Component } from "./components/module1/module1.component";
import { Module2Component } from "./components/module2/module2.component";

export const routes: Routes = [
  { path: "module1", component: Module1Component },
  { path: "module2", component: Module2Component },
  { path: "", redirectTo: "/module1", pathMatch: "full" },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
