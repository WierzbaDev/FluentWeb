import {Component, HostListener} from '@angular/core';
import {RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-nav',
  imports: [
    RouterLink,
    CommonModule,
  ],
  standalone: true,
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css'
})
export class NavComponent {
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
