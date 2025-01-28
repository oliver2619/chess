import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {Store} from '@ngrx/store';
import {gamePhaseSelector} from '../selector/game-selector';
import {map} from 'rxjs';
import {GamePhase} from '../state/game-state';
import {RouteData} from '../app.routes';

export const gamePhaseGuard: CanActivateFn = (route, _) => {
  const store = inject(Store);
  const router = inject(Router);
  return store.select(gamePhaseSelector).pipe(
    map(phase => {
      const data = route.data as RouteData;
      if(data.phases.indexOf(phase) >= 0) {
        return true;
      }
      switch(phase) {
        case GamePhase.OPENINGS:
          return router.createUrlTree(['openings']);
        case GamePhase.PLAYING:
        case GamePhase.INITIAL:
        case GamePhase.TERMINATED:
          return router.createUrlTree(['game']);
        case GamePhase.EDITED:
          return router.createUrlTree(['edit']);
      }
    })
  );
};
