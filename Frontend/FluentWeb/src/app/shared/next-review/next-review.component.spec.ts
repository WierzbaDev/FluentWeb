import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NextReviewComponent } from './next-review.component';

describe('NextReviewComponent', () => {
  let component: NextReviewComponent;
  let fixture: ComponentFixture<NextReviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NextReviewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NextReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
