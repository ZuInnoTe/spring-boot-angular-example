import { TestBed } from "@angular/core/testing";

import { InventoryService } from "./inventory.service";

describe("InventoryService", () => {
  let service: Service1Service;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InventoryService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
