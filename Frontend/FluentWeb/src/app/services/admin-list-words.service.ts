import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Page} from '../model/page.model';
import {WordDetails} from './word.service';
import {environment} from '../../environments/enviroment';

@Injectable({
  providedIn: 'root'
})
export class AdminListWordsService {
  private baseUrl = environment.apiUrl;
  apiUrl = this.baseUrl + '/api/admin/words';

  constructor(private http: HttpClient) { }

  getAllWords(page: number, size: number) {
    return this.http.get<Page<WordDetails>>(`${this.apiUrl}?page=${page}&size=${size}`)
  }
}
