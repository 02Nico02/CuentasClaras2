import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { GroupService } from '../../services/group/group.service';
import { Title } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NavComponent } from '../../shared/nav/nav.component';
import { NotificationComponent } from '../../shared/notification/notification.component';

@Component({
  selector: 'app-crear-grupo',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, CommonModule, NavComponent, NotificationComponent],
  templateUrl: './crear-grupo.component.html',
  styleUrl: './crear-grupo.component.css'
})
export class CrearGrupoComponent implements OnInit {

  grupoForm: FormGroup;

  categorias: any[] = [
    { id: 1, nombre: 'Familia', icon: 'icon' },
    { id: 2, nombre: 'Viaje', icon: 'icon' },
    { id: 3, nombre: 'Salida', icon: 'icon' },
    { id: 4, nombre: 'Trabajo', icon: 'icon' },
    { id: 5, nombre: 'Amigos', icon: 'icon' },
    { id: 6, nombre: 'Estudio', icon: 'icon' },
    { id: 7, nombre: 'Deportes', icon: 'icon' },
  ];

  constructor(private formBuilder: FormBuilder, private groupService: GroupService, private titleService: Title, private router: Router) {
    this.grupoForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      categoria: ['', Validators.required],
    });
  }
  ngOnInit(): void {
    this.titleService.setTitle('Cuentas Claras - Iniciar Sesión');
  }

  crearGrupo(): void {
    if (this.grupoForm.valid) {
      const grupoData = this.grupoForm.value;
      // this.groupService.crearGrupo(grupoData).subscribe(
      //   response => {
      //     console.log('Grupo creado con éxito:', response);
      //   },
      //   error => {
      //     console.error('Error al crear el grupo:', error);
      //   }
      // );

      // Por el momento un id base
      const grupoId = 1
      this.router.navigate(['/grupo', grupoId, 'detalle']);
    }
  }

  get nombre() {
    return this.grupoForm.controls["nombre"];
  }

  get categoria() {
    return this.grupoForm.controls["categoria"];
  }
}
