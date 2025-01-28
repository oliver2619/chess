import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidatorFn,
  Validators
} from '@angular/forms';
import {Store} from '@ngrx/store';
import {dialogActions} from '../../action/dialog-actions';
import {BoardModel} from '../../model/board-model';
import {boardActions} from '../../action/board-actions';
import {FileService} from '../../service/file.service';
import {FenParser} from '../../model/fen';

interface ImportFenFormValue {
  fen: string;
}

const fenValidator: ValidatorFn = (control: AbstractControl) => {
  try {
    new FenParser().parse(String(control.value));
    return null;
  } catch (e) {
    return ({fen: e instanceof Error ? e.message : String(e)});
  }
}

@Component({
  selector: 'c-import-fen',
  imports: [
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './import-fen.component.html',
  styleUrl: './import-fen.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImportFenComponent {

  readonly formGroup: FormGroup;

  get canOk(): boolean {
    return this.formGroup.valid;
  }

  private get formValue(): ImportFenFormValue {
    return this.formGroup.value;
  }

  constructor(private readonly store: Store, private readonly fileService: FileService, formBuilder: FormBuilder) {
    this.formGroup = formBuilder.group({});
    this.formGroup.addControl('fen', formBuilder.control('', [Validators.required, fenValidator]));
  }

  ok() {
    const board = new FenParser().parse(this.formValue.fen).board.toState();
    this.store.dispatch(boardActions.load(board));
    this.store.dispatch(dialogActions.close());
  }

  cancel() {
    this.store.dispatch(dialogActions.close());
  }

  upload() {
    this.fileService.upload('text/plain').then(it => this.updateFen(it));
  }

  private updateFen(fen: string) {
    const v = this.formValue;
    v.fen = fen;
    this.formGroup.setValue(v);
  }
}
