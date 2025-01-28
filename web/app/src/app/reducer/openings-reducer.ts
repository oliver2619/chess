import {createReducer, on} from '@ngrx/store';
import {OpeningsState} from '../state/openings-state';
import {openingsActions} from '../action/openings-actions';

const initialOpeningsState: OpeningsState = {
  moves: []
};

function onOpeningsCreate(_: OpeningsState): OpeningsState {
  return initialOpeningsState;
}

export const openingsReducer = createReducer(
  initialOpeningsState,
  on(openingsActions.create, onOpeningsCreate)
)
