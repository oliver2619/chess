import {HistoryElementState, HistoryState} from '../state/history-state';
import {HistoryElementJson, HistoryJson} from '../json/history-json';
import {BoardModel} from './board-model';
import {MoveModel} from './move-model';

class HistoryElementModel {

  constructor(readonly move: MoveModel, readonly remainingTimeBeforeMove: number) {
  }

  static fromState(state: HistoryElementState): HistoryElementModel {
    return new HistoryElementModel(MoveModel.fromState(state.move), state.remainingTimeBeforeMove);
  }

  toJson(): HistoryElementJson {
    return {
      move: this.move.toJson(),
      remainingTimeBeforeMove: this.remainingTimeBeforeMove
    };
  }

  toState(): HistoryElementState {
    return {
      move: this.move.toState(),
      remainingTimeBeforeMove: this.remainingTimeBeforeMove
    };
  }

}

export class HistoryModel {

  private constructor(private initialBoard: BoardModel, private initialMoveIndex: number, private elements: HistoryElementModel[]) {
  }

  static newInstance(initialBoard: BoardModel, initialMoveIndex: number): HistoryModel {
    return new HistoryModel(initialBoard, initialMoveIndex, []);
  }

  static fromJson(json: HistoryJson): HistoryModel {
    const initialBoard = BoardModel.fromJson(json.initialBoard);
    let board = initialBoard;
    return new HistoryModel(initialBoard, json.initialMoveIndex, json.elements.map(el => {
      const figure = board.getFigure(el.move.start);
      if (figure == undefined) {
        throw new Error('JSON contained an illegal move.');
      }
      const mm = MoveModel.fromJson(el.move, figure.color, figure.type);
      board = board.moved(mm);
      return new HistoryElementModel(mm, el.remainingTimeBeforeMove);
    }));
  }

  static fromState(state: HistoryState): HistoryModel {
    return new HistoryModel(BoardModel.fromState(state.initialBoard), state.initialMoveIndex, state.elements.map(it => HistoryElementModel.fromState(it)));
  }

  clear() {
    this.elements = [];
  }

  setInitial(board: BoardModel, moveIndex: number) {
    this.initialBoard = board;
    this.initialMoveIndex = moveIndex;
  }

  toJson(): HistoryJson {
    return {
      initialBoard: this.initialBoard.toJson(),
      initialMoveIndex: this.initialMoveIndex,
      elements: this.elements.map(it => it.toJson())
    };
  }

  toState(): HistoryState {
    return {
      initialBoard: this.initialBoard.toState(),
      initialMoveIndex: this.initialMoveIndex,
      elements: this.elements.map(it => it.toState())
    };
  }
}
