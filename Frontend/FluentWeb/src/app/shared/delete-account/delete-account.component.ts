import {Component, EventEmitter, Output} from '@angular/core';
import {UserAccountService} from '../../services/user-account.service';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-delete-account',
  imports: [],
  templateUrl: './delete-account.component.html',
  styleUrl: './delete-account.component.css'
})
export class DeleteAccountComponent {

  @Output() close = new EventEmitter<void>();

  constructor(private userAccountService: UserAccountService, private authService: AuthService) {
  }

  deleteAccount() {
    console.log('deleted user');
    this.userAccountService.deleteUser().subscribe({
      next: (response) => {
        console.log('deleted user', response);
      },
      error: (error) => {
        console.log('deleted user', error);
      }
      });
    this.authService.logout();
  }

  closeMenu() {
    this.close.emit();
  }
}
