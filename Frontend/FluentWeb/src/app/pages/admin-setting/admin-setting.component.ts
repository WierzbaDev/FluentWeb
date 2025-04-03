import {Component} from '@angular/core';
import {UserPanelComponent} from '../../shared/user-panel/user-panel.component';
import {AdminNavComponent} from '../../shared/admin-nav/admin-nav.component';
import {CustomDatePipe} from '../../shared/date/date.pipe';
import {ConfirmLogoutComponent} from '../../shared/confirm-logout/confirm-logout.component';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-admin-setting',
  imports: [
    UserPanelComponent,
    AdminNavComponent,
    CustomDatePipe,
    ConfirmLogoutComponent,
    NgIf
  ],
  templateUrl: './admin-setting.component.html',
  styleUrl: './admin-setting.component.css'
})
export class AdminSettingComponent {
  isLogoutPanel: boolean = false;
  someDateVariable: Date = new Date();

  closeLogoutPanel() {
    this.isLogoutPanel = false;
  }

  toggleLogoutPanel() {
    this.isLogoutPanel = !this.isLogoutPanel;
  }
}
