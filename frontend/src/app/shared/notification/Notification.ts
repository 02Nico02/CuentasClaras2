export interface Notification {
    id: number;
    type: string;
    fechaCreacion: Date;

    // InvitacionAmistadDTO
    idRemitenteAmistad?: number;
    usernameRemitenteAmistad?: string;

    // InvitacionGrupoDTO
    idGrupo?: number;
    nombreGrupo?: string;
    idRemitenteGrupo?: number;
    usernameRemitenteGrupo?: string;
}
