import { ChangeDetectionStrategy, Component } from '@angular/core';
import {Store} from '@ngrx/store';
import {openingsActions} from '../../action/openings-actions';
import {Router} from '@angular/router';

@Component({
  selector: 'c-openings-menu',
  imports: [],
  templateUrl: './openings-menu.component.html',
  styleUrl: './openings-menu.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OpeningsMenuComponent {

  constructor(private readonly store: Store, private readonly router: Router) {
  }

  onNew() {
    this.store.dispatch(openingsActions.create());
    this.router.navigateByUrl('/openings');
  }

  onOpen() {

  }

  onImport() {

  }

  onRate() {

  }
}
