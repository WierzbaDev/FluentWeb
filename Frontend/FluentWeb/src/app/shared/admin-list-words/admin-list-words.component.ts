import {Component, HostListener, OnInit} from '@angular/core';
import {WordDetails, WordService} from '../../services/word.service';
import {AdminListWordsService} from '../../services/admin-list-words.service';
import {Page} from '../../model/page.model';
import {NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-admin-list-words',
  imports: [
    NgForOf,
    FormsModule,
    NgIf
  ],
  templateUrl: './admin-list-words.component.html',
  styleUrl: './admin-list-words.component.css'
})
export class AdminListWordsComponent implements OnInit{
  words: WordDetails[] = [];
  totalPages: number = 0;
  pageSize: number = 2;
  currentPage: number = 1;
  inputValue: number = 0;

  // Variable to editing
  editingRowId: number | null = null;
  tempWord: WordDetails = {} as WordDetails;
  cerfLevels: string[] = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];

  constructor(private adminListWordsService: AdminListWordsService, private wordService: WordService) {
  }

  loadWords(page: number) {
    this.adminListWordsService.getAllWords(page, this.pageSize).subscribe((response: Page<WordDetails>) => {
      this.words = response.content;
      this.totalPages = response.totalPages;
      this.currentPage = page;
    });
  }

  nextPage() {
    if (this.currentPage + 1 < this.totalPages) {
      this.loadWords(this.currentPage + 1);
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.loadWords(this.currentPage - 1);
    }
  }

  ngOnInit(): void {
    this.loadWords(this.currentPage - 1)
  }

  @HostListener('document:keydown', ['$event'])
  handleKetDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      if (this.inputValue - 1 < this.totalPages && this.inputValue - 1 >= 0) {
        this.loadWords(this.inputValue - 1);
      }
    }
  }

  startEditing(word: WordDetails) {
    this.editingRowId = word.id;
    this.tempWord = { ...word };
    console.log("Edytuje ",this.editingRowId);
  }

  saveEditing() {
    if (this.tempWord) {
      const index = this.words.findIndex(w => w.id === this.tempWord!.id);
      if (index !== -1) {
        this.words[index] = { ...this.tempWord };
        this.wordService.putWord(this.tempWord).subscribe({
          next: (updatedWord) => {
            console.log("Updated word: ", updatedWord);
            this.words[index] = updatedWord;
          },
          error: (error) => {
            console.error("Error while updating word", error);
          }
        });
      }
    }
    this.cancelEditing();
  }

  cancelEditing() {
    this.editingRowId = null;
    this.tempWord = {} as WordDetails;
  }

  deleteWord() {
    if (this.editingRowId) {
      this.wordService.deleteWord(this.editingRowId).subscribe({
        next: () => {
          alert("Pomyślnie usunięto słowo z id: " + this.editingRowId)
          this.loadWords(this.inputValue - 1);
        },
        error: (error) => console.error("Error ", error)
      });
    }
  }
}
