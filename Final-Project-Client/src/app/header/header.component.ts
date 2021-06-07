import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  searchName: string

  constructor(private router: Router, private cookieService: CookieService) { }

  ngOnInit(): void {
  }

  // Search bar for entity
  onSearchUser() {
    this.router.navigate(['/searchPerson/'], { queryParams: { searchName: this.searchName } }).then(() => {
      this.searchName = ''
    });
  }

  // Log-Out
  exit() {
    this.cookieService.delete('user-role');
    window.location.reload();
  }


}
