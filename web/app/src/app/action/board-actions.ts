import {createActionGroup, emptyProps, props} from '@ngrx/store';
import {BoardState} from '../state/board-state';
import {FigureType} from '../state/figure-type';
import {FigureColor} from '../state/figure-color';

export interface BoardInsertFigureAction {
  readonly field: number;
  readonly color: FigureColor;
  readonly figureType: FigureType;
}

export interface BoardRemoveFigureAction {
  readonly field: number;
}

export interface BoardMoveFigureAction {
  readonly start: number;
  readonly end: number;
}

export const boardActions = createActionGroup({
  source: 'Board',
  events: {
    clear: emptyProps(),
    edit: emptyProps(),
    load: props<BoardState>(),
    reset: emptyProps(),
    insertFigure: props<BoardInsertFigureAction>(),
    moveFigure: props<BoardMoveFigureAction>(),
    removeFigure: props<BoardRemoveFigureAction>(),
  }
});
