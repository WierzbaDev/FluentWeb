import {Component, HostListener, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ConfirmLogoutComponent} from '../confirm-logout/confirm-logout.component';
import {UserAccountService, UserInfo} from '../../services/user-account.service';

@Component({
  selector: 'app-user-profile-nav',
  imports: [CommonModule, ConfirmLogoutComponent],
  templateUrl: './user-profile-nav.component.html',
  styleUrl: './user-profile-nav.component.css'
})

export class UserProfileNavComponent implements OnInit {
  isMenuOpen = false;
  isLogoutPanelOpen = false;
  user: UserInfo = {} as UserInfo;

  constructor(private userService: UserAccountService) {
  }

  toggleMenu(event: Event) {
    event.stopPropagation();
    this.isMenuOpen = !this.isMenuOpen;
  }

  toggleLogoutPanel() {
    this.isLogoutPanelOpen = !this.isLogoutPanelOpen;
  }

  closeLogoutPanel() {
    this.isLogoutPanelOpen = false;
  }

  @HostListener('document:click', ['$event'])
  closeMenu(event: Event) {
    const target = event.target as HTMLElement;
    if (!target.closest('.header') && !target.closest('.user-menu')) {
      this.isMenuOpen = false;
    }
  }

  ngOnInit() {
    this.userService.getUserInfo().subscribe({
      next: (response) => {
        this.user = response;
      },
      error: (err) => {
        console.error("Error while retrieving user info", err);
      }
    })
  }
}
