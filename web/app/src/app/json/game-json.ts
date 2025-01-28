import {BoardJson} from './board-json';
import {FigureColor} from '../state/figure-color';
import {GamePhase} from '../state/game-state';
import {TimeRulesJson} from './time-rules-json';
import {HistoryJson} from './history-json';
import {PlayerJson} from './player-json';

export interface GameJson {
  readonly version: 1;
  readonly board: BoardJson
  readonly drawOffered: boolean;
  readonly endingNegotiatedByPlayers: boolean;
  readonly history: HistoryJson;
  readonly phase: keyof typeof GamePhase;
  readonly remainingSeconds: readonly number[];
  readonly winner: keyof typeof FigureColor | undefined;
  readonly players: readonly PlayerJson[];
  readonly timeRules: TimeRulesJson;
}
