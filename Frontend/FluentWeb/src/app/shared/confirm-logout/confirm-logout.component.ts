import {Component, EventEmitter, Output} from '@angular/core';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-confirm-logout',
  imports: [],
  templateUrl: './confirm-logout.component.html',
  styleUrl: './confirm-logout.component.css'
})
export class ConfirmLogoutComponent {

  @Output() close = new EventEmitter<void>();

  constructor(private authService: AuthService) {
  }

  confirmLogout() {
    this.authService.logout();
    this.close.emit();
  }

  closePanel() {
    this.close.emit();
  }
}
