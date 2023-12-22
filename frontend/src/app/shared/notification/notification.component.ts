import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Notification } from './Notification';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '../../services/user/user.service';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification.component.html',
  styleUrl: './notification.component.css'
})
export class NotificationComponent implements OnInit {
  @Input() notifications: Notification[] = [];
  @Output() openModal = new EventEmitter<boolean>();

  constructor(private router: Router, private userService: UserService) { }

  ngOnInit(): void { }

  onNotificationClick() {
    this.openModal.emit(true);
  }

  acceptInvitation(notification: Notification): void {

    if (notification.type === "Amistad") {
      this.userService.acceptFriendRequest(notification.id).subscribe(
        response => {
          this.notifications = this.notifications.filter(notif => notif.id !== notification.id);
        },
        error => {
          alert("Error al aceptar la invitación");
          console.error("Error:", error);
        }
      );
    } else if (notification.type === "Grupo") {
      this.userService.acceptGroupInvitation(notification.id).subscribe(
        response => {
          this.router.navigate([`/grupo/${notification.idGrupo}/detalle`]);
        },
        error => {
          alert("Error al aceptar la invitación al grupo");
          console.error("Error:", error);
        }
      );
    } else {
      alert("Tipo de notificación no reconocido");
    }
  }

  declineInvitation(notification: Notification): void {
    if (notification.type === "Amistad") {
      this.userService.declineFriendRequest(notification.id).subscribe(
        response => {
          this.notifications = this.notifications.filter(notif => notif.id !== notification.id);
        },
        error => {
          alert("Error al rechazar la invitación");
          console.error("Error:", error);
        }
      );
    } else if (notification.type === "Grupo") {
      this.userService.declineGroupInvitation(notification.id).subscribe(
        response => {
          this.notifications = this.notifications.filter(notif => notif.id !== notification.id);
        },
        error => {
          alert("Error al rechazar la invitación al grupo");
          console.error("Error:", error);
        }
      );
    } else {
      alert("Tipo de notificación no reconocido");
    }
  }

  formatTheNotification(notification: Notification): string {
    if (notification.type === "Amistad") {
      return `Solicitud de amistad de: ${notification.usernameRemitenteAmistad}`;
    } else if (notification.type === "Grupo") {
      return `Te invito a unirte al grupo: ${notification.nombreGrupo}`;
    } else {
      return "Notificación desconocida";
    }
  }

  calculateTimePassed(date: Date): string {
    const fecha = new Date(date);
    const now = new Date();
    const diff = Math.abs(now.getTime() - fecha.getTime());
    const minutes = Math.floor(diff / (1000 * 60));
    if (minutes < 60) {
      return `${minutes} min`;
    }
    const hours = Math.floor(minutes / 60);
    if (hours < 24) {
      return `${hours} h`;
    }
    const days = Math.floor(hours / 24);
    return `${days} d`;
  }

}
