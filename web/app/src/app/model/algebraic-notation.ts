import {Notation} from './notation';
import {BoardModel} from './board-model';
import {MoveModel} from './move-model';
import {FigureType} from '../state/figure-type';

export class SAN implements Notation {

  parse(move: string, board: BoardModel): MoveModel {
    // TODO implement
    return new MoveModel(board.colorOnTurn, FigureType.PAWN, 1, 2, undefined);
  }

  stringify(move: MoveModel, board: BoardModel): string {
    // TODO implement
    return 'a1-b2';
  }

}
