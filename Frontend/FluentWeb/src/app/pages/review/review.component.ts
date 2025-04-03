import {Component, ElementRef, HostListener, OnInit, ViewChild} from '@angular/core';
import {RouterLink} from '@angular/router';
import {Word, WordService} from '../../services/word.service';
import {FormsModule} from '@angular/forms';
import {NgIf} from '@angular/common';
import {environment} from '../../../environments/environment.prod';

@Component({
  selector: 'app-review',
  standalone: true,
  imports: [
    RouterLink,
    FormsModule,
    NgIf
  ],
  templateUrl: './review.component.html',
  styleUrl: './review.component.css',
})
export class ReviewComponent implements OnInit {
  private apiUrl = environment.apiUrl;
  words: Word[] = [];
  reviewQueue: Word[] = [];
  currentIndex: number = 0;

  @ViewChild('answerInput', {static: false}) answerInput!: ElementRef;

  userAnswer: string = '';
  feedback: string | null = null;
  showCorrectAnswer: boolean = false;
  correctAnswer: string | null = null;
  correctCount: number = 0;
  wrongCount: number = 0;

  constructor(private wordService: WordService) {}

  ngOnInit() {
    this.loadNextReview();
  }

  ngAfterViewInit() {
    this.focusInput();
  }

  focusInput() {
    setTimeout(() => {
      if (this.answerInput && this.answerInput.nativeElement) {
        this.answerInput.nativeElement.focus();
      }
    }, 100);
  }

  @HostListener('document:keydown', ['$event'])
  handleKeydown(event: KeyboardEvent) {

    if (this.showCorrectAnswer) {
      this.nextWord();
      this.focusInput();
    } else if (event.key === 'Enter') {
      this.checkAnswer();
      console.log("Enter pressed")
    }
  }

  loadNextReview() {
    this.wordService.getWords(this.apiUrl + '/api/user/review/words').subscribe((data) => {
      if (data.length === 0) {
        this.words = [];
        this.reviewQueue = [];
        this.feedback = "🎉 Gratulacje ukończyłeś dzisiejszą powtórkę!";
        return;
      }

      this.words = [...data];
      this.reviewQueue = this.wordService.generateQueue(this.words, {
        A1: 1, A2: 2, B1: 3, B2: 4, C1: 5, C2: 6
      });
      this.currentIndex = 0;
      this.userAnswer = '';
      this.feedback = null;
    });
  }

  checkAnswer() {
    const currentWord = this.reviewQueue[this.currentIndex];

    if (!currentWord) {
      console.error("Brak aktualnego słowa w kolejce.");
      return;
    }

    const wordId = currentWord.word.id;
    if (!wordId) {
      console.error("Brak wordId w aktualnym słowie:", currentWord);
      return;
    }

    this.wordService.checkAnswer(wordId, this.userAnswer, 'PL').subscribe({
      next: (response) => {
        this.feedback = response ? "✅ Poprawna odpowiedź!" : "❌ Niepoprawna odpowiedź!";
        response ? this.correctCount++ : this.wrongCount++;

        if (!response) this.correctAnswer = currentWord.word.translation.PL;
        this.showCorrectAnswer = true;
        this.userAnswer = "";
      },
      error: (err) => {
        console.error("Błąd podczas sprawdzania odpowiedzi:", err);
        this.feedback = "❌ Wystąpił błąd podczas sprawdzania odpowiedzi.";
      }
    });
  }

  nextWord() {
    if (this.reviewQueue.length === 0) {
      this.feedback = "🎉 Ukończyłeś powtórkę!";
      return;
    }

    this.showCorrectAnswer = false;
    this.feedback = null;
    this.correctAnswer = null;
    this.userAnswer = "";

    if (this.currentIndex >= this.reviewQueue.length - 1) {
      this.feedback = "🎉 Ukończyłeś powtórkę!";
      this.reviewQueue = [];
      return;
    }

    this.currentIndex++;
  }

  skipWord() {
    if (this.reviewQueue.length > 1) {
      this.reviewQueue.splice(this.currentIndex, 1);
      this.currentIndex = Math.min(this.currentIndex, this.reviewQueue.length - 1);
    }
    this.feedback = null;
    this.userAnswer = '';
  }
  speak(word: string) {
    if ('speechSynthesis' in window) {
      const utterance = new SpeechSynthesisUtterance(word);
      utterance.lang = 'en-GB';
      window.speechSynthesis.speak(utterance);
    }
  }
}
