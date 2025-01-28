import {ConvertedFigureType, MoveState} from '../state/move-state';
import {MoveJson} from '../json/move-json';
import {FigureType} from '../state/figure-type';
import {FigureColor} from '../state/figure-color';

export class MoveModel {

  constructor(
    readonly color: FigureColor,
    readonly figure: FigureType,
    readonly start: number,
    readonly end: number,
    readonly conversion?: ConvertedFigureType
  ) {
  }

  static fromJson(json: MoveJson, color: FigureColor, figure: FigureType): MoveModel {
    return new MoveModel(
      color,
      figure,
      json.start,
      json.end,
      json.conversion == undefined ? undefined : (FigureType as any)[json.conversion]
    );
  }

  static fromState(state: MoveState): MoveModel {
    return new MoveModel(
      state.color,
      state.figure,
      state.start,
      state.end,
      state.conversion
    );
  }

  toJson(): MoveJson {
    return {
      start: this.start,
      end: this.end,
      conversion: this.conversion == undefined ? undefined : (FigureType as any)[this.conversion],
    }
  }

  toState(): MoveState {
    return {
      color: this.color,
      figure: this.figure,
      start: this.start,
      end: this.end,
      conversion: this.conversion
    };
  }
}
