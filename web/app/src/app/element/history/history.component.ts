import {ChangeDetectionStrategy, Component, computed, signal} from '@angular/core';
import {Store} from '@ngrx/store';
import {gameHistorySelector} from '../../selector/game-selector';
import {HistoryState} from '../../state/history-state';
import {Notation} from '../../model/notation';
import {SAN} from '../../model/algebraic-notation';
import {BoardModel} from '../../model/board-model';
import {MoveModel} from '../../model/move-model';
import {FigureColor} from '../../state/figure-color';
import {dialogActions} from '../../action/dialog-actions';
import {gameActions} from '../../action/game-actions';

interface Entry {
  readonly index: number;
  white: string;
  black: string;
}

@Component({
  selector: 'c-history',
  imports: [],
  templateUrl: './history.component.html',
  styleUrl: './history.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistoryComponent {

  readonly entries = signal<Entry[]>([]);
  readonly canUndo = computed(() => this.entries().length > 0);

  private notation: Notation = new SAN();

  constructor(private readonly store: Store) {
    store.select(gameHistorySelector).subscribe({
      next: history => this.update(history)
    });
  }

  onUndo() {
    this.store.dispatch(gameActions.undo());
  }

  onExport() {
    this.store.dispatch(dialogActions.exportPgn());
  }

  private update(history: HistoryState) {
    let index = history.initialMoveIndex;
    let board = BoardModel.fromState(history.initialBoard);
    const entries: Entry[] = [];
    let current: Entry | undefined;
    history.elements.forEach(el => {
      const m = MoveModel.fromState(el.move);
      if (current == undefined) {
        current = {
          index,
          white: '',
          black: ''
        };
        ++index;
        entries.push(current);
      }
      if (board.colorOnTurn === FigureColor.WHITE) {
        current.white = this.notation.stringify(m, board);
        board = board.moved(m);
      } else {
        current.black = this.notation.stringify(m, board);
        board = board.moved(m);
        current = undefined;
      }
    });
    this.entries.set(entries);
  }
}
