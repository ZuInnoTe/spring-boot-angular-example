import { HarnessLoader } from "@angular/cdk/testing";
import { Router } from "@angular/router";
import { RouterTestingHarness } from "@angular/router/testing";
import { provideRouter } from "@angular/router";
import { TestbedHarnessEnvironment } from "@angular/cdk/testing/testbed";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { InventoryComponent } from "./inventory.component";

import {
  LoggingService,
  LogLevel,
} from "../../services/logging/logging.service";

let loader: HarnessLoader;

describe("InventoryComponent", () => {
  let component: InventoryComponent;
  let fixture: ComponentFixture<InventoryComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [InventoryComponent],
      providers: [
        { provide: LoggingService, useClass: LoggingService },
        { provide: "logLevel", useValue: LogLevel.Info },
        provideRouter([]),
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(InventoryComponent);
    loader = TestbedHarnessEnvironment.loader(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
