import { ChangeDetectionStrategy, Component } from '@angular/core';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'c-help-menu',
  imports: [
    RouterLink
  ],
  templateUrl: './help-menu.component.html',
  styleUrl: './help-menu.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HelpMenuComponent {

}
