import {FigureColor} from '../state/figure-color';
import {BoardFlags} from '../state/board-state';
import {FigureType} from '../state/figure-type';
import {BoardModel} from './board-model';

export interface FenParseResult {
  readonly board: BoardModel;
  readonly nextMoveIndex: number;
}

export class FenParser {

  private static readonly figureTypes: { [key: string]: FigureType } = {
    'p': FigureType.PAWN,
    'n': FigureType.KNIGHT,
    'b': FigureType.BISHOP,
    'r': FigureType.ROOK,
    'q': FigureType.QUEEN,
    'k': FigureType.KING
  };

  private colorMasks: bigint[] = [BigInt(0), BigInt(0)];
  private figureMasks: bigint[] = [BigInt(0), BigInt(0), BigInt(0), BigInt(0), BigInt(0)];
  private kingFields: number[] = [0, 0];

  parse(input: string): FenParseResult {
    this.reset();
    const groups = input.split(/\s+/);
    if (groups.length != 6) {
      throw new RangeError('Input must contain 6 groups.');
    }
    this.parseRows(groups[0].split('/'));
    const colorOnTurn = FenParser.parseColorOnTurn(groups[1]);
    const flags = FenParser.parseCastelling(groups[2]) | FenParser.parseEnPassant(groups[3]);
    const remisMoves = FenParser.parseRemisMoves(groups[4]);
    const nextMoveIndex = FenParser.parseNextMoveIndex(groups[5]);
    return {
      board: BoardModel.of(this.colorMasks, this.figureMasks, this.kingFields, colorOnTurn, flags, remisMoves),
      nextMoveIndex
    };
  }

  private reset() {
    this.colorMasks = [BigInt(0), BigInt(0)];
    this.figureMasks = [BigInt(0), BigInt(0), BigInt(0), BigInt(0), BigInt(0)];
    this.kingFields = [0, 0];
  }

  private parseRows(rows: string[]) {
    if (rows.length != 8) {
      throw new RangeError('Input must contain 8 rows.');
    }
    rows.forEach((row, i) => this.parseRow(row, 7 - i));
  }

  private parseRow(rowInput: string, rowIndex: number) {
    let line = 0;
    for (const i of rowInput) {
      if (line > 7) {
        throw new RangeError('Row must contain exactly 8 fields.');
      }
      const figureType = FenParser.figureTypes[i.toLowerCase()];
      if (figureType == undefined) {
        const space = Number.parseInt(i);
        if (isNaN(space) || space < 1) {
          throw new RangeError(`Illegal spacing ${i}.`);
        }
        line += space;
      } else {
        const color = i === i.toUpperCase() ? FigureColor.WHITE : FigureColor.BLACK;
        const field = line | (rowIndex << 3);
        const mask = BigInt(1) << BigInt(field);
        this.colorMasks[color] |= mask;
        if (figureType === FigureType.KING) {
          this.kingFields[color] = field;
        } else {
          this.figureMasks[figureType] |= mask;
        }
        ++line;
      }
    }
    if (line !== 8) {
      throw new RangeError('Row must contain exactly 8 fields.');
    }
  }

  private static parseColorOnTurn(c: string): FigureColor {
    switch (c) {
      case 'w':
        return FigureColor.WHITE;
      case 'b':
        return FigureColor.BLACK;
      default:
        throw new RangeError(`Illegal player on turn ${c}.`);
    }
  }

  private static parseCastelling(cl: string): number {
    let ret = 0;
    if (cl === '-') {
      return 0;
    }
    for (const c of cl) {
      switch (c) {
        case 'k':
          ret = ret | BoardFlags.BLACK_CASTLE_KING;
          break;
        case 'q':
          ret = ret | BoardFlags.BLACK_CASTLE_QUEEN;
          break;
        case 'K':
          ret = ret | BoardFlags.WHITE_CASTLE_KING;
          break;
        case 'Q':
          ret = ret | BoardFlags.WHITE_CASTLE_QUEEN;
          break;
        default:
          throw new RangeError(`Illegal castelling flag ${c}.`);
      }
    }
    return ret;
  }

  private static parseEnPassant(ep: string): number {
    if (ep === '-') {
      return 0;
    }
    if (ep.length !== 2) {
      throw new RangeError(`Illegal en passant value ${ep}.`);
    }
    const line = ep.toLowerCase().charCodeAt(0) - 'a'.charCodeAt(0);
    if (line < 0 || line > 7) {
      throw new RangeError(`Illegal en passant value ${ep}.`);
    }
    return BoardFlags.EN_PASSANT_ENABLED | line;
  }

  private static parseRemisMoves(rm: string): number {
    const ret = Number.parseInt(rm);
    if (isNaN(ret)) {
      throw new RangeError(`Illegal number of remis moves ${rm}.`);
    }
    return ret;
  }

  private static parseNextMoveIndex(nmi: string): number {
    const ret = Number.parseInt(nmi);
    if (isNaN(ret)) {
      throw new RangeError(`Illegal next move index ${nmi}.`);
    }
    return ret;
  }
}

export class FenStringifier {

  private static readonly figures: string[] = ['p', 'n', 'b', 'r', 'q', 'k'];

  static stringify(board: BoardModel, nextMoveIndex: number): string {
    if (nextMoveIndex < 1) {
      throw new RangeError(`Next move index must be greater than 0`);
    }
    const groups: string[] = [
      this.getFigures(board),
      this.getColorOnTurn(board.colorOnTurn),
      this.getCastelling(board.flags),
      this.getEnPassant(board.flags, board.colorOnTurn),
      this.getRemisMoves(board.consecutiveRemisMoves),
      this.getNextMoveIndex(nextMoveIndex)
    ];
    return groups.join(' ');
  }

  private static getFigures(board: BoardModel): string {
    const rows: string[] = [];
    for (let i = 7; i >= 0; --i) {
      rows.push(this.getFiguresOfRow(board, i));
    }
    return rows.join('/');
  }

  private static getFiguresOfRow(board: BoardModel, rowIndex: number): string {
    let space = 0;
    let ret = '';
    for (let l = 0; l < 8; ++l) {
      const figure = board.getFigure(l | (rowIndex << 3));
      if (figure === undefined) {
        ++space;
      } else {
        if (space > 0) {
          ret += space.toString();
          space = 0;
        }
        const f = FenStringifier.figures[figure.type];
        ret += figure.color === FigureColor.WHITE ? f.toUpperCase() : f.toLowerCase();
      }
    }
    if (space > 0) {
      ret += space.toString();
    }
    return ret;
  }

  private static getColorOnTurn(color: FigureColor): string {
    return color === FigureColor.WHITE ? 'w' : 'b';
  }

  private static getCastelling(flags: BoardFlags): string {
    if ((flags & BoardFlags.ALL_CASTLE) === 0) {
      return '-';
    }
    let ret = '';
    if ((flags & BoardFlags.WHITE_CASTLE_KING) === BoardFlags.WHITE_CASTLE_KING) {
      ret += 'K';
    }
    if ((flags & BoardFlags.WHITE_CASTLE_QUEEN) === BoardFlags.WHITE_CASTLE_QUEEN) {
      ret += 'Q';
    }
    if ((flags & BoardFlags.BLACK_CASTLE_KING) === BoardFlags.BLACK_CASTLE_KING) {
      ret += 'k';
    }
    if ((flags & BoardFlags.BLACK_CASTLE_QUEEN) === BoardFlags.BLACK_CASTLE_QUEEN) {
      ret += 'q';
    }
    return ret;
  }

  private static getEnPassant(flags: BoardFlags, colorOnTurn: FigureColor): string {
    if ((flags & BoardFlags.EN_PASSANT_ENABLED) === BoardFlags.EN_PASSANT_ENABLED) {
      const line = String.fromCharCode('a'.charCodeAt(0) + (flags & 7));
      const row = colorOnTurn === FigureColor.WHITE ? 6 : 3;
      return `${line}${row}`;
    } else {
      return '-';
    }
  }

  private static getRemisMoves(rm: number): string {
    return rm.toString();
  }

  private static getNextMoveIndex(nmi: number): string {
    return nmi.toString();
  }
}
