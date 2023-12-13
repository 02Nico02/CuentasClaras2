import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { LoginService } from '../../services/auth/login.service';
import { LoginRequest } from '../../services/auth/loginRequest';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterModule,ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {

  // acá se especifican los campos que van a estar en el formulario
  // con sus respectivas validaciones
  loginForm=this.formBuilder.group({
    email:["ylucaroni@gmail.com",[Validators.required,Validators.email]],
    password:["",[Validators.required]]
  })

  constructor(private formBuilder:FormBuilder, private router:Router, private loginService:LoginService){

  }

  ngOnInit(): void {
      
  }

  login(){
    if(this.loginForm.valid){
      this.loginService.login(this.loginForm.value as LoginRequest)
      console.log("Llamaar al servicio de login")
      this.router.navigateByUrl("/")
      this.loginForm.reset()
    }
    else{
      console.log("Hiciste todo mal")
      this.loginForm.markAllAsTouched()
    }
  }

  get email(){
    return this.loginForm.controls.email;
  }

  get password()
  {
    return this.loginForm.controls.password;
  }
}
