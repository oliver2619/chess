import {AiConfigurationState, PlayerState} from '../state/player-state';
import {AiConfigurationJson, PlayerJson} from '../json/player-json';

class AiConfigurationModel {

  private constructor(private algorithm: string, private level: number, private openingBook: boolean, private permanentThink: boolean) {
  }

  static fromJson(json: AiConfigurationJson): AiConfigurationModel {
    return new AiConfigurationModel(json.algorithm, json.level, json.openingBook, json.permanentThink);
  }

  static fromState(state: AiConfigurationState): AiConfigurationModel {
    return new AiConfigurationModel(state.algorithm, state.level, state.openingBook, state.permanentThink);
  }

  toJson(): AiConfigurationJson {
    return {
      algorithm: this.algorithm,
      level: this.level,
      openingBook: this.openingBook,
      permanentThink: this.permanentThink,
    };
  }

  toState(): AiConfigurationState {
    return {
      algorithm: this.algorithm,
      level: this.level,
      openingBook: this.openingBook,
      permanentThink: this.permanentThink
    }
  }
}

export class PlayerModel {

  private constructor(private readonly name: string, private ai: AiConfigurationModel | undefined) {
  }

  static newInstance(name: string): PlayerModel {
    return new PlayerModel(name, undefined);
  }

  static fromJson(json: PlayerJson): PlayerModel {
    return new PlayerModel(json.name, json.ai == undefined ? undefined : AiConfigurationModel.fromJson(json.ai));
  }

  static fromState(state: PlayerState): PlayerModel {
    return new PlayerModel(state.name, state.ai == undefined ? undefined : AiConfigurationModel.fromState(state.ai));
  }

  toJson(): PlayerJson {
    return {
      name: this.name,
      ai: this.ai?.toJson()
    }
  }

  toState(): PlayerState {
    return {
      name: this.name,
      ai: this.ai?.toState()
    };
  }
}
