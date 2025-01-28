import {HistoryModel} from './history-model';
import {BoardModel} from './board-model';

export class PgnParseResult {

  constructor(readonly title: string, readonly white: string, readonly black: string, readonly board: BoardModel, readonly history: HistoryModel) {
  }
}

export class PgnParser {

  parse(pgn: string): PgnParseResult[] {
    return [new PgnParseResult('Game 1', 'White', 'Black', BoardModel.initial(), HistoryModel.newInstance(BoardModel.initial(), 1))];
  }
}
