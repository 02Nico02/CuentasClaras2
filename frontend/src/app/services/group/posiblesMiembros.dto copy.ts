import { User } from "../auth/user";

export interface PosiblesMiembrosDTO {
    amigos: User[];
    usuarios: User[];
}