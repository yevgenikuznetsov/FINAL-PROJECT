import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowAllRemarksComponent } from './show-all-remarks.component';

describe('ShowAllRemarksComponent', () => {
  let component: ShowAllRemarksComponent;
  let fixture: ComponentFixture<ShowAllRemarksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShowAllRemarksComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowAllRemarksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
