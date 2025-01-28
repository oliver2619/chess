import {ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {MenuBarButtonDirective} from '../../directive/menu-bar-button.directive';
import {Store} from '@ngrx/store';
import {gamePhaseSelector} from '../../selector/game-selector';
import {GamePhase} from '../../state/game-state';

@Component({
  selector: 'c-menu-bar',
  imports: [
    MenuBarButtonDirective
  ],
  templateUrl: './menu-bar.component.html',
  styleUrl: './menu-bar.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MenuBarComponent {

  readonly sessionEnabled = signal(false);

  constructor(store: Store) {
    store.select(gamePhaseSelector).subscribe({
      next: phase => {
        this.sessionEnabled.set(phase === GamePhase.INITIAL || phase === GamePhase.PLAYING);
      }
    });
  }
}
