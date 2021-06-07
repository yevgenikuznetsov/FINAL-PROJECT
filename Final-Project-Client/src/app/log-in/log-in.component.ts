import { HttpHeaders } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { DataCollactionService } from '../data-collaction.service';
import { User } from '../user';

@Component({
  selector: 'app-log-in',
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.css']
})
export class LogInComponent implements OnInit {
  @ViewChild('userInput') dataFromInput: NgForm;

  isLogIn: boolean = false;
  error: boolean = false;
  notExists: boolean = false;

  constructor(private dataService: DataCollactionService, private cookieService: CookieService) { }

  // Check Coockie if user was connected to the system
  ngOnInit(): void {
    var valueFromCook: string = this.cookieService.get('user-role');

    if (valueFromCook === 'analyst' || valueFromCook === 'security forces') {
      this.isLogIn = true;
    }
  }

  onSubmit() {
    var user = new User();

    // Get user input
    user.email = this.dataFromInput.form.value.email
    user.password = this.dataFromInput.form.value.password

    if (user.email == '' || user.password == '') {
      this.error = true
    } else {
      this.error = false

      // Check in DB if user exist
      this.dataService.checkUser(user).then((value: User) => {
        if (value == null) {
          this.notExists = true;
        } else {
          this.notExists = false;

          // Save user detiels in cookie
          this.cookieService.set('user-role', value.type);
          this.cookieService.set('user-location', value.location)
          this.dataService.user = value;

          window.location.reload();
        }
      })
    }
  }
}
