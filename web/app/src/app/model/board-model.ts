import {BoardFlags, BoardState} from '../state/board-state';
import {FigureColor} from '../state/figure-color';
import {BoardJson} from '../json/board-json';
import {FigureModel} from './figure-model';
import {MoveModel} from './move-model';
import {FigureType} from '../state/figure-type';
import {MaskModel} from './mask-model';

export class BoardModel {

  get colorOnTurn(): FigureColor {
    return this._colorOnTurn;
  }

  get consecutiveRemisMoves(): number {
    return this._consecutiveRemisMoves;
  }

  get flags(): BoardFlags {
    return this._flags;
  }

  private constructor(
    private readonly _colorMasks: readonly bigint[],
    private readonly _figureMasks: readonly bigint[],
    private readonly _kingFields: readonly number[],
    private readonly _colorOnTurn: FigureColor,
    private readonly _flags: BoardFlags,
    private readonly _consecutiveRemisMoves: number,
  ) {
  }

  static of(colorMasks: bigint[], figureMasks: bigint[], kingFields: number[], colorOnTurn: FigureColor, flags: BoardFlags, remisMoves: number): BoardModel {
    return new BoardModel(colorMasks.slice(0), figureMasks.slice(0), kingFields.slice(0), colorOnTurn, flags, remisMoves);
  }

  static initial(): BoardModel {
    return new BoardModel([
        MaskModel.rowMask(0) | MaskModel.rowMask(1),
        MaskModel.rowMask(6) | MaskModel.rowMask(7)
      ], [
        MaskModel.rowMask(1) | MaskModel.rowMask(6),
        MaskModel.fieldToMask(1) | MaskModel.fieldToMask(6) | MaskModel.fieldToMask(57) | MaskModel.fieldToMask(62),
        MaskModel.fieldToMask(2) | MaskModel.fieldToMask(5) | MaskModel.fieldToMask(58) | MaskModel.fieldToMask(61),
        MaskModel.fieldToMask(0) | MaskModel.fieldToMask(7) | MaskModel.fieldToMask(56) | MaskModel.fieldToMask(63),
        MaskModel.fieldToMask(3) | MaskModel.fieldToMask(59)
      ],
      [4, 60],
      FigureColor.WHITE,
      BoardFlags.ALL_CASTLE,
      0
    );
  }

  static empty(): BoardModel {
    return new BoardModel(
      [MaskModel.fieldToMask(4), MaskModel.fieldToMask(60)],
      [BigInt(0), BigInt(0), BigInt(0), BigInt(0), BigInt(0)],
      [4, 60],
      FigureColor.WHITE,
      0 as BoardFlags,
      0
    );
  }

  static fromState(state: BoardState): BoardModel {
    return new BoardModel(
      [...state.colorMasks],
      [...state.figureMasks],
      [...state.kingFields],
      state.colorOnTurn,
      state.flags,
      state.consecutiveRemisMoves
    );
  }

  static fromJson(json: BoardJson): BoardModel {
    return new BoardModel(
      json.colorMasks.map(it => BigInt(it)),
      json.figureMasks.map(it => BigInt(it)),
      [...json.kingFields],
      FigureColor[json.colorOnTurn],
      json.flags,
      json.consecutiveRemisMoves
    );
  }

  canInsertFigure(color: FigureColor, type: FigureType): boolean {
    if (type === FigureType.KING) {
      return false;
    }
    const cnt = this._figureMasks.map(m => MaskModel.countFields(m & this._colorMasks[color]));
    let convertedFigures = 0;
    if(cnt[FigureType.KNIGHT] > 2) {
      convertedFigures += cnt[FigureType.KNIGHT] - 2;
    }
    if(cnt[FigureType.BISHOP] > 2) {
      convertedFigures += cnt[FigureType.BISHOP] - 2;
    }
    if(cnt[FigureType.ROOK] > 2) {
      convertedFigures += cnt[FigureType.ROOK] - 2;
    }
    if(cnt[FigureType.QUEEN] > 1) {
      convertedFigures += cnt[FigureType.QUEEN] - 1;
    }
    switch(type) {
      case FigureType.PAWN:
        return convertedFigures + cnt[FigureType.PAWN] < 8;
      case FigureType.KNIGHT:
        return cnt[FigureType.KNIGHT] < 2 || convertedFigures + cnt[FigureType.PAWN] < 8;
      case FigureType.BISHOP:
        return cnt[FigureType.BISHOP] < 2 || convertedFigures + cnt[FigureType.PAWN] < 8;
      case FigureType.ROOK:
        return cnt[FigureType.ROOK] < 2 || convertedFigures + cnt[FigureType.PAWN] < 8;
      case FigureType.QUEEN:
        return cnt[FigureType.QUEEN] < 1 || convertedFigures + cnt[FigureType.PAWN] < 8;
    }
  }

  canRemoveFigure(field: number): boolean {
    const f = this.getFigure(field);
    return f != undefined && f.type !== FigureType.KING;
  }

  canInsertFigureAt(color: FigureColor, type: FigureType, field: number): boolean {
    if (!this.canInsertFigure(color, type)) {
      return false;
    }
    const mask = MaskModel.fieldToMask(field);
    return ((this._colorMasks[0] | this._colorMasks[1]) & mask) === 0n;
  }

  canResume(): boolean {
    return this.canResumeWithColor(FigureColor.WHITE) || this.canResumeWithColor(FigureColor.BLACK);
  }

  canResumeWithColor(color: FigureColor): boolean {
    // TODO implement, call WASM
    return true;
  }

  canResumeWithEnPassant(color: FigureColor, line: number): boolean {
    if (color === FigureColor.WHITE) {
      const figure = this.getFigure(line | 32);
      return figure != undefined && figure.type === FigureType.PAWN && figure.color === FigureColor.BLACK;
    } else {
      const figure = this.getFigure(line | 24);
      return figure != undefined && figure.type === FigureType.PAWN && figure.color === FigureColor.WHITE;
    }
  }

  editInsertFigure(color: FigureColor, type: FigureType, field: number): BoardModel {
    if (!this.canInsertFigureAt(color, type, field)) {
      throw new Error('Figure cannot be inserted.');
    }
    const mask = MaskModel.fieldToMask(field);
    const newColorMasks = [...this._colorMasks];
    const newFigureMasks = [...this._figureMasks];
    newColorMasks[color] |= mask;
    newFigureMasks[type] |= mask;
    return new BoardModel(newColorMasks, newFigureMasks, this._kingFields, this._colorOnTurn, this._flags, this._consecutiveRemisMoves);
  }

  editMoveFigure(start: number, end: number): BoardModel {
    const startFigure = this.getFigure(start);
    const endFigure = this.getFigure(end);
    if (startFigure == undefined || endFigure != undefined) {
      throw new Error('Figure cannot be moved.');
    }
    const startMask = MaskModel.fieldToMask(start);
    const endMask = MaskModel.fieldToMask(end);
    const newColorMasks = [...this._colorMasks];
    const newFigureMasks = [...this._figureMasks];
    const newKingFields = [...this._kingFields];
    newColorMasks[startFigure.color] &= ~startMask;
    newColorMasks[startFigure.color] |= endMask;
    if (startFigure.type === FigureType.KING) {
      newKingFields[startFigure.color] = end;
    } else {
      newFigureMasks[startFigure.type] &= ~startMask;
      newFigureMasks[startFigure.type] |= endMask;
    }
    return new BoardModel(newColorMasks, newFigureMasks, newKingFields, this._colorOnTurn, this._flags, this._consecutiveRemisMoves);
  }

  editRemoveFigure(field: number): BoardModel {
    const figure = this.getFigure(field);
    if (figure == undefined || figure.type === FigureType.KING) {
      throw new RangeError('No figure found that can be removed from specified field.');
    }
    const mask = MaskModel.fieldToMask(field);
    const newColorMasks = [...this._colorMasks];
    const newFigureMasks = [...this._figureMasks];
    newColorMasks[figure.color] &= ~mask;
    newFigureMasks[figure.type] &= ~mask;
    return new BoardModel(newColorMasks, newFigureMasks, this._kingFields, this._colorOnTurn, this._flags, this._consecutiveRemisMoves);
  }

  getFigure(field: number): FigureModel | undefined {
    const mask = MaskModel.fieldToMask(field);
    if ((this._colorMasks[FigureColor.WHITE] & mask) !== BigInt(0)) {
      return new FigureModel(FigureColor.WHITE, this.getFigureTypeFromMask(mask), field);
    } else if ((this._colorMasks[FigureColor.BLACK] & mask) !== BigInt(0)) {
      return new FigureModel(FigureColor.BLACK, this.getFigureTypeFromMask(mask), field);
    } else {
      return undefined;
    }
  }

  getFigures(callback: (figure: FigureModel) => void) {
    for (let color = 0; color < 2; ++color) {
      callback(new FigureModel(color, FigureType.KING, this._kingFields[color]));
      for (let type = 0; type < 5; ++type) {
        MaskModel.forEachField(this._colorMasks[color] & this._figureMasks[type], field => callback(new FigureModel(color, type, field)));
      }
    }
  }

  getFigureCount(color: FigureColor, type: FigureType): number {
    return MaskModel.countFields(this._colorMasks[color] & this._figureMasks[type]);
  }

  getCapturedFigures(color: FigureColor, type: FigureType): number {
    // TODO converted figures
    const cnt = this.getFigureCount(color, type);
    switch (type) {
      case FigureType.PAWN:
        return 8 - cnt;
      case FigureType.KNIGHT:
      case FigureType.BISHOP:
      case FigureType.ROOK:
        return 2 - cnt;
      case FigureType.QUEEN:
        return 1 - cnt;
      case FigureType.KING:
        throw RangeError('Kings cannot be captured');
    }
  }

  getCastlingFlagsForResumption(): BoardFlags {
    let flags: BoardFlags = 0;
    if (this._kingFields[FigureColor.WHITE] === 4) {
      let f = this.getFigure(0);
      if (f != undefined && f.color === FigureColor.WHITE && f.type === FigureType.ROOK) {
        flags |= BoardFlags.WHITE_CASTLE_QUEEN;
      }
      f = this.getFigure(7);
      if (f != undefined && f.color === FigureColor.WHITE && f.type === FigureType.ROOK) {
        flags |= BoardFlags.WHITE_CASTLE_KING;
      }
    }
    if (this._kingFields[FigureColor.BLACK] === 60) {
      let f = this.getFigure(56);
      if (f != undefined && f.color === FigureColor.BLACK && f.type === FigureType.ROOK) {
        flags |= BoardFlags.BLACK_CASTLE_QUEEN;
      }
      f = this.getFigure(63);
      if (f != undefined && f.color === FigureColor.BLACK && f.type === FigureType.ROOK) {
        flags |= BoardFlags.BLACK_CASTLE_KING;
      }
    }
    return flags;
  }

  resumedWith(colorOnTurn: FigureColor, flags: BoardFlags, consecutiveRemisMoves: number): BoardModel {
    return new BoardModel(this._colorMasks, this._figureMasks, this._kingFields, colorOnTurn, flags, consecutiveRemisMoves);
  }

  moved(move: MoveModel): BoardModel {
    // TODO implement
    return this;
  }

  toJson(): BoardJson {
    return {
      colorMasks: this._colorMasks.map(it => it.toString()),
      colorOnTurn: (FigureColor as any)[this._colorOnTurn],
      flags: this._flags,
      kingFields: [...this._kingFields],
      consecutiveRemisMoves: this._consecutiveRemisMoves,
      figureMasks: this._figureMasks.map(it => it.toString())
    };
  }

  toState(): BoardState {
    return {
      colorMasks: [...this._colorMasks],
      figureMasks: [...this._figureMasks],
      flags: this._flags,
      kingFields: [...this._kingFields],
      consecutiveRemisMoves: this._consecutiveRemisMoves,
      colorOnTurn: this._colorOnTurn
    };
  }

  private getFigureTypeFromMask(mask: bigint): FigureType {
    if ((this._figureMasks[FigureType.PAWN] & mask) === mask) {
      return FigureType.PAWN;
    } else if ((this._figureMasks[FigureType.KNIGHT] & mask) === mask) {
      return FigureType.KNIGHT;
    } else if ((this._figureMasks[FigureType.BISHOP] & mask) === mask) {
      return FigureType.BISHOP;
    } else if ((this._figureMasks[FigureType.ROOK] & mask) === mask) {
      return FigureType.ROOK;
    } else if ((this._figureMasks[FigureType.QUEEN] & mask) === mask) {
      return FigureType.QUEEN;
    } else {
      return FigureType.KING;
    }
  }
}
