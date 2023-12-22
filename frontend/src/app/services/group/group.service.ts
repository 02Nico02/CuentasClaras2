import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { GrupoDTO } from './grupo.dto';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GroupService {

  constructor(private http: HttpClient) { }

  obtenerDetalleGrupo(idGrupo: string): Observable<GrupoDTO> {
    return this.http.get<GrupoDTO>(`${environment.urlApi}group/${idGrupo}`);
  }

  pagarDeuda(deuda: any, idGrupo:number): Observable<any>{

    let aux={
      "monto": deuda.monto,
      "destinatarioId": deuda.idAcreedor,
      "grupoId": idGrupo
    }
    console.log(`${environment.urlApi}pay/new`)
    return this.http.post<any>(`${environment.urlApi}pay/new`, aux)
  }

  crearGrupo(grupo: any): Observable<any>{
    console.log(grupo)
    return this.http.post<any>(environment.urlApi + "group/newGroup", grupo)
      
    
    
  }
}
