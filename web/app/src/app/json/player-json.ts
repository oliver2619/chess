export interface AiConfigurationJson {
  readonly level: number;
  readonly algorithm: string;
  readonly openingBook: boolean;
  readonly permanentThink: boolean;
}

export interface PlayerJson {
  readonly name: string;
  readonly ai?: AiConfigurationJson;
}
