export interface AiConfigurationState {
  readonly level: number;
  readonly algorithm: string;
  readonly openingBook: boolean;
  readonly permanentThink: boolean;
}

export interface PlayerState {
  readonly name: string;
  readonly ai?: AiConfigurationState;
}
