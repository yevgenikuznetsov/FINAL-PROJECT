import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InformListComponent } from './inform-list.component';

describe('InformListComponent', () => {
  let component: InformListComponent;
  let fixture: ComponentFixture<InformListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InformListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InformListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
