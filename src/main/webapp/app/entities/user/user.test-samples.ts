import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 11763,
  login: 'C',
};

export const sampleWithPartialData: IUser = {
  id: 11533,
  login: '4uT_z1',
};

export const sampleWithFullData: IUser = {
  id: 6591,
  login: 'Rp2lb@nHyNZ',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
