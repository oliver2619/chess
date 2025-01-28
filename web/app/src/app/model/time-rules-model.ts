import {TimeRulesJson} from '../json/time-rules-json';
import {TimeRulesState} from '../state/time-rules-state';

export class TimeRulesModel {

  get totalSeconds(): number {
    return this._totalSeconds;
  }

  private constructor(
    private _totalSeconds: number,
    private movesUntilTimeResets: number | undefined,
    private bonusSecondsPerMove: number
  ) {
  }

  static newInstance(): TimeRulesModel {
    return new TimeRulesModel(5 * 60, undefined, 0);
  }

  static fromJson(json: TimeRulesJson): TimeRulesModel {
    return new TimeRulesModel(json.totalSeconds, json.movesUntilTimeResets, json.bonusSecondsPerMove);
  }

  static fromState(state: TimeRulesState): TimeRulesModel {
    return new TimeRulesModel(state.totalSeconds, state.movesUntilTimeResets, state.bonusSecondsPerMove);
  }

  toJson(): TimeRulesJson {
    return {
      bonusSecondsPerMove: this.bonusSecondsPerMove,
      movesUntilTimeResets: this.movesUntilTimeResets,
      totalSeconds: this._totalSeconds
    }
  }

  toState(): TimeRulesState {
    return {
      bonusSecondsPerMove: this.bonusSecondsPerMove,
      movesUntilTimeResets: this.movesUntilTimeResets,
      totalSeconds: this._totalSeconds
    };
  }
}
