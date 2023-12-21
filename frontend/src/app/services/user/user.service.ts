import { HttpClient ,HttpErrorResponse} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { GrupoPreviewDTO } from '../group/grupoPreview';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http:HttpClient) {}

    getUserByUsername():Observable<GrupoPreviewDTO[]>
    {
      return this.http.get<GrupoPreviewDTO[]>(environment.urlApi+"users/my-groups").pipe(
        // tap((userData) => {
        //   localStorage.setItem("grupos", userData)

        // }),
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