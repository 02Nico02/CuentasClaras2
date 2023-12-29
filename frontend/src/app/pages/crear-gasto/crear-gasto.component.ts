import { Component, OnInit } from '@angular/core';
import { NavComponent } from '../../shared/nav/nav.component';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { GastoService } from '../../services/gasto/gasto.service';
import { CategoriaDTO, MiembroDTO } from '../../services/group/grupo.dto';
import { GroupService } from '../../services/group/group.service';
import { FormaDividir, GastoAutor, GastoDTO } from '../../services/gasto/gasto.dto';

@Component({
  selector: 'app-crear-gasto',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, NavComponent, FormsModule],
  templateUrl: './crear-gasto.component.html',
  styleUrl: './crear-gasto.component.css'
})
export class CrearGastoComponent implements OnInit {

  newGasto: GastoDTO = {
    id: 0,
    gastoAutor: [] as GastoAutor[],
    nombre: '',
    fecha: '',
    imagen: '',
    grupoId: 0,
    formaDividir: {} as FormaDividir,
    categoria: {} as CategoriaDTO
  };
  categorias: CategoriaDTO[] = [];
  autoresDisponibles: MiembroDTO[] = [];
  today = new Date();
  userNameInvalid: boolean = false;
  montoInvalid: boolean = false;
  fechaInvalid: boolean = false;
  nameInvalid: boolean = false;
  formaDivisionPorcentajeInvalid: boolean = false;
  formaDivisionMontoInvalid: boolean = false;
  msgErrorMonto: String = '';
  msgErrorFecha: String = '';
  mensajeErrorMontos: string = '';
  mensajeErrorPorcentaje: string = '';
  formaDividirAnterior: string = '';
  totalMontos: number = 0;

  constructor(private formBuilder: FormBuilder, private router: Router, private titleService: Title, private gastoService: GastoService, private groupService: GroupService) {

    this.gastoService.getAllExpenseCategories().subscribe(data => {
      this.categorias = data;
    });
    let groupId = history.state.groupId;
    this.groupService.obtenerMiembrosGrupo(groupId).subscribe(
      data => {
        this.autoresDisponibles = data;
        this.newGasto.formaDividir.divisionIndividual = data.map(miembro => ({
          id: miembro.idUsuario,
          userId: miembro.idUsuario,
          userName: miembro.userName,
          monto: 0
        }));
        console.log(this.autoresDisponibles)
      },
      error => {
        console.error('Error al obtener los miembros del grupo:', error);
      }
    );
  }

  ngOnInit(): void {
    this.titleService.setTitle('Cuentas Claras - Nuevo gasto');
  }

  fechaValida(control: FormControl) {
    const inputDate = new Date(control.value);
    const today = new Date();

    if (inputDate > today) {
      return { fechaInvalida: true };
    }
    return null;
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

      // FALTA PONER LA FOTO EN EL GASTO
      // reader.onload = () => {
      //   this.gasto.imagen = reader.result as string;
      // };

      reader.onerror = (error) => {
        console.error('Error al leer el archivo:', error);
      };
    }
  }

  imagenValida(control: AbstractControl): ValidationErrors | null {
    const archivo = control.value;

    if (archivo === null) {
      return { 'sinImagen': true };
    }

    // Validar el tipo de archivo
    const tipoPermitido = ['image/jpeg', 'image/jpg', 'image/png', 'application/pdf'];
    if (!tipoPermitido.includes(archivo.type)) {
      return { 'tipoInvalido': true }; // Devuelve un error si el tipo no es válido
    }

    // Validar el tamaño del archivo
    const maxSize = 5 * 1024 * 1024; // 5MB en bytes
    if (archivo.size > maxSize) {
      return { 'tamanioExcedido': true }; // Devuelve un error si el tamaño excede el límite
    }

    return null; // o devuelve un error si es necesario
  }

  agregarGastoAutor(idAutor: number, monto: number): void {
    // Verifica si el autor ya ha sido agregado para evitar duplicados
    const autorExistente = this.newGasto.gastoAutor.find(gastoAutor => gastoAutor.userId === idAutor);

    if (autorExistente) {
      console.warn('El autor ya fue agregado.');
      return;
    }

    // Busca el autor seleccionado en la lista de autores disponibles
    const autorSeleccionado = this.autoresDisponibles.find(autor => autor.idUsuario === idAutor);

    if (!autorSeleccionado) {
      this.userNameInvalid = true
      console.error('Autor no encontrado.');
      return;
    }

    this.validarMonto(monto);
    if (this.montoInvalid) return;

    // Crea un nuevo FormGroup para el autor y el monto
    const nuevoAutor: GastoAutor = {
      id: 0,
      userId: autorSeleccionado.idUsuario,
      userName: autorSeleccionado.userName,
      monto: monto
    };

    this.newGasto.gastoAutor.push(nuevoAutor);
    this.totalMontos += monto;

    const indexAutorSeleccionado = this.autoresDisponibles.findIndex(autor => autor.idUsuario === idAutor);
    if (indexAutorSeleccionado > -1) {
      this.autoresDisponibles.splice(indexAutorSeleccionado, 1);
    }

    this.validarMontosDivision()
  }
  validarMonto(monto: number | null): void {
    console.log("entre");

    if (!monto) {
      this.montoInvalid = true;
      this.msgErrorMonto = 'Por favor, ingrese un monto válido.';
      return;
    }

    if (monto < 0.01) {
      this.montoInvalid = true;
      this.msgErrorMonto = 'El monto debe ser mayor a 0.';
      return;
    }

    const decimalPart = monto.toString().split('.')[1];
    if (decimalPart && decimalPart.length > 2) {
      this.montoInvalid = true;
      this.msgErrorMonto = 'El monto no puede tener más de dos decimales.';
      return;
    }

    this.montoInvalid = false; // Asegurarse de que el monto sea válido
    this.msgErrorMonto = '';  // Limpiar el mensaje de error si no hay problemas
  }

  public get hayAutoresDisponibles(): boolean {
    return this.autoresDisponibles.length > 0;
  }

  eliminarAutor(autor: GastoAutor): void {
    const index = this.newGasto.gastoAutor.findIndex(a => a.userId === autor.userId);
    if (index !== -1) {
      this.totalMontos -= autor.monto;
      this.newGasto.gastoAutor.splice(index, 1);
      this.autoresDisponibles.push({
        idUsuario: autor.userId,
        userName: autor.userName ? autor.userName : '',
        balance: 0
      });
    }

    this.validarMontosDivision()
  }

  validarNombre(): void {
    console.log(this.nameInvalid)
    console.log("nombre:")
    console.log(this.newGasto.nombre)
    if (this.newGasto.nombre && this.newGasto.nombre.trim().length > 0) {
      console.log("nombre existente")
      this.nameInvalid = false
      return
    }
    this.nameInvalid = true
  }

  validarFecha(): void {
    if (!this.newGasto.fecha) {
      this.fechaInvalid = true;
      this.msgErrorFecha = "La fecha es requerida"
      return
    }
    const inputDate = new Date(this.newGasto.fecha);
    const today = new Date();
    this.fechaInvalid = inputDate > today;
    this.msgErrorFecha = "La fecha ingresada no puede ser posterior a la fecha actual."
  }

  dividirEnPartesIguales() {
    if (!this.totalMontos) {
      console.error("El total de montos es 0 o no está definido.");
      return;
    }

    const cantidadUsuarios = this.newGasto.formaDividir.divisionIndividual.length;

    if (this.newGasto.formaDividir.formaDividir === 'MONTO') {
      let montoPorUsuario = this.totalMontos / cantidadUsuarios;
      montoPorUsuario = parseFloat(montoPorUsuario.toFixed(2));

      let montoTotalAsignado = 0;
      this.newGasto.formaDividir.divisionIndividual.forEach((division, index) => {
        if (index < cantidadUsuarios - 1) {
          division.monto = montoPorUsuario;
          montoTotalAsignado += montoPorUsuario;
        } else {
          division.monto = parseFloat((this.totalMontos - montoTotalAsignado).toFixed(2));
        }
      });

    } else if (this.newGasto.formaDividir.formaDividir === 'PORCENTAJE') {
      const porcentajePorUsuario = 100 / cantidadUsuarios;
      let porcentajeTotalAsignado = 0;

      this.newGasto.formaDividir.divisionIndividual.forEach(division => {
        division.monto = parseFloat(porcentajePorUsuario.toFixed(2));
        porcentajeTotalAsignado += division.monto;
      });

      if (porcentajeTotalAsignado !== 100) {
        const ajustePorcentaje = 100 - porcentajeTotalAsignado;
        this.newGasto.formaDividir.divisionIndividual[cantidadUsuarios - 1].monto += ajustePorcentaje;
      }
    }
    this.validarMontosDivision()
  }

  sumatoriaDivisionIndividual(): number {
    return this.newGasto.formaDividir.divisionIndividual.reduce((total, division) => total + division.monto, 0);
  }

  validarMontosDivision() {
    if (this.newGasto.formaDividir.formaDividir === 'MONTO') {
      this.formaDivisionPorcentajeInvalid = false;
      this.validarSumaMontos()
    } else if (this.newGasto.formaDividir.formaDividir === 'PORCENTAJE') {
      this.formaDivisionMontoInvalid = false;
      this.validarPorcentaje()
    }
  }

  validarSumaMontos(): boolean {
    this.formaDivisionPorcentajeInvalid = false;
    const suma = this.sumatoriaDivisionIndividual();

    if (!this.esNumeroValido(suma)) {
      this.formaDivisionMontoInvalid = true;
      this.mensajeErrorMontos = `La suma de los montos individuales tiene más de dos decimales.`;
      return false;
    }

    if (suma === this.totalMontos) {
      this.formaDivisionMontoInvalid = false;
      this.mensajeErrorMontos = '';
      return true;
    }

    this.formaDivisionMontoInvalid = true;
    const diff = this.totalMontos - suma;
    this.mensajeErrorMontos = `La suma de los montos individuales difiere de $${this.totalMontos}. Diferencia: ${parseFloat(diff.toFixed(2))}`;
    return false;
  }

  validarPorcentaje(): boolean {
    this.formaDivisionMontoInvalid = false;
    const suma = this.sumatoriaDivisionIndividual();

    if (!this.esNumeroValido(suma)) {
      this.formaDivisionPorcentajeInvalid = true;
      this.mensajeErrorPorcentaje = `La suma de los porcentajes tiene más de dos decimales.`;
      return false;
    }

    if (suma === 100) {
      this.formaDivisionPorcentajeInvalid = false;
      this.mensajeErrorPorcentaje = '';
      return true;
    }

    this.formaDivisionPorcentajeInvalid = true;
    const diff = 100 - suma;
    this.mensajeErrorPorcentaje = `La suma de los porcentajes difiere de 100%. Diferencia: ${parseFloat(diff.toFixed(2))}`;
    return false;
  }

  esNumeroValido(numero: number): boolean {
    const decimales = (numero.toString().split('.')[1] || '').length;
    return decimales <= 2;
  }

  cambiarFormaDividir() {
    if (!this.formaDividirAnterior) {
      this.formaDividirAnterior = this.newGasto.formaDividir.formaDividir;
    } else if (this.newGasto.formaDividir.formaDividir === 'MONTO' && this.formaDividirAnterior === 'PORCENTAJE') {
      this.cambiarPorcentajeAMonto();
      this.validarSumaMontos()
    } else if (this.newGasto.formaDividir.formaDividir === 'PORCENTAJE' && this.formaDividirAnterior === 'MONTO') {
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

    this.newGasto.formaDividir.divisionIndividual.forEach(division => {
      let montoUsuario = (division.monto / 100) * this.totalMontos;
      montoUsuario = parseFloat(montoUsuario.toFixed(2));

      division.monto = montoUsuario;
    });

    const montoTotalAsignado = this.newGasto.formaDividir.divisionIndividual.reduce((acc, division) => acc + division.monto, 0);
    if (montoTotalAsignado !== this.totalMontos) {
      const ajuste = this.totalMontos - montoTotalAsignado;
      this.newGasto.formaDividir.divisionIndividual[this.newGasto.formaDividir.divisionIndividual.length - 1].monto += ajuste;
    }

    this.formaDividirAnterior = 'MONTO';
  }


  cambiarMontoAPorcentaje() {
    const sumMontos = this.sumatoriaDivisionIndividual();

    if (sumMontos === 0) {
      console.error("La suma de los montos es 0. No se pueden calcular porcentajes.");
      return;
    }
    const cantidadUsuarios = this.newGasto.formaDividir.divisionIndividual.length;

    let montoTotalAsignado = 0;
    this.newGasto.formaDividir.divisionIndividual.forEach((division, index) => {
      if (index < cantidadUsuarios - 1) {
        let porcentaje = (division.monto / this.totalMontos) * 100;
        porcentaje = parseFloat(porcentaje.toFixed(2));
        division.monto = porcentaje;
        montoTotalAsignado += porcentaje;
      } else {
        division.monto = parseFloat((100 - montoTotalAsignado).toFixed(2))
      }
    });

    this.formaDividirAnterior = 'PORCENTAJE';
  }

  crearGasto() {

  }

}
