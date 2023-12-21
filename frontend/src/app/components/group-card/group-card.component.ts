import { Component, Input } from '@angular/core';
import { GrupoPreviewDTO } from '../../services/group/grupoPreview';

@Component({
  selector: 'app-group-card',
  standalone: true,
  imports: [],
  templateUrl: './group-card.component.html',
  styleUrl: './group-card.component.css'
})
export class GroupCardComponent {
  @Input() groupData? : GrupoPreviewDTO 
}
