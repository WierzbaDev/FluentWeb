import {Component} from '@angular/core';
import {CustomDatePipe} from "../../shared/date/date.pipe";
import {AdminListUsersComponent} from '../../shared/admin-list-users/admin-list-users.component';
import {AdminNavComponent} from '../../shared/admin-nav/admin-nav.component';
import {ConfirmLogoutComponent} from '../../shared/confirm-logout/confirm-logout.component';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-admin-users',
  imports: [
    CustomDatePipe,
    AdminListUsersComponent,
    AdminNavComponent,
    ConfirmLogoutComponent,
    NgIf
  ],
  templateUrl: './admin-users.component.html',
  styleUrl: './admin-users.component.css'
})
export class AdminUsersComponent {
  someDateVariable: Date = new Date();
  isLogoutPanel: boolean = false;

  toggleLogoutPanel() {
    this.isLogoutPanel = !this.isLogoutPanel;
  }

  closeLogoutPanel() {
    this.isLogoutPanel = false;
  }
}
