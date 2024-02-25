import { Injectable } from "@angular/core";
import {
  HttpInterceptor,
  HttpRequest,
  HttpResponse,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse,
} from "@angular/common/http";

import { Observable, throwError } from "rxjs";
import { map, catchError } from "rxjs/operators";

import { GlobalerrorhandlerService } from "../globalerrorhandler.service";

@Injectable()
export class HttpConfigInterceptor implements HttpInterceptor {
  constructor(private globalerrorhandler: GlobalerrorhandlerService) {}
  intercept(
    request: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      map((event: HttpEvent<any>) => {
        return event;
      }),
      catchError((error: HttpErrorResponse) => {
        this.globalerrorhandler.display_error(
          "Backend error: " + error.message,
        );

        return throwError(error);
      }),
    );
  }
}
