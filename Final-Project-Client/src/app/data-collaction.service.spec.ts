import { TestBed } from '@angular/core/testing';

import { DataCollactionService } from './data-collaction.service';

describe('DataCollactionService', () => {
  let service: DataCollactionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DataCollactionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
