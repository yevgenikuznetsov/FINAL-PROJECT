import { Component, OnInit } from '@angular/core';
import { DataCollactionService } from 'src/app/data-collaction.service';
import { HistoryL } from 'src/app/history-l';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {
  events: HistoryL[] = []
  loading: boolean = false

  constructor(private dataService: DataCollactionService) {
    // View History List - The sites that we send Crawler
    this.dataService.getHistoryList().then((event) => {
      if (event != null) {
        event.forEach((element: HistoryL) => {
          this.events.push(element)
        })
      }
    }).then(() => {
      this.loading = true
    })
  }

  ngOnInit(): void {

  }

}
