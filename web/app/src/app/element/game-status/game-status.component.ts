import {ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {Store} from '@ngrx/store';
import {gameCanAcceptDrawSelector, gamePhaseSelector, gameSelector} from '../../selector/game-selector';
import {gameActions} from '../../action/game-actions';
import {GamePhase} from '../../state/game-state';

@Component({
  selector: 'c-game-status',
  imports: [],
  templateUrl: './game-status.component.html',
  styleUrl: './game-status.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameStatusComponent {

  readonly canAcceptDraw = signal(false);
  readonly isGameOver = signal(false);
  readonly winner = signal('');
  readonly looser = signal('');
  readonly isRemis = signal(false);
  readonly endingNegotiatedByPlayers = signal(false);

  constructor(private readonly store: Store) {
    store.select(gameCanAcceptDrawSelector).subscribe({
      next: it => this.canAcceptDraw.set(it)
    });
    store.select(gameSelector).subscribe({
      next: game => {
        this.isGameOver.set(game.phase === GamePhase.TERMINATED);
        if (game.winner != undefined) {
          this.winner.set(game.players[game.winner].name);
          this.looser.set(game.players[1 - game.winner].name);
        } else {
          this.winner.set('');
          this.looser.set('');
        }
        this.isRemis.set(game.winner == undefined);
        this.endingNegotiatedByPlayers.set(game.endingNegotiatedByPlayers);
      }
    });
  }

  acceptDraw() {
    this.store.dispatch(gameActions.acceptDraw());
  }
}
