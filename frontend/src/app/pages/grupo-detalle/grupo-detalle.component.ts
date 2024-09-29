import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { DeudaUsuarioDTO, GrupoDTO } from '../../services/group/grupo.dto';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GroupService } from '../../services/group/group.service';
import { NavComponent } from '../../shared/nav/nav.component';
import { PosiblesMiembrosDTO } from '../../services/group/posiblesMiembros.dto copy';
import { AmigoDTO } from '../../services/user/amigo.dto';
import { UserService } from '../../services/user/user.service';
import { User } from '../../services/auth/user';


@Component({
  selector: 'app-grupo-detalle',
  standalone: true,
  imports: [RouterModule, ReactiveFormsModule, CommonModule, FormsModule, NavComponent],
  templateUrl: './grupo-detalle.component.html',
  styleUrl: './grupo-detalle.component.css'
})
export class GrupoDetalleComponent implements OnInit {


  grupo2?: GrupoDTO


  visibleActividades: any[] = [];

  limiteActividades = 5;

  filtroUsuarios: string = '';
  amigos: User[] = [];
  usuarios: User[] = [];
  grupoId: string = "1";

  constructor(private route: ActivatedRoute, private router: Router, private titleService: Title, private groupService: GroupService, private userService: UserService) { }

  ngOnInit(): void {
    this.titleService.setTitle('Cuentas Claras - Detalle grupo');
    this.llamarAPI()
  }

  llamarAPI() {
    this.grupoId = this.route.snapshot.paramMap.get('id') || "1";
    this.groupService.obtenerDetalleGrupo(this.grupoId).subscribe({
      next: (res) => {
        this.grupo2 = res
        this.cargarActividades();
      }
    })

  }

  cargarActividades() {
    this.visibleActividades = this.grupo2?.actividades.slice(0, this.limiteActividades) || [];
  }

  mostrarMasActividades() {
    this.limiteActividades += 5;
    this.cargarActividades();
  }

  mostrarMenosActividades() {
    this.limiteActividades -= 5;
    this.cargarActividades();
  }

  agregarMiembroSeleccionado(usuario: User) {
    let grupoId: number = parseInt(this.grupoId, 10);
    this.userService.sendGroupInvitation(usuario.id, grupoId).subscribe(
      response => {
        const usuarioEnLista = this.usuarios.find(u => u.id === usuario.id);
        const amigoEnLista = this.amigos.find(a => a.id === usuario.id);
        if (usuarioEnLista) {
          usuarioEnLista.solicitudEnviada = true;
        } else if (amigoEnLista) {
          amigoEnLista.solicitudEnviada = true;
        }
      },
      error => {
        console.error('Error al enviar solicitud de amistad:', error);
      }
    );
  }

  agregarGasto() {
    this.router.navigate(['/crear-gasto'], { state: { groupId: this.grupo2?.id } });
  }

  pagarDeuda(deuda: DeudaUsuarioDTO) {
    this.groupService.pagarDeuda(deuda, (this.grupo2?.id || 0)).subscribe({
      next: (res) => {
      },
      error: (error) => {
      },
      complete: () => {
        location.reload();
      },
    })
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

  mostrarDetallesActividad(actividad: any) {
    this.router.navigateByUrl("/gasto/" + actividad.id + "/detalle")
  }

  buscarUsuarios(): void {
    if (this.filtroUsuarios.trim() !== '') {
      this.groupService.obtenerPosiblesMiembros(this.grupoId, this.filtroUsuarios).subscribe(
        data => {
          let posiblesMiembros: PosiblesMiembrosDTO = data;
          this.amigos = posiblesMiembros.amigos;
          this.usuarios = posiblesMiembros.usuarios;
        },
        error => {
          console.error('Error al obtener usuarios:', error);
        }
      );
    }
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

  formattedDate(date: string): string {
    const fecha = new Date(date);
    const dia = String(fecha.getUTCDate()).padStart(2, '0');
    const mes = String(fecha.getUTCMonth() + 1).padStart(2, '0');
    const anio = String(fecha.getUTCFullYear()).slice(-2);

    return `${dia}/${mes}/${anio}`;
  }
}
