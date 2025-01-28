import {createActionGroup, emptyProps, props} from '@ngrx/store';
import {GameState} from '../state/game-state';
import {HistoryState} from '../state/history-state';
import {BoardFlags, BoardState} from '../state/board-state';
import {FigureColor} from '../state/figure-color';

export interface ResumeFromPgnAction {
  readonly white: string;
  readonly black: string;
  readonly board: BoardState;
  readonly history: HistoryState;
}

export interface ResumeFromBoardAction {
  readonly colorOnTurn: FigureColor;
  readonly flags: BoardFlags;
}

export const gameActions = createActionGroup({
  source: 'Game',
  events: {
    abort: emptyProps(),
    acceptDraw: emptyProps(),
    offerDraw: emptyProps(),
    restart: emptyProps(),
    load: props<GameState>(),
    resumeFromPgn: props<ResumeFromPgnAction>(),
    resumeFromBoard: props<ResumeFromBoardAction>(),
    undo: emptyProps(),
  }
});
