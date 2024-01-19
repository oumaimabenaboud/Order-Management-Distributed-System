import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StructureAdminViewComponent } from './structure-admin-view.component';

describe('StructureAdminViewComponent', () => {
  let component: StructureAdminViewComponent;
  let fixture: ComponentFixture<StructureAdminViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [StructureAdminViewComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(StructureAdminViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
