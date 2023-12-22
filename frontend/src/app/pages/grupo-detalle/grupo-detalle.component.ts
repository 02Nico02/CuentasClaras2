import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { GrupoDTO } from '../../services/group/grupo.dto';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GroupService } from '../../services/group/group.service';
import { NavComponent } from '../../shared/nav/nav.component';


@Component({
  selector: 'app-grupo-detalle',
  standalone: true,
  imports: [RouterModule, ReactiveFormsModule, CommonModule, FormsModule,NavComponent],
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
      // {
      //   id: 1,
      //   usuarioDebe: true,
      //   data: 'Le debes al usuario2 $',
      //   monto: 300
      // },
      // {
      //   id: 2,
      //   usuarioDebe: false,
      //   data: 'El usuario3 te debe $',
      //   monto: 300
      // },
      // {
      //   id: 3,
      //   usuarioDebe: false,
      //   data: 'El usuario3 le debe al usuario4 $',
      //   monto: 400
      // },
      // {
      //   id: 4,
      //   usuarioDebe: false,
      //   data: 'El usuario2 le debe al usuario4 $',
      //   monto: 200
      // }
    ]
  };

  visibleActividades: any[] = [];

  limiteActividades = 5;

  filtroUsuarios: string = '';
  amigos: any[] = [
    { id: 6, userName: 'usuario6' },
    { id: 7, userName: 'usuario7' },
  ];
  usuarios: any[] = [
    { id: 8, userName: 'usuario8' },
    { id: 9, userName: 'usuario9' },
  ];

  constructor(private route: ActivatedRoute, private router: Router, private titleService: Title, private groupService:GroupService) { }

  ngOnInit(): void {
    this.titleService.setTitle('Cuentas Claras - Detalle grupo');
    console.log(this.visibleActividades.length)
    this.llamarAPI()
    this.cargarActividades();
  }

  llamarAPI(){
    const grupoId = this.route.snapshot.paramMap.get('id') || "1";
    this.groupService.obtenerDetalleGrupo(grupoId).subscribe({
      next:(res)=>{
        this.grupo2=res
        console.log(this.grupo2)
      }
    })

  }

  cargarActividades() {
    this.visibleActividades = this.grupo.actividades.slice(0, this.limiteActividades);
    console.log(this.visibleActividades)
    console.log(this.visibleActividades.length)
  }

  mostrarMasActividades() {
    this.limiteActividades += 5;
    this.cargarActividades();
  }

  mostrarMenosActividades() {
    this.limiteActividades -= 5;
    this.cargarActividades();
  }

  agregarMiembroSeleccionado(usuario: any) {
    alert(`Usuario ${usuario.userName} invitado al grupo.`);
  }

  agregarGasto() {
    alert("Falta implementar")
  }

  pagarDeuda(deuda: any) {
    console.log(deuda)
    this.groupService.pagarDeuda(deuda,(this.grupo2?.id||0)).subscribe({
      next:(res)=>{
        console.log("La deuda se debería haber creado")
        console.log(res.status)
      },
      error:(error)=>{
        console.log(error.status)
        console.log("Murió la deuda")
      },
      complete:()=>{
        console.log("Deuda Completadisima")
        location.reload();
      }
    })
    // llamar al servicio enviandole el idGrupo
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
}
