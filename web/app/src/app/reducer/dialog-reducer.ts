import {createReducer, on} from '@ngrx/store';
import {DialogState} from '../state/dialog-state';
import {dialogActions} from '../action/dialog-actions';
import {DialogComponents} from '../element/dialog-components';

const initialDialogState: DialogState = {component: undefined};

function show(component: keyof DialogComponents): (state: DialogState) => DialogState {
  return _ => {
    return {component};
  };
}

export const dialogReducer = createReducer(
  initialDialogState,
  on(dialogActions.close, _ => initialDialogState),
  on(dialogActions.exportFen, show('exportFen')),
  on(dialogActions.exportPgn, show('exportPgn')),
  on(dialogActions.importFen, show('importFen')),
  on(dialogActions.resumeGameFromCurrentBoard, show('resumeGameFromCurrentBoard')),
  on(dialogActions.resumeGameFromPgn, show('resumeGameFromPgn')),
  on(dialogActions.saveGame, show('saveGame')),
);
