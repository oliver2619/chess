import {FigureColor} from './figure-color';

export enum BoardFlags {
  ALL_DISABLED = 0,
  EN_PASSANT_ENABLED = 8,
  WHITE_CASTLE_QUEEN = 16,
  WHITE_CASTLE_KING = 32,
  WHITE_CASTLE_BOTH = WHITE_CASTLE_KING | WHITE_CASTLE_QUEEN,
  BLACK_CASTLE_QUEEN = 64,
  BLACK_CASTLE_KING = 128,
  BLACK_CASTLE_BOTH = BLACK_CASTLE_QUEEN | BLACK_CASTLE_KING,
  ALL_CASTLE = WHITE_CASTLE_BOTH | BLACK_CASTLE_BOTH,
}

export interface BoardState {
  readonly colorOnTurn: FigureColor;
  readonly colorMasks: readonly bigint[];
  readonly figureMasks: readonly bigint[];
  readonly kingFields: readonly number[];
  readonly flags: BoardFlags;
  readonly consecutiveRemisMoves: number;
}
