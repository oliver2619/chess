import {createFeatureSelector, createSelector} from '@ngrx/store';
import {DialogState} from '../state/dialog-state';

const dialogSelector = createFeatureSelector<DialogState>('dialog');

export const dialogComponentSelector = createSelector(
  dialogSelector,
  dialog => dialog.component
)
