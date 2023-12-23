import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GastoDTO } from './gasto.dto';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GastoService {


  constructor(private http: HttpClient) { }

  getGastoById(idGasto: string): Observable<GastoDTO> {
    return this.http.get<GastoDTO>(`${environment.urlApi}spent/${idGasto}`);
  }


}
