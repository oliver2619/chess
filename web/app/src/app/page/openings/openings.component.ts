import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BoardComponent} from "../../element/board/board.component";
import {HistoryComponent} from "../../element/history/history.component";
import {MovesComponent} from "../../element/moves/moves.component";

@Component({
  selector: 'c-openings',
  imports: [
    BoardComponent,
    HistoryComponent,
    MovesComponent,
  ],
  templateUrl: './openings.component.html',
  styleUrl: './openings.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OpeningsComponent {

}
