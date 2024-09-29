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

  formatMonto(monto: number): string {
    const formattedMonto = monto.toLocaleString('es-AR', {
      style: 'currency',
      currency: 'ARS'
    });

    return formattedMonto;
  }
}
