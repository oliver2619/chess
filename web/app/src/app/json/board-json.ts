import {FigureColor} from '../state/figure-color';
import {BoardFlags} from '../state/board-state';

export interface BoardJson {
  readonly colorOnTurn: keyof typeof FigureColor;
  readonly colorMasks: readonly string[];
  readonly figureMasks: readonly string[];
  readonly kingFields: readonly number[];
  readonly flags: BoardFlags;
  readonly consecutiveRemisMoves: number;
}
