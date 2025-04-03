import {Component, Inject, OnInit, PLATFORM_ID} from '@angular/core';
import {isPlatformBrowser, NgIf} from '@angular/common';
import {AuthService} from '../../services/auth.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-verify-account',
  imports: [
    NgIf
  ],
  templateUrl: './verify-account.component.html',
  styleUrl: './verify-account.component.css'
})
export class VerifyAccountComponent implements OnInit {
  isVerified: boolean = false;
  uuid: string = '';

  constructor(private authService: AuthService, private route: ActivatedRoute, @Inject(PLATFORM_ID) private platformId: object) {
  }

  ngOnInit(): void {

    const sourceCode = this.route.snapshot.paramMap.get('uuid');
    if (sourceCode) {
      this.uuid = sourceCode;
      this.authService.verifyUser(this.uuid).subscribe({
        next: () => {
          this.isVerified = true;
          if (isPlatformBrowser(this.platformId)) {
            setTimeout(() => {
              console.log("Próba zamknięcia przeglądarki")
              window.close();
            }, 3000);
          }
        },
        error: (err) => {
          console.error("Error while verification user ", err);
        }
      });
    }
  }
}
