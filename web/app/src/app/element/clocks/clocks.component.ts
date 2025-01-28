import {ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {Store} from '@ngrx/store';
import {gameRemainingTimesSelector} from '../../selector/game-selector';
import {TimePipe} from '../../pipe/time.pipe';
import {FigureColor} from '../../state/figure-color';

@Component({
  selector: 'c-clocks',
  imports: [
    TimePipe
  ],
  templateUrl: './clocks.component.html',
  styleUrl: './clocks.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ClocksComponent {

  timeWhite = signal(0);
  timeBlack = signal(0);

  constructor(store: Store) {
    store.select(gameRemainingTimesSelector).subscribe({
      next: times => {
        this.timeWhite.set(times[FigureColor.WHITE]);
        this.timeBlack.set(times[FigureColor.BLACK]);
      }
    });
  }

}
