import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PodiumComponent } from './podium.component';

describe('PodiumComponent', () => {
  let component: PodiumComponent;
  let fixture: ComponentFixture<PodiumComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PodiumComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PodiumComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
