import {TestBed} from '@angular/core/testing';

import {AdminListUsersService} from './admin-list-users.service';

describe('AdminListUsersService', () => {
  let service: AdminListUsersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminListUsersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
