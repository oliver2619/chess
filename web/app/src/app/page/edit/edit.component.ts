import { ChangeDetectionStrategy, Component } from '@angular/core';
import {BoardComponent} from "../../element/board/board.component";
import {ClocksComponent} from "../../element/clocks/clocks.component";
import {HistoryComponent} from "../../element/history/history.component";
import {PiecesComponent} from "../../element/pieces/pieces.component";

@Component({
  selector: 'c-edit',
    imports: [
        BoardComponent,
        PiecesComponent
    ],
  templateUrl: './edit.component.html',
  styleUrl: './edit.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditComponent {

}
