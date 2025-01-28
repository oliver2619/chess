import { ChangeDetectionStrategy, Component } from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {Store} from '@ngrx/store';
import {FileService} from '../../service/file.service';
import {dialogActions} from '../../action/dialog-actions';
import {gameSelector} from '../../selector/game-selector';
import {GameModel} from '../../model/game-model';

const defaultFilename = 'game.json';

interface SaveGameFormValue {
  filename: string;
}

@Component({
  selector: 'c-save-game',
  imports: [FormsModule, ReactiveFormsModule],
  templateUrl: './save-game.component.html',
  styleUrl: './save-game.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SaveGameComponent {

  readonly formGroup: FormGroup;

  private json = '';

  private get value(): SaveGameFormValue {
    return this.formGroup.value;
  }

  constructor(private readonly store: Store, private readonly fileService: FileService, formBuilder: FormBuilder) {
    this.formGroup = formBuilder.group({});
    this.formGroup.addControl('filename', formBuilder.control(defaultFilename, Validators.required));
    this.store.select(gameSelector).subscribe({
      next: game => this.json = JSON.stringify(GameModel.fromState(game).toJson())
    });
  }

  cancel() {
    this.store.dispatch(dialogActions.close());
  }

  download() {
    const filename = this.value.filename;
    this.fileService.download({
      data: this.json,
      mimeType: 'application/json',
      filename: filename === '' ? defaultFilename : filename
    });
    this.store.dispatch(dialogActions.close());
  }

}
