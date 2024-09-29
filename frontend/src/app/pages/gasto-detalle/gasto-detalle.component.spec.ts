import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GastoDetalleComponent } from './gasto-detalle.component';

describe('GastoDetalleComponent', () => {
  let component: GastoDetalleComponent;
  let fixture: ComponentFixture<GastoDetalleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GastoDetalleComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GastoDetalleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
