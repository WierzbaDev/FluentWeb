import {Component, OnInit} from '@angular/core';
import {NgForOf, NgIf} from '@angular/common';
import {Word, WordService} from '../../services/word.service';

@Component({
  selector: 'app-next-review',
  imports: [
    NgForOf,
    NgIf
  ],
  templateUrl: './next-review.component.html',
  styleUrl: './next-review.component.css'
})
export class NextReviewComponent implements OnInit {
  words: Word[] = []
  isAnyWords: boolean = false;

  constructor(private wordService: WordService) {}

  ngOnInit() {
    this.wordService.getWordsToDashBoard().subscribe({
      next: (words) => {
        this.words = words;
        this.isAnyWords = words.length > 0;
      },
      error: (err) => {
        console.error("[NextReviewComponent] Błąd pobierania słówek: ", err)
      }
    });
  }
}
