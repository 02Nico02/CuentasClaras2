import { Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GastoService } from '../../services/gasto/gasto.service';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { GastoAutor, GastoDTO } from '../../services/gasto/gasto.dto';
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
  mensajeErrorMontoGasto: string = '';
  usuariosDisponibles: GastoAutor[] = [];

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
      this.agregarUsuariosDisponibles()
    } else {
      this.router.navigate(['/home']);
    }

  }

  agregarUsuariosDisponibles(): void {
    for (const usuario of this.gasto.formaDividir.divisionIndividual) {
      if (!this.gasto.gastoAutor.some(autor => autor.userId === usuario.userId)) {
        this.gasto.gastoAutor.push({
          id: -1,
          userId: usuario.userId,
          userName: usuario.userName,
          monto: 0
        });
      }
    }
  }

  guardarCambios(): void {
    if (this.validarGastoCompleto()) {
      const gastoDTO: GastoDTO = {
        id: this.gasto.id,
        gastoAutor: this.gasto.gastoAutor
          .filter(gastoAutor => gastoAutor.monto !== 0)
          .map(gastoAutor => ({
            id: gastoAutor.id,
            userId: gastoAutor.userId,
            monto: gastoAutor.monto
          })),
        nombre: this.gasto.nombre,
        fecha: new Date(this.gasto.fecha).toISOString(),
        imagen: this.gasto.imagen,
        grupoId: this.gasto.grupoId,
        formaDividir: {
          id: this.gasto.formaDividir.id,
          formaDividir: this.gasto.formaDividir.formaDividir,
          divisionIndividual: this.gasto.formaDividir.divisionIndividual.map(division => ({
            id: division.id,
            userId: division.userId,
            monto: division.monto
          }))
        },
        categoria: {
          id: this.gasto.categoria.id
        },
      };
      this.gastoService.editarGasto(gastoDTO, this.gasto.id).subscribe(
        response => {
          console.log("todo ok, pero debe no existir la ruta")
          console.log(response.id)
          this.gasto = response;
          this.router.navigate([`/gasto/${response.id}/detalle`]);
        },
        error => {
          console.error('Error al guardar el gasto', error);
          if (error.status === 401) {
            this.router.navigate(['/login']);
          } else if (error.status === 403) {
            console.log("error 403")
          } else if (error.status === 404) {
            console.log("error 404")
          } else {
            console.log("Error general")
          }
        }
      );
    } else {
      alert("no esta bien")
    }
  }

  validarGastoCompleto(): boolean {
    this.validarName();
    this.validarFecha();
    this.validarMontosYActualizarTotal();
    this.validarMontosDivision();
    if (this.formValid.nombre && this.formValid.fecha && this.formValid.montoGasto && this.formValid.formaDivisionPorcentaje && this.formValid.formaDivisionMonto) {
      return true;
    } else {
      return false;
    }
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
      this.formValid.fecha = false;
      return;
    }

    const fechaIngresada = new Date(this.gasto.fecha);
    const fechaActual = new Date();

    if (fechaIngresada > fechaActual) {
      this.formValid.fecha = false;
    } else {
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


  validarMontosYActualizarTotal(): void {
    let total = 0;
    for (const autor of this.gasto.gastoAutor) {
      if (autor.monto < 0 || !this.esNumeroValido(autor.monto)) {
        this.mensajeErrorMontoGasto = "Error: El monto ingresado no puede ser negativo y debe tener máximo dos decimales.";
        this.formValid.montoGasto = false;
        return;
      }
      total += autor.monto;
    }
    this.totalMontos = total;
    this.formValid.montoGasto = true;
    this.validarMontosDivision();
  }

  esNumeroValido(numero: number): boolean {
    const decimales = (numero.toString().split('.')[1] || '').length;
    return decimales <= 2;
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

      let montoTotalAsignado = 0;
      this.gasto.formaDividir.divisionIndividual.forEach((division, index) => {
        if (index < cantidadUsuarios - 1) {
          division.monto = montoPorUsuario;
          montoTotalAsignado += montoPorUsuario;
        } else {
          division.monto = parseFloat((this.totalMontos - montoTotalAsignado).toFixed(2));
        }
      });

    } else if (this.gasto.formaDividir.formaDividir === 'PORCENTAJE') {
      const porcentajePorUsuario = 100 / cantidadUsuarios;
      let porcentajeTotalAsignado = 0;

      this.gasto.formaDividir.divisionIndividual.forEach(division => {
        division.monto = parseFloat(porcentajePorUsuario.toFixed(2));
        porcentajeTotalAsignado += division.monto;
      });

      if (porcentajeTotalAsignado !== 100) {
        const ajustePorcentaje = 100 - porcentajeTotalAsignado;
        this.gasto.formaDividir.divisionIndividual[cantidadUsuarios - 1].monto += ajustePorcentaje;
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
    return this.gasto.formaDividir.divisionIndividual.reduce((total, division) => total + division.monto, 0);
  }

  validarSumaMontos(): boolean {
    this.formValid.formaDivisionPorcentaje = true;
    const suma = this.sumatoriaDivisionIndividual();

    if (!this.esNumeroValido(suma)) {
      this.formValid.formaDivisionMonto = false;
      this.mensajeErrorMontos = `La suma de los montos individuales tiene más de dos decimales.`;
      return false;
    }

    if (suma === this.totalMontos) {
      this.formValid.formaDivisionMonto = true;
      this.mensajeErrorMontos = '';
      return true;
    }

    this.formValid.formaDivisionMonto = false;
    const diff = this.totalMontos - suma;
    this.mensajeErrorMontos = `La suma de los montos individuales difiere de $${this.totalMontos}. Diferencia: ${parseFloat(diff.toFixed(2))}`;
    return false;
  }

  validarPorcentaje(): boolean {
    this.formValid.formaDivisionMonto = true;
    const suma = this.sumatoriaDivisionIndividual();

    if (!this.esNumeroValido(suma)) {
      this.formValid.formaDivisionPorcentaje = false;
      this.mensajeErrorPorcentaje = `La suma de los porcentajes tiene más de dos decimales.`;
      return false;
    }

    if (suma === 100) {
      this.formValid.formaDivisionPorcentaje = true;
      this.mensajeErrorPorcentaje = '';
      return true;
    }

    this.formValid.formaDivisionPorcentaje = false;
    const diff = 100 - suma;
    this.mensajeErrorPorcentaje = `La suma de los porcentajes difiere de 100%. Diferencia: ${parseFloat(diff.toFixed(2))}`;
    return false;
  }

  onFileChange(event: any): void {
    const file = event.target.files[0];

    // Validar tamaño máximo (5MB en este ejemplo)
    const maxSize = 5 * 1024 * 1024; // 5MB en bytes

    if (file && file.size > maxSize) {
      alert('El archivo supera el tamaño máximo permitido de 5MB.');
      return;
    }

    // Validar tipo de archivo
    const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg', 'application/pdf'];

    if (file && !allowedTypes.includes(file.type)) {
      alert('El tipo de archivo no es válido. Solo se permiten imágenes (JPG, JPEG, PNG) y PDF.');
      return;
    }

    if (file) {
      const reader = new FileReader();

      reader.readAsDataURL(file);

      reader.onload = () => {
        this.gasto.imagen = reader.result as string;
      };

      reader.onerror = (error) => {
        console.error('Error al leer el archivo:', error);
      };
    }
  }
}
