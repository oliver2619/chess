import {ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import {provideRouter, withHashLocation} from '@angular/router';

import {routes} from './app.routes';
import {provideState, provideStore} from '@ngrx/store';
import {provideEffects} from '@ngrx/effects';
import {gameReducer} from './reducer/game-reducer';
import {dialogReducer} from './reducer/dialog-reducer';
import {openingsReducer} from './reducer/openings-reducer';
import {uiReducer} from './reducer/ui-reducer';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes, withHashLocation()),
    provideStore(),
    provideState('game', gameReducer),
    provideState('ui', uiReducer),
    provideState('dialog', dialogReducer),
    provideState('openings', openingsReducer),
    provideEffects()
  ]
};
