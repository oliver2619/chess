import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OpeningsMenuComponent } from './openings-menu.component';

describe('OpeningsMenuComponent', () => {
  let component: OpeningsMenuComponent;
  let fixture: ComponentFixture<OpeningsMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpeningsMenuComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OpeningsMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
