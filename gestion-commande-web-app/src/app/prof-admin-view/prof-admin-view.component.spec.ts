import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfAdminViewComponent } from './prof-admin-view.component';

describe('ProfAdminViewComponent', () => {
  let component: ProfAdminViewComponent;
  let fixture: ComponentFixture<ProfAdminViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProfAdminViewComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ProfAdminViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
