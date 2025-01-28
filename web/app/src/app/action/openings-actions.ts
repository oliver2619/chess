import {createActionGroup, emptyProps} from '@ngrx/store';

export const openingsActions = createActionGroup({
  source: 'Openings',
  events: {
    create: emptyProps(),
    load: emptyProps(),
    importPgn: emptyProps(),
    rate: emptyProps(),
  },
});
