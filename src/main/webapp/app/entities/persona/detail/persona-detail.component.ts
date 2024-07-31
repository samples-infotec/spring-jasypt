import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IPersona } from '../persona.model';

@Component({
  standalone: true,
  selector: 'core-persona-detail',
  templateUrl: './persona-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PersonaDetailComponent {
  @Input() persona: IPersona | null = null;

  previousState(): void {
    window.history.back();
  }
}
