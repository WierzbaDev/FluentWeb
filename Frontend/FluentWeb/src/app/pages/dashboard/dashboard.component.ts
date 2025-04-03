import {Component} from '@angular/core';
import {CustomDatePipe} from '../../shared/date/date.pipe';
import {NavComponent} from '../../shared/nav/nav.component';
import {UserProfileNavComponent} from '../../shared/user-profile-nav/user-profile-nav.component';
import {RouterLink} from '@angular/router';
import {NextLessonListComponent} from '../../shared/next-lesson-list/next-lesson-list.component';
import {NextReviewComponent} from '../../shared/next-review/next-review.component';
import {RankingListComponent} from '../../shared/ranking-list/ranking-list.component';

@Component({
  selector: 'app-dashboard',
  imports: [CustomDatePipe, NavComponent, UserProfileNavComponent, RouterLink, NextLessonListComponent, NextReviewComponent, RankingListComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  someDateVariable: Date = new Date();

}
