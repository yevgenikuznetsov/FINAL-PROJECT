import { Component, OnInit } from '@angular/core';
import { DataCollactionService } from '../data-collaction.service';
import { MoreInformation } from '../more-information';
import { Chart } from 'node_modules/chart.js';

import { ChartDataSets, ChartOptions, ChartType } from 'chart.js';
import { Color, BaseChartDirective, Label } from 'ng2-charts';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {
  isAnalyst: boolean;
  moreInformation: MoreInformation = new MoreInformation();

  // Set the values ​​of the diagram
  public lineChartData: ChartDataSets[] = [
    { data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], label: 'Total messages was found' }
  ];
  public lineChartLabels: Label[] = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];

  public lineChartColors: Color[] = [
    {
      backgroundColor: 'rgba(148,159,177,0.2)',
      borderColor: 'rgba(148,159,177,1)',
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: 'rgb(255, 255, 255)',
      pointHoverBorderColor: 'rgb(255, 255, 255)'
    }
  ];

  public lineChartLegend = true;
  public lineChartType: ChartType = 'line';

  constructor(private dataService: DataCollactionService, private cookieService: CookieService) { }

  ngOnInit(): void {
    this.initInformation();

    if (this.cookieService.get('user-role') == 'analyst') {
      this.isAnalyst = true;
    }

    Chart.defaults.global.defaultFontColor = 'white';
  }

  // Get value from DB to set in chart
  initInformation() {
    this.dataService.getMoreInformation().then((data: MoreInformation) => {
      this.moreInformation = data;
    }).then(() => {
      this.lineChartData[0].data = this.moreInformation.month
    })
  }

}
