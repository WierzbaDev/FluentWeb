import {AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild} from '@angular/core';
import {RouterLink} from '@angular/router';
import {LessonService} from '../../services/lesson.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {NgIf, NgStyle} from '@angular/common';
import {environment} from '../../../environments/environment.prod';

interface Word {
  sourceWord: string;
  translation: { PL: string };
  wordCerfLevel: string;
}

@Component({
  selector: 'app-lesson',
  standalone: true,
  imports: [
    RouterLink,
    FormsModule,
    NgIf,
    NgStyle
  ],
  templateUrl: './lesson.component.html',
  styleUrl: './lesson.component.css'
})
export class LessonComponent implements OnInit, AfterViewInit {
  private api = environment.apiUrl;
  words: Word[] = [];
  learningQueue: Word[] = [];
  currentIndex: number = 0;

  @ViewChild('answerInput', { static: false }) answerInput!: ElementRef;

  userAnswer: string = '';
  feedback: string | null = null;
  showCorrectAnswer: boolean = false;
  correctAnswer: string | null = null;

  constructor(private lessonService: LessonService, private http: HttpClient) {}

  ngOnInit(): void {
    this.loadNextLesson();
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
    }
  }

  loadNextLesson() {
    this.lessonService.getLesson().subscribe((data) => {
      if (data.length === 0) {
        this.words = [];
        this.learningQueue = [];
        this.feedback = "🎉 Ukończyłeś wszystkie lekcje!";
        alert("🎉 Ukończyłeś już wszystkie lekcje!")
        return;
      }
      this.words = [...data];
      this.generateLearningQueue();
      this.currentIndex = 0;
      this.feedback = null;
      this.userAnswer = "";
      setTimeout(() => this.focusInput(), 100);

      console.log("Created new lesson with data: ", data)
    });
  }

  generateLearningQueue() {
    const difficultyMap: Record<string, number> = {
      A1: 1, A2: 2, B1: 3, B2: 4, C1: 5, C2: 6
    };

    this.learningQueue = this.words.flatMap(word =>
      Array.from({ length: difficultyMap[word.wordCerfLevel] || 1 }, () => word)
    );

    this.shuffleArray(this.learningQueue);
  }

  shuffleArray(array: any[]) {
    for (let i = array.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [array[i], array[j]] = [array[j], array[i]];
    }
  }

  checkAnswer() {
    const currentWord = this.learningQueue[this.currentIndex];

    if (!currentWord) return;

    const wordId = (currentWord as any).id;
    if (!wordId) return;

    const apiUrl = `${this.api}/api/user/review/check?wordId=${wordId}&userAnswer=${encodeURIComponent(this.userAnswer.trim())}&language=PL`;
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    this.http.post<boolean>(apiUrl, {}, { headers }).subscribe({
      next: (response) => {
        this.feedback = response ? "✅ Poprawna odpowiedź!" : "❌ Niepoprawna odpowiedź!";
        if (!response) this.correctAnswer = currentWord.translation.PL;
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
    if (this.learningQueue.length === 0) {
      this.feedback = "🎉 Ukończyłeś lekcję!";
      return;
    }

    this.showCorrectAnswer = false;
    this.feedback = null;
    this.correctAnswer = null;
    this.userAnswer = "";

    if (this.currentIndex >= this.learningQueue.length - 1) {
      this.feedback = "🎉 Ukończyłeś lekcję!";
      this.learningQueue = [];
      return;
    }

    this.currentIndex++;
    setTimeout(() => this.focusInput(), 100);
  }

  speak(word: string) {
    if ('speechSynthesis' in window) {
      const utterance = new SpeechSynthesisUtterance(word);
      utterance.lang = 'en-GB';
      window.speechSynthesis.speak(utterance);
    }
  }

  skipWord() {
    if (this.learningQueue.length > 1) {
      this.learningQueue.splice(this.currentIndex, 1);
      this.currentIndex = Math.min(this.currentIndex, this.learningQueue.length - 1);
    }
    this.feedback = null;
    this.userAnswer = '';
    setTimeout(() => this.focusInput(), 100);
  }

  get progressWidth() : number {
    return this.learningQueue.length > 0
    ? ((this.currentIndex + 1) / this.learningQueue.length) * 100
      : 0;
  }
}
