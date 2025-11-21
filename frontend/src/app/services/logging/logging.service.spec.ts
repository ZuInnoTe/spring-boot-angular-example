import { TestBed } from "@angular/core/testing";

import { LoggingService, LogLevel } from "./logging.service";

describe("LoggingService", () => {
  let service: LoggingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: "logLevel", useValue: LogLevel.Info }],
    });
    service = TestBed.inject(LoggingService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
