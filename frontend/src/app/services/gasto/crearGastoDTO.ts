export interface CrearGastoDTO {
    nombre: string;
    fecha: string;
    grupoId: number;
    formaDividir: FormaDividirCrear;
    categoriaId: number;
    gastoAutor: GastoAutorCrear[];
}

export interface GastoAutorCrear {
    userId: number;
    monto: number;
}

export interface FormaDividirCrear {
    formaDividir: string;
    divisionIndividual: DivisionIndividualCrear[];
}

export interface DivisionIndividualCrear {
    userId: number;
    monto: number;
}