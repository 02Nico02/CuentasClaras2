import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { User } from '../../services/auth/user';
import { UserService } from '../../services/user/user.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  errorMessage:String="";
  user?:User;

  constructor(private userService:UserService){
    this.userService.getUser(environment.userId).subscribe({
      next:(userData)=>{
        this.user=userData
      },
      error:(errorData)=>{
        this.errorMessage=errorData
      },
      complete:()=>{
        console.info("User Data ok")
      }

    }) 
  }
}
