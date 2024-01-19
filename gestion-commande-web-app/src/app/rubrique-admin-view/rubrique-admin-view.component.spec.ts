import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RubriqueAdminViewComponent } from './rubrique-admin-view.component';

describe('RubriqueAdminViewComponent', () => {
  let component: RubriqueAdminViewComponent;
  let fixture: ComponentFixture<RubriqueAdminViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RubriqueAdminViewComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RubriqueAdminViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
