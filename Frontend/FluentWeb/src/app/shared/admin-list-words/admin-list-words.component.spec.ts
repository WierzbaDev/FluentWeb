import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminListWordsComponent } from './admin-list-words.component';

describe('AdminListWordsComponent', () => {
  let component: AdminListWordsComponent;
  let fixture: ComponentFixture<AdminListWordsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminListWordsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminListWordsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
