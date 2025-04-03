import {Component, HostListener} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {CreateWord, WordService} from '../../services/word.service';

@Component({
  selector: 'app-add-word',
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './add-word.component.html',
  styleUrl: './add-word.component.css'
})
export class AddWordComponent {
  cerfLevel: string[] = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];
  level: string = '';
  sourceWord: string = '';
  translation: string = '';
  createWordPanel: boolean = false;
  word: CreateWord = {
    sourceWord: '',
    wordCerfLevel: '',
    translation: { PL: ''},
  }

  constructor(private wordService: WordService) {}

  create() {
    console.log('Creating Word');

    this.word.sourceWord = this.sourceWord;
    this.word.translation = { PL: this.translation };
    this.word.wordCerfLevel = this.level;

    if (this.word.sourceWord.length > 0 && this.word.wordCerfLevel.length > 0 && this.word.translation.PL.length > 0) {
      this.wordService.createWord(this.word).subscribe({
        next: () => {
          alert("Pomyślnie stworzono nowe słowo!");
          this.sourceWord = '';
          this.translation = '';
        },
        error: (err) => {
          console.error("Error while creating word", err);
        }
      });
    } else
      alert("Uzupełnij wszystkie pola!")
  }

  toggleCreateWordPanel() {
    this.createWordPanel = !this.createWordPanel;
  }

  closeCreateWordPanel() {
    this.createWordPanel = false;
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    if (this.createWordPanel && !(event.target as HTMLElement).closest('.container') && !(event.target as HTMLElement).closest('.create-button-panel')) {
      this.createWordPanel = false;
    }
  }
}
