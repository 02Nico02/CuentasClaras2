import { Component, OnInit, ElementRef, AfterViewInit } from '@angular/core';
import { NavComponent } from '../../shared/nav/nav.component';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { GastoService } from '../../services/gasto/gasto.service';
import { CategoriaDTO, MiembroDTO } from '../../services/group/grupo.dto';
import { GroupService } from '../../services/group/group.service';
import { FormaDividir, GastoAutor, GastoDTO } from '../../services/gasto/gasto.dto';
import { CrearGastoDTO, FormaDividirCrear, GastoAutorCrear } from '../../services/gasto/crearGastoDTO';

@Component({
  selector: 'app-crear-gasto',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, NavComponent, FormsModule],
  templateUrl: './crear-gasto.component.html',
  styleUrl: './crear-gasto.component.css'
})
export class CrearGastoComponent implements OnInit {

  categorias: CategoriaDTO[] = [];
  autoresDisponibles: MiembroDTO[] = [];
  today = new Date();
  groupId: string = "0";
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
  errors = {
    userName: false,
    monto: false,
    fecha: false,
    name: false,
    formaDivisionPorcentaje: false,
    formaDivisionMonto: false,
    categoria: false,
    gastoAutor: false,
    formaDividir: false,
  };
  messages = {
    monto: '',
    fecha: '',
    name: '',
    formaDivisionPorcentaje: '',
    formaDivisionMonto: '',
    imagen: '',
  };
  imagenInvalid: boolean = false;
  formaDividirAnterior: string = '';
  totalMontos: number = 0;
  selectedFile: File | null = null;
  imageURL: string | ArrayBuffer | null = null;

  constructor(private formBuilder: FormBuilder, private router: Router, private titleService: Title, private gastoService: GastoService, private groupService: GroupService) {

    this.initializeData();
  }

  ngOnInit(): void {
    this.titleService.setTitle('Cuentas Claras - Nuevo gasto');
  }

  private initializeData(): void {
    this.groupId = history.state.groupId;
    this.gastoService.getAllExpenseCategories().subscribe(data => {
      this.categorias = data;
    });
    this.groupService.obtenerMiembrosGrupo(this.groupId).subscribe(
      data => {
        this.autoresDisponibles = data;
        this.newGasto.formaDividir.divisionIndividual = data.map(miembro => ({
          id: miembro.idUsuario,
          userId: miembro.idUsuario,
          userName: miembro.userName,
          monto: 0
        }));
      },
      error => {
        console.error('Error al obtener los miembros del grupo:', error);
      }
    );
  }

  hasErrors(): boolean {
    console.log("Entre al hasErrors")
    return Object.values(this.errors).some(error => error);
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
    this.selectedFile = null;
    this.imagenInvalid = false;
    const file = event.target.files[0];

    const maxSize = 5 * 1024 * 1024;

    if (file && file.size > maxSize) {
      this.imagenInvalid = true;
      this.messages.imagen = 'El archivo supera el tamaño máximo permitido de 5MB.';
      return;
    }

    const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg', 'application/pdf'];

    if (file && !allowedTypes.includes(file.type)) {
      this.imagenInvalid = true;
      this.messages.imagen = 'El tipo de archivo no es válido. Solo se permiten imágenes (JPG, JPEG, PNG) y PDF.';
      return;
    }

    if (file) {
      this.selectedFile = file;

      const reader = new FileReader();

      reader.readAsDataURL(file);
      reader.onload = () => {
        this.imageURL = reader.result;
      };

      reader.onerror = (error) => {
        console.error('Error al leer el archivo:', error);
      };
    }
  }

  agregarGastoAutor(idAutor: number, monto: number): void {
    const autorExistente = this.newGasto.gastoAutor.find(gastoAutor => gastoAutor.userId === idAutor);

    if (autorExistente) {
      console.warn('El autor ya fue agregado.');
      return;
    }

    const autorSeleccionado = this.autoresDisponibles.find(autor => autor.idUsuario === idAutor);

    if (!autorSeleccionado) {
      this.errors.userName = true
      console.error('Autor no encontrado.');
      return;
    }

    this.validarMonto(monto);
    if (this.errors.monto) return;

    const nuevoAutor: GastoAutor = {
      id: 0,
      userId: autorSeleccionado.idUsuario,
      userName: autorSeleccionado.userName,
      monto: monto
    };

    this.newGasto.gastoAutor.push(nuevoAutor);
    this.errors.gastoAutor = false;
    this.totalMontos += monto;

    const indexAutorSeleccionado = this.autoresDisponibles.findIndex(autor => autor.idUsuario === idAutor);
    if (indexAutorSeleccionado > -1) {
      this.autoresDisponibles.splice(indexAutorSeleccionado, 1);
    }

    this.validarMontosDivision()
  }
  validarMonto(monto: number | null): void {

    if (!monto) {
      this.errors.monto = true;
      this.messages.monto = 'Por favor, ingrese un monto válido.';
      return;
    }

    if (monto < 0.01) {
      this.errors.monto = true;
      this.messages.monto = 'El monto debe ser mayor a 0.';
      return;
    }

    const decimalPart = monto.toString().split('.')[1];
    if (decimalPart && decimalPart.length > 2) {
      this.errors.monto = true;
      this.messages.monto = 'El monto no puede tener más de dos decimales.';
      return;
    }

    this.errors.monto = false;
    this.messages.monto = '';
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

    if (this.newGasto.gastoAutor.length === 0) {
      this.errors.gastoAutor = true
    }

    this.validarMontosDivision()
  }

  validarNombre(): void {
    if (this.newGasto.nombre && this.newGasto.nombre.trim().length > 0) {
      this.errors.name = false
      return
    }
    this.errors.name = true
  }

  validarFecha(): void {
    if (!this.newGasto.fecha) {
      this.errors.fecha = true;
      this.messages.fecha = "La fecha es requerida"
      return
    }
    const inputDate = new Date(this.newGasto.fecha);
    const today = new Date();
    this.errors.fecha = inputDate > today;
    this.messages.fecha = "La fecha ingresada no puede ser posterior a la fecha actual."
  }

  validarCategoria(): void {
    this.errors.categoria = !this.newGasto.categoria.id;
  }

  validarGastoAutor(): void {
    this.errors.gastoAutor = this.newGasto.gastoAutor.length === 0;
  }

  validarFormaDividir(): void {
    this.errors.formaDividir = !this.newGasto.formaDividir.formaDividir;
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
      this.errors.formaDivisionPorcentaje = false;
      this.validarSumaMontos()
    } else if (this.newGasto.formaDividir.formaDividir === 'PORCENTAJE') {
      this.errors.formaDivisionMonto = false;
      this.validarPorcentaje()
    }
  }

  validarSumaMontos(): boolean {
    this.errors.formaDivisionPorcentaje = false;
    const suma = this.sumatoriaDivisionIndividual();

    if (!this.esNumeroValido(suma)) {
      this.errors.formaDivisionMonto = true;
      this.messages.formaDivisionMonto = `La suma de los montos individuales tiene más de dos decimales.`;
      return false;
    }

    if (suma === this.totalMontos) {
      this.errors.formaDivisionMonto = false;
      this.messages.formaDivisionMonto = '';
      return true;
    }

    this.errors.formaDivisionMonto = true;
    const diff = this.totalMontos - suma;
    this.messages.formaDivisionMonto = `La suma de los montos individuales difiere de $${this.totalMontos}. Diferencia: ${parseFloat(diff.toFixed(2))}`;
    return false;
  }

  validarPorcentaje(): boolean {
    this.errors.formaDivisionMonto = false;
    const suma = this.sumatoriaDivisionIndividual();

    if (!this.esNumeroValido(suma)) {
      this.errors.formaDivisionPorcentaje = true;
      this.messages.formaDivisionPorcentaje = `La suma de los porcentajes tiene más de dos decimales.`;
      return false;
    }

    if (suma === 100) {
      this.errors.formaDivisionPorcentaje = false;
      this.messages.formaDivisionPorcentaje = '';
      return true;
    }

    this.errors.formaDivisionPorcentaje = true;
    const diff = 100 - suma;
    this.messages.formaDivisionPorcentaje = `La suma de los porcentajes difiere de 100%. Diferencia: ${parseFloat(diff.toFixed(2))}`;
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

  formValid(): boolean {
    this.validarNombre();
    this.validarFecha();
    this.validarCategoria();
    this.validarGastoAutor();
    this.validarFormaDividir();
    this.validarMontosDivision();

    return Object.values(this.errors).every(error => !error);
  }
  crearGasto() {
    if (this.formValid()) {
      const formaDividirCrear: FormaDividirCrear = {
        formaDividir: this.newGasto.formaDividir.formaDividir,
        divisionIndividual: this.newGasto.formaDividir.divisionIndividual.map(division => ({
          userId: division.userId,
          monto: division.monto
        }))
      };
      const gastoAutorCrear: GastoAutorCrear[] = this.newGasto.gastoAutor.map(autor => ({
        userId: autor.userId,
        monto: autor.monto
      }));
      const crearGastoData: CrearGastoDTO = {
        nombre: this.newGasto.nombre,
        fecha: this.newGasto.fecha,
        grupoId: parseInt(this.groupId, 10),
        formaDividir: formaDividirCrear,
        categoriaId: this.newGasto.categoria.id,
        gastoAutor: gastoAutorCrear
      };
      // formData.append('data', JSON.stringify(crearGastoData));
      // if (this.selectedFile) {
      //   formData.append('file', this.selectedFile, this.selectedFile.name);
      // }
      this.gastoService.crearGasto(crearGastoData).subscribe(
        data => {
          if (this.selectedFile) {
            this.gastoService.subirImagen(data.id, this.selectedFile).subscribe(gasto => {
              this.router.navigate([`/gasto/${data.id}/detalle`]);
            });
          } else {
            this.router.navigate([`/gasto/${data.id}/detalle`]);

          }
        },
        error => {
          console.error(error);
        }
      );
    } else {
      console.log(this.errors);
    }
  }

  cancelar() {
    this.router.navigate([`/grupo/${this.groupId}/detalle`]);
  }

}
