import {Component, Input, OnInit} from '@angular/core';
import {RankingService} from '../../services/ranking.service';
import {CommonModule, NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-ranking-list',
  templateUrl: './ranking-list.component.html',
  imports: [
    NgForOf,
    NgIf,
    CommonModule
  ],
  styleUrls: ['./ranking-list.component.css']
})
export class RankingListComponent implements OnInit {
  rankingList: { position: number; name: string; score: bigint }[] = [];
  userRanking: { position: number; name: string; score: bigint } | null = null;
  userRankingDisplayed = false;

  @Input() ranking!: { position: number; name: string; score: bigint }[];

  constructor(private rankingService: RankingService) {}

  ngOnInit() {
    this.rankingService.getRankingList().subscribe((data) => {
      this.rankingList = data;
    });

    this.rankingService.getUserRanking().subscribe((userRanking) => {
      if (userRanking && !this.userRankingDisplayed) {
        this.userRanking = userRanking;
        this.userRankingDisplayed = true;
      }
    });
  }
}
