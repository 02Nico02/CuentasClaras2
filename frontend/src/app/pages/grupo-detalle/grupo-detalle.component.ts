import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { GrupoDTO } from '../../services/group/grupo.dto';
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


  grupo: GrupoDTO = {
    id: 1,
    nombre: 'MiGrupo',
    balance: -300.0,
    categoria: {
      id: 1,
      nombre: 'Familia',
      icon: 'https://png.pngtree.com/element_our/md/20180516/md_5afbf695122a5.jpg'
    },
    miembros: [
      {
        idUsuario: 2,
        userName: 'usuario2',
        balance: -300.0
      },
      {
        idUsuario: 3,
        userName: 'usuario3',
        balance: 600.0
      },
      {
        idUsuario: 4,
        userName: 'usuario4',
        balance: 0.0
      },
      {
        idUsuario: 5,
        userName: 'usuario5',
        balance: 0.0
      }
    ],
    actividades: [
      {
        id: 1,
        type: "pago",
        data: "Usuario2 pago $400 al usuario3",
        fecha: new Date()
      },
      {
        id: 1,
        type: "gasto",
        data: "Usuario1 gasto $600 en cena",
        fecha: new Date()
      }, {
        id: 1,
        type: "pago",
        data: "Usuario2 pago $400 al usuario3",
        fecha: new Date()
      },
      {
        id: 1,
        type: "gasto",
        data: "Usuario1 gasto $600 en cena",
        fecha: new Date()
      }, {
        id: 1,
        type: "pago",
        data: "Usuario2 pago $400 al usuario3",
        fecha: new Date()
      },
      {
        id: 1,
        type: "gasto",
        data: "Usuario1 gasto $600 en cena",
        fecha: new Date()
      }, {
        id: 1,
        type: "pago",
        data: "Usuario2 pago $400 al usuario3",
        fecha: new Date()
      },
      {
        id: 1,
        type: "gasto",
        data: "Usuario1 gasto $600 en cena",
        fecha: new Date()
      }
    ],
    deudasUsuarios: [
      {
        id: 1,
        usuarioDebe: true,
        data: 'Le debes al usuario2 $',
        monto: 300
      },
      {
        id: 2,
        usuarioDebe: false,
        data: 'El usuario3 te debe $',
        monto: 300
      },
      {
        id: 3,
        usuarioDebe: false,
        data: 'El usuario3 le debe al usuario4 $',
        monto: 400
      },
      {
        id: 4,
        usuarioDebe: false,
        data: 'El usuario2 le debe al usuario4 $',
        monto: 200
      }
    ]
  };

  visibleActividades: any[] = [];

  limiteActividades = 5;

  filtroUsuarios: string = '';
  amigos: User[] = [];
  usuarios: User[] = [];
  grupoId: string = "1";

  constructor(private route: ActivatedRoute, private router: Router, private titleService: Title, private groupService: GroupService, private userService: UserService) { }

  ngOnInit(): void {
    this.titleService.setTitle('Cuentas Claras - Detalle grupo');
    console.log(this.visibleActividades.length)
    // this.cargarActividades();
    this.llamarAPI()
  }

  llamarAPI() {
    this.grupoId = this.route.snapshot.paramMap.get('id') || "1";
    this.groupService.obtenerDetalleGrupo(this.grupoId).subscribe({
      next: (res) => {
        this.grupo2 = res
        console.log(this.grupo2)
      }
    })

  }

  cargarActividades() {
    this.visibleActividades = this.grupo.actividades.slice(0, this.limiteActividades);
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
    console.log(usuario)
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
    alert("Falta implementar")
  }

  pagarDeuda(idDeuda: number) {
    alert("Falta implementar")
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
    if (balance > 0) {
      return `+$${balance.toFixed(2)}`;
    } else if (balance < 0) {
      return `-$${(-balance).toFixed(2)}`;
    } else {
      return '$0.00';
    }
  }

  mostrarDetallesActividad(actividad: any) {
    alert("Detalle del gasto con id= " + actividad.id)
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
}
