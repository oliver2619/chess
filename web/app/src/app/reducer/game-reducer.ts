import {createReducer, on} from '@ngrx/store';
import {GameState} from '../state/game-state';
import {
  boardActions,
  BoardInsertFigureAction,
  BoardMoveFigureAction,
  BoardRemoveFigureAction
} from '../action/board-actions';
import {BoardModel} from '../model/board-model';
import {BoardState} from '../state/board-state';
import {gameActions, ResumeFromBoardAction, ResumeFromPgnAction} from '../action/game-actions';
import {GameModel} from '../model/game-model';
import {openingsActions} from '../action/openings-actions';

const initialGameState = GameModel.newInstance().toState();

function onBoardClear(state: GameState): GameState {
  const game = GameModel.fromState(state);
  game.clearBoard();
  return game.toState();
}

function onBoardEdit(state: GameState): GameState {
  const game = GameModel.fromState(state);
  game.switchToEditMode();
  return game.toState();
}

function onBoardReset(state: GameState): GameState {
  const game = GameModel.fromState(state);
  game.resetBoard();
  return game.toState();
}

function onBoardLoad(state: GameState, board: BoardState): GameState {
  const game = GameModel.fromState(state);
  game.loadBoard(BoardModel.fromState(board));
  return game.toState();
}

function onGameAbort(state: GameState): GameState {
  const game = GameModel.fromState(state);
  game.abort();
  return game.toState();
}

function onGameAcceptDraw(state: GameState): GameState {
  const game = GameModel.fromState(state);
  game.acceptDraw();
  return game.toState();
}

function onGameOfferDraw(state: GameState): GameState {
  const game = GameModel.fromState(state);
  game.offerDraw();
  return game.toState();
}

function onGameRestart(state: GameState): GameState {
  const game = GameModel.fromState(state);
  game.restart();
  return game.toState();
}

function onGameLoad(_: GameState, action: GameState): GameState {
  return action;
}

function onGameResumeFromPgn(state: GameState, action: ResumeFromPgnAction): GameState {
  const game = GameModel.fromState(state);
  game.resumeFromPgn(action);
  return game.toState();
}

function onGameResumeFromBoard(state: GameState, action: ResumeFromBoardAction): GameState {
  const game = GameModel.fromState(state);
  game.resumeFromBoard(action);
  return game.toState();
}

function onGameUndo(state: GameState): GameState {
  const game = GameModel.fromState(state);
  game.undo();
  return game.toState();
}

function onOpeningsCreate(state: GameState): GameState {
  const game = GameModel.fromState(state);
  game.newOpenings();
  return game.toState();
}

function onBoardRemoveFigure(state: GameState, action: BoardRemoveFigureAction): GameState {
  const game = GameModel.fromState(state);
  game.editRemoveFigure(action.field);
  return game.toState();
}

function onBoardInsertFigure(state: GameState, action: BoardInsertFigureAction): GameState {
  const game = GameModel.fromState(state);
  game.editInsertFigure(action.color, action.figureType, action.field);
  return game.toState();
}

function onBoardMoveFigure(state: GameState, action: BoardMoveFigureAction): GameState {
  const game = GameModel.fromState(state);
  game.editMoveFigure(action.start, action.end);
  return game.toState();
}

export const gameReducer = createReducer(
  initialGameState,
  on(boardActions.clear, onBoardClear),
  on(boardActions.edit, onBoardEdit),
  on(boardActions.insertFigure, onBoardInsertFigure),
  on(boardActions.load, onBoardLoad),
  on(boardActions.moveFigure, onBoardMoveFigure),
  on(boardActions.reset, onBoardReset),
  on(boardActions.removeFigure, onBoardRemoveFigure),
  on(gameActions.abort, onGameAbort),
  on(gameActions.acceptDraw, onGameAcceptDraw),
  on(gameActions.offerDraw, onGameOfferDraw),
  on(gameActions.restart, onGameRestart),
  on(gameActions.load, onGameLoad),
  on(gameActions.resumeFromPgn, onGameResumeFromPgn),
  on(gameActions.resumeFromBoard, onGameResumeFromBoard),
  on(gameActions.undo, onGameUndo),
  on(openingsActions.create, onOpeningsCreate),
);
