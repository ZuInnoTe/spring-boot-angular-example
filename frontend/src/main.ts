import { bootstrapApplication } from "@angular/platform-browser";
import { AppComponent } from "./app/app.component";
import { provideAnimations } from "@angular/platform-browser/animations";
import { provideHttpClient, withXsrfConfiguration } from "@angular/common/http";
import { GlobalErrorHandlerInterceptorProvider } from "./app/services/globalerrorhandler/interceptor/httpconfig.interceptor";
import { routes } from "./app/app-routing.module";
import { provideRouter } from "@angular/router";
import {
  LogLevel,
  LoggingService,
} from "./app/services/logging/logging.service";

bootstrapApplication(AppComponent, {
  providers: [
    { provide: LoggingService, useClass: LoggingService },
    { provide: "logLevel", useValue: LogLevel.Info },
    provideAnimations(),
    provideRouter(routes),
    provideHttpClient(
      withXsrfConfiguration(
        // Spring Boot standard: https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-token-repository-cookiecd
        { cookieName: "XSRF-TOKEN", headerName: "X-XSRF-TOKEN" },
      ),
    ),
    GlobalErrorHandlerInterceptorProvider,
  ],
}).catch((e) => console.error(e));
