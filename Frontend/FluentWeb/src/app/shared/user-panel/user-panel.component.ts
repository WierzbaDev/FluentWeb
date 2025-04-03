import {Component, Input, OnInit} from '@angular/core';
import {ChangePasswordComponent} from "../change-password/change-password.component";
import {DeleteAccountComponent} from "../delete-account/delete-account.component";
import {DatePipe, NgIf} from "@angular/common";
import {UserAccountService, UserInfo} from '../../services/user-account.service';
import {RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {ChangesDataService} from '../../services/changes-data.service';

@Component({
  selector: 'app-user-panel',
  imports: [
    ChangePasswordComponent,
    DeleteAccountComponent,
    NgIf,
    RouterLink,
    FormsModule
  ],
  templateUrl: './user-panel.component.html',
  styleUrl: './user-panel.component.css',
  providers: [DatePipe]
})
export class UserPanelComponent implements OnInit {
  @Input() isAdmin: boolean = false;
  @Input() dashboardRoute: string = '/dashboard';

  user: UserInfo = { name: '', surname: '', email: '', birthday: '' };
  userCopy: UserInfo = {} as UserInfo;
  showPasswordPanel: boolean = false;
  showNamePanel: boolean = false;
  showSurnamePanel: boolean = false;
  isDeletePanelOpen: boolean = false;

  constructor(private userAccountService: UserAccountService, private changesDataService: ChangesDataService) {}

  togglePassword() {
    this.showPasswordPanel = !this.showPasswordPanel;
    if (this.showPasswordPanel) {
      this.isDeletePanelOpen = false;
    }
  }

  toggleName() {
    this.showNamePanel = !this.showNamePanel;
  }

  closeNamePanel() {
    this.showNamePanel = false;
  }

  saveNewName() {
    this.changesDataService.updateUsername(this.userCopy.name).subscribe({
      next: data => {
        this.user = data;
      },
      error: err => {
        console.log("error while changing new name", err);
      }
    });
    this.closeNamePanel();
  }

  toggleSurnamePanel() {
    this.showSurnamePanel = !this.showSurnamePanel;
  }

  closeSurnamePanel() {
    this.showSurnamePanel = false;
  }

  saveNewSurname() {
    this.changesDataService.updateSurname(this.userCopy.surname).subscribe({
      next: data => {
        this.user = data;
      },
      error: err => {
        console.log("error while changing new surname", err);
      }
    });
    this.closeSurnamePanel();
  }

  toggleDeletePanel() {
    this.isDeletePanelOpen = !this.isDeletePanelOpen;
    if (this.isDeletePanelOpen) {
      this.showPasswordPanel = false;
    }
  }

  closeDeletePanel() {
    this.isDeletePanelOpen = false;
  }

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData() {
    this.userAccountService.getUserInfo().subscribe((data)=> {
      this.user = data;
      this.userCopy = data;
    });
  }
}
