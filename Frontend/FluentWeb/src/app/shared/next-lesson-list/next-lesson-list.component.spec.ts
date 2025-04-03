import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NextLessonListComponent } from './next-lesson-list.component';

describe('NextLessonListComponent', () => {
  let component: NextLessonListComponent;
  let fixture: ComponentFixture<NextLessonListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NextLessonListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NextLessonListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
