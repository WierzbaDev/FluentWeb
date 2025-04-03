import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminSettingComponent } from './admin-setting.component';

describe('AdminDashboardComponent', () => {
  let component: AdminSettingComponent;
  let fixture: ComponentFixture<AdminSettingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminSettingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminSettingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
