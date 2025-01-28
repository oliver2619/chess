import {ExportFenComponent} from './export-fen/export-fen.component';
import {ImportFenComponent} from './import-fen/import-fen.component';
import {SaveGameComponent} from './save-game/save-game.component';
import {ResumeGameFromPgnComponent} from './resume-game-from-pgn/resume-game-from-pgn.component';
import {
  ResumeGameFromCurrentBoardComponent
} from './resume-game-from-current-board/resume-game-from-current-board.component';
import {ExportPgnComponent} from './export-pgn/export-pgn.component';

export class DialogComponents {
  readonly exportFen = ExportFenComponent;
  readonly exportPgn = ExportPgnComponent;
  readonly importFen = ImportFenComponent;
  readonly saveGame = SaveGameComponent;
  readonly resumeGameFromPgn = ResumeGameFromPgnComponent;
  readonly resumeGameFromCurrentBoard = ResumeGameFromCurrentBoardComponent;
}

export const dialogComponents = new DialogComponents();

