import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '94b3a0b9-e65d-4b75-bc83-55fc6f99d09e',
};

export const sampleWithPartialData: IAuthority = {
  name: '2206af1c-62ce-43cc-958c-8d3f6851a057',
};

export const sampleWithFullData: IAuthority = {
  name: '7d99921e-15a4-48d0-89b8-c693325e4269',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
