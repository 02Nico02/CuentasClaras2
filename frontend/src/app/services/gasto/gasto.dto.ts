import { CategoriaDTO } from "../group/grupo.dto";

export interface GastoDTO {
    id: number;
    gastoAutor: GastoAutor[];
    nombre: string;
    fecha: string;
    imagen: string;
    grupoId: number;
    formaDividir: FormaDividir;
    categoria: CategoriaDTO;
    editable: boolean;
}

export interface GastoAutor {
    id: number;
    monto: number;
    userId: number;
    userName: string;
}

export interface FormaDividir {
    id: number;
    formaDividir: string;
    divisionIndividual: DivisionIndividual[];
}

export interface DivisionIndividual {
    id: number;
    userId: number;
    userName: string;
    monto: number;
}
