import {FigureType} from './figure-type';
import {FigureColor} from './figure-color';

export type ConvertedFigureType = FigureType.KNIGHT | FigureType.BISHOP | FigureType.ROOK | FigureType.QUEEN;

export interface MoveState {

  readonly color: FigureColor;
  readonly figure: FigureType;
  readonly start: number;
  readonly end: number;
  readonly conversion?: ConvertedFigureType;
}
