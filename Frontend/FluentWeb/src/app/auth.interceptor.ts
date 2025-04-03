import {HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest} from '@angular/common/http';
import {inject} from '@angular/core';
import {AuthService} from './services/auth.service';
import {BehaviorSubject, catchError, filter, Observable, switchMap, take, throwError} from 'rxjs';
import {ToastrService} from 'ngx-toastr';
import {environment} from '../environments/enviroment';

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn): Observable<HttpEvent<any>> => {
  const baseUrl = environment.apiUrl;
  const authService = inject(AuthService);
  const toastr = inject(ToastrService);

  if (req.url.startsWith(baseUrl + '/api/auth/refresh')) {
    console.log("[Interceptor] Pomijam dodawanie Authorization")
    return next(req.clone());
  }

  if (typeof localStorage === 'undefined') {
    return next(req);
  }

  const token = localStorage.getItem('token');
  console.log("[Interceptor] wysyłany token: ", token);

  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  return next(req).pipe(
    catchError((error) => {
      console.warn("[Interceptor] błąd HTTP", error.status, error.error);

      if (typeof error.error === 'string') {
        try {
          error.error = JSON.parse(error.error);
        } catch (e) {
          console.warn("[Interceptor] nie udało się sparsować odpowiedzi jako JSON")
        }
      }

      if (error.status === 200 && error.error?.text) {
        console.log("[Interceptor] Odpowiedź serwera: ", error.error.text)
        return next(req);
      }

      if (error.status === 401) {
        if (error.error?.message === "Invalid Credentials") {
          return throwError(() => error);
        }
        console.warn("[Interceptor] otrzymał 401, odświeżam token");
        return handle401Error(req, next, authService);
      }

      if (error.status === 403) {
        console.warn("[Interceptor] otrzymał 403");
        const errorMessage = error?.error?.message || "Brak szczegółów błędu";

        if (errorMessage.includes("USER_NOT_VERIFIED")) {
          toastr.info("Musisz potwierdzić swój adres e-mail.", "Weryfikacja wymagana");
        } else if (errorMessage.includes("USER_IS_BANNED")) {
          toastr.info("Użytkownik został zablokowany", "Nie możesz się zalogować");
        } else if (errorMessage.includes("USER_BLOCKED_TOKEN")) {
          toastr.info("Token użytkownika został zablokowany", "Nie możesz się zalogować");
        } else {
          toastr.error("Wystąpił błąd autoryzacji!", "Błąd");
        }
      }

      if (error.status === 409) {
        console.warn("[Interceptor] konflikt 409:", error.error);
        const errorMessage = error?.error?.message || "Brak szczegółów błędu";

        if (errorMessage.includes("USER_ALREADY_EXISTS")) {
          toastr.warning("Użytkownik już istnieje!", "Rejestracja");
        } else {
          toastr.error("Wystąpił błąd rejestracji!", "Błąd");
        }
      }

      return throwError(() => error);
    })
  );
};

let isRefreshing = false;
const refreshTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

function handle401Error(req: HttpRequest<any>, next: HttpHandlerFn, authService: AuthService): Observable<HttpEvent<any>> {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    return authService.refreshToken().pipe(
      switchMap((token) => {
        isRefreshing = false;
        if (!token) {
          console.error("[Interceptor] Błąd: Otrzymano pusty token");
          authService.logout();
          return throwError(() => new Error("Brak tokenu po odświeżeniu"));
        }

        localStorage.setItem("token", token);
        refreshTokenSubject.next(token);

        return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
      }),
      catchError((err) => {
        console.error("[Interceptor] Błąd przy odświeżaniu tokena", err);
        isRefreshing = false;
        authService.logout();
        return throwError(() => err);
      })
    );
  } else {
    return refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap(() => {
        const newToken = localStorage.getItem("token");
        return next(req.clone({ setHeaders: { Authorization: `Bearer ${newToken}` } }));
      })
    );
  }
}
