import {createFeatureSelector, createSelector} from '@ngrx/store';
import {GamePhase, GameState} from '../state/game-state';

export const gameSelector = createFeatureSelector<GameState>('game');

export const gameRemainingTimesSelector = createSelector(
  gameSelector,
  game => game.remainingSeconds
)

export const gamePhaseSelector = createSelector(
  gameSelector,
  game => game.phase
)

export const gameBoardSelector = createSelector(
  gameSelector,
  game => game.board
);

export const gameCanAbortSelector = createSelector(
  gameSelector,
  game => (game.phase === GamePhase.PLAYING || game.phase === GamePhase.INITIAL) && game.players[0].ai != undefined && game.players[1].ai != undefined
);

export const gameHumanOnTurnSelector = createSelector(
  gameSelector,
  game => (game.phase === GamePhase.PLAYING || game.phase === GamePhase.INITIAL) && game.players[game.board.colorOnTurn].ai == undefined
);

export const gameCanOfferDrawSelector = createSelector(
  gameSelector,
  game => (game.phase === GamePhase.PLAYING || game.phase === GamePhase.INITIAL) && game.players[game.board.colorOnTurn].ai == undefined && !game.drawOffered
);

export const gameCanAcceptDrawSelector = createSelector(
  gameSelector,
  game => (game.phase === GamePhase.PLAYING || game.phase === GamePhase.INITIAL) && game.players[game.board.colorOnTurn].ai == undefined && game.drawOffered
);

export const gameHistorySelector = createSelector(
  gameSelector,
  game => game.history
);
