import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { User } from '../../services/auth/user';
import { UserService } from '../../services/user/user.service';
import { environment } from '../../../environments/environment';
import { LoginService } from '../../services/auth/login.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  errorMessage: String = "";
  user?: User;
  userLoginOn: boolean = false

  constructor(private userService: UserService, private loginService: LoginService, private router: Router) {
    // this.userService.getUser(environment.userId).subscribe({
    //   next:(userData)=>{
    //     this.user=userData
    //   },
    //   error:(errorData)=>{
    //     this.errorMessage=errorData
    //   },
    //   complete:()=>{
    //     console.info("User Data ok")
    //   }

    // }) 
  }

  ngOnInit(): void {
    this.loginService.currentUserLoginOn.subscribe(
      {
        next: (userLoginOn) => {
          this.userLoginOn = userLoginOn;
        }
      }
    )
  }

  logout() {
    this.loginService.logout().subscribe(
      () => {
        console.log("cerrada");
        // cualquier otra acción después del logout
      },
      error => {
        console.error("Error al cerrar sesión:", error);
      }
    );
    this.router.navigate(['/'])

  }
}
