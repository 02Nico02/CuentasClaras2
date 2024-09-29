import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GrupoParejaComponent } from './grupo-pareja.component';

describe('GrupoParejaComponent', () => {
  let component: GrupoParejaComponent;
  let fixture: ComponentFixture<GrupoParejaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GrupoParejaComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GrupoParejaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
