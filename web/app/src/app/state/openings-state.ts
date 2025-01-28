import {BoardState} from './board-state';
import {MoveState} from './move-state';

export interface OpeningsMoveState extends MoveState {
  readonly eco: string;
  readonly name: string;
}

export interface OpeningsState {
  readonly moves: readonly OpeningsMoveState[];
}
