import {Component, EventEmitter, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ChangesDataService} from '../../services/changes-data.service';

@Component({
  selector: 'app-change-birthday',
  imports: [
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './change-birthday.component.html',
  styleUrl: './change-birthday.component.css'
})
export class ChangeBirthdayComponent {
  newBirthday: Date = new Date();

  constructor(private changesDataService: ChangesDataService) {
  }

  @Output() close = new EventEmitter<void>();

  saveNewBirthday() {
    console.log("Changed date of birthday!", this.newBirthday);
    this.changesDataService.updateBirthday(this.newBirthday).subscribe({
      next: (response) => {
        console.log("Changed date of birthday successfully", response);
      },
      error: (err) => {
        console.error("Error while changing date of birthday", err);
      }
    });
  }

  closePanel() {
    this.close.emit();
  }
}
