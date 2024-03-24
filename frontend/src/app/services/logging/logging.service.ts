import { Inject, Injectable, Provider } from "@angular/core";

export enum LogLevel {
  All = 0,
  Debug = 1,
  Info = 2,
  Warn = 3,
  Error = 4,
}

@Injectable({
  providedIn: "root",
})
export class LoggingService {
  private logLevel: LogLevel;
  constructor(@Inject("logLevel") logLevel: LogLevel) {
    this.logLevel = logLevel;
  }

  public error(msg: string) {
    if (this.logLevelEnabled(LogLevel.Error)) {
      console.error(this.formatMessage(msg));
    }
  }

  public warn(msg: string) {
    if (this.logLevelEnabled(LogLevel.Warn)) {
      console.warn(this.formatMessage(msg));
    }
  }

  public info(msg: string) {
    if (this.logLevelEnabled(LogLevel.Info)) {
      console.info(this.formatMessage(msg));
    }
  }

  public debug(msg: string) {
    if (this.logLevelEnabled(LogLevel.Debug)) {
      console.debug(this.formatMessage(msg));
    }
  }

  private formatMessage(msg: string) {
    let currentDate = new Date();
    return `${currentDate} - ${msg}`;
  }

  private logLevelEnabled(logLevel: LogLevel) {
    if (logLevel >= this.logLevel) return true;
    return false;
  }
}
