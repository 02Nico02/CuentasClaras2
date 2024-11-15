import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { GrupoDTO, MiembroDTO } from './grupo.dto';
import { Observable } from 'rxjs';
import { PosiblesMiembrosDTO } from './posiblesMiembros.dto copy';
import { GrupoParejaDTO } from './grupoPareja.dto';

@Injectable({
  providedIn: 'root'
})
export class GroupService {

  constructor(private http: HttpClient) { }

  obtenerDetalleGrupo(idGrupo: string): Observable<GrupoDTO> {
    return this.http.get<GrupoDTO>(`${environment.urlApi}group/${idGrupo}`);
  }

  obtenerDetalleGrupoPareja(idGrupo: string): Observable<GrupoParejaDTO> {
    return this.http.get<GrupoParejaDTO>(`${environment.urlApi}group/pareja/${idGrupo}`);
  }

  obtenerPosiblesMiembros(groupId: string, usernameQuery: string): Observable<PosiblesMiembrosDTO> {
    return this.http.get<PosiblesMiembrosDTO>(`${environment.urlApi}group/${groupId}/searchUsers?usernameQuery=${usernameQuery}`);
  }
  obtenerMiembrosGrupo(groupId: string): Observable<MiembroDTO[]> {
    return this.http.get<MiembroDTO[]>(`${environment.urlApi}group/${groupId}/members`);
  }

  pagarDeuda(deuda: any, idGrupo: number): Observable<any> {

    let aux = {
      "monto": deuda.monto,
      "destinatarioId": deuda.idAcreedor,
      "grupoId": idGrupo
    }
    console.log(`${environment.urlApi}pay/new`)
    return this.http.post<any>(`${environment.urlApi}pay/new`, aux)
  }

  crearGrupo(grupo: any): Observable<any> {
    console.log(grupo)
    return this.http.post<any>(environment.urlApi + "group/newGroup", grupo)
  }
}
