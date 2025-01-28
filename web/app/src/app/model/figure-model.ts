import {FigureColor} from '../state/figure-color';
import {FigureType} from '../state/figure-type';

export class FigureModel {

  constructor(readonly color: FigureColor, readonly type: FigureType, readonly field: number) {
  }
}
