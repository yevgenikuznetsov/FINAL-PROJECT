import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { element } from 'protractor';
import { DataCollactionService } from '../../data-collaction.service';
import { Information } from '../../information';

@Component({
  selector: 'app-calender-event',
  templateUrl: './calender-event.component.html',
  styleUrls: ['./calender-event.component.css']
})
export class CalenderEventComponent implements OnInit {
  eventList: Information[] = []

  lastSearch: Date = new Date("01-Jan-2019 00:00:00");
  lastSearchString: string;

  constructor(private dataService: DataCollactionService, public datepipe: DatePipe) { }

  ngOnInit(): void {
    // Get event list - The last suspects messages that was found
    this.dataService.getEventList().then((event) => {
      if (event != null) {
        event.forEach((element: Information) => {
          var timeTemp = new Date(element.searchDate);

          // Set the last time
          if (timeTemp.getTime() > this.lastSearch.getTime()) {
            this.lastSearch = timeTemp
          }
          this.eventList.push(element)
        })
      }
    })
  }




}
