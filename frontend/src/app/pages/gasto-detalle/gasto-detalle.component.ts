import { Component, OnInit } from '@angular/core';
import { GastoDTO } from '../../services/gasto/gasto.dto';
import { Title } from '@angular/platform-browser';
import { GastoService } from '../../services/gasto/gasto.service';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavComponent } from '../../shared/nav/nav.component';

@Component({
  selector: 'app-gasto-detalle',
  standalone: true,
  imports: [CommonModule, NavComponent],
  templateUrl: './gasto-detalle.component.html',
  styleUrl: './gasto-detalle.component.css'
})
export class GastoDetalleComponent implements OnInit {

  gasto?: GastoDTO;
  gastoId: string = "1";

  constructor(private route: ActivatedRoute, private titleService: Title, private gastoService: GastoService) { }

  ngOnInit(): void {
    this.llamarAPI();
    this.titleService.setTitle('Cuentas Claras - Detalle gasto');
  }

  llamarAPI() {
    this.gastoId = this.route.snapshot.paramMap.get('id') || "1";
    this.gastoService.getGastoById(this.gastoId).subscribe({
      next: (res) => {
        this.gasto = res
      }
    })
  }

  editarGasto(idGasto: number): void {
    alert("editar gasto: " + idGasto)
  }
  getstringdistribution(formaDividir: string): string {
    return formaDividir === "PORCENTAJE" ? "Distribución por Porcentaje" : "Distribución por Monto"
  }
}
