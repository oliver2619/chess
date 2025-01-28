import {FigureColor} from './figure-color';
import {FigureType} from './figure-type';

export interface BoardUiState {
  readonly flipped: boolean;
  readonly selectedField: number | undefined;
}

export interface SelectedPieceUiState {
  readonly color: FigureColor;
  readonly type: FigureType;
}

export interface PiecesUiState {
  readonly selected: SelectedPieceUiState | undefined;
}

export interface UiState{
  readonly board: BoardUiState;
  readonly pieces: PiecesUiState;
}
