import { Component, OnInit } from '@angular/core';
import { AmigoDTO } from '../../services/user/amigo.dto';
import { UserService } from '../../services/user/user.service';
import { CommonModule } from '@angular/common';
import { NavComponent } from '../../shared/nav/nav.component';
import { Title } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { User } from '../../services/auth/user';

@Component({
  selector: 'app-friends-list',
  standalone: true,
  imports: [CommonModule, NavComponent, FormsModule],
  templateUrl: './friends-list.component.html',
  styleUrl: './friends-list.component.css'
})
export class FriendsListComponent implements OnInit {

  friends: AmigoDTO[] = [];
  filtroAmigos: string = '';
  usuarios: User[] = [];

  constructor(private userService: UserService, private titleService: Title, private router: Router) { }

  ngOnInit(): void {

    this.titleService.setTitle('Cuentas Claras - Amigos');
    this.userService.getFriends().subscribe(
      data => {
        this.friends = data;
      },
      error => {
        console.error('Error al obtener la lista de amigos:', error);
      }
    );
  }

  getClassBalance(balance: number): string {
    if (balance < 0) {
      return 'negative-balance';
    } else if (balance > 0) {
      return 'positive-balance';
    } else {
      return 'zero-balance';
    }
  }

  coupleGroupView(idGroup: number): void {
    this.router.navigateByUrl("/group-duo/" + idGroup)
  }

  agregarAmigoSeleccionado(idUsuario: number): void {
    this.userService.sendFriendRequestById(idUsuario).subscribe(
      response => {
        const usuario = this.usuarios.find(u => u.id === idUsuario);
        if (usuario) {
          usuario.solicitudEnviada = true;
        }
      },
      error => {
        console.error('Error al enviar solicitud de amistad:', error);
      }
    );
  }

  buscarUsuarios(): void {
    if (this.filtroAmigos.trim() !== '') {
      this.userService.getUsersNotFriends(this.filtroAmigos).subscribe(
        data => {
          this.usuarios = data;
        },
        error => {
          console.error('Error al obtener usuarios:', error);
        }
      );
    }
  }
}
