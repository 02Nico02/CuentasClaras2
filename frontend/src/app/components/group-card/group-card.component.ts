import { Component, Input } from '@angular/core';
import { GrupoPreviewDTO } from '../../services/group/grupoPreview';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-group-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './group-card.component.html',
  styleUrl: './group-card.component.css'
})
export class GroupCardComponent {
  @Input() groupData?: GrupoPreviewDTO

  getBalanceMessage(balance: number): string {
    if (balance < 0) {
      return 'Debes';
    } else if (balance > 0) {
      return 'Te deben';
    } else {
      return 'Estás al día';
    }
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

  formatMonto(monto: number): string {
    const formattedMonto = monto.toLocaleString('es-AR', {
      style: 'currency',
      currency: 'ARS'
    });

    return formattedMonto;
  }
}
