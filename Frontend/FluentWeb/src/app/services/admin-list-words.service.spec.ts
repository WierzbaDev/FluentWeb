import {TestBed} from '@angular/core/testing';

import {AdminListWordsService} from './admin-list-words.service';

describe('AdminListWordsService', () => {
  let service: AdminListWordsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminListWordsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
