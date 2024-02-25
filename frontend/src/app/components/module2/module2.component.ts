import { Component, ViewChild } from "@angular/core";
import { MatAccordion } from "@angular/material/expansion";

@Component({
  selector: "app-module2",
  templateUrl: "./module2.component.html",
  styleUrls: ["./module2.component.scss"],
})
export class Module2Component {
  @ViewChild(MatAccordion) accordion: MatAccordion;
}
