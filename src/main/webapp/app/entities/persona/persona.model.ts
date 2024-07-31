import dayjs from 'dayjs/esm';

export interface IPersona {
  id: number;
  nombre?: string | null;
  primerApellido?: string | null;
  segundoApellido?: string | null;
  fechaNacimiento?: dayjs.Dayjs | null;
  edad?: number | null;
  curp?: string | null;
}

export type NewPersona = Omit<IPersona, 'id'> & { id: null };
