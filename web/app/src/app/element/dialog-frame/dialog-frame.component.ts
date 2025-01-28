import { ChangeDetectionStrategy, Component } from '@angular/core';
import {Store} from '@ngrx/store';
import {dialogActions} from '../../action/dialog-actions';

@Component({
  selector: 'c-dialog-frame',
  imports: [],
  templateUrl: './dialog-frame.component.html',
  styleUrl: './dialog-frame.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DialogFrameComponent {

  constructor(private readonly store: Store) {  }

  close() {
    this.store.dispatch(dialogActions.close());
  }
}
