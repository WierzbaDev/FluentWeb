import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Page} from '../model/page.model';
import {Observable} from 'rxjs';
import {environment} from '../../environments/enviroment';

export interface UserDetails {
  userId: number;
  name: string;
  surname: string;
  email: string;
  birthday: Date;
  role: string;
  verify: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AdminListUsersService {
  private baseUrl = environment.apiUrl;
  apiUrl = this.baseUrl + '/api/admin/users';

  constructor(private http: HttpClient) { }

  getUsers(page: number, size: number) {
    return this.http.get<Page<UserDetails>>(`${this.apiUrl}?page=${page}&size=${size}`);
  }

  patchUser(user: Partial<UserDetails>, id: number): Observable<UserDetails> {
    return this.http.patch<UserDetails>(`${this.apiUrl}/${id}`, user);
  }
}
