import {ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {Store} from '@ngrx/store';
import {gameBoardSelector, gamePhaseSelector} from '../../selector/game-selector';
import {GamePhase} from '../../state/game-state';
import {Router} from '@angular/router';
import {boardActions} from '../../action/board-actions';
import {dialogActions} from '../../action/dialog-actions';
import {NgClass} from '@angular/common';
import {uiBoardSelector} from '../../selector/ui-selector';
import {uiActions} from '../../action/ui-actions';
import {BoardModel} from '../../model/board-model';
import {FigureType} from '../../state/figure-type';

@Component({
  selector: 'c-board-menu',
  imports: [
    NgClass
  ],
  templateUrl: './board-menu.component.html',
  styleUrl: './board-menu.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BoardMenuComponent {

  readonly editEnabled = signal(false);
  readonly editActionsEnabled = signal(false);
  readonly flipped = signal(false);
  readonly removeFigureEnabled = signal(false);

  private board: BoardModel | undefined;
  private selectedField: number | undefined;

  constructor(private readonly store: Store, private readonly router: Router) {
    store.select(gamePhaseSelector).subscribe({
      next: phase => {
        this.editEnabled.set(phase === GamePhase.TERMINATED || phase === GamePhase.OPENINGS);
        this.editActionsEnabled.set(phase === GamePhase.EDITED);
      }
    });
    store.select(gameBoardSelector).subscribe({
      next: board => {
        this.board = BoardModel.fromState(board);
        this.removeFigureEnabled.set(this.canRemoveFigure());
      }
    });
    store.select(uiBoardSelector).subscribe({
      next: board => {
        this.flipped.set(board.flipped);
        this.selectedField = board.selectedField;
        this.removeFigureEnabled.set(this.canRemoveFigure());
      }
    });
  }

  clear() {
    this.store.dispatch(boardActions.clear());
  }

  edit() {
    this.store.dispatch(boardActions.edit());
    this.router.navigateByUrl('/edit');
  }

  exportFen() {
    this.store.dispatch(dialogActions.exportFen());
  }

  flip() {
    this.store.dispatch(uiActions.flipBoard());
  }

  importFen() {
    this.store.dispatch(dialogActions.importFen());
  }

  reset() {
    this.store.dispatch(boardActions.reset());
  }

  removeFigure() {
    if (this.selectedField != undefined) {
      this.store.dispatch(boardActions.removeFigure({field: this.selectedField}));
    }
  }

  private canRemoveFigure(): boolean {
    if (this.selectedField == undefined || this.board == undefined) {
      return false;
    }
    return this.board.canRemoveFigure(this.selectedField);
  }
}
