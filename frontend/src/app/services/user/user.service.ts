import { HttpClient ,HttpErrorResponse} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { User } from '../auth/user';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http:HttpClient) {}

    getUser(id:number):Observable<User>
    {
      return this.http.get<User>(environment.urlApi+"users/"+id).pipe(
        catchError(this.handleError)
      )
    }
   

private handleError(error: HttpErrorResponse){
  if(error.status ===0){
    console.log("Se ha producido un error", error.error)
  }
  else{
    console.log("Backend retorno el codigo de estado", error.status, error.error)
    
  }
  return throwError(()=> new Error ("algo falló. por favor intente nuevamente"))
}
}