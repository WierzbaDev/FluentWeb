import {Component, EventEmitter, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ChangePassword, UserAccountService} from '../../services/user-account.service';

@Component({
  selector: 'app-change-password',
  imports: [
    FormsModule
  ],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css'
})
export class ChangePasswordComponent {
  newPassword: string = '';
  confirmPassword: string = '';
  passwordVisibly: boolean = false;

  @Output() close = new EventEmitter<void>();

  constructor(private userAccountService: UserAccountService) {
  }

  changePassword() {
    if (this.newPassword !== this.confirmPassword) {
      alert('Hasła nie pasują do siebie!');
      return;
    }

    const changePasswordObject: ChangePassword = {
      password: this.newPassword,
      repeatPassword: this.confirmPassword
    };

    this.userAccountService.changePassword(changePasswordObject).subscribe({
      next: (response) => {
        console.log('Odpowiedź z serwera:', response);
        alert("Hasło zostało zmienione poprawnie!");
      },
      error: (error) => {
        console.error('Błąd zmiany hasła:', error);
        if (error.status === 400) {
          alert("Błąd w danych wejściowych (np. złe hasło, token itp.)");
        } else if (error.status === 500) {
          alert("Błąd serwera. Spróbuj ponownie później.");
        } else {
          alert("Błąd podczas zmiany hasła!");
        }
      }
    });

    this.close.emit();
  }

  togglePasswordVisibility() {
    this.passwordVisibly = !this.passwordVisibly;
  }

  closePanel() {
    this.close.emit();
  }
}
