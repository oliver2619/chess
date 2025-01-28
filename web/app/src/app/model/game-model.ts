import {GameJson} from '../json/game-json';
import {GamePhase, GameState} from '../state/game-state';
import {BoardModel} from './board-model';
import {TimeRulesModel} from './time-rules-model';
import {FigureColor} from '../state/figure-color';
import {HistoryModel} from './history-model';
import {PlayerModel} from './player-model';
import {ResumeFromBoardAction, ResumeFromPgnAction} from '../action/game-actions';
import {FigureType} from '../state/figure-type';

export class GameModel {

  private constructor(
    private board: BoardModel,
    private timeRules: TimeRulesModel,
    private winner: FigureColor | undefined,
    private phase: GamePhase,
    private remainingSeconds: number[],
    private endingNegotiatedByPlayers: boolean,
    private drawOffered: boolean,
    private history: HistoryModel,
    private readonly players: PlayerModel[],
  ) {
  }

  static newInstance(): GameModel {
    const board = BoardModel.initial();
    return new GameModel(
      board,
      TimeRulesModel.newInstance(),
      undefined,
      GamePhase.TERMINATED,
      [0, 0],
      false,
      false,
      HistoryModel.newInstance(board, 1),
      [PlayerModel.newInstance('White'), PlayerModel.newInstance('Black')]
    );
  }

  static fromJson(json: GameJson): GameModel {
    return new GameModel(
      BoardModel.fromJson(json.board),
      TimeRulesModel.fromJson(json.timeRules),
      json.winner == undefined ? undefined : FigureColor[json.winner],
      GamePhase[json.phase],
      [...json.remainingSeconds],
      json.endingNegotiatedByPlayers,
      json.drawOffered,
      HistoryModel.fromJson(json.history),
      json.players.map(it => PlayerModel.fromJson(it))
    );
  }

  static fromState(state: GameState): GameModel {
    return new GameModel(
      BoardModel.fromState(state.board),
      TimeRulesModel.fromState(state.timeRules),
      state.winner,
      state.phase,
      [...state.remainingSeconds],
      state.endingNegotiatedByPlayers,
      state.drawOffered,
      HistoryModel.fromState(state.history),
      state.players.map(it => PlayerModel.fromState(it))
    );
  }

  abort() {
    this.phase = GamePhase.TERMINATED;
    this.winner = 1 - this.board.colorOnTurn;
    this.endingNegotiatedByPlayers = true;
    this.drawOffered = false;
  }

  acceptDraw() {
    this.phase = GamePhase.TERMINATED;
    this.winner = undefined;
    this.endingNegotiatedByPlayers = true;
    this.drawOffered = true;
  }

  clearBoard() {
    this.loadBoard(BoardModel.empty());
  }

  editInsertFigure(color: FigureColor, type: FigureType, field: number) {
    if (this.phase != GamePhase.EDITED) {
      throw new Error('Figure cannot be inserted during this game phase.');
    }
    this.board = this.board.editInsertFigure(color, type, field);
  }

  editMoveFigure(start: number, end: number) {
    if (this.phase != GamePhase.EDITED) {
      throw new Error('Figure cannot be moved during this game phase.');
    }
    this.board = this.board.editMoveFigure(start, end);
  }

  editRemoveFigure(field: number) {
    if (this.phase != GamePhase.EDITED) {
      throw new Error('Figure cannot be removed during this game phase.');
    }
    this.board = this.board.editRemoveFigure(field);
  }

  loadBoard(board: BoardModel) {
    this.switchToEditMode();
    this.board = board;
    this.history.setInitial(board, 1);
  }

  newOpenings() {
    this.phase = GamePhase.OPENINGS;
    this.board = BoardModel.initial();
    this.winner = undefined;
    this.history.clear();
    this.history.setInitial(BoardModel.initial(), 1);
    this.endingNegotiatedByPlayers = false;
    this.drawOffered = false;
  }

  offerDraw() {
    this.drawOffered = true;
  }

  resetBoard() {
    this.loadBoard(BoardModel.initial());
  }

  restart() {
    this.board = BoardModel.initial();
    this.phase = GamePhase.INITIAL;
    this.history.clear();
    this.history.setInitial(this.board, 1);
    this.resume();
  }

  resumeFromPgn(action: ResumeFromPgnAction) {
    this.board = BoardModel.fromState(action.board);
    this.phase = GamePhase.PLAYING;
    this.history = HistoryModel.fromState(action.history);
    this.resume();
  }

  resumeFromBoard(action: ResumeFromBoardAction) {
    this.board = this.board.resumedWith(action.colorOnTurn, action.flags, 0);
    this.phase = GamePhase.PLAYING;
    this.history = HistoryModel.newInstance(this.board, 1);
    this.resume();
  }

  switchToEditMode() {
    this.phase = GamePhase.EDITED;
    this.winner = undefined;
    this.history.clear();
    this.endingNegotiatedByPlayers = false;
    this.drawOffered = false;
  }

  toJson(): GameJson {
    return {
      version: 1,
      board: this.board.toJson(),
      drawOffered: this.drawOffered,
      endingNegotiatedByPlayers: this.endingNegotiatedByPlayers,
      history: this.history.toJson(),
      phase: (GamePhase as any)[this.phase],
      remainingSeconds: [...this.remainingSeconds],
      winner: this.winner == undefined ? undefined : (FigureColor as any)[this.winner],
      players: this.players.map(it => it.toJson()),
      timeRules: this.timeRules.toJson(),
    };
  }

  toState(): GameState {
    return {
      board: this.board.toState(),
      boardCanBeResumed: this.board.canResume(),
      timeRules: this.timeRules.toState(),
      history: this.history.toState(),
      winner: this.winner,
      remainingSeconds: [...this.remainingSeconds],
      phase: this.phase,
      endingNegotiatedByPlayers: this.endingNegotiatedByPlayers,
      drawOffered: this.drawOffered,
      players: this.players.map(it => it.toState())
    };
  }

  undo() {
    // TODO implement
  }

  private checkmate() {
    this.phase = GamePhase.TERMINATED;
    this.winner = 1 - this.board.colorOnTurn;
    this.endingNegotiatedByPlayers = false;
    this.drawOffered = false;
  }

  private resume() {
    this.remainingSeconds = [this.timeRules.totalSeconds, this.timeRules.totalSeconds];
    this.winner = undefined;
    this.drawOffered = false;
    this.endingNegotiatedByPlayers = false;
  }

  private stealmate() {
    this.phase = GamePhase.TERMINATED;
    this.winner = undefined;
    this.endingNegotiatedByPlayers = false;
    this.drawOffered = false;
  }

  private switchToOpeningsMode() {
    this.phase = GamePhase.OPENINGS;
    this.winner = undefined;
    this.history.clear();
    this.endingNegotiatedByPlayers = false;
    this.drawOffered = false;
  }
}
