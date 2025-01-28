import {ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {NgClass} from '@angular/common';
import {Store} from '@ngrx/store';
import {uiSelectedPieceSelector} from '../../selector/ui-selector';
import {FigureColor} from '../../state/figure-color';
import {uiActions} from '../../action/ui-actions';
import {FigureType} from '../../state/figure-type';
import {AssetsService} from '../../service/assets.service';
import {gameBoardSelector, gamePhaseSelector} from '../../selector/game-selector';
import {BoardModel} from '../../model/board-model';
import {GamePhase} from '../../state/game-state';

interface PiecesElement {
  readonly color: FigureColor;
  readonly type: FigureType;
  readonly imgTitle: string;
}

@Component({
  selector: 'c-pieces',
  imports: [
    NgClass
  ],
  templateUrl: './pieces.component.html',
  styleUrl: './pieces.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PiecesComponent {

  readonly selected = signal<PiecesElement | undefined>(undefined);
  readonly white = signal<PiecesElement[]>([]);
  readonly black = signal<PiecesElement[]>([]);

  private board: BoardModel | undefined;
  private gamePhase = GamePhase.TERMINATED;

  constructor(private readonly store: Store, private readonly assetService: AssetsService) {
    store.select(uiSelectedPieceSelector).subscribe({
      next: piece => {
        if (piece == undefined) {
          this.selected.set(undefined);
        } else {
          this.selected.set({
            color: piece.color,
            type: piece.type,
            imgTitle: this.assetService.getFigureImageTitle(piece.color, piece.type)
          });
        }
      }
    });
    store.select(gameBoardSelector).subscribe({
      next: board => {
        this.board = BoardModel.fromState(board);
        this.updatePieces();
      }
    });
    store.select(gamePhaseSelector).subscribe({
      next: phase => {
        this.gamePhase = phase;
        this.updatePieces();
      }
    })
  }

  onSelect(figure: PiecesElement) {
    if (this.selected()?.imgTitle === figure.imgTitle) {
      this.store.dispatch(uiActions.clearSelection());
    } else if(this.gamePhase === GamePhase.EDITED) {
      this.store.dispatch(uiActions.selectPiece({color: figure.color, figureType: figure.type}));
    }
  }

  private updatePieces() {
    if(this.board == undefined) {
      return;
    }
    const white: PiecesElement[] = [];
    const black: PiecesElement[] = [];
    const types = [FigureType.PAWN, FigureType.KNIGHT, FigureType.BISHOP, FigureType.ROOK, FigureType.QUEEN];
    if (this.gamePhase === GamePhase.EDITED) {
      types.forEach(type => {
        if(this.board!!.canInsertFigure(FigureColor.WHITE, type)) {
          white.push(this.createElement(FigureColor.WHITE, type));
        }
        if(this.board!!.canInsertFigure(FigureColor.BLACK, type)) {
          black.push(this.createElement(FigureColor.BLACK, type));
        }
      });
      const sel = this.selected();
      if(sel != undefined && !this.board.canInsertFigure(sel.color, sel.type)) {
        this.store.dispatch(uiActions.clearSelection());
      }
    } else {
      types.forEach(type => {
        let cnt = this.board!!.getCapturedFigures(FigureColor.WHITE, type);
        for(let i = 0; i < cnt; ++i) {
          white.push(this.createElement(FigureColor.WHITE, type));
        }
        cnt = this.board!!.getCapturedFigures(FigureColor.BLACK, type);
        for(let i = 0; i < cnt; ++i) {
          black.push(this.createElement(FigureColor.BLACK, type));
        }
      });
      if(this.selected() != undefined) {
        this.store.dispatch(uiActions.clearSelection());
      }
    }
    this.white.set(white);
    this.black.set(black);
  }

  private createElement(color: FigureColor, type: FigureType): PiecesElement {
    return {
      type,
      color,
      imgTitle: this.assetService.getFigureImageTitle(color, type)
    };
  }
}
