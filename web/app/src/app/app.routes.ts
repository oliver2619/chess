import {Routes} from '@angular/router';
import {GameComponent} from './page/game/game.component';
import {OpeningsComponent} from './page/openings/openings.component';
import {EditComponent} from './page/edit/edit.component';
import {GamePhase} from './state/game-state';
import {gamePhaseGuard} from './guard/game-phase.guard';
import {SettingsComponent} from './page/settings/settings.component';

export interface RouteData {
  readonly phases: readonly GamePhase[];
}

export const routes: Routes = [{
  path: '',
  pathMatch: 'full',
  redirectTo: 'edit',
}, {
  path: 'edit',
  component: EditComponent,
  canActivate: [gamePhaseGuard],
  data: {phases: [GamePhase.EDITED]},
}, {
  path: 'game',
  component: GameComponent,
  canActivate: [gamePhaseGuard],
  data: {phases: [GamePhase.INITIAL, GamePhase.PLAYING, GamePhase.TERMINATED]},
}, {
  path: 'openings',
  component: OpeningsComponent,
  canActivate: [gamePhaseGuard],
  data: {phases: [GamePhase.OPENINGS]},
}, {
  path: 'settings',
  component: SettingsComponent,
}, {
  path: '**',
  redirectTo: 'edit',
}];
