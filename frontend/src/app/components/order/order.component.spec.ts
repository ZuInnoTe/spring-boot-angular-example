import { TestbedHarnessEnvironment } from "@angular/cdk/testing/testbed";

import { ComponentFixture, TestBed } from "@angular/core/testing";;
import { provideRouter } from "@angular/router";
import { OrderComponent } from "./order.component";

import {
  LoggingService,
  LogLevel,
} from "../../services/logging/logging.service";

describe("OrderComponent", () => {
  let component: OrderComponent;
  let fixture: ComponentFixture<OrderComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [OrderComponent],
      providers: [
        { provide: LoggingService, useClass: LoggingService },
        { provide: "logLevel", useValue: LogLevel.Info },
        provideRouter([]),
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(OrderComponent);
    loader = TestbedHarnessEnvironment.loader(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
