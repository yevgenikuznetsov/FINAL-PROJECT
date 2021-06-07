import { Component, OnInit } from '@angular/core';
import { MapsAPILoader } from '@agm/core';
import { DataCollactionService } from 'src/app/data-collaction.service';
import { ActivatedRoute } from '@angular/router';
import { Person } from 'src/app/person';

@Component({
  selector: 'app-location',
  templateUrl: './location.component.html',
  styleUrls: ['./location.component.css']
})
export class LocationComponent implements OnInit {
  latitude: number = 32.0870396;
  longitude: number = 34.8676295;
  zoom: number;

  personName: string
  markers: Array<any> = [];

  private geocoder;
  personRes: Person = new Person();

  constructor(private router: ActivatedRoute, private getPersonService: DataCollactionService, private mapsAPILoader: MapsAPILoader) { }

  // Get person name from query Params
  ngOnInit(): void {
    this.router.queryParams.subscribe(params => {
      this.personName = params.searchName;
      this.getPersonDeitals()
    })

    this.zoom = 9;
  }

  // Get all information about person fron DB
  getPersonDeitals() {
    this.getPersonService.getPerson(this.personName).then((person) => {
      if (person != null) {
        this.personRes = person
        this.personName = person.name

        this.saveLocation();
      }
    })
  }

  saveLocation() {
    for (var value in this.personRes.location) {
      this.findLatAndLon(this.personRes.location[value]);
    }
  }

  // Find person location in cordination 
  findLatAndLon(location: string) {
    if (!this.geocoder) this.geocoder = new google.maps.Geocoder()
    this.geocoder.geocode({
      'address': location
    }, (results, status) => {
      if (status == google.maps.GeocoderStatus.OK) {
        if (results[0].geometry.location) {
          this.markers.push({
            lat: results[0].geometry.location.lat(),
            lng: results[0].geometry.location.lng()
          })

        }
      } else {
        console.log("Sorry, this search produced no results.");
      }
    })
  }

}
