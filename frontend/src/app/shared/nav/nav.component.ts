import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { LoginService } from '../../services/auth/login.service';
import { Router } from '@angular/router';
import { NotificationComponent } from '../notification/notification.component';

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [CommonModule, NotificationComponent],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css'
})
export class NavComponent {
  @Input() pagActual: string | undefined;

  saldoUsuario: number = 0.0;
  isDropdownOpen: boolean = false;

  constructor(private loginService: LoginService, private router: Router) { }

  formatBalance(): string {
    if (this.saldoUsuario > 0) {
      return `+$${this.saldoUsuario.toFixed(2)}`;
    } else if (this.saldoUsuario < 0) {
      return `-$${Math.abs(this.saldoUsuario).toFixed(2)}`;
    } else {
      return '$0';
    }
  }

  logout() {
    this.loginService.logout().subscribe({
      next:() => {
        console.log("cerrada");
      },
      error:(errorData) => {
        console.error("Error al cerrar sesión:", errorData);
      },
      complete:()=>{
        this.router.navigate(['/login'])
      }
    });
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }
}
