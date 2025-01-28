import {createReducer, on} from '@ngrx/store';
import {UiState} from '../state/ui-state';
import {UiSelectFieldAction, uiActions, UiSelectPieceAction} from '../action/ui-actions';

const initialUiState: UiState = {
  board: {
    flipped: false,
    selectedField: undefined,
  },
  pieces: {
    selected: undefined,
  }
};

function onUiFlipBoard(state: UiState): UiState {
  return {
    ...state,
    board: {
      ...state.board,
      flipped: !state.board.flipped,
    }
  };
}

function onUiSelectField(state: UiState, action: UiSelectFieldAction): UiState {
  return {
    ...state,
    board: {
      ...state.board,
      selectedField: action.field,
    },
    pieces: {
      ...state.pieces,
      selected: undefined,
    },
  };
}

function onUiSelectPiece(state: UiState, action: UiSelectPieceAction): UiState {
  return {
    ...state,
    board: {
      ...state.board,
      selectedField: undefined,
    },
    pieces: {
      ...state.pieces,
      selected: {
        type: action.figureType,
        color: action.color,
      }
    }
  };
}

function onUiClearSelection(state: UiState): UiState {
  return {
    ...state,
    board: {
      ...state.board,
      selectedField: undefined,
    },
    pieces: {
      ...state.pieces,
      selected: undefined,
    }
  };
}

export const uiReducer = createReducer(
  initialUiState,
  on(uiActions.flipBoard, onUiFlipBoard),
  on(uiActions.selectField, onUiSelectField),
  on(uiActions.selectPiece, onUiSelectPiece),
  on(uiActions.clearSelection, onUiClearSelection),
);
