import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RankingListComponent } from './ranking-list.component';

describe('RankingListComponent', () => {
  let component: RankingListComponent;
  let fixture: ComponentFixture<RankingListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RankingListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RankingListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
