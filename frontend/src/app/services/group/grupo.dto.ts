export interface GrupoDTO {
    id: number;
    nombre: string;
    balance: number,
    categoria: CategoriaDTO;
    miembros: MiembroDTO[];
    actividades: Actividad[];
    deudasUsuarios: DeudaUsuarioDTO[];
}

export interface CategoriaDTO {
    id: number;
    nombre?: string;
    icon?: string;
}

export interface MiembroDTO {
    idUsuario: number;
    userName: string;
    balance: number;
}

export interface Actividad {
    id: number;
    type: string;
    data: string;
    fecha: Date;
}

export interface DeudaUsuarioDTO {
    id: number;
    usuarioDebe: boolean;
    data: string;
    monto: number;
    idAcreedor: number
}
