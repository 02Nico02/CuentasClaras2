import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Notification } from './Notification';
import { CommonModule } from '@angular/common';

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

  ngOnInit(): void {
    this.notifications = [
      {
        id: 1,
        type: 'Amistad',
        fechaCreacion: new Date(),
        idRemitenteAmistad: 101,
        usernameRemitenteAmistad: 'user1'
      },
      {
        id: 2,
        type: 'Grupo',
        fechaCreacion: new Date(),
        idGrupo: 201,
        nombreGrupo: 'Grupo de Viajes',
        idRemitenteGrupo: 102,
        usernameRemitenteGrupo: 'user2'
      },
      {
        id: 3,
        type: 'Amistad',
        fechaCreacion: new Date(),
        idRemitenteAmistad: 103,
        usernameRemitenteAmistad: 'user3'
      },
    ];
  }

  onNotificationClick() {
    this.openModal.emit(true);
  }

  acceptInvitation(notification: Notification): void {
    alert("Implementar aceptar")
  }

  declineInvitation(notification: Notification): void {
    alert("Implementar rechazar")
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
    const now = new Date();
    const diff = Math.abs(now.getTime() - date.getTime());
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
