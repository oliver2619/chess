import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExportPgnComponent } from './export-pgn.component';

describe('ExportPgnComponent', () => {
  let component: ExportPgnComponent;
  let fixture: ComponentFixture<ExportPgnComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExportPgnComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExportPgnComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
