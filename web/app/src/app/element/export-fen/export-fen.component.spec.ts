import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExportFenComponent } from './export-fen.component';

describe('ExportFenComponent', () => {
  let component: ExportFenComponent;
  let fixture: ComponentFixture<ExportFenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExportFenComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExportFenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
