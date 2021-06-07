import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LogInComponent } from './log-in/log-in.component';
import { MainPageComponent } from './main-page/main-page.component';
import { PersonInformationComponent } from './person-information/person-information.component';

const routes: Routes = [
  { path: "", component: MainPageComponent },
  { path: "searchPerson", component: PersonInformationComponent },
  { path: "logIn", component: LogInComponent }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
