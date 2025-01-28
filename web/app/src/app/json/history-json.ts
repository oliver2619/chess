import {BoardJson} from './board-json';
import {MoveJson} from './move-json';

export interface HistoryElementJson {
  readonly remainingTimeBeforeMove: number;
  readonly move: MoveJson;
}

export interface HistoryJson {
  readonly initialBoard: BoardJson;
  readonly initialMoveIndex: number;
  readonly elements: readonly HistoryElementJson[];
}
