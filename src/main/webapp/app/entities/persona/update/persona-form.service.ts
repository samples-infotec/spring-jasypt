import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPersona, NewPersona } from '../persona.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPersona for edit and NewPersonaFormGroupInput for create.
 */
type PersonaFormGroupInput = IPersona | PartialWithRequiredKeyOf<NewPersona>;

type PersonaFormDefaults = Pick<NewPersona, 'id'>;

type PersonaFormGroupContent = {
  id: FormControl<IPersona['id'] | NewPersona['id']>;
  nombre: FormControl<IPersona['nombre']>;
  primerApellido: FormControl<IPersona['primerApellido']>;
  segundoApellido: FormControl<IPersona['segundoApellido']>;
  fechaNacimiento: FormControl<IPersona['fechaNacimiento']>;
  edad: FormControl<IPersona['edad']>;
  curp: FormControl<IPersona['curp']>;
};

export type PersonaFormGroup = FormGroup<PersonaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PersonaFormService {
  createPersonaFormGroup(persona: PersonaFormGroupInput = { id: null }): PersonaFormGroup {
    const personaRawValue = {
      ...this.getFormDefaults(),
      ...persona,
    };
    return new FormGroup<PersonaFormGroupContent>({
      id: new FormControl(
        { value: personaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombre: new FormControl(personaRawValue.nombre, {
        validators: [Validators.required, Validators.minLength(2)],
      }),
      primerApellido: new FormControl(personaRawValue.primerApellido, {
        validators: [Validators.required, Validators.minLength(2)],
      }),
      segundoApellido: new FormControl(personaRawValue.segundoApellido, {
        validators: [Validators.minLength(2)],
      }),
      fechaNacimiento: new FormControl(personaRawValue.fechaNacimiento),
      edad: new FormControl(personaRawValue.edad, {
        validators: [Validators.min(0), Validators.max(100)],
      }),
      curp: new FormControl(personaRawValue.curp),
    });
  }

  getPersona(form: PersonaFormGroup): IPersona | NewPersona {
    return form.getRawValue() as IPersona | NewPersona;
  }

  resetForm(form: PersonaFormGroup, persona: PersonaFormGroupInput): void {
    const personaRawValue = { ...this.getFormDefaults(), ...persona };
    form.reset(
      {
        ...personaRawValue,
        id: { value: personaRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PersonaFormDefaults {
    return {
      id: null,
    };
  }
}
