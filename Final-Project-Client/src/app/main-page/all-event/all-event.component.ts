import { Component, OnInit } from '@angular/core';
import { DataCollactionService } from 'src/app/data-collaction.service';
import { Information } from 'src/app/information';

@Component({
  selector: 'app-all-event',
  templateUrl: './all-event.component.html',
  styleUrls: ['./all-event.component.css']
})
export class AllEventComponent implements OnInit {

  allEvent: Information[] = []
  lastAlert: string = '01-Jan-2019 00:00:00'

  constructor(private dataService: DataCollactionService) { }

  ngOnInit(): void {
    this.getAllEvent()

    // Refresh the page to see if there are any new suspicious messages
    setInterval(() => { window.location.reload() }, 300000)
  }

  // View all suspicious information found so far
  getAllEvent() {
    this.dataService.getAllEvent().then((data) => {
      data.forEach(element => {
        this.allEvent.push(element)

        if (Date.parse(this.lastAlert) < Date.parse(element.searchDate)) {
          this.lastAlert = element.searchDate
        }
      });
    })
  }

}
