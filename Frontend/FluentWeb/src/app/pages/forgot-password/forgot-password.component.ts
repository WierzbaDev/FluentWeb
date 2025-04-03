import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-forgot-password',
  imports: [
    FormsModule,
    NgIf
  ],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent implements OnInit {
  email: string = '';
  sentEmail: boolean = false;

  constructor(private authService: AuthService) {
  }

  send() {
    if (this.email.length > 0) {
    this.authService.userForgotPassword(this.email).subscribe({
      next: () => {
        console.log("Sent email to backend!");
        this.sentEmail = true;
      },
      error: (err) => {
        console.error("Error while sending email to backend", err);
      }
    });
      }
  }

  ngOnInit(): void {
    this.sentEmail = false;
  }
}
