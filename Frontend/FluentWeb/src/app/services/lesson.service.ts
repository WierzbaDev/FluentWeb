import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/enviroment';

interface Word {
  sourceWord: string;
  translation: {
    PL: string;
  };
  wordCerfLevel: string;
}

@Injectable({
  providedIn: 'root'
})
export class LessonService {
  private baseUrl = environment.apiUrl;
  private apiUrl = this.baseUrl + '/api/user/review/lesson'

  constructor(private http: HttpClient) { }

  getWords(): Observable<Word[]> {
    return this.http.get<Word[]>(this.apiUrl + "?limit=11");
  }

  getLesson(): Observable<Word[]> {
    return this.http.get<Word[]>(this.apiUrl + "?limit=14");
  }
}
