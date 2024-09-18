import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable, catchError, throwError } from 'rxjs';
import { RegisterRequest } from './registerRequest';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private http: HttpClient) { }

  register(credentials: RegisterRequest): Observable<any> {
    console.log(credentials)
    return this.http.post<any>(environment.urlApi + "users/register", credentials).pipe(
      catchError(this.handleError)
    )
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = "Algo falló. Por favor intente nuevamente";
    if (error.status === 0) {
      console.log("Se ha producido un error", error.error)
    } else {
      console.log("tipy error: " + error.error.field)
      if (typeof error.error === 'object' && error.error.field) {
        if (error.error.field === 'username') {
          errorMessage = error.error.error;
        } else if (error.error.field === 'email') {
          errorMessage = error.error.error;
        }
      } else {
        console.log("El backend retornó el código de estado", error.status, error.error);
      }
    }
    return throwError(() => errorMessage);
  }






}

