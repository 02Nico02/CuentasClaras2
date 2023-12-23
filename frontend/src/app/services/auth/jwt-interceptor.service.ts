import { HttpEvent, HttpHandler, HttpHandlerFn, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginService } from './login.service';

// @Injectable({
//   providedIn: 'root'
// })
export function JwtInterceptorService(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {

  let token: String = localStorage?.["token"];
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

