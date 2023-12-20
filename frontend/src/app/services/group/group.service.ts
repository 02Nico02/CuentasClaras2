import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { GrupoDTO } from './grupo.dto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GroupService {

  constructor(private http: HttpClient) { }

  obtenerDetalleGrupo(idGrupo: number): Observable<GrupoDTO> {
    return this.http.get<GrupoDTO>(`${environment.urlApi}group/${idGrupo}`);
  }
}
