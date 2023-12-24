import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GastoDTO } from './gasto.dto';
import { Observable, catchError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';
import { CategoriaDTO } from '../group/grupo.dto';

@Injectable({
  providedIn: 'root'
})
export class GastoService {


  constructor(private http: HttpClient, private router: Router) { }

  getGastoById(idGasto: string): Observable<GastoDTO> {
    return this.http.get<GastoDTO>(`${environment.urlApi}spent/${idGasto}`).pipe(
      catchError(error => {
        if (error.status === 0) {
          if (error.error && error.error.error) {
            const errorMessage = error.error.error;
            console.error('Error del servidor:', errorMessage);
            this.router.navigate(['/login']);
          } else {
            this.router.navigate(['/login']);
          }
        }
        throw error;
      })
    );
  }

  getAllExpenseCategories(): Observable<CategoriaDTO[]> {
    return this.http.get<CategoriaDTO[]>(`${environment.urlApi}category/expenses`).pipe(
      catchError(error => {
        if (error.status === 0) {
          if (error.error && error.error.error) {
            const errorMessage = error.error.error;
            console.error('Error del servidor:', errorMessage);
            this.router.navigate(['/login']);
          } else {
            this.router.navigate(['/login']);
          }
        }
        throw error;
      })
    );
  }
}
