import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { GrupoDTO } from './grupo.dto';
import { Observable } from 'rxjs';
import { PosiblesMiembrosDTO } from './posiblesMiembros.dto copy';

@Injectable({
  providedIn: 'root'
})
export class GroupService {

  constructor(private http: HttpClient) { }

  obtenerDetalleGrupo(idGrupo: string): Observable<GrupoDTO> {
    return this.http.get<GrupoDTO>(`${environment.urlApi}group/${idGrupo}`);
  }

  obtenerPosiblesMiembros(groupId: string, usernameQuery: string): Observable<PosiblesMiembrosDTO> {
    return this.http.get<PosiblesMiembrosDTO>(`${environment.urlApi}group/${groupId}/searchUsers?usernameQuery=${usernameQuery}`);
  }
}
