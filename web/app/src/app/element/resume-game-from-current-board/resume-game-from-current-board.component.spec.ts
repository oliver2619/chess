import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResumeGameFromCurrentBoardComponent } from './resume-game-from-current-board.component';

describe('ResumeGameFromCurrentBoardComponent', () => {
  let component: ResumeGameFromCurrentBoardComponent;
  let fixture: ComponentFixture<ResumeGameFromCurrentBoardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResumeGameFromCurrentBoardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ResumeGameFromCurrentBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
