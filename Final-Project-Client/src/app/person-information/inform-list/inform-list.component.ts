import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DataCollactionService } from 'src/app/data-collaction.service';
import { Person } from 'src/app/person'
import { Remark } from 'src/app/remark';

@Component({
  selector: 'app-inform-list',
  templateUrl: './inform-list.component.html',
  styleUrls: ['./inform-list.component.css']
})
export class InformListComponent implements OnInit {
  personName: string
  personExsist: boolean
  personRes: Person = new Person();
  allMark: Map<string, string> = new Map<string, string>()
  remarker: Remark;

  @ViewChild('remark') dataFromInput: NgForm

  constructor(private router: ActivatedRoute, private getPersonService: DataCollactionService) {
    this.remarker = new Remark();
  }

  ngOnInit(): void {
    this.router.queryParams.subscribe(params => {
      this.personName = params.searchName;
      this.getPersonService.searchName = this.personName;

      this.getPersonDeitals()
      // Get all person remarks
      this.getPersonService.getAllRemarks(this.personName).then((data: Remark) => {
        if (data != null) {
          this.remarker.remarks = data.remarks
        }
      })
    })
  }

  // Get Person information from DB
  getPersonDeitals() {
    this.getPersonService.getPerson(this.personName).then((person) => {
      if (person == null) {
        this.personExsist = false
      } else {
        this.personExsist = true

        this.personRes = person
        this.personName = person.name
      }
    })
  }

  // Save a remark for Entity
  onSubmit() {
    this.remarker.remarks[(new Date()).toLocaleString()] = this.dataFromInput.form.value.text

    this.getPersonService.postRemark(this.remarker, this.personName).then(() => {
      window.location.reload()
    })
  }
}
