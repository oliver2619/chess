import {FigureType} from '../state/figure-type';

export interface MoveJson {
  readonly start: number;
  readonly end: number;
  readonly conversion?: keyof typeof FigureType;
}
