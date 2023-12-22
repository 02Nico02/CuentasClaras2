import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { GrupoPreviewDTO } from '../group/grupoPreview';
import { AmigoDTO } from './amigo.dto';
import { User } from '../auth/user';
import { userPreview } from '../auth/userPreview';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  getUserByUsername(): Observable<GrupoPreviewDTO[]> {
    return this.http.get<GrupoPreviewDTO[]>(environment.urlApi + "users/my-groups").pipe(
      // tap((userData) => {
      //   localStorage.setItem("grupos", userData)

      // }),
      catchError(this.handleError)
    )
  }

  getFriends(): Observable<AmigoDTO[]> {
    return this.http.get<AmigoDTO[]>(environment.urlApi + "users/friendsList");
  }
  getUsersNotFriends(usernameQuery: string): Observable<any[]> {
    return this.http.get<any[]>(`${environment.urlApi}users/usersNotFriends?usernameQuery=${usernameQuery}`);
  }

  sendFriendRequestById(receiverId: number): Observable<any> {
    return this.http.post<any>(`${environment.urlApi}users/sendFriendRequestById?receiverId=${receiverId}`, {});
  }

  getUserInfo(): Observable<userPreview> {
    return this.http.get<userPreview>(`${environment.urlApi}users/userInfo`);
  }

  acceptFriendRequest(invitationId: number): Observable<any> {
    return this.http.post<any>(`${environment.urlApi}users/acceptFriendRequest?invitationId=${invitationId}`, {});
  }

  declineFriendRequest(invitationId: number): Observable<any> {
    return this.http.post<any>(`${environment.urlApi}users/rejectFriendRequest?invitationId=${invitationId}`, {});
  }

  acceptGroupInvitation(invitationId: number): Observable<any> {
    return this.http.post<any>(`${environment.urlApi}users/acceptGroupInvitation?invitationId=${invitationId}`, {});
  }
  declineGroupInvitation(invitationId: number): Observable<any> {
    return this.http.post<any>(`${environment.urlApi}users/rejectGroupInvitation?invitationId=${invitationId}`, {});
  }

  sendGroupInvitation(receiverId: number, groupId: number): Observable<any> {
    console.log("Antes de enviar receiver: " + receiverId + " grupo:" + groupId)
    return this.http.post<any>(`${environment.urlApi}users/sendGroupInvitation?receiverId=${receiverId}&groupId=${groupId}`, {});
  }




  private handleError(error: HttpErrorResponse) {
    if (error.status === 0) {
      console.log("Se ha producido un error", error.error)
    }
    else {
      console.log("Backend retorno el codigo de estado", error.status, error.error)

    }
    return throwError(() => new Error("algo falló. por favor intente nuevamente"))
  }
}