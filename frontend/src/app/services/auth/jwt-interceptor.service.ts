import { HttpEvent, HttpHandler, HttpHandlerFn, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginService } from './login.service';

// @Injectable({
//   providedIn: 'root'
// })
export function JwtInterceptorService (req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>>{

  // constructor(private loginService: LoginService) {   
    console.log("Interceptor: Entrando en el interceptor");
    // let token: String = this.loginService.userToken
    let token: String = localStorage?.["token"];
    console.log("Token:", token);
    if (token != "") {
      req = req.clone({
        setHeaders: {
          "Content-Type": "application/json;charset=utf-8",
          "Accept": "application/json",
          "Authorization": `Bearer ${token}`
        }
      })
    }
    return next(req)
  }
// export class JwtInterceptorService implements HttpInterceptor {

//   constructor(private loginService: LoginService) { }


//   intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
//     console.log("Interceptor: Entrando en el interceptor");
//     // let token: String = this.loginService.userToken
//     let token: String = localStorage?.["token"];
//     console.log("Token:", token);
//     if (token != "") {
//       req = req.clone({
//         setHeaders: {
//           "Content-Type": "application/json;charset=utf-8",
//           "Accept": "application/json",
//           "Authorization": `Bearer ${token}`
//         }
//       })
//     }
//     return next.handle(req)
//   }

