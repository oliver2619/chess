import {MoveState} from './move-state';
import {BoardState} from './board-state';

export interface HistoryElementState {
  readonly move: MoveState;
  readonly remainingTimeBeforeMove: number;
}

export interface HistoryState {
  readonly initialBoard: BoardState;
  readonly initialMoveIndex: number;
  readonly elements: readonly HistoryElementState[];
}
