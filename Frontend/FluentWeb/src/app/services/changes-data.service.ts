import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/enviroment';

@Injectable({
  providedIn: 'root'
})
export class ChangesDataService {
  private baseUrl = environment.apiUrl;
  apiUrl = this.baseUrl + '/api/user/account';

  constructor(private http: HttpClient) { }

  updateUsername(newName: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/me/edit`, { name: newName})
  }

  updateSurname(newSurname: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/me/edit`, { surname: newSurname})
  }

  updateBirthday(newBirthday: Date): Observable<any> {
    return this.http.patch(`${this.apiUrl}/me/edit`, { birthday: newBirthday})
  }
}
