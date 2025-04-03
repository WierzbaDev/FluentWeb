import {Component} from '@angular/core';
import {Router, RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../../../services/auth.service';

@Component({
  selector: 'app-register',
  imports: [RouterModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  name: string = '';
  surname: string = '';
  email: string = '';
  password: string = '';
  repeatPassword: string = '';
  birthday: string = '';
  passwordVisible: boolean = false;



  constructor(private authService: AuthService, private router: Router) {}

  register() {
    if (!this.name || !this.surname || !this.email || !this.password || !this.repeatPassword || this.birthday === '') {
      alert("Wszystkie pola są wymagane");
      return;
    }

    const formattedDate = this.formatDate(this.birthday);

    if (this.password !== this.repeatPassword) {
      alert("Hasła nie są identyczne");
      return;
    }

    this.authService.register(this.name, this.surname, this.email, this.password, this.repeatPassword,  formattedDate).subscribe({
      next: () => {
       console.log("Rejestracja zakończona sukcesem!")
        this.router.navigate(['/auth/login']);
      },
      error: (error: any) => {
        console.error("Error while logging in!", error);

        if (error.status === 400) {
          alert("Nieprawidłowe dane rejestracji!")
        } else if (error.status === 409) {
          alert("Konto z takim Emailem już istnieje")
        } else {
          alert("Wystąpił błąd. Spróbuj ponownie później")
        }
      }
    });
  }

  formatDate(date: string): string {
    return new Date(date).toISOString().split('T')[0];
  }

  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
  }
}
