import {Component, OnInit} from '@angular/core';
import {RankingListComponent} from '../../shared/ranking-list/ranking-list.component';
import {UserProfileNavComponent} from '../../shared/user-profile-nav/user-profile-nav.component';
import {RankingService} from '../../services/ranking.service';
import {CommonModule} from '@angular/common';
import {PodiumComponent} from '../../shared/podium/podium.component';
import {NavComponent} from '../../shared/nav/nav.component';
import {CustomDatePipe} from '../../shared/date/date.pipe';

@Component({
  selector: 'app-ranking',
  imports: [
    RankingListComponent,
    UserProfileNavComponent,
    CommonModule,
    PodiumComponent,
    NavComponent,
    CustomDatePipe
  ],
  templateUrl: './ranking.component.html',
  styleUrl: './ranking.component.css'
})

export class RankingComponent implements OnInit {
  rankingList: { position: number; name: string; score: bigint }[] = [];
  podium: { position: number; name: string; score: bigint }[] = [];
  restOfRanking: { position: number; name: string; score: bigint }[] = [];
  userRanking: { position: number; name: string; score: bigint } | null = null;
  someDateVariable: Date = new Date();

  constructor(private rankingService: RankingService) {}

  ngOnInit() {
    this.rankingService.getRankingList().subscribe((data) => {
      this.rankingList = data;
      this.podium = this.rankingList.slice(0, 3);
      this.restOfRanking = this.rankingList.slice(3);
    });

    this.rankingService.getUserRanking().subscribe((userRanking) => {
      this.userRanking = userRanking;
    });
  }
}
