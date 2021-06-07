import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllRemarksDialogComponent } from './all-remarks-dialog.component';

describe('AllRemarksDialogComponent', () => {
  let component: AllRemarksDialogComponent;
  let fixture: ComponentFixture<AllRemarksDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AllRemarksDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AllRemarksDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
