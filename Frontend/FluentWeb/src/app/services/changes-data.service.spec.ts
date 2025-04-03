import {TestBed} from '@angular/core/testing';

import {ChangesDataService} from './changes-data.service';

describe('ChangesDataService', () => {
  let service: ChangesDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChangesDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
