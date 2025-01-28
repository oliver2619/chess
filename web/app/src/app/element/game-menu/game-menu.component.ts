import {ChangeDetectionStrategy, Component, ElementRef, signal, ViewChild} from '@angular/core';
import {Store} from '@ngrx/store';
import {gameActions} from '../../action/game-actions';
import {Router} from '@angular/router';
import {gameSelector} from '../../selector/game-selector';
import {GamePhase} from '../../state/game-state';
import {dialogActions} from '../../action/dialog-actions';
import {GameJson} from '../../json/game-json';
import {GameModel} from '../../model/game-model';
import {FileService} from '../../service/file.service';

@Component({
  selector: 'c-game-menu',
  imports: [],
  templateUrl: './game-menu.component.html',
  styleUrl: './game-menu.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameMenuComponent {

  readonly canStartNew = signal(false);
  readonly canResume = signal(false);
  readonly canSave = signal(false);

  constructor(private readonly store: Store, private readonly router: Router, private readonly fileUploadService: FileService) {
    store.select(gameSelector).subscribe({
      next: game => {
        this.canStartNew.set(game.phase === GamePhase.TERMINATED || game.phase === GamePhase.EDITED);
        this.canResume.set((game.phase === GamePhase.TERMINATED || game.phase === GamePhase.EDITED) && game.boardCanBeResumed);
        this.canSave.set(game.phase === GamePhase.PLAYING || game.phase === GamePhase.INITIAL);
      }
    });
  }

  restart() {
    this.store.dispatch(gameActions.restart());
    this.router.navigateByUrl('/game');
  }

  load() {
    this.fileUploadService.upload('application/json').then(json => this.onLoadGame(JSON.parse(json)));
  }

  importPgn() {
    this.store.dispatch(dialogActions.resumeGameFromPgn());
  }

  resume() {
    this.store.dispatch(dialogActions.resumeGameFromCurrentBoard());
  }

  save() {
    this.store.dispatch(dialogActions.saveGame());
  }

  private onLoadGame(json: GameJson) {
    this.store.dispatch(gameActions.load(GameModel.fromJson(json).toState()));
    this.router.navigateByUrl('/game');
  }
}
