import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';

@Component({
  selector: 'app-reset-password',
  imports: [
    FormsModule,
    RouterModule
  ],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css'
})
export class ResetPasswordComponent implements OnInit {
  passwordVisible: boolean = false;
  password: string = '';
  repeatPassword: string = '';
  code: string = '';

  constructor(private authService: AuthService, private route: ActivatedRoute, private router: Router) {
  }

  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
  }

  resetPassword() {
    if (this.password !== this.repeatPassword) {
      alert("Hasła nie są identyczne");
      return;
    }

    if (this.code.length < 0) {
      alert("Nie podano kodu w url!");
      return;
    }

    this.authService.resetPassword(this.code, this.password, this.repeatPassword).subscribe({
      next: () => {
        console.log("Password changed!");
        this.router.navigate(['/auth/login']);
      },
      error: (err) => {
        console.error("Error while changing password! ", err);
      }
    });
  }

  ngOnInit(): void {
    const sourceCode = this.route.snapshot.paramMap.get('code');
    if (sourceCode) {
      this.code = sourceCode;
    }
  }
}
