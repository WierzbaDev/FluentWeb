import {Component} from '@angular/core';
import {Router, RouterModule} from '@angular/router';
import {AuthService} from '../../../services/auth.service';
import {FormsModule} from '@angular/forms';
import {UserAccountService} from '../../../services/user-account.service';
import {catchError, Observable, of} from 'rxjs';

@Component({
  selector: 'app-login',
  imports: [RouterModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  navigate = '/dashboard'
  passwordVisible: boolean = false;

  // error mapping object
  errorMessage: { [key: string]: string } = {
    "User with this email was banned": "Użytkownik z tym adresem e-mail został zablokowany lub chciałbyć zapomniany i nie może się zalogować.",
    "Invalid Credentials": "Niepoprawny e-mail lub/i hasło.",
    "This user does not exists": "Użytkownik z tym adresem e-mail nie istnieje.",
    "User is not verified": "Użytkownik nie został zweryfikowany"
  };

  constructor(private authService: AuthService, private userAccountService: UserAccountService, private router: Router) {}

  isAdmin(): Observable<boolean> {
    return this.userAccountService.isAdmin().pipe(
      catchError(err => {
        console.log("Error while retrieving role ", err);
        return of(false);
      })
    )
  }

  login() {
    if (this.email && this.password) {
      this.authService.login(this.email, this.password).subscribe({
        next: () => {
          console.log("Logged in");

          this.isAdmin().subscribe(isAdmin => {
            if (isAdmin) {
              this.navigate = '/admin/users';
            }

            this.router.navigate([this.navigate]).then(() => {
              console.log(`Navigate to ${this.navigate} successfully`);
            }).catch(err => {
              console.log("Navigate error: ", err);
            });
          });
        },
        error: (error) => {
          console.log("Error while logging in!", error);

          let backendMessage = error.error?.message || "Unknown error";
          let errorMessage = "Wystąpił nieznany błąd."

          Object.keys(this.errorMessage).forEach(key => {
            if (backendMessage.includes(key)) {
              errorMessage = this.errorMessage[key];
            }
          });

          alert(errorMessage);
        }
      });
    } else {
      alert("Wszystkie pola są wymagane");
    }
  }

  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
  }
}
