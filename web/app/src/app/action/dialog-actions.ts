import {createActionGroup, emptyProps} from '@ngrx/store';

export const dialogActions = createActionGroup({
  source: 'Dialog',
  events: {
    exportFen: emptyProps(),
    exportPgn: emptyProps(),
    close: emptyProps(),
    importFen: emptyProps(),
    loadGame: emptyProps(),
    resumeGameFromCurrentBoard: emptyProps(),
    resumeGameFromPgn: emptyProps(),
    saveGame: emptyProps(),
  }
});
