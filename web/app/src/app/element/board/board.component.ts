import {AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, ViewChild} from '@angular/core';
import {Store} from '@ngrx/store';
import {BoardMenuComponent} from '../board-menu/board-menu.component';
import {gameBoardSelector} from '../../selector/game-selector';
import {FigureColor} from '../../state/figure-color';
import {FigureType} from '../../state/figure-type';
import {BoardModel} from '../../model/board-model';
import {uiActions} from '../../action/ui-actions';
import {uiBoardSelector, uiSelectedPieceSelector} from '../../selector/ui-selector';
import {AssetsService} from '../../service/assets.service';
import {boardActions} from '../../action/board-actions';

@Component({
  selector: 'c-board',
  imports: [
    BoardMenuComponent
  ],
  templateUrl: './board.component.html',
  styleUrl: './board.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BoardComponent implements AfterViewInit {

  @ViewChild('svg')
  svg: ElementRef<SVGSVGElement> | undefined;

  private figuresParent: SVGGElement | undefined;
  private labelsParent: SVGGElement | undefined;
  private selectionParent: SVGGElement | undefined;
  private board: BoardModel | undefined;
  private flipped = false;
  private selectedField: number | undefined;
  private selectedFigureType: FigureType | undefined;
  private selectedFigureColor: FigureColor | undefined;

  constructor(private readonly store: Store, private readonly assetService: AssetsService) {
    store.select(uiBoardSelector).subscribe({
      next: board => {
        this.flipped = board.flipped;
        this.selectedField = board.selectedField;
        this.updateFields();
        this.updateFigures();
        this.updateSelection();
      }
    });
    store.select(uiSelectedPieceSelector).subscribe({
      next: figure => {
        this.selectedFigureType = figure?.type;
        this.selectedFigureColor = figure?.color;
      }
    });
    store.select(gameBoardSelector).subscribe({
      next: board => {
        this.board = BoardModel.fromState(board);
        this.updateFigures();
      }
    });
  }

  ngAfterViewInit() {
    for (let color = 0; color < 2; ++color) {
      for (let type = 0; type < 6; ++type) {
        const title = this.assetService.getFigureImageTitle(color, type);
        this.assetService.get(`/figures/${title}.svg`).then(svg => document.getElementById(`board.figure.${title}`)!!.appendChild(svg));
      }
    }
    this.initBoard();
    this.updateFields();
    this.updateFigures();
  }

  onClickBoard(ev: MouseEvent) {
    if (ev.offsetX < 45 || ev.offsetY < 45 || ev.offsetX >= 45 * 9 || ev.offsetY >= 45 * 9) {
      this.store.dispatch(uiActions.selectField({field: undefined}));
    } else {
      if (this.board == undefined) {
        return;
      }
      let x = Math.floor((ev.offsetX - 45) / 45);
      let y = 7 - Math.floor((ev.offsetY - 45) / 45);
      if (this.flipped) {
        x = 7 - x;
        y = 7 - y;
      }
      const field = x | (y << 3);
      const figure = this.board.getFigure(field);
      if (figure == undefined) {
        if (this.selectedField != undefined && this.board.getFigure(this.selectedField) != undefined) {
          this.store.dispatch(boardActions.moveFigure({start: this.selectedField, end: field}));
        } else if (this.selectedFigureColor != undefined && this.selectedFigureType != undefined) {
          this.store.dispatch(boardActions.insertFigure({
            field,
            figureType: this.selectedFigureType,
            color: this.selectedFigureColor
          }));
        }
      } else {
        this.store.dispatch(uiActions.selectField({field}));
      }
    }
  }

  private initBoard() {
    if (this.svg != undefined) {
      const svg = this.svg.nativeElement;
      svg.width.baseVal.valueAsString = `${45 * 10}px`;
      svg.height.baseVal.valueAsString = `${45 * 10}px`;
      this.initLabels(svg);
      this.initFields(svg);
      this.selectionParent = this.appendGroup(svg);
      this.figuresParent = this.appendGroup(svg);
    }
  }

  private initFields(svg: SVGSVGElement) {
    const whiteFields = this.appendGroup(svg);
    whiteFields.style.fill = '#e0e0e0';
    whiteFields.style.stroke = '#ffffff';
    whiteFields.style.strokeWidth = '1px';
    const blackFields = this.appendGroup(svg);
    blackFields.style.fill = '#a0a0a0';
    for (let y = 0; y < 8; ++y) {
      for (let x = 0; x < 8; ++x) {
        if (((x + y) & 1) === 1) {
          this.appendRect(x, y, whiteFields);
        } else {
          this.appendRect(x, y, blackFields);
        }
      }
    }
  }

  private updateFields() {
    if (this.flipped) {
      this.labelsParent?.setAttribute('transform', `rotate(180 ${45 * 5} ${45 * 5})`);
    } else {
      this.labelsParent?.removeAttribute('transform');
    }
  }

  private updateFigures() {
    if (this.figuresParent != undefined && this.board != undefined) {
      this.figuresParent.innerHTML = '';
      this.board.getFigures(figure => {
        this.appendFigure(figure.color, figure.type, figure.field);
      });
    }
  }

  private updateSelection() {
    if (this.selectionParent != undefined) {
      this.selectionParent.innerHTML = '';
      if (this.selectedField != undefined) {
        const x = this.selectedField & 7;
        const y = this.selectedField >> 3;
        const use = document.createElementNS('http://www.w3.org/2000/svg', 'use');
        use.x.baseVal.value = this.flipped ? (8 - x) * 45 : (x + 1) * 45;
        use.y.baseVal.value = this.flipped ? (y + 1) * 45 : (8 - y) * 45;
        use.href.baseVal = `#board.field.selection`;
        this.selectionParent.appendChild(use);
      }
    }
  }

  private appendFigure(color: FigureColor, type: FigureType, field: number) {
    if (this.figuresParent != undefined) {
      const x = field & 7;
      const y = field >> 3;
      const figure = this.assetService.getFigureImageTitle(color, type);
      const use = document.createElementNS('http://www.w3.org/2000/svg', 'use');
      use.x.baseVal.value = this.flipped ? (8 - x) * 45 : (x + 1) * 45;
      use.y.baseVal.value = this.flipped ? (y + 1) * 45 : (8 - y) * 45;
      use.href.baseVal = `#board.figure.${figure}`;
      this.figuresParent.appendChild(use);
    }
  }

  private initLabels(svg: SVGSVGElement) {
    this.labelsParent = this.appendGroup(svg);
    this.labelsParent.style.fill = '#e0e0e0';
    this.labelsParent.style.stroke = '#808080';
    this.labelsParent.style.strokeWidth = '1px';
    this.labelsParent.style.fontSize = '30px';
    for (let i = 0; i < 8; ++i) {
      this.appendText(`${String.fromCharCode('A'.charCodeAt(0) + i)}`, i, -1, false, this.labelsParent);
      this.appendText(`${String.fromCharCode('A'.charCodeAt(0) + i)}`, i, 8, true, this.labelsParent);
      this.appendText(`${i + 1}`, -1, i, true, this.labelsParent);
      this.appendText(`${i + 1}`, 8, i, false, this.labelsParent);
    }
  }

  private appendGroup(parent: SVGElement): SVGGElement {
    const ret = document.createElementNS('http://www.w3.org/2000/svg', 'g');
    parent.appendChild(ret);
    return ret;
  }

  private appendRect(x: number, y: number, parent: SVGElement): SVGRectElement {
    const rt = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
    rt.x.baseVal.value = (x + 1) * 45;
    rt.y.baseVal.value = (8 - y) * 45;
    rt.width.baseVal.value = 45;
    rt.height.baseVal.value = 45;
    parent.appendChild(rt);
    return rt;
  }

  private appendText(text: string, x: number, y: number, rotated: boolean, parent: SVGElement) {
    const t = document.createElementNS('http://www.w3.org/2000/svg', 'text');
    const px = (x + 1) * 45 + 12;
    const py = (9 - y) * 45 - 12;
    t.setAttribute('x', `${px}px`);
    t.setAttribute('y', `${py}px`);
    if (rotated) {
      t.setAttribute('transform', `rotate(180 ${px + 11} ${py - 10})`);
    }
    t.textContent = text;
    parent.appendChild(t);
  }
}
