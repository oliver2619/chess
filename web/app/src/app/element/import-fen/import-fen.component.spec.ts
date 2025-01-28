import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportFenComponent } from './import-fen.component';

describe('ImportFenComponent', () => {
  let component: ImportFenComponent;
  let fixture: ComponentFixture<ImportFenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImportFenComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ImportFenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
