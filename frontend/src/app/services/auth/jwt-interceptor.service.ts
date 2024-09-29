import { HttpEvent, HttpHandler, HttpHandlerFn, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginService } from './login.service';

// @Injectable({
//   providedIn: 'root'
// })
export function JwtInterceptorService(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {

  let token: string | null = localStorage.getItem("token");
  if (token) {
    req = req.clone({
      setHeaders: {
        "Authorization": `Bearer ${token}`
      }
    });
  }

  if (!(req.body instanceof FormData)) {
    const headers = req.headers.set("Content-Type", "application/json;charset=utf-8")
      .set("Accept", "application/json");

    req = req.clone({ headers });
  }
  return next(req)
}

