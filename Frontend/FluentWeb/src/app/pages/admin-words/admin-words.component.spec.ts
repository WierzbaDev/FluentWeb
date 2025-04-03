import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminWordsComponent } from './admin-words.component';

describe('AdminWordsComponent', () => {
  let component: AdminWordsComponent;
  let fixture: ComponentFixture<AdminWordsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminWordsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminWordsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
