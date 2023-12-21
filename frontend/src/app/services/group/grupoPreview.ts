import { CategoriaDTO } from "./grupo.dto";

export interface GrupoPreviewDTO {
    id: number;
    nombre: string;
    balance: number,
    categoria: CategoriaDTO;
    miembros: MiembroPreviewDTO[];
}

export interface MiembroPreviewDTO {
    id: number;
    userName: string;
}