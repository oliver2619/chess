import {createFeatureSelector, createSelector} from '@ngrx/store';
import {UiState} from '../state/ui-state';

const uiSelector = createFeatureSelector<UiState>('ui');

export const uiBoardSelector = createSelector(
  uiSelector,
  ui => ui.board
);

export const uiSelectedPieceSelector = createSelector(
  uiSelector,
  ui => ui.pieces.selected
);
