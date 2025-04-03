import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddWordComponent } from './add-word.component';

describe('AddWordComponent', () => {
  let component: AddWordComponent;
  let fixture: ComponentFixture<AddWordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddWordComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddWordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
