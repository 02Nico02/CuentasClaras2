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
    this.currentUserLoginOn= new BehaviorSubject<boolean>(sessionStorage.getItem("token")!=null)
    this.currentUserData= new BehaviorSubject<String>(sessionStorage.getItem("token")||"")
   }

  login(credentials:LoginRequest):Observable<any>{
    console.log(credentials)
    console.log(typeof(credentials))
    JSON.stringify(credentials);
    console.log(credentials)
    console.log(typeof(credentials))
    return this.http.post<any>(environment.urlApi+"users/auth",credentials).pipe(
      tap((userData)=>{
        console.log({userData})
        console.log(userData.token)
        sessionStorage.setItem("token",userData.token)
        this.currentUserData.next(userData)
        this.currentUserLoginOn.next(true)
      }),
      map((userData)=>userData.token),
      catchError(this.handleError)
    )
  }

  logout():void{
    sessionStorage.removeItem("token")
    this.currentUserLoginOn.next(false)
  }

  private handleError(error:HttpErrorResponse){
    if(error.status===0){
      
      console.log("Se ha producido un error", error.error)
    }
    else{
      console.log("El backend retornó el código de estado", error.status, error.error)   
    }
    return throwError(()=> new Error("Algo falló. Por favor intente nuevamente"))
  }

  get userData():Observable<String>{
    return this.currentUserData.asObservable();
  }

  get userLoginOn(): Observable<boolean>{
    return this.currentUserLoginOn.asObservable()  
  }

  
}
