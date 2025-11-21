import { TestBed } from "@angular/core/testing";

import { InventoryService } from "./inventory.service";
import { LogLevel } from "../logging/logging.service";

describe("InventoryService", () => {
  let service: InventoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: "logLevel", useValue: LogLevel.Info }],
    });
    service = TestBed.inject(InventoryService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
