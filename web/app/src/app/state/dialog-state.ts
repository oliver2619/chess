import {DialogComponents} from '../element/dialog-components';

export interface DialogState {
  component: keyof DialogComponents | undefined;
}

