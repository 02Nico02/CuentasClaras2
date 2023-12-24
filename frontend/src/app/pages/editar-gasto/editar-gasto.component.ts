import { Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GastoService } from '../../services/gasto/gasto.service';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { GastoDTO } from '../../services/gasto/gasto.dto';
import { NavComponent } from '../../shared/nav/nav.component';
import { Title } from '@angular/platform-browser';
import { CategoriaDTO } from '../../services/group/grupo.dto';

@Component({
  selector: 'app-editar-gasto',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, NavComponent],
  templateUrl: './editar-gasto.component.html',
  styleUrl: './editar-gasto.component.css'
})
export class EditarGastoComponent {

  gasto: GastoDTO = {
    id: 0,
    gastoAutor: [
      {
        id: 0,
        monto: 0,
        userId: 0,
        userName: ''
      }],
    nombre: '',
    fecha: '',
    imagen: '',
    grupoId: 0,
    formaDividir: {
      id: 0,
      formaDividir: '',
      divisionIndividual: []
    },
    categoria: {
      id: 0,
      nombre: '',
      icon: ''
    },
    editable: false
  };
  totalMontos: number = 0;
  gastoEdit: GastoDTO | undefined;
  categorias: CategoriaDTO[] = [];
  categoriaSelec: CategoriaDTO | undefined;

  formValid: {
    nombre: boolean,
    fecha: boolean,
    montoGasto: boolean,
    imagen: boolean,
    grupo: boolean,
    formaDivisionMonto: boolean,
    formaDivisionPorcentaje: boolean,
  } = {
      nombre: true,
      fecha: true,
      montoGasto: true,
      imagen: true,
      grupo: true,
      formaDivisionMonto: true,
      formaDivisionPorcentaje: true
    };
  formaDividirAnterior: string = 'MONTO';

  mensajeErrorMontos: string = '';
  mensajeErrorPorcentaje: string = '';

  constructor(private route: ActivatedRoute,
    private gastoService: GastoService,
    private router: Router, private titleService: Title) { }

  ngOnInit(): void {
    this.titleService.setTitle('Cuentas Claras - Editar gasto');
    let newGasto = history.state.gasto;
    if (newGasto) {
      this.gasto = newGasto as GastoDTO;
      this.gastoEdit = newGasto
      this.actualizarTotal()
      this.gasto.fecha = this.convertirFechaParaInput(this.gasto.fecha);
      this.gastoService.getAllExpenseCategories().subscribe(data => {
        this.categorias = data;
      });
    } else {
      this.router.navigate(['/home']);
    }

  }

  onImagenSeleccionada(event: any): void {
    // const file = event.target.files[0];
    // if (file) {
    //   const reader = new FileReader();
    //   reader.readAsDataURL(file);
    //   reader.onload = (e) => {
    //     this.imagenSeleccionada = e.target?.result;
    //   };
    // }
  }

  guardarCambios(): void {
    // if (this.gastoForm.valid) {
    //   this.gastoService.editarGasto(this.gasto).subscribe(
    //     response => {
    //       // Manejar la respuesta
    //     },
    //     error => {
    //       console.error('Error al guardar el gasto', error);
    //       // Mostrar un mensaje de error al usuario
    //     }
    //   );
    //   console.log("Todo bien")
    // }
  }
  onFileChange($event: Event) {
  }


  validarName() {
    if (!this.gasto.nombre || this.gasto.nombre.trim() === '' || this.gasto.nombre.trim().length < 3) {
      this.formValid.nombre = false;
    } else {
      this.formValid.nombre = true;
    }
  }

  validarFecha() {
    if (!this.gasto.fecha) {
      console.log("false en 1")
      this.formValid.fecha = false;
      return;
    }

    const fechaIngresada = new Date(this.gasto.fecha);
    const fechaActual = new Date();

    if (fechaIngresada > fechaActual) {
      console.log("false en 2")
      this.formValid.fecha = false;
    } else {
      console.log("True")
      this.formValid.fecha = true;
    }
  }


  convertirFechaParaInput(fechaCompleta: string): string {
    console.log(fechaCompleta)
    return fechaCompleta.split('T')[0];
  }

  actualizarTotal() {
    let total = 0;
    for (let autor of this.gasto.gastoAutor) {
      total += autor.monto;
    }
    this.totalMontos = total;
  }


  validarMontosYActualizarTotal() {
    let total = 0;
    for (const autor of this.gasto.gastoAutor) {
      if (autor.monto < 0) {
        this.formValid.montoGasto = false;
        return
      }
      total += autor.monto;
    }
    this.totalMontos = total;
    this.formValid.montoGasto = true;
    this.validarMontosDivision()
  }

  cambiarFormaDividir() {
    if (this.gasto.formaDividir.formaDividir === 'MONTO' && this.formaDividirAnterior === 'PORCENTAJE') {
      this.cambiarPorcentajeAMonto();
      this.validarSumaMontos()
    } else if (this.gasto.formaDividir.formaDividir === 'PORCENTAJE' && this.formaDividirAnterior === 'MONTO') {
      this.cambiarMontoAPorcentaje();
      this.validarPorcentaje()
    }
  }

  cambiarPorcentajeAMonto() {
    const sumPorcentajes = this.sumatoriaDivisionIndividual();

    if (sumPorcentajes !== 100) {
      console.error("La suma de los porcentajes no es 100.");
      return;
    }

    this.gasto.formaDividir.divisionIndividual.forEach(division => {
      let montoUsuario = (division.monto / 100) * this.totalMontos;
      montoUsuario = parseFloat(montoUsuario.toFixed(2));

      division.monto = montoUsuario;
    });

    const montoTotalAsignado = this.gasto.formaDividir.divisionIndividual.reduce((acc, division) => acc + division.monto, 0);
    if (sumPorcentajes !== this.totalMontos) {
      const ajuste = this.totalMontos - montoTotalAsignado;
      const cantidadUsuarios = this.gasto.formaDividir.divisionIndividual.length;
      const indiceAleatorio = Math.floor(Math.random() * cantidadUsuarios);
      this.gasto.formaDividir.divisionIndividual[indiceAleatorio].monto += ajuste;
    }

    this.formaDividirAnterior = 'MONTO';
  }


  cambiarMontoAPorcentaje() {
    const sumMontos = this.sumatoriaDivisionIndividual();

    if (sumMontos === 0) {
      console.error("La suma de los montos es 0. No se pueden calcular porcentajes.");
      return;
    }

    this.gasto.formaDividir.divisionIndividual.forEach(division => {
      let porcentaje = (division.monto / this.totalMontos) * 100;
      porcentaje = parseFloat(porcentaje.toFixed(2));
      division.monto = porcentaje;
    });

    const sumPorcentajes = this.sumatoriaDivisionIndividual();
    if (sumPorcentajes !== 100) {
      const ajuste = 100 - sumPorcentajes;
      const cantidadUsuarios = this.gasto.formaDividir.divisionIndividual.length;
      const indiceAleatorio = Math.floor(Math.random() * cantidadUsuarios);
      this.gasto.formaDividir.divisionIndividual[indiceAleatorio].monto += ajuste;
    }

    this.formaDividirAnterior = 'PORCENTAJE';
  }


  dividirEnPartesIguales() {
    if (!this.totalMontos) {
      console.error("El total de montos es 0 o no está definido.");
      return;
    }

    const cantidadUsuarios = this.gasto.formaDividir.divisionIndividual.length;

    if (this.gasto.formaDividir.formaDividir === 'MONTO') {
      let montoPorUsuario = this.totalMontos / cantidadUsuarios;
      montoPorUsuario = parseFloat(montoPorUsuario.toFixed(2));


      this.gasto.formaDividir.divisionIndividual.forEach(division => {
        division.monto = montoPorUsuario;
      });
      const montoTotalAsignado = montoPorUsuario * cantidadUsuarios;
      if (montoTotalAsignado !== this.totalMontos) {
        const indiceAleatorio = Math.floor(Math.random() * cantidadUsuarios);
        const ajuste = this.totalMontos - montoTotalAsignado;

        this.gasto.formaDividir.divisionIndividual[indiceAleatorio].monto += ajuste;
      }

    } else if (this.gasto.formaDividir.formaDividir === 'PORCENTAJE') {
      const porcentajePorUsuario = 100 / cantidadUsuarios;
      this.gasto.formaDividir.divisionIndividual.forEach(division => {
        division.monto = parseFloat(porcentajePorUsuario.toFixed(2));
      });

      const porcentajeTotalAsignado = porcentajePorUsuario * cantidadUsuarios;
      if (porcentajeTotalAsignado !== 100) {
        const ajustePorcentaje = 100 - porcentajeTotalAsignado;
        const indiceAleatorio = Math.floor(Math.random() * cantidadUsuarios);

        this.gasto.formaDividir.divisionIndividual[indiceAleatorio].monto += ajustePorcentaje;
      }


    }
    this.validarMontosDivision()
  }





  validarMontosDivision() {
    if (this.gasto.formaDividir.formaDividir === 'MONTO') {
      this.formValid.formaDivisionPorcentaje = true;
      this.validarSumaMontos()
    } else if (this.gasto.formaDividir.formaDividir === 'PORCENTAJE') {
      this.formValid.formaDivisionMonto = true;
      this.validarPorcentaje()
    }
  }
  sumatoriaDivisionIndividual(): number {
    let suma = 0;
    for (let division of this.gasto.formaDividir.divisionIndividual) {
      suma += division.monto;
    }

    return suma;
  }
  validarSumaMontos(): boolean {
    this.formValid.formaDivisionPorcentaje = true;
    const suma = this.sumatoriaDivisionIndividual();
    if (suma === this.totalMontos) {
      this.formValid.formaDivisionMonto = true;
      this.mensajeErrorMontos = '';
      return true;
    }
    this.formValid.formaDivisionMonto = false;
    this.mensajeErrorMontos = `La suma de los montos individuales difiere de $${this.totalMontos}. Diferencia: ${this.totalMontos - suma}`;
    return false;
  }

  validarPorcentaje(): boolean {
    this.formValid.formaDivisionMonto = true;
    const suma = this.sumatoriaDivisionIndividual();
    if (suma === 100) {
      this.formValid.formaDivisionPorcentaje = true;
      this.mensajeErrorPorcentaje = '';
      return true;
    }
    this.formValid.formaDivisionPorcentaje = false;
    this.mensajeErrorPorcentaje = `La suma de los porcentajes difiere de 100%. Diferencia: ${100 - suma}`;
    return false;
  }
}
