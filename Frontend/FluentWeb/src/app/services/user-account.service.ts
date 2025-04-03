import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/enviroment';

export interface UserInfo {
  name: string;
  surname: string;
  email: string;
  birthday: string;
}

export interface ChangePassword {
  password: string;
  repeatPassword: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserAccountService {
  private baseUrl = environment.apiUrl;
  private apiUrl = this.baseUrl + '/api/user/account';

  constructor(private http: HttpClient) { }

  isAdmin(): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/isAdmin`);
  }

  getUserInfo(): Observable<UserInfo> {
    return this.http.get<UserInfo>(this.apiUrl + "/info");
  }

  changePassword(request: ChangePassword): Observable<string> {
    return this.http.post<string>(this.apiUrl + "/change-password", request);
  }

  deleteUser(): Observable<void> {
    return this.http.post<void>(this.apiUrl + "/forget", {});
  }
}
