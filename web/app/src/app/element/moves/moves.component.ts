import {ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {DecimalPipe} from '@angular/common';

interface Entry {
  readonly move: String;
  readonly name: string;
  readonly rating: number;
}

@Component({
  selector: 'c-moves',
  imports: [
    DecimalPipe
  ],
  templateUrl: './moves.component.html',
  styleUrl: './moves.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MovesComponent {

  readonly entries = signal<Entry[]>([]);
  readonly backEnabled = signal(true);

  constructor() {
    const e: Entry[] = [];
    e.push({
      name: 'King',
      move: 'e4',
      rating: 0.01
    });
    this.entries.set(e);
  }

  onBack() {

  }

  onMove(i: number) {

  }
}
