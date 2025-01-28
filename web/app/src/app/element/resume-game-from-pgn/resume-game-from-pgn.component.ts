import {AfterViewInit, ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {Store} from '@ngrx/store';
import {dialogActions} from '../../action/dialog-actions';
import {PgnParser, PgnParseResult} from '../../model/pgn-parser';
import {gameActions} from '../../action/game-actions';
import {Router} from '@angular/router';
import {FileService} from '../../service/file.service';

@Component({
  selector: 'c-resume-game-from-pgn',
  imports: [],
  templateUrl: './resume-game-from-pgn.component.html',
  styleUrl: './resume-game-from-pgn.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ResumeGameFromPgnComponent implements AfterViewInit {

  readonly games = signal<PgnParseResult[]>([]);

  constructor(private readonly store: Store, private readonly router: Router, private readonly fileUploadService: FileService) {
  }

  ngAfterViewInit() {
    this.upload();
  }

  cancel() {
    this.store.dispatch(dialogActions.close());
  }

  resume(i: number) {
    const game = this.games()[i];
    this.store.dispatch(gameActions.resumeFromPgn({
      white: game.white,
      black: game.black,
      board: game.board.toState(),
      history: game.history.toState()
    }));
    this.store.dispatch(dialogActions.close());
    this.router.navigateByUrl('/game');
  }

  upload() {
    this.fileUploadService.upload('text/plain').then(pgn => this.parsePgn(pgn));
  }

  private parsePgn(pgn: string) {
    const result = new PgnParser().parse(pgn);
    this.games.set(result);
  }
}
