import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BoardComponent} from '../../element/board/board.component';
import {HistoryComponent} from '../../element/history/history.component';
import {PiecesComponent} from '../../element/pieces/pieces.component';
import {ClocksComponent} from '../../element/clocks/clocks.component';
import {GameStatusComponent} from '../../element/game-status/game-status.component';

@Component({
  selector: 'c-game',
  imports: [
    BoardComponent,
    HistoryComponent,
    PiecesComponent,
    ClocksComponent,
    GameStatusComponent
  ],
  templateUrl: './game.component.html',
  styleUrl: './game.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameComponent {
}
