import {MoveModel} from './move-model';
import {BoardModel} from './board-model';

export interface Notation {

  parse(move: string, board: BoardModel): MoveModel;

  stringify(move: MoveModel, board: BoardModel): string;

}
