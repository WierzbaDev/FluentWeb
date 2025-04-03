import {Inject, Injectable, PLATFORM_ID} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {catchError, map, Observable, throwError} from 'rxjs';
import {ToastrService} from 'ngx-toastr';
import {jwtDecode} from 'jwt-decode';
import {isPlatformBrowser} from '@angular/common';
import {environment} from '../../environments/enviroment';

export interface ForgotPasswordRequest {
  email: string;
}

export interface PasswordChanger {
  password: string;
  repeatPassword: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = environment.apiUrl;
  private apiUrl = this.baseUrl + '/api/auth';
  private userRoles: string[] = [];
  private readonly isBrowser: boolean;
  request: ForgotPasswordRequest = {} as ForgotPasswordRequest;
  changePassword: PasswordChanger = {} as PasswordChanger;

  constructor(private http: HttpClient,
              private router: Router,
              private toastr: ToastrService,
              @Inject(PLATFORM_ID) private platformId: Object) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, {email, password}).pipe(
      map((response: any) => {
        this.saveTokens(response.accessToken, response.refreshToken);
        this.decodeToken(response.accessToken);
        return response;
      }),
      catchError((error) => {
        this.toastr.error("Nie udało się zalogować", "Błąd");
        return throwError(() => error);
      })
    );
  }

  refreshToken(): Observable<string> {
    const refreshToken = this.getRefreshToken();

    console.log("[AuthService] Odświeżanie tokena...");

    if (!refreshToken) {
      console.error("[AuthService] Brak refreshToken - wylogowanie");
      this.logout();
      return throwError(() => new Error('Brak refreshToken'));
    }

    const refreshUrl = `${this.apiUrl}/refresh?refreshToken=${encodeURIComponent(refreshToken)}`;

    return this.http.post<{ accessToken: string, refreshToken?: string }>(
      refreshUrl,
      {},
      { headers: { 'Content-Type': 'application/json' } }
    ).pipe(
      map((response) => {
        console.log('[AuthService] Nowy accessToken:', response.accessToken);
        console.log('[AuthService] Nowy refreshToken:', response.refreshToken ?? "BRAK - używamy starego");

        if (response.refreshToken) {
          this.saveTokens(response.accessToken, response.refreshToken);
        } else {
          this.saveToken(response.accessToken);
        }

        console.log('[AuthService] Zapisany accessToken w localStorage:', this.getToken());
        console.log('[AuthService] Zapisany refreshToken w localStorage:', this.getRefreshToken());

        return response.accessToken;
      }),
      catchError((error) => {
        console.error('[AuthService] Błąd przy odświeżaniu tokena', error);
        this.logout();
        return throwError(() => error);
      })
    );
  }



  register(name: string, surname: string, email: string, password: string, repeatPassword: string, birthday: string) {
    const userData = {
      name,
      surname,
      email,
      password,
      repeatPassword,
      birthday
    };

    return this.http.post(`${this.apiUrl}/register`, userData, { responseType: 'text'}).pipe(
      catchError((error) => {
        this.toastr.error("Rejestracja się nie powiodła", "Błąd");
        return throwError(() => error);
      })
    )
  }

  logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("userRoles");
    this.userRoles = [];
    this.router.navigate(['/auth/login']);
  }

  getToken(): string | null {
    return localStorage.getItem("token");
  }

  getRefreshToken(): string | null {
    return localStorage.getItem("refreshToken");
  }

  getUserRoles(): string[] {
    if (this.userRoles.length === 0 && this.isBrowser) {
      const storedRoles = localStorage.getItem("userRoles");
      this.userRoles = storedRoles ? JSON.parse(storedRoles) : [];
    }
    return this.userRoles;
  }

  userForgotPassword(email: string): Observable<void> {
    this.request.email = email;
    return this.http.post<void>(`${this.apiUrl}/forgot-password`, this.request);
  }

  resetPassword(code: string, password: string, repeatPassword: string): Observable<void> {
    this.changePassword = { password: password, repeatPassword: repeatPassword };
    return this.http.post<void>(`${this.apiUrl}/reset-password`, this.changePassword, { headers: { 'X-Reset-Token': code }});
  }

  verifyUser(uuid: string): Observable<void> {
    return this.http.get<void>(`${this.apiUrl}/verify`, { params: {uuid} });
  }

  private saveToken(token: string) {
    localStorage.setItem("token", token);
  }

  private saveTokens(token: string, refreshToken: string) {
    localStorage.setItem("token", token);
    localStorage.setItem("refreshToken", refreshToken);
  }

  private decodeToken(token: string) {
    try {
      const decodedToken: any = jwtDecode(token);
      this.userRoles = decodedToken.roles || [];
      localStorage.setItem('userRoles', JSON.stringify(this.userRoles));
    } catch (error) {
      console.error("Error while decoding token", error);
    }
  }
}
