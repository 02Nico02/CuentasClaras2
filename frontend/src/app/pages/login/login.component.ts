import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { LoginService } from '../../services/auth/login.service';
import { LoginRequest } from '../../services/auth/loginRequest';
import { Title } from '@angular/platform-browser';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  loginError: string = "";
  loginForm: FormGroup;
  private errorHandled: boolean = false;

  constructor(private formBuilder: FormBuilder, private router: Router, private loginService: LoginService, private titleService: Title) {
    this.loginForm = this.formBuilder.group({
      userName: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.titleService.setTitle('Cuentas Claras - Iniciar Sesión');
  }

  login(): void {
    if (this.loginForm.valid) {
      this.errorHandled = false;
      this.loginService.login(this.loginForm.value as LoginRequest)
        .pipe(
          catchError((error) => {
            if (!this.errorHandled) {
              this.errorHandled = true;
              if (error && error.error && error.error.error) {
                this.loginError = error.error.error;
              } else {
                this.loginError = "Error desconocido";
              }
            }
            return throwError(error);
          })
        )
        .subscribe({
          next: (userData) => {
            console.log({ userData });
          },
          error: (errorData) => {
            console.error("Error en la suscripción:", errorData);
            if (!this.errorHandled) {
              this.errorHandled = true;
              this.loginError = errorData;
            }
          },
          complete: () => {
            this.router.navigateByUrl("/home");
            this.loginForm.reset();
          }
        });
    } else {
      console.log("Formulario inválido");
      this.loginForm.markAllAsTouched();
    }
  }


  get userName() {
    return this.loginForm.controls["userName"];
  }

  get password() {
    return this.loginForm.controls["password"];
  }
}
