import {Component, OnInit} from '@angular/core';
import {LessonService} from '../../services/lesson.service';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-next-lesson-list',
  imports: [
    NgForOf,
  ],
  templateUrl: './next-lesson-list.component.html',
  styleUrl: './next-lesson-list.component.css'
})
export class NextLessonListComponent implements OnInit{
  words: { sourceWord: string; translation: string; wordCerfLevel: string }[] = [];

  constructor(private lessonService: LessonService) {}

  ngOnInit() {
    this.lessonService.getWords().subscribe((data) => {
      this.words = data.map(word => ({
        sourceWord: word.sourceWord,
        translation: word.translation.PL,
        wordCerfLevel: word.wordCerfLevel
      }));
    });
  }
}
