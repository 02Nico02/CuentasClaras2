import { Actividad, DeudaUsuarioDTO } from "./grupo.dto";

export interface GrupoParejaDTO {
    id: number;
    balance: number,
    amigo: AmigoDTO,
    deudasUsuarios: DeudaUsuarioDTO[];
    actividades: Actividad[];
}

export interface AmigoDTO {
    idUsuario: number;
    userName: string;
}
