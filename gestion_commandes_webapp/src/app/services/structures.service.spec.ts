import { TestBed } from '@angular/core/testing';

import { StructuresService } from './structures.service';

describe('ServicesService', () => {
  let service: StructuresService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StructuresService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
