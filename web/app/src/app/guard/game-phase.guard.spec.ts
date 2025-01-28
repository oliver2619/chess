import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { gamePhaseGuard } from './game-phase.guard';

describe('gamePhaseGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => gamePhaseGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
