import {Component, Injectable} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {UserPanelComponent} from '../../shared/user-panel/user-panel.component';
import {ConfirmLogoutComponent} from "../../shared/confirm-logout/confirm-logout.component";
import {CustomDatePipe} from "../../shared/date/date.pipe";
import {NgIf} from "@angular/common";
import {NavComponent} from "../../shared/nav/nav.component";

@Component({
  selector: 'app-setting',
  imports: [
    FormsModule,
    UserPanelComponent,
    ConfirmLogoutComponent,
    CustomDatePipe,
    NgIf,
    NavComponent
  ],
  templateUrl: './setting.component.html',
  styleUrl: './setting.component.css'
})

@Injectable({
  providedIn: 'root'
})

export class SettingComponent {
  someDateVariable: Date = new Date();
  isLogoutPanelOpen: boolean = false;
  isOpen: boolean = false;

  toggleLogoutPanel() {
    this.isLogoutPanelOpen = !this.isLogoutPanelOpen;
  }

  closeLogoutPanel() {
    this.isLogoutPanelOpen = false;
  }

  toggleNav() {
    this.isOpen = !this.isOpen;
  }
}
