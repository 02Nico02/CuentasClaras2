import { Component, OnInit } from '@angular/core';
import { FormBuilder,ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { RegisterService } from '../../services/auth/register.service';
import { RegisterRequest } from '../../services/auth/registerRequest';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterModule,ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  registerForm=this.formBuilder.group({
    username:["yana",[Validators.required]],
    nombres:["yanasu ariel",[Validators.required]],
    apellido:["lucaroni",[Validators.required]],
    email:["ylucaroni@ejemplo.com",[Validators.required]],
    password:["1234User",[Validators.required]],
  })

  constructor(private formBuilder:FormBuilder, private router:Router, private registerService:RegisterService){

  }

  ngOnInit(): void {
      
  }

  register(){
    console.log(this.registerForm.valid)
    if(this.registerForm.valid){
      this.registerService.register(this.registerForm.value as RegisterRequest).subscribe({
        next:(userData)=>{
          console.log(userData)
        },
        error:(errorData)=>{
          console.log(errorData,"vocé no sabe nada")
          // this.loginError=errorData
        },
        complete:()=>{
          console.info("Registro completado la rompimos toda")
          this.router.navigateByUrl("/login")
          // this.loginForm.reset()
        }
      })
    }
  }

  get username(){
    return this.registerForm.controls.username;
  }
  get nombres(){
    return this.registerForm.controls.nombres;
  }
  get apellido(){
    return this.registerForm.controls.apellido;
  }
  get email(){
    return this.registerForm.controls.email;
  }

  get password()
  {
    return this.registerForm.controls.password;
  }
}
