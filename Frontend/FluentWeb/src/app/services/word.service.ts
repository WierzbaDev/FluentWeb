import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, Observable, tap, throwError} from 'rxjs';
import {environment} from '../../environments/enviroment';

export interface Translation {
  PL: string;
}

export interface WordDetails {
  id: number;
  sourceWord: string;
  translation: Translation;
  wordCerfLevel: string;
}

export interface CreateWord {
  sourceWord: string;
  translation: Translation;
  wordCerfLevel: string;
}

export interface Word {
  easeFactor: number;
  failedCount: number;
  id: number;
  intervalDays: number;
  lastReviewed: string;
  nextReview: string;
  repetitionCount: number;
  successCount: number;
  userId: number;
  word: WordDetails;
}

@Injectable({
  providedIn: 'root'
})
export class WordService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getWords(apiUrl: string): Observable<Word[]> {
    return this.http.get<Word[]>(apiUrl);
  }

  checkAnswer(wordId: number, userAnswer: string, language: string): Observable<boolean> {
    const apiUrl = `${this.baseUrl}/api/user/review/check?wordId=${wordId}&userAnswer=${encodeURIComponent(userAnswer.trim())}&language=${language}`;
    return this.http.post<boolean>(apiUrl, {});
  }

  generateQueue(words: Word[], difficultyMap: Record<string, number>): Word[] {
    const queue = words.flatMap(word =>
      Array.from({ length: difficultyMap[word.word.wordCerfLevel] || 1 }, () => word)
    );
    this.shuffleArray(queue);
    return queue;
  }

  shuffleArray(array: any[]) {
    for (let i = array.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [array[i], array[j]] = [array[j], array[i]];
    }
  }

  getWordsToDashBoard(): Observable<Word[]> {
    return this.http.get<Word[]>(`${this.baseUrl}/api/user/review/words` + "/11").pipe(
      tap(words => console.log("[WordService] Pobranie słów:", words)),
      catchError(error => {
        console.error("[WordService] Błąd:", error);
        return throwError(() => error);
      })
    );
  }

  putWord(word: WordDetails): Observable<WordDetails> {
    return this.http.put<WordDetails>(`${this.baseUrl}/api/admin/words/${word.id}`, word);
  }

  deleteWord(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/api/admin/words/${id}`);
  }

  createWord(word: CreateWord): Observable<WordDetails> {
    return this.http.post<WordDetails>(`${this.baseUrl}/api/admin/words`, word);
  }
}
