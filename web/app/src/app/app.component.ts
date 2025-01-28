import {Component, signal} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {MenuBarComponent} from './element/menu-bar/menu-bar.component';
import {Store} from '@ngrx/store';
import {dialogComponentSelector} from './selector/dialog-selector';
import {DialogFrameComponent} from './element/dialog-frame/dialog-frame.component';
import {dialogComponents} from './element/dialog-components';
import {NgComponentOutlet} from '@angular/common';

@Component({
  selector: 'c-root',
  imports: [RouterOutlet, MenuBarComponent, DialogFrameComponent, NgComponentOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {

  readonly dialogVisible = signal(false);
  readonly dialogComponent = signal<any>(undefined);

  constructor(store: Store) {
    store.select(dialogComponentSelector).subscribe({
      next: it => {
        this.dialogVisible.set(it != undefined);
        this.dialogComponent.set(it == undefined ? undefined : dialogComponents[it]);
      }
    });
  }
}
