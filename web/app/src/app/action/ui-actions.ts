import {createActionGroup, emptyProps, props} from '@ngrx/store';
import {FigureColor} from '../state/figure-color';
import {FigureType} from '../state/figure-type';

export interface UiSelectFieldAction {
  readonly field: number | undefined;
}

export interface UiSelectPieceAction {
  readonly color: FigureColor;
  readonly figureType: FigureType;
}

export const uiActions = createActionGroup({
  source: 'UI',
  events: {
    flipBoard: emptyProps(),
    clearSelection: emptyProps(),
    selectPiece: props<UiSelectPieceAction>(),
    selectField: props<UiSelectFieldAction>(),
  }
});
