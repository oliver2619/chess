export interface TimeRulesState {
  readonly totalSeconds: number
  readonly bonusSecondsPerMove: number;
  readonly movesUntilTimeResets?: number;
}
