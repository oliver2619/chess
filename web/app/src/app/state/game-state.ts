import {FigureColor} from './figure-color';
import {BoardState} from './board-state';
import {TimeRulesState} from './time-rules-state';
import {HistoryState} from './history-state';
import {PlayerState} from './player-state';

export enum GamePhase {
  INITIAL, PLAYING, TERMINATED, EDITED, OPENINGS
}

export interface GameState {
  readonly board: BoardState;
  readonly drawOffered: boolean;
  readonly endingNegotiatedByPlayers: boolean;
  readonly history: HistoryState;
  readonly phase: GamePhase;
  readonly remainingSeconds: readonly number[];
  readonly winner: FigureColor | undefined;
  readonly players: readonly PlayerState[];
  readonly timeRules: TimeRulesState;
  readonly boardCanBeResumed: boolean;
}
