import { Injectable, NgZone } from "@angular/core";

import { MatSnackBar } from "@angular/material/snack-bar";

@Injectable({
  providedIn: "root",
})
export class GlobalerrorhandlerService {
  constructor(
    private zone: NgZone,
    private snackBar: MatSnackBar,
  ) {}
  // note the css styles for the snackbar for this handler are globally defined in styles.css

  display_error(errormsg: string) {
    this.snackBar.open(errormsg, "❎", {
      duration: 10000,
      panelClass: ["snackbar-error"],
    });
  }

  display_warn(warnmsg: string) {
    this.zone.run(() => {
      this.snackBar.open(warnmsg, "⚠", {
        duration: 10000,
        panelClass: ["snackbar-warning"],
      });
    });
  }
  display_success(successmsg: string) {
    this.zone.run(() => {
      this.snackBar.open(successmsg, "✓", {
        duration: 10000,
        panelClass: ["snackbar-success"],
      });
    });
  }
}
