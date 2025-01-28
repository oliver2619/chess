import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResumeGameFromPgnComponent } from './resume-game-from-pgn.component';

describe('ResumeGameFromPgnComponent', () => {
  let component: ResumeGameFromPgnComponent;
  let fixture: ComponentFixture<ResumeGameFromPgnComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResumeGameFromPgnComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ResumeGameFromPgnComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
