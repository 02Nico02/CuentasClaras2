import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CuloComponent } from './culo.component';

describe('CuloComponent', () => {
  let component: CuloComponent;
  let fixture: ComponentFixture<CuloComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CuloComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CuloComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
