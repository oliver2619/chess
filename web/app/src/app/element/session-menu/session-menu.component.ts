import {ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {Store} from '@ngrx/store';
import {
  gameCanAbortSelector,
  gameCanAcceptDrawSelector,
  gameCanOfferDrawSelector,
  gameHumanOnTurnSelector
} from '../../selector/game-selector';
import {gameActions} from '../../action/game-actions';

@Component({
  selector: 'c-session-menu',
  imports: [],
  templateUrl: './session-menu.component.html',
  styleUrl: './session-menu.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SessionMenuComponent {

  readonly canAbort = signal(false);
  readonly canResign = signal(false);
  readonly canOfferDraw = signal(false);

  constructor(private readonly store: Store) {
    store.select(gameHumanOnTurnSelector).subscribe({
      next: it => this.canResign.set(it)
    });
    store.select(gameCanAbortSelector).subscribe({
      next: it => this.canAbort.set(it)
    });
    store.select(gameCanOfferDrawSelector).subscribe({
      next: it => this.canOfferDraw.set(it)
    });
  }

  abort() {
    this.store.dispatch(gameActions.abort());
  }

  offerDraw() {
    this.store.dispatch(gameActions.offerDraw());
  }
}
