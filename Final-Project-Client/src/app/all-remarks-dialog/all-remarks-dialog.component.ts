import { Component, Inject, OnInit } from '@angular/core';
import { DataCollactionService } from '../data-collaction.service';
import { Remark } from '../remark';

@Component({
  selector: 'app-all-remarks-dialog',
  templateUrl: './all-remarks-dialog.component.html',
  styleUrls: ['./all-remarks-dialog.component.css']
})
export class AllRemarksDialogComponent implements OnInit {
  allMark: Map<string, string> = new Map<string, string>()
  remarker: Remark = new Remark();


  constructor(private getPersonService: DataCollactionService) { }

  ngOnInit(): void {
    // Get all Person Remarks from server 
    this.getPersonService.getAllRemarks(this.getPersonService.getSearchName()).then((data: Remark) => {
      if (data != null) {
        this.remarker.remarks = data.remarks

        for (var value in this.remarker.remarks) {
          this.allMark.set(value, this.remarker.remarks[value])
        }
      }
    })
  }

}
