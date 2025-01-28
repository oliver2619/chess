import {ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Store} from '@ngrx/store';
import {FileService} from '../../service/file.service';
import {dialogActions} from '../../action/dialog-actions';
import {gameHistorySelector} from '../../selector/game-selector';
import {HistoryState} from '../../state/history-state';
import {PgnStringifier} from '../../model/pgn-stringifier';
import {HistoryModel} from '../../model/history-model';

const defaultFilename = 'game.pgn.txt';

interface ExportPgnFormValue {
  filename: string;
}

@Component({
  selector: 'c-export-pgn',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './export-pgn.component.html',
  styleUrl: './export-pgn.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExportPgnComponent {

  readonly pgn = signal('');
  readonly formGroup: FormGroup;

  private get value(): ExportPgnFormValue {
    return this.formGroup.value;
  }

  constructor(private readonly store: Store, private readonly fileService: FileService, formBuilder: FormBuilder) {
    this.formGroup = formBuilder.group({});
    this.formGroup.addControl('filename', formBuilder.control(defaultFilename, Validators.required));
    store.select(gameHistorySelector).subscribe({
      next: history => this.updatePgn(history)
    });
  }

  close() {
    this.store.dispatch(dialogActions.close());
  }

  copy() {
    navigator.clipboard.writeText(this.pgn());
  }

  download() {
    const filename = this.value.filename;
    this.fileService.download({
      data: this.pgn(),
      mimeType: 'text/plain',
      filename: filename === '' ? defaultFilename : filename
    });
  }

  private updatePgn(history: HistoryState){
    const h = HistoryModel.fromState(history);
    this.pgn.set(new PgnStringifier().stringify(h));
  }
}
