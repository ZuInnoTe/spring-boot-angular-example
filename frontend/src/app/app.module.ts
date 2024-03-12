import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {
	HttpClientModule,
	HTTP_INTERCEPTORS,
	HttpClientXsrfModule,
  } from '@angular/common/http';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
// Own components
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { Module1Component } from './components/module1/module1.component';
import { Module2Component } from './components/module2/module2.component';
import { HttpConfigInterceptor } from './services/globalerrorhandler/interceptor/httpconfig.interceptor';
// Angular Material Components
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import {MatSnackBarModule} from '@angular/material/snack-bar'; 
import { MatTableModule } from '@angular/material/table';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';

@NgModule({
	declarations: [AppComponent, HeaderComponent, Module1Component, Module2Component],
	imports: [
		BrowserModule,
		AppRoutingModule,
		BrowserAnimationsModule,
		MatButtonModule,
		MatDatepickerModule,
		MatExpansionModule,
		MatFormFieldModule,
		MatIconModule,
		MatInputModule,
		MatNativeDateModule,
		MatSnackBarModule,
		MatTableModule,
		MatToolbarModule,
		MatTooltipModule,
		HttpClientXsrfModule,  
		HttpClientXsrfModule.withOptions({  // Spring Boot standard: https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-token-repository-cookiecd
			cookieName: 'XSRF-TOKEN',
			headerName: 'X-XSRF-TOKEN', 
		  }),
	],
	providers: [  {
		provide: HTTP_INTERCEPTORS,
		useClass: HttpConfigInterceptor,
		multi: true,
	  }],
	bootstrap: [AppComponent]
})
export class AppModule {}
