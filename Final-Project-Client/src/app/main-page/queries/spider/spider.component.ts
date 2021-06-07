import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, NgForm, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog'
import { CookieService } from 'ngx-cookie-service';
import { DataCollactionService } from 'src/app/data-collaction.service';
import { DialogComponent } from 'src/app/dialog/dialog.component';


@Component({
  selector: 'app-spider',
  templateUrl: './spider.component.html',
  styleUrls: ['./spider.component.css']
})
export class SpiderComponent implements OnInit {
  defindeTime: string;
  spiderForm: FormGroup;
  reg: string = '(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?';

  constructor(private dataService: DataCollactionService, public dilog: MatDialog, private cookieService: CookieService) { }

  ngOnInit(): void {
    this.spiderForm = new FormGroup({
      emailFormControl: new FormControl(null, [
        Validators.required,
        Validators.pattern(this.reg)])
    });

    // Check when the global database was last searched
    this.updateTime()


    // Check the global database for new information every 5 minutes
    setInterval(() => {
      this.findDataInGlobalDB()
    }, 300000)
  }

  updateTime() {
    this.dataService.getSearchTime().then((value) => {
      if (value != null) {
        this.defindeTime = value.date;
      } else {
        this.defindeTime = "01-Jan-2021 00:00:00"
      }
    })
  }

  // Sending a Web Crawler
  onSubmit() {
    // Check user input
    if (!this.spiderForm.valid) {
      return;
    }

    this.dataService.collectInformation(this.spiderForm.value.emailFormControl, this.cookieService.get('user-location')).then(() => {
      this.dataService.checkInformation(this.spiderForm.value.emailFormControl).then((value) => {
        if (value == true) {
          this.dilog.open(DialogComponent)
          setTimeout(() => {
            window.location.reload()
          }, 5000);
        }
        this.spiderForm.reset()
      })
    })
  }

  // Search in global DB if it contain suspicious information
  findDataInGlobalDB() {
    this.dataService.findDataInGlobalDB(this.cookieService.get('user-location')).then((value) => {
      if (value == true) {
        // Show warning (pop-up window) that the information contains suspicious text
        this.dilog.open(DialogComponent)
        setTimeout(() => {
          window.location.reload()
        }, 5000);
      } else {
        window.location.reload()
      }
      this.spiderForm.reset()
    })
  }
}
