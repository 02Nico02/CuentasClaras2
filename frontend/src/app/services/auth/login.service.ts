import { Injectable } from '@angular/core';
import { LoginRequest } from './loginRequest';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError, BehaviorSubject, tap, map } from 'rxjs';
import { User } from './user';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LoginService {


  currentUserLoginOn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false)
  currentUserData: BehaviorSubject<String> = new BehaviorSubject<String>("")

  constructor(private http: HttpClient) {
    this.currentUserLoginOn = new BehaviorSubject<boolean>(localStorage.getItem("token") != null)
    this.currentUserData = new BehaviorSubject<String>(localStorage.getItem("token") || "")
  }

  login(credentials: LoginRequest): Observable<any> {
    return this.http.post<any>(environment.urlApi + "users/auth", credentials).pipe(
      tap((userData) => {
        localStorage.setItem("token", userData.token)
        this.currentUserData.next(userData)
        console.log(userData)
        this.currentUserLoginOn.next(true)
      }),
      map((userData) => userData.token),
      catchError(this.handleError)
    )
  }

  logout(): Observable<any> {
    return this.http.post<any>(environment.urlApi + "users/logout", {}).pipe(
      tap(() => {
        localStorage.removeItem("token");
        this.currentUserLoginOn.next(false);
      }),
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    console.log("error")
    let errorMessage = "Algo falló. Por favor intente nuevamente";

    if (error.error instanceof ErrorEvent) {
      console.error('Ocurrió un error:', error.error.message);
    } else {
      errorMessage = error.error || error.statusText || errorMessage;

      if (error.status === 401) {
        console.log("El error es: ", error);
        if (error.error && typeof error.error === 'object' && error.error.error) {
          errorMessage = error.error.error;
        } else {
          errorMessage = "Usuario o contraseña incorrecta";
        }
      }

      console.error(`El backend retornó el código de estado ${error.status}, con el mensaje: ${errorMessage}`);
    }

    return throwError(() => errorMessage);
  }

  get userData(): Observable<String> {
    return this.currentUserData.asObservable();
  }

  get userLoginOn(): Observable<boolean> {
    return this.currentUserLoginOn.asObservable()
  }

  get userToken(): String {
    return this.currentUserData.value
  }


}
