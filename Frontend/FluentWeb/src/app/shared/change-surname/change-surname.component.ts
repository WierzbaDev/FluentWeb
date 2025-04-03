import {Component, EventEmitter, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ChangesDataService} from '../../services/changes-data.service';

@Component({
  selector: 'app-change-surname',
  imports: [
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './change-surname.component.html',
  styleUrl: './change-surname.component.css'
})
export class ChangeSurnameComponent {
  newSurname: string = '';

  constructor(private changesDataService: ChangesDataService) {
  }

  @Output() close = new EventEmitter<void>();

  saveNewSurname() {
    console.log("Changed surname!", this.newSurname);
    this.changesDataService.updateSurname(this.newSurname).subscribe({
      next: (response) => {
        console.log("Changed surname successfully", response);
      },
      error: (err) => {
        console.error("Error while changing surname", err);
      }
    });
  }

  closePanel() {
    this.close.emit();
  }
}
