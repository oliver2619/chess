import {ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {Store} from '@ngrx/store';
import {dialogActions} from '../../action/dialog-actions';
import {FigureColor} from '../../state/figure-color';
import {ButtonActiveDirective} from '../../directive/button-active.directive';
import {gameActions} from '../../action/game-actions';
import {Router} from '@angular/router';
import {BoardFlags} from '../../state/board-state';
import {gameBoardSelector} from '../../selector/game-selector';
import {BoardModel} from '../../model/board-model';

@Component({
  selector: 'c-resume-game-from-current-board',
  imports: [
    ButtonActiveDirective
  ],
  templateUrl: './resume-game-from-current-board.component.html',
  styleUrl: './resume-game-from-current-board.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ResumeGameFromCurrentBoardComponent {

  readonly whiteEnabled = signal(false);
  readonly blackEnabled = signal(false);
  readonly colorOnTurn = signal(FigureColor.WHITE);
  readonly castlingWhiteQueen = signal(false);
  readonly castlingWhiteQueenEnabled = signal(false);
  readonly castlingWhiteKing = signal(false);
  readonly castlingWhiteKingEnabled = signal(false);
  readonly castlingBlackQueen = signal(false);
  readonly castlingBlackQueenEnabled = signal(false);
  readonly castlingBlackKing = signal(false);
  readonly castlingBlackKingEnabled = signal(false);
  readonly enPassant = signal(-1);
  readonly enPassantEnabled = signal<boolean[]>([]);

  private boardModel: BoardModel | undefined;

  constructor(private readonly store: Store, private readonly router: Router) {
    this.store.select(gameBoardSelector).subscribe({
      next: board => {
        this.boardModel = BoardModel.fromState(board);
        const castlingFlags = this.boardModel.getCastlingFlagsForResumption();
        this.castlingWhiteQueenEnabled.set((castlingFlags & BoardFlags.WHITE_CASTLE_QUEEN) === BoardFlags.WHITE_CASTLE_QUEEN);
        this.castlingWhiteQueen.set(this.castlingWhiteQueenEnabled());
        this.castlingWhiteKingEnabled.set((castlingFlags & BoardFlags.WHITE_CASTLE_KING) === BoardFlags.WHITE_CASTLE_KING);
        this.castlingWhiteKing.set(this.castlingWhiteKingEnabled());
        this.castlingBlackQueenEnabled.set((castlingFlags & BoardFlags.BLACK_CASTLE_QUEEN) === BoardFlags.BLACK_CASTLE_QUEEN);
        this.castlingBlackQueen.set(this.castlingBlackQueenEnabled());
        this.castlingBlackKingEnabled.set((castlingFlags & BoardFlags.BLACK_CASTLE_KING) === BoardFlags.BLACK_CASTLE_KING);
        this.castlingBlackKing.set(this.castlingBlackKingEnabled());
        this.whiteEnabled.set(this.boardModel.canResumeWithColor(FigureColor.WHITE));
        this.blackEnabled.set(this.boardModel.canResumeWithColor(FigureColor.BLACK));
        if (this.whiteEnabled()) {
          this.colorOnTurn.set(FigureColor.WHITE);
        } else if (this.blackEnabled()) {
          this.colorOnTurn.set(FigureColor.BLACK);
        }
        this.updateEnPassantFlags();
      }
    });
  }

  setColorOnTurn(color: FigureColor) {
    this.colorOnTurn.set(color);
    this.updateEnPassantFlags();
  }

  cancel() {
    this.store.dispatch(dialogActions.close());
  }

  resume() {
    let flags: BoardFlags = BoardFlags.ALL_DISABLED;
    if (this.castlingWhiteQueen()) {
      flags |= BoardFlags.WHITE_CASTLE_QUEEN;
    }
    if (this.castlingWhiteKing()) {
      flags |= BoardFlags.WHITE_CASTLE_KING;
    }
    if (this.castlingBlackQueen()) {
      flags |= BoardFlags.BLACK_CASTLE_QUEEN;
    }
    if (this.castlingBlackKing()) {
      flags |= BoardFlags.BLACK_CASTLE_KING;
    }
    if (this.enPassant() >= 0) {
      flags |= BoardFlags.EN_PASSANT_ENABLED | this.enPassant();
    }
    this.store.dispatch(gameActions.resumeFromBoard({
      colorOnTurn: this.colorOnTurn(),
      flags
    }));
    this.store.dispatch(dialogActions.close());
    this.router.navigateByUrl('/game');
  }

  private updateEnPassantFlags() {
    if (this.boardModel != undefined) {
      const array: boolean[] = [];
      for (let l = 0; l < 8; ++l) {
        array.push(this.boardModel.canResumeWithEnPassant(this.colorOnTurn(), l));
      }
      this.enPassantEnabled.set(array);
    }
  }

}
