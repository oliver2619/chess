import {ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {Store} from '@ngrx/store';
import {dialogActions} from '../../action/dialog-actions';
import {gameBoardSelector} from '../../selector/game-selector';
import {BoardModel} from '../../model/board-model';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {FileService} from '../../service/file.service';
import {FenStringifier} from '../../model/fen';

const defaultFilename = 'board.fen.txt';

interface ExportFenFormValue {
  filename: string;
  nextMoveIndex: number;
}

@Component({
  selector: 'c-export-fen',
  imports: [FormsModule, ReactiveFormsModule],
  templateUrl: './export-fen.component.html',
  styleUrl: './export-fen.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExportFenComponent {

  readonly fen = signal('');
  readonly formGroup: FormGroup;

  private board: BoardModel = BoardModel.empty();

  private get value(): ExportFenFormValue {
    return this.formGroup.value;
  }

  constructor(private readonly store: Store, private readonly fileService: FileService, formBuilder: FormBuilder) {
    this.formGroup = formBuilder.group({});
    this.formGroup.addControl('filename', formBuilder.control(defaultFilename, Validators.required));
    const nextMoveIndex = formBuilder.control(1, [Validators.required, Validators.min(1)]);
    this.formGroup.addControl('nextMoveIndex', nextMoveIndex);
    this.store.select(gameBoardSelector).subscribe({
      next: board => {
        this.board = BoardModel.fromState(board);
        this.updateFen(this.value.nextMoveIndex);
      }
    });
    nextMoveIndex.valueChanges.subscribe({
      next: i => {
        if(i != null && nextMoveIndex.valid) {
          this.updateFen(i);
        }
      }
    });
  }

  close() {
    this.store.dispatch(dialogActions.close());
  }

  copy() {
    navigator.clipboard.writeText(this.fen());
  }

  download() {
    const filename = this.value.filename;
    this.fileService.download({
      data: this.fen(),
      mimeType: 'text/plain',
      filename: filename === '' ? defaultFilename : filename
    });
  }

  private updateFen(nextMoveIndex: number) {
    this.fen.set(FenStringifier.stringify(this.board, nextMoveIndex));
  }
}
