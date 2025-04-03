import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {forkJoin, map, Observable} from 'rxjs';
import {environment} from '../../environments/enviroment';

@Injectable({
  providedIn: 'root'
})
export class RankingService {
  private baseUrl = environment.apiUrl;
  private apiUrl = this.baseUrl + '/api/userScore/top';
  private userUrl = this.baseUrl + '/api/user/account/info';

  constructor(private http: HttpClient) {}

  getRankingList(): Observable<{ position: number; name: string; score: bigint }[]> {
    return this.http.get<{ position: number; name: string; score: bigint }[]>(this.apiUrl);
  }

  getUserInfo(): Observable<{ name: string }> {
    return this.http.get<{ name: string }>(this.userUrl);
  }

  getUserRanking(): Observable<{ position: number; name: string; score: bigint } | null> {
    return forkJoin({
      ranking: this.getRankingList(),
      user: this.getUserInfo()
    }).pipe(
      map(({ ranking, user }) => {
        return ranking.find(player => player.name === user.name) || null;
      })
    );
  }
}
