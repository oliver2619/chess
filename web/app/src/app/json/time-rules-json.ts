export interface TimeRulesJson {
  readonly totalSeconds: number
  readonly bonusSecondsPerMove: number;
  readonly movesUntilTimeResets?: number;
}
