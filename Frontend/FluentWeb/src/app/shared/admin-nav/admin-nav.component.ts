import {Component, HostListener} from '@angular/core';
import {RouterLink} from '@angular/router';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-admin-nav',
    imports: [
        RouterLink,
        NgIf
    ],
  templateUrl: './admin-nav.component.html',
  styleUrl: './admin-nav.component.css'
})
export class AdminNavComponent {
  isNavOpen = false;

  toggleNav() {
    this.isNavOpen = !this.isNavOpen;
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    if (this.isNavOpen && !(event.target as HTMLElement).closest('.nav-container') && !(event.target as HTMLElement).closest('.hamburger')) {
      this.isNavOpen = false;
    }
  }
}
