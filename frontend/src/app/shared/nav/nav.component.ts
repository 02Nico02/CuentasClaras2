import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { LoginService } from '../../services/auth/login.service';
import { Router } from '@angular/router';
import { NotificationComponent } from '../notification/notification.component';
import { UserService } from '../../services/user/user.service';
import { userPreview } from '../../services/auth/userPreview';

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [CommonModule, NotificationComponent],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css'
})
export class NavComponent implements OnInit {

  isDropdownOpen: boolean = false;
  userInfo: userPreview | null = null;

  constructor(private loginService: LoginService, private router: Router, private userService: UserService) { }

  ngOnInit(): void {
    this.fetchUserInfo();
  }

  formatBalance(): string {
    if (this.userInfo && typeof this.userInfo.balance !== 'undefined') {
      if (this.userInfo.balance > 0) {
        return `+$${this.userInfo.balance.toFixed(2)}`;
      } else if (this.userInfo.balance < 0) {
        return `-$${Math.abs(this.userInfo.balance).toFixed(2)}`;
      } else {
        return '$0';
      }
    } else {
      return 'Balance no disponible';
    }
  }

  logout() {
    this.loginService.logout().subscribe({
      next: () => {
        console.log("cerrada");
      },
      error: (errorData) => {
        console.error("Error al cerrar sesión:", errorData);
      }
    });
    this.router.navigate(['/login'])
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  fetchUserInfo(): void {
    this.userService.getUserInfo().subscribe(
      response => {
        this.userInfo = response;
        console.log(response)
      },
      error => {
        console.error('Error al obtener información del usuario:', error);
      }
    );
  }
}
