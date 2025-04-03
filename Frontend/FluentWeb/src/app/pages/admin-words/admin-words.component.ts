import {Component} from '@angular/core';
import {AdminNavComponent} from "../../shared/admin-nav/admin-nav.component";
import {AdminListWordsComponent} from '../../shared/admin-list-words/admin-list-words.component';
import {ConfirmLogoutComponent} from '../../shared/confirm-logout/confirm-logout.component';
import {CustomDatePipe} from '../../shared/date/date.pipe';
import {NgIf} from '@angular/common';
import {AddWordComponent} from '../../shared/add-word/add-word.component';

@Component({
  selector: 'app-admin-words',
  imports: [
    AdminNavComponent,
    AdminListWordsComponent,
    ConfirmLogoutComponent,
    CustomDatePipe,
    NgIf,
    AddWordComponent
  ],
  templateUrl: './admin-words.component.html',
  styleUrl: './admin-words.component.css'
})
export class AdminWordsComponent {
  isLogoutPanel: boolean = false;
  someDateVariable: Date = new Date();

  closeLogoutPanel() {
    this.isLogoutPanel = false;
  }

  toggleLogoutPanel() {
    this.isLogoutPanel = !this.isLogoutPanel;
  }
}
