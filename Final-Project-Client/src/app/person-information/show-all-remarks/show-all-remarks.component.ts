import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AllRemarksDialogComponent } from 'src/app/all-remarks-dialog/all-remarks-dialog.component';


@Component({
  selector: 'app-show-all-remarks',
  templateUrl: './show-all-remarks.component.html',
  styleUrls: ['./show-all-remarks.component.css']
})
export class ShowAllRemarksComponent implements OnInit {

  constructor(public dilog: MatDialog) { }

  ngOnInit(): void {
  }


  // Show all remarks for person
  onSubmit() {
    this.dilog.open(AllRemarksDialogComponent, {
      height: '50%',
      width: '50%',
    });
  }

}

