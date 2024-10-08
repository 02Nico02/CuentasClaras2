import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { User } from '../../services/auth/user';
import { UserService } from '../../services/user/user.service';
import { LoginService } from '../../services/auth/login.service';
import { GrupoPreviewDTO } from '../../services/group/grupoPreview';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NavComponent } from '../../shared/nav/nav.component';
import { GroupCardComponent } from '../../components/group-card/group-card.component';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule, NavComponent, GroupCardComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  errorMessage: String = "";
  user?: User;
  userLoginOn: boolean = false
  userGroups?: GrupoPreviewDTO[]

  constructor(private userService: UserService, private loginService: LoginService, private router: Router, private titleService: Title) {

  }

  ngOnInit(): void {
    this.titleService.setTitle('Cuentas Claras - Grupos');
    this.llamarAPI()
  }

  llamarAPI() {
    this.userService.getUserByUsername().subscribe({
      next: (userData) => {
        // console.log(userData)
        this.userGroups = userData
        console.log(this.userGroups[0])
        // typeof(userData)
      },
      error: (errorData) => {
        this.errorMessage = errorData
      },
      complete: () => {
        console.info("User Data ok")
      }

    })
  }

  createGroup() {
    this.router.navigate(['/crear-grupo'])
  }
}
