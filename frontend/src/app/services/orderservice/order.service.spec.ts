import { TestBed } from "@angular/core/testing";

import { OrderService } from "./order.service";

import { LogLevel } from "../logging/logging.service";

describe("OrderService", () => {
  let service: OrderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: "logLevel", useValue: LogLevel.Info }],
    });
    service = TestBed.inject(OrderService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
