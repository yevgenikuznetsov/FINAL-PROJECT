import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { HeaderComponent } from './header/header.component';
import { CalenderEventComponent } from './main-page/calender-event/calender-event.component';
import { QueriesComponent } from './main-page/queries/queries.component'
import { AgmCoreModule } from '@agm/core'

import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DialogComponent } from './dialog/dialog.component'
import { ScrollingModule } from '@angular/cdk/scrolling';
import { SpiderComponent } from './main-page/queries/spider/spider.component';
import { HistoryComponent } from './main-page/queries/history/history.component';
import { PersonInformationComponent } from './person-information/person-information.component';
import { MainPageComponent } from './main-page/main-page.component';
import { InformListComponent } from './person-information/inform-list/inform-list.component';
import { LocationComponent } from './person-information/location/location.component';

import { ChartsModule } from 'ng2-charts';

import { DatePipe } from '@angular/common';
import { ShowAllRemarksComponent } from './person-information/show-all-remarks/show-all-remarks.component';
import { AllRemarksDialogComponent } from './all-remarks-dialog/all-remarks-dialog.component';
import { LogInComponent } from './log-in/log-in.component';
import { CookieService } from 'ngx-cookie-service';
import { AllEventComponent } from './main-page/all-event/all-event.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    CalenderEventComponent,
    QueriesComponent,
    DialogComponent,
    SpiderComponent,
    HistoryComponent,
    PersonInformationComponent,
    MainPageComponent,
    InformListComponent,
    LocationComponent,
    ShowAllRemarksComponent,
    AllRemarksDialogComponent,
    LogInComponent,
    AllEventComponent,
  ],
  entryComponents: [AllRemarksDialogComponent, DialogComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ScrollingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatDialogModule,
    ChartsModule,
    BrowserAnimationsModule,
    AgmCoreModule.forRoot({
      apiKey: 'AIzaSyDLCZeO_EkbRg8YLmR8k2OWKqcnjli70wo',
      libraries: ['places']
    })
  ],
  providers: [DatePipe, CookieService],
  bootstrap: [AppComponent]
})
export class AppModule { }
