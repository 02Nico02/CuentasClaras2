import { Component, OnInit } from '@angular/core';
import { GastoDTO } from '../../services/gasto/gasto.dto';
import { Title } from '@angular/platform-browser';
import { GastoService } from '../../services/gasto/gasto.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavComponent } from '../../shared/nav/nav.component';
import { environment } from '../../../environments/environment';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

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
  environment = environment;

  constructor(private route: ActivatedRoute, private titleService: Title, private gastoService: GastoService, private router: Router) { }

  ngOnInit(): void {
    this.llamarAPI();
    this.titleService.setTitle('Cuentas Claras - Detalle gasto');
  }

  llamarAPI() {
    this.gastoId = this.route.snapshot.paramMap.get('id') || "1";
    this.gastoService.getGastoById(this.gastoId).subscribe({
      next: (res) => {
        this.gasto = res
        console.log(this.gasto.fecha)
      }
    })
  }

  editarGasto(): void {
    if (this.gasto?.editable) {
      this.router.navigate(['/editar-gasto'], { state: { gasto: this.gasto } });
    }
  }
  getstringdistribution(formaDividir: string): string {
    return formaDividir === "PORCENTAJE" ? "Distribución por Porcentaje" : "Distribución por Monto"
  }
  hacerZoom(event: Event) {
    const imgElement = event.target as HTMLElement;
    if (imgElement.classList.contains('zoomed')) {
      imgElement.classList.remove('zoomed');
    } else {
      imgElement.classList.add('zoomed');
    }
  }

  convertirFechaAFormatoDeseado(fechaISO: string): string {
    const fecha = new Date(fechaISO);
    const dia = String(fecha.getUTCDate()).padStart(2, '0');
    const mes = String(fecha.getUTCMonth() + 1).padStart(2, '0');
    const anio = String(fecha.getUTCFullYear()).slice(-2);

    return `${dia}/${mes}/${anio}`;
  }


  mostrarFechaLocal(fechaISO: string): string {
    return this.convertirFechaAFormatoDeseado(fechaISO);
  }

  formatMonto(monto: number): string {
    const formattedMonto = monto.toLocaleString('es-AR', {
      style: 'currency',
      currency: 'ARS'
    });

    return formattedMonto;
  }

  getTotalGastado(): number {
    if (!this.gasto?.gastoAutor) return 0;

    return this.gasto.gastoAutor.reduce((total, autor) => total + autor.monto, 0);
  }
}
