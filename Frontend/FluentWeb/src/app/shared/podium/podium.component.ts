import {Component, Input, OnChanges} from '@angular/core';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-podium',
  templateUrl: './podium.component.html',
  imports: [
    NgIf
  ],
  styleUrls: ['./podium.component.css']
})
export class PodiumComponent implements OnChanges {
  @Input() podium: { position: number; name: string; score: bigint }[] = [];

  ngOnChanges() {
    console.log('Podium received:', this.podium);
    if (!this.podium) {
      this.podium = [];
    }
  }
}
