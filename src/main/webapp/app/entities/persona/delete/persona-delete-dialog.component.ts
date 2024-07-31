import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPersona } from '../persona.model';
import { PersonaService } from '../service/persona.service';

@Component({
  standalone: true,
  templateUrl: './persona-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PersonaDeleteDialogComponent {
  persona?: IPersona;

  protected personaService = inject(PersonaService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.personaService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
