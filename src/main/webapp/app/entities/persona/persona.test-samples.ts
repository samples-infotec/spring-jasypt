import dayjs from 'dayjs/esm';

import { IPersona, NewPersona } from './persona.model';

export const sampleWithRequiredData: IPersona = {
  id: 13343,
  nombre: 'bamboo',
  primerApellido: 'sympathetically the pant',
};

export const sampleWithPartialData: IPersona = {
  id: 30969,
  nombre: 'detailed incidentally infrastructure',
  primerApellido: 'ouch',
  segundoApellido: 'round yippee',
  curp: 'whoa',
};

export const sampleWithFullData: IPersona = {
  id: 21177,
  nombre: 'tenderly apparatus',
  primerApellido: 'opposite',
  segundoApellido: 'happily',
  fechaNacimiento: dayjs('2024-07-30'),
  edad: 17,
  curp: 'why which atop',
};

export const sampleWithNewData: NewPersona = {
  nombre: 'oh zesty',
  primerApellido: 'loneliness than',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
