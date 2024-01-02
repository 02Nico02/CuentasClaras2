import { Component, Inject, LOCALE_ID, OnInit } from '@angular/core';
import { NavComponent } from '../../shared/nav/nav.component';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Title } from '@angular/platform-browser';
import { GroupService } from '../../services/group/group.service';
import { GrupoParejaDTO } from '../../services/group/grupoPareja.dto';
import { DeudaUsuarioDTO } from '../../services/group/grupo.dto';

@Component({
  selector: 'app-grupo-pareja',
  standalone: true,
  imports: [RouterModule, NavComponent, CommonModule],
  templateUrl: './grupo-pareja.component.html',
  styleUrl: './grupo-pareja.component.css'
})
export class GrupoParejaComponent implements OnInit {

  grupoId: string = "-1";
  grupoPareja?: GrupoParejaDTO;

  limiteActividades = 5;
  visibleActividades: any[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private titleService: Title, private groupService: GroupService, @Inject(LOCALE_ID) private locale: string) { console.log(this.locale); }

  ngOnInit(): void {
    this.titleService.setTitle('Cuentas Claras - Detalle grupo pareja');
    this.llamarAPI()
  }

  getClassBalance(balance: number): string {
    if (balance < 0) {
      return 'saldo-negativo';
    } else if (balance >= 0) {
      return 'saldo-positivo';
    } else {
      return 'zero-balance';
    }
  }

  formatBalanceString(balance: number): string {
    if (balance > 0) {
      return this.formatBalance(balance) + ` a favor`;
    } else if (balance < 0) {
      return this.formatBalance(balance) + ` debes`;
    } else {
      return `Estas al día`;
    }
  }

  formatBalance(balance: number): string {
    const formattedBalance = balance.toLocaleString('es-AR', {
      style: 'currency',
      currency: 'ARS'
    });

    return formattedBalance;
  }

  agregarGasto() {
    this.router.navigate(['/crear-gasto'], { state: { groupId: this.grupoPareja?.id } });
  }
  eliminarAmigo() {

  }

  llamarAPI() {
    this.grupoId = this.route.snapshot.paramMap.get('id') || "-1";
    this.groupService.obtenerDetalleGrupoPareja(this.grupoId).subscribe({
      next: (res) => {
        this.grupoPareja = res
        this.cargarActividades();
      }
    })

  }

  cargarActividades() {
    this.visibleActividades = this.grupoPareja?.actividades.slice(0, this.limiteActividades) || [];
  }

  mostrarMasActividades() {
    this.limiteActividades += 5;
    this.cargarActividades();
  }

  mostrarMenosActividades() {
    this.limiteActividades -= 5;
    this.cargarActividades();
  }

  pagarDeuda(deuda: DeudaUsuarioDTO) {
    this.groupService.pagarDeuda(deuda, (this.grupoPareja?.id || 0)).subscribe({
      next: (res) => {
      },
      error: (error) => {
      },
      complete: () => {
        location.reload();
      },
    })
  }

  regresarAmigos() {
    this.router.navigate(['/amigos']);
  }

}
