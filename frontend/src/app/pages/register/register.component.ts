import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { RegisterService } from '../../services/auth/register.service';
import { RegisterRequest } from '../../services/auth/registerRequest';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  registerError: string = "";

  constructor(private formBuilder: FormBuilder, private router: Router, private registerService: RegisterService, private titleService: Title) {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20), Validators.pattern('[a-zA-Z0-9]*')]],
      nombres: ['', [Validators.required, Validators.pattern('^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$')]],
      apellido: ['', [Validators.required, Validators.pattern('^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$')]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
    })
  }

  ngOnInit(): void {
    this.titleService.setTitle('Cuentas Claras - Registrarse');
  }

  register() {
    console.log(this.registerForm.valid)
    if (this.registerForm.valid) {
      this.registerService.register(this.registerForm.value as RegisterRequest).subscribe({
        next: (userData) => {
          console.log(userData)
        },
        error: (errorData) => {
          this.registerError = errorData;
        },
        complete: () => {
          this.router.navigateByUrl("/login")
          // this.loginForm.reset()
        }
      })
    }
  }

  get username() {
    return this.registerForm.controls["username"];
  }
  get nombres() {
    return this.registerForm.controls["nombres"];
  }
  get apellido() {
    return this.registerForm.controls["apellido"];
  }
  get email() {
    return this.registerForm.controls["email"];
  }

  get password() {
    return this.registerForm.controls["password"];
  }
}
